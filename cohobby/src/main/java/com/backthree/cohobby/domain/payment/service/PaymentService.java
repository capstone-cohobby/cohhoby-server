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
import com.backthree.cohobby.domain.payment.entity.PaymentType;
import com.backthree.cohobby.domain.payment.entity.UserCard;
import com.backthree.cohobby.domain.payment.repository.PaymentRepository;
import com.backthree.cohobby.domain.payment.repository.UserCardRepository;
import com.backthree.cohobby.domain.rent.entity.Rent;
import com.backthree.cohobby.domain.rent.entity.RentStatus;
import com.backthree.cohobby.domain.rent.repository.RentRepository;
import com.backthree.cohobby.domain.user.entity.User;
import com.backthree.cohobby.domain.user.repository.UserRepository;
import com.backthree.cohobby.domain.hobby.repository.HobbyRepository;
import com.backthree.cohobby.global.config.payments.PaymentsConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
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
    private final UserRepository userRepository;
    private final HobbyRepository hobbyRepository;
    private final UserCardRepository userCardRepository;
    private final PaymentsConfig paymentsConfig;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 결제 의도 생성(DB에 PENDING 상태로 저장
    public PaymentIntentResponse createPaymentIntent(PaymentIntentRequest request, User user) {
        //Rent 정보 조회
        Rent rent = rentRepository.findById(request.rentId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 대여입니다."));
        if(!rent.getTotalPrice().equals(request.amount())){ //Rent 대여 금액과 결제 금액 비교
            throw new IllegalArgumentException("결제 요청 금액이 대여 금액과 일치하지 않습니다.");
        }

        // 대여료 결제 시 카드 등록 여부 확인 (borrower가 결제하는 경우)
        if (rent.getBorrower().getId().equals(user.getId())) {
            boolean hasCard = userCardRepository.findByUser(user).isPresent();
            if (!hasCard) {
                throw new IllegalArgumentException("대여료 결제를 위해 카드 등록이 필요합니다. 마이페이지에서 카드를 등록해주세요.");
            }
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
                .paymentType(PaymentType.RENTAL_FEE) // 대여료로 설정
                .createdAt(LocalDateTime.now())
                .autoBilling(false) // 일반 결제는 자동결제 아님
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
        // TODO: 프론트 구현으로 sdk 연결 이후에 실제 테스트 가능하도록 주석 해제
        // HttpHeaders headers = createAuthHeaders();
        // Map<String, Object> body = Map.of(
        //         "paymentKey", request.paymentKey(),
        //         "orderId", request.orderId(),
        //         "amount", request.amount()
        // );
        // String pgResponse = restTemplate.postForObject(
        //         paymentsConfig.getToss().getConfirmUrl(),
        //         new HttpEntity<>(body, headers),
        //         String.class
        // );

        try{
            // 임시 응답 (실제 API 연동 전까지 사용)
            String pgResponse = "{ \"status\": \"DONE\", \"approvedAt\": \"2025-10-07T10:00:00\" }";


            //성공 시 Payment 상태 업데이트
            log.info("PG사 승인 응답: {}", pgResponse);
            payment.confirmSuccess(request.paymentKey(), LocalDateTime.now(), "TOSS", payment.isAutoBilling());

            // 결제가 CAPTURED 상태로 변경된 경우 추가 처리
            if (payment.getStatus() == PaymentStatus.CAPTURED) {
                handlePaymentCaptured(payment);
            }

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
            case "DONE" -> {
                payment.confirmSuccess(request.getPaymentKey(), LocalDateTime.now(), "TOSS_WEBHOOK", payment.isAutoBilling());
                // 결제가 CAPTURED 상태로 변경된 경우 추가 처리
                if (payment.getStatus() == PaymentStatus.CAPTURED) {
                    handlePaymentCaptured(payment);
                }
            }
            case "CANCELLED" -> payment.confirmFailure("TOSS_CANCELLED", "토스에서 결제 취소됨");
            case "FAILED" -> payment.confirmFailure("TOSS_FAILED", "토스에서 결제 실패");
            default -> log.warn("결제 도중 예외 상태 전달: {}", request.getStatus());
        }

        log.info("Webhook 처리 완료: orderId = {}, status = {}", request.getOrderId(), request.getStatus());
    }

    // 결제가 CAPTURED 상태일 때 처리하는 로직
    private void handlePaymentCaptured(Payment payment) {
        Rent rent = payment.getRent();
        Integer paymentAmount = payment.getAmountCaptured() != null 
            ? payment.getAmountCaptured() 
            : payment.getAmountExpected();

        // 1. Rent 상태를 CONFIRMED로 변경
        rent.updateStatus(RentStatus.CONFIRMED);
        rentRepository.save(rent);

        // 2. borrower와 owner의 score 증가
        User borrower = userRepository.findById(rent.getBorrower().getId())
            .orElseThrow(() -> new IllegalArgumentException("대여자 정보를 찾을 수 없습니다."));
        User owner = userRepository.findById(rent.getOwner().getId())
            .orElseThrow(() -> new IllegalArgumentException("대여주인 정보를 찾을 수 없습니다."));

        borrower.addScore(paymentAmount);
        owner.addScore(paymentAmount);
        userRepository.save(borrower);
        userRepository.save(owner);

        // 3. Rent의 Post의 Hobby에도 결제 금액만큼 score 증가
        var hobby = hobbyRepository.findById(rent.getPost().getHobby().getId())
            .orElseThrow(() -> new IllegalArgumentException("취미 정보를 찾을 수 없습니다."));

        hobby.addScore(paymentAmount);
        hobbyRepository.save(hobby);

        log.info("결제 완료 처리: Rent ID={}, 결제 금액={}, 대여자 score={}, 대여주인 score={}, 취미 score={}", 
            rent.getId(), paymentAmount, borrower.getScore(), owner.getScore(), hobby.getScore());
    }

    //개별 결제 조회
    @Transactional(readOnly = true)
    public PaymentDetailResponse getPaymentDetail(Long paymentId, User user) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 결제 정보를 찾을 수 없습니다: " + paymentId));

        return PaymentDetailResponse.from(payment, user);
    }

    // 보증금 자동결제 (빌링키 사용)
    public Payment processDepositAutoPayment(Rent rent) {
        // 대여자(borrower)의 카드 정보 조회
        User borrower = rent.getBorrower();
        UserCard userCard = userCardRepository.findByUser(borrower)
                .orElseThrow(() -> new IllegalArgumentException("등록된 카드가 없습니다. userId: " + borrower.getId()));

        // 보증금이 없으면 예외
        if (rent.getDeposit() == null || rent.getDeposit() <= 0) {
            throw new IllegalArgumentException("보증금이 설정되지 않았습니다. rentId: " + rent.getId());
        }

        // 주문번호 생성
        String pgOrderNo = "cohobby-deposit-" + UUID.randomUUID().toString();
        String orderName = rent.getPost().getGoods() + " - 보증금";

        // Payment 엔티티 생성 (PENDING 상태)
        Payment payment = Payment.builder()
                .rent(rent)
                .method(PaymentMethod.CARD)
                .amountExpected(rent.getDeposit())
                .currency("KRW")
                .orderName(orderName)
                .pgOrderNo(pgOrderNo)
                .status(PaymentStatus.PENDING)
                .paymentType(PaymentType.DEPOSIT) // 보증금으로 설정
                .autoBilling(true) // 자동결제로 설정
                .createdAt(LocalDateTime.now())
                .build();
        paymentRepository.save(payment);

        // 토스페이먼츠 자동결제 API 호출
        HttpHeaders headers = createAuthHeaders();
        Map<String, Object> body = Map.of(
                "customerKey", "customer_" + borrower.getId(), // 고객 키
                "amount", rent.getDeposit(),
                "orderId", pgOrderNo,
                "orderName", orderName
        );

        try {
            String url = paymentsConfig.getToss().getBillingPayUrl() + "/" + userCard.getBillingKey();
            String response = restTemplate.postForObject(
                    url,
                    new HttpEntity<>(body, headers),
                    String.class
            );

            // 응답 파싱
            JsonNode jsonNode = objectMapper.readTree(response);
            String paymentKey = jsonNode.get("paymentKey").asText();
            String status = jsonNode.get("status").asText();

            if ("DONE".equals(status)) {
                payment.confirmSuccess(paymentKey, LocalDateTime.now(), "TOSS", true);
                // 결제가 CAPTURED 상태로 변경된 경우 추가 처리
                if (payment.getStatus() == PaymentStatus.CAPTURED) {
                    handlePaymentCaptured(payment);
                }
                log.info("보증금 자동결제 완료: rentId={}, paymentId={}, amount={}", 
                        rent.getId(), payment.getId(), rent.getDeposit());
            } else {
                payment.confirmFailure("AUTO_PAYMENT_FAILED", "자동결제 실패: " + status);
                log.error("보증금 자동결제 실패: rentId={}, status={}", rent.getId(), status);
            }

        } catch (Exception e) {
            payment.confirmFailure("AUTO_PAYMENT_ERROR", e.getMessage());
            log.error("보증금 자동결제 오류: rentId={}, error={}", rent.getId(), e.getMessage());
            throw new RuntimeException("보증금 자동결제에 실패했습니다: " + e.getMessage());
        }

        return payment;
    }

    // 대여료 자동결제 (빌링키 사용)
    public Payment processRentalFeeAutoPayment(Rent rent) {
        // 대여자(borrower)의 카드 정보 조회
        User borrower = rent.getBorrower();
        UserCard userCard = userCardRepository.findByUser(borrower)
                .orElseThrow(() -> new IllegalArgumentException("등록된 카드가 없습니다. userId: " + borrower.getId()));

        // 대여료가 없으면 예외
        if (rent.getTotalPrice() == null || rent.getTotalPrice() <= 0) {
            throw new IllegalArgumentException("대여료가 설정되지 않았습니다. rentId: " + rent.getId());
        }

        // 주문번호 생성
        String pgOrderNo = "cohobby-rental-" + UUID.randomUUID().toString();
        String orderName = rent.getPost().getGoods() + " - 대여료";

        // Payment 엔티티 생성 (PENDING 상태)
        Payment payment = Payment.builder()
                .rent(rent)
                .method(PaymentMethod.CARD)
                .amountExpected(rent.getTotalPrice())
                .currency("KRW")
                .orderName(orderName)
                .pgOrderNo(pgOrderNo)
                .status(PaymentStatus.PENDING)
                .paymentType(PaymentType.RENTAL_FEE) // 대여료로 설정
                .autoBilling(true) // 자동결제로 설정
                .createdAt(LocalDateTime.now())
                .build();
        paymentRepository.save(payment);

        // 토스페이먼츠 자동결제 API 호출
        HttpHeaders headers = createAuthHeaders();
        Map<String, Object> body = Map.of(
                "customerKey", "customer_" + borrower.getId(), // 고객 키
                "amount", rent.getTotalPrice(),
                "orderId", pgOrderNo,
                "orderName", orderName
        );

        try {
            String url = paymentsConfig.getToss().getBillingPayUrl() + "/" + userCard.getBillingKey();
            String response = restTemplate.postForObject(
                    url,
                    new HttpEntity<>(body, headers),
                    String.class
            );

            // 응답 파싱
            JsonNode jsonNode = objectMapper.readTree(response);
            String paymentKey = jsonNode.get("paymentKey").asText();
            String status = jsonNode.get("status").asText();

            if ("DONE".equals(status)) {
                payment.confirmSuccess(paymentKey, LocalDateTime.now(), "TOSS", true);
                // 결제가 CAPTURED 상태로 변경된 경우 추가 처리
                if (payment.getStatus() == PaymentStatus.CAPTURED) {
                    handlePaymentCaptured(payment);
                }
                log.info("대여료 자동결제 완료: rentId={}, paymentId={}, amount={}",
                        rent.getId(), payment.getId(), rent.getTotalPrice());
            } else {
                payment.confirmFailure("AUTO_PAYMENT_FAILED", "자동결제 실패: " + status);
                log.error("대여료 자동결제 실패: rentId={}, status={}", rent.getId(), status);
            }

        } catch (Exception e) {
            payment.confirmFailure("AUTO_PAYMENT_ERROR", e.getMessage());
            log.error("대여료 자동결제 오류: rentId={}, error={}", rent.getId(), e.getMessage());
            throw new RuntimeException("대여료 자동결제에 실패했습니다: " + e.getMessage());
        }

        return payment;
    }
}
