package com.backthree.cohobby.domain.chatting.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChattingRoomDto {
    private Long id;
    private Long postId;
    private Long ownerId;
    private Long borrowerId;
    private String name;
}
