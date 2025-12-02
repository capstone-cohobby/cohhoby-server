package com.backthree.cohobby.domain.payment.dto.response;

import com.backthree.cohobby.domain.payment.entity.UserCard;

public record UserCardResponse(
        Long cardId,
        String cardNumber,
        String cardCompany,
        String cardType,
        boolean deletable
) {
    public static UserCardResponse from(UserCard userCard, boolean deletable) {
        return new UserCardResponse(
                userCard.getId(),
                userCard.getCardNumber(),
                userCard.getCardCompany(),
                userCard.getCardType(),
                deletable
        );
    }
}

