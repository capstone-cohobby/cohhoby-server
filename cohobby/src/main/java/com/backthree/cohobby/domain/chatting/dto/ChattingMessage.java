package com.backthree.cohobby.domain.chatting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChattingMessage {
    private Long roomId;           // 대상 채팅방
    private Long senderId;         // 전송자 식별자
    private String sender;         // 표시명(옵션)
    private String content;        // 내용
    private LocalDateTime createdAt;
}

