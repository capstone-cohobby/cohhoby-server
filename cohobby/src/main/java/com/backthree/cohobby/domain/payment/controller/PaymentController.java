package com.backthree.cohobby.domain.payment.controller;

import com.backthree.cohobby.domain.payment.dto.request.PaymentConfirmRequest;
import com.backthree.cohobby.domain.payment.dto.request.PaymentIntentRequest;
import com.backthree.cohobby.domain.payment.dto.request.TossWebhookRequest;
import com.backthree.cohobby.domain.payment.dto.response.PaymentDetailResponse;
import com.backthree.cohobby.domain.payment.dto.response.PaymentIntentResponse;
import com.backthree.cohobby.domain.payment.dto.response.PaymentConfirmResponse;
import com.backthree.cohobby.domain.payment.service.PaymentService;
import com.backthree.cohobby.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {
    private final PaymentService paymentService;

    //결제 의도 생성
    @PostMapping("/intents")
    public ResponseEntity<PaymentIntentResponse> createPaymentIntent(
            @RequestBody PaymentIntentRequest request,
            User user //수정사항: 사용자 인증 로직 필요
    ){
        PaymentIntentResponse response = paymentService.createPaymentIntent(request, user);
        return ResponseEntity.ok(response);
    }

    //결제 승인
    @PostMapping("/confirm")
    public ResponseEntity<PaymentConfirmResponse> confirmPayment(
            @RequestBody PaymentConfirmRequest request
            ){
        PaymentConfirmResponse response = paymentService.confirmPayment(request);
        return ResponseEntity.ok(response);
    }

    //웹훅 수신
    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(
            @RequestBody TossWebhookRequest request,
            @RequestHeader("Toss-Signature") String signatureHeader){
        //1. 보안 검증
        /* sdk 연결 후에 시그니처 검증까지 실행하도록 주석 해제
        if(!paymentService.isValidSignature(request, signatureHeader)){
            log.warn("Webhook signature 검증 실패: {}", request);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid signature");
        }
        */

        //2. 서비스 로직으로 웹훅 처리
        try{
            paymentService.handleWebhook(request);
            return ResponseEntity.ok("Webhook 생성");
        }catch(Exception e){
            log.error("Webhook 처리 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error");
        }


    }

    //개별 결제 조회
    @GetMapping("/{paymentId}")
    public ResponseEntity PaymentDetailResponse(
            @PathVariable Long paymentId,
            @RequestBody User user //사용자 검증 로직
    ){
        //1. 유저 검증

        //2. 결제 정보 빈환
        PaymentDetailResponse response = paymentService.getPaymentDetail(paymentId, user);
        return ResponseEntity.ok(response);

    }
}
