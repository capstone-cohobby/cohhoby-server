package com.backthree.cohobby.domain.chatting.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReadReceiptDto {
    private Long roomId;
    private Long userId;              // 읽음을 보고한 사용자
    private Long lastReadMessageId;   // 그 사용자가 읽은 마지막 메시지 ID
}