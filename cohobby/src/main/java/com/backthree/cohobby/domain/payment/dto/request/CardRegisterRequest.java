package com.backthree.cohobby.domain.payment.dto.request;

public record CardRegisterRequest(
        String authKey // 토스페이먼츠 인증 키 (프론트엔드에서 토스페이먼츠 위젯으로 카드 인증 후 받은 값)
) {
}

