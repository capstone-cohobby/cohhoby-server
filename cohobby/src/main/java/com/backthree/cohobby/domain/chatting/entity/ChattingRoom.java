package com.backthree.cohobby.domain.chatting.entity;

import com.backthree.cohobby.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChattingRoom {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user1_id", nullable = false)
    private User user1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user2_id", nullable = false)
    private User user2;

    @Column(name = "user1_last_read_message_id")
    private Long user1LastReadMessageId;

    @Column(name = "user2_last_read_message_id")
    private Long user2LastReadMessageId;

    public void updateLastRead(User user, Long messageId) {
        if (messageId == null) return;
        if (user1.getId().equals(user.getId())) {
            if (user1LastReadMessageId == null || messageId > user1LastReadMessageId) {
                user1LastReadMessageId = messageId;
            }
        } else if (user2.getId().equals(user.getId())) {
            if (user2LastReadMessageId == null || messageId > user2LastReadMessageId) {
                user2LastReadMessageId = messageId;
            }
        } else {
            throw new IllegalArgumentException("채팅방에 속하지 않은 사용자");
        }
    }

    public Long getLastReadOf(User user) {
        if (user1.getId().equals(user.getId())) return user1LastReadMessageId;
        if (user2.getId().equals(user.getId())) return user2LastReadMessageId;
        return null;
    }
}
