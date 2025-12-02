package com.backthree.cohobby.domain.payment.dto.response;

import com.backthree.cohobby.domain.payment.entity.UserCard;

public record CardRegisterResponse(
        Long cardId,
        String cardNumber,
        String cardCompany,
        String cardType
) {
    public static CardRegisterResponse from(UserCard userCard) {
        return new CardRegisterResponse(
                userCard.getId(),
                userCard.getCardNumber(),
                userCard.getCardCompany(),
                userCard.getCardType()
        );
    }
}

