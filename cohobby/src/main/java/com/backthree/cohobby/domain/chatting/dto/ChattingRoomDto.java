package com.backthree.cohobby.domain.chatting.dto;

import lombok.*;

import java.time.LocalDateTime;

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
    private String lastMessage;
    private LocalDateTime lastMessageTime;
    private String peerName; // 상대방 이름
    private Long peerId; // 상대방 ID
    private String peerProfilePicture; // 상대방 프로필 사진
    private String postGoods; // 게시물 상품명
    private Integer unreadCount; // 읽지 않은 메시지 수
}
