package com.backthree.cohobby.domain.chatting.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChattingDto {
    private Long id;
    private Long roomId;
    private Long senderId;
    private Long receiverId;
    private String text;
    private LocalDateTime time;
}
