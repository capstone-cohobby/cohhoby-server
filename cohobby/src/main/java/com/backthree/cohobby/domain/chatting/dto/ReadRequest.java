package com.backthree.cohobby.domain.chatting.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReadRequest {
    private Long roomId;
    private Long userId;
    private Long lastMessageId;
}