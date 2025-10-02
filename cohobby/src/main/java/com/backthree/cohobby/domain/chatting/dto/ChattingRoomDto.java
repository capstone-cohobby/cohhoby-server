package com.backthree.cohobby.domain.chatting.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChattingRoomDto {
    private Long id;
    private Long user1Id;
    private Long user2Id;
    private String name;
}
