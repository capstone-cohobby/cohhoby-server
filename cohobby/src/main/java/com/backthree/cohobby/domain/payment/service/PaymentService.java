package com.backthree.cohobby.domain.payment.service;

import com.backthree.cohobby.domain.payment.dto.request.PaymentConfirmRequest;
import com.backthree.cohobby.domain.payment.dto.request.PaymentIntentRequest;
import com.backthree.cohobby.domain.payment.dto.request.TossWebhookRequest;
import com.backthree.cohobby.domain.payment.dto.response.PaymentDetailResponse;
import com.backthree.cohobby.domain.payment.dto.response.PaymentIntentResponse;
import com.backthree.cohobby.domain.payment.dto.response.PaymentConfirmResponse;
import com.backthree.cohobby.domain.payment.entity.Payment;
import com.backthree.cohobby.domain.payment.entity.PaymentMethod;
import com.backthree.cohobby.domain.payment.entity.PaymentStatus;
import com.backthree.cohobby.domain.payment.repository.PaymentRepository;
import com.backthree.cohobby.domain.rent.entity.Rent;
import com.backthree.cohobby.domain.rent.repository.RentRepository;
import com.backthree.cohobby.domain.user.entity.User;
import com.backthree.cohobby.global.config.payments.PaymentsConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final RentRepository rentRepository;
    private final PaymentsConfig paymentsConfig;

    // 결제 의도 생성(DB에 PENDING 상태로 저장
    public PaymentIntentResponse createPaymentIntent(PaymentIntentRequest request, User user) {
        //Rent 정보 조회
        Rent rent = rentRepository.findById(request.rentId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 대여입니다."));
        if(!rent.getTotalPrice().equals(request.amount())){ //Rent 대여 금액과 결제 금액 비교
            throw new IllegalArgumentException("결제 요청 금액이 대여 금액과 일치하지 않습니다.");
        }

        //구매상품 가져오기
        String orderName = rent.getPost().getGoods();
        //주문번호 생성 - 랜덤으로
        String pgOrderNo = "cohobby-"+ UUID.randomUUID().toString();

        Payment payment = Payment.builder()
                .rent(rent)
                .method(PaymentMethod.TRANSFER) //계좌이체를 디폴트로 설정
                .amountExpected(request.amount())
                .currency("KRW") //KRW를 디폴트로 설정
                .orderName(orderName)
                .pgOrderNo(pgOrderNo)
                .status(PaymentStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
        paymentRepository.save(payment);

        return PaymentIntentResponse.from(payment, user, paymentsConfig);
    }

    //결제 승인
    public PaymentConfirmResponse confirmPayment(PaymentConfirmRequest request) {
        //주문번호로 payment 조회
        Payment payment = paymentRepository.findByPgOrderNo(request.orderId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문입니다."));

        //금액 위변조 검증
        if(!payment.getAmountExpected().equals(request.amount())){
            throw new IllegalArgumentException("결제 금액이 요청 금액과 일치하지 않습니다.");
        }

        //PG사 결제 승인 API 호출
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = createAuthHeaders();

        //PG사에 보낼 요청 본문 생성
        Map<String, Object> body = Map.of(
                "paymentKey", request.paymentKey(),
                "orderId", request.orderId(),
                "amount", request.amount()
        );

        try{
            //PG사에 승인 요청 보내기(pg사의 결제승인 api 호출)
            /*  프론트 구현으로 sdk 연결 이후에 실제 테스트 가능
            String pgResponse = restTemplate.postForObject(
                    paymentsConfig.getToss().getConfirmUrl(),
                    new HttpEntity<>(body, headers),
                    String.class
            );
            */
            String pgResponse = "{ \"status\": \"DONE\", \"approvedAt\": \"2025-10-07T10:00:00\" }";


            //성공 시 Payment 상태 업데이트
            log.info("PG사 승인 응답: {}", pgResponse);
            payment.confirmSuccess(request.paymentKey(), LocalDateTime.now(), "TOSS");

        }catch(Exception e){
            //실패 시 Payment 상태 업데이트 + 예외 처리
            log.error("PG사 결제 승인 실패: {}", e.getMessage());
            payment.confirmFailure("PG_ERROR", e.getMessage());
            throw new RuntimeException("결제 승인에 실패했습니다");
        }

        //save는 @Transactional에 의해 자동 처리됨
        return PaymentConfirmResponse.from(payment);
    }

    private HttpHeaders createAuthHeaders() {
        HttpHeaders headers = new HttpHeaders();
        //secret key를 basic auth로 설정
        //spring이 내부적으로 Base64 인코딩 수행해줌
        headers.setBasicAuth(paymentsConfig.getToss().getSecretKey(), "");
        //요청/응답 타입 JSON
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return headers;
    }

    //Signature 검증
    public boolean isValidSignature(Object requestBody, String signatureHeader) {
        try{
            String secretkey = paymentsConfig.getToss().getSecretKey();

            //body를 JSON 문자열로 변환
            ObjectMapper mapper = new ObjectMapper();
            String bodyJson = mapper.writeValueAsString(requestBody);

            Mac sha256HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(secretkey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256HMAC.init(secretKey);

            byte[] hash = sha256HMAC.doFinal(bodyJson.getBytes(StandardCharsets.UTF_8));
            String computedSignature = Base64.getEncoder().encodeToString(hash);

            return computedSignature.equals(signatureHeader);
        } catch (Exception e) {
            log.error("Webhook Signature 검증 실패", e);
            return false;
        }
    }

    //웹훅 처리 로직
    public void handleWebhook(TossWebhookRequest request) {
        //orderNo로 payment 존재하는지 검증
        Payment payment = paymentRepository.findByPgOrderNo(request.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문: " + request.getOrderId()));

        //이미 승인된 payment라면 토스에서 보내주는 paymentKey와 db의 paymentKey가 일치하는지 비교)
        if (payment.getPgPaymentKey() != null
                && !payment.getPgPaymentKey().equals(request.getPaymentKey())) {
            throw new IllegalStateException("PaymentKey 불일치: 요청된 key="
                    + request.getPaymentKey()
                    + ", 저장된 key="
                    + payment.getPgPaymentKey());
        }

        switch(request.getStatus().toUpperCase()){
            case "DONE" -> payment.confirmSuccess(request.getPaymentKey(), LocalDateTime.now(), "TOSS_WEBHOOK");
            case "CANCELLED" -> payment.confirmFailure("TOSS_CANCELLED", "토스에서 결제 취소됨");
            case "FAILED" -> payment.confirmFailure("TOSS_FAILED", "토스에서 결제 실패");
            default -> log.warn("결제 도중 예외 상태 전달: {}", request.getStatus());
        }

        log.info("Webhook 처리 완료: orderId = {}, status = {}", request.getOrderId(), request.getStatus());
    }

    //개별 결제 조회
    @Transactional(readOnly = true)
    public PaymentDetailResponse getPaymentDetail(Long paymentId, User user) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 결제 정보를 찾을 수 없습니다: " + paymentId));

        return PaymentDetailResponse.from(payment, user);
    }
}
