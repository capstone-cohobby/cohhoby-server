package com.backthree.cohobby.domain.chatting.entity;

import com.backthree.cohobby.domain.post.entity.Post;
import com.backthree.cohobby.domain.user.entity.User;
import com.backthree.cohobby.domain.rent.entity.Rent;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChattingRoom {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "borrower_id", nullable = false)
    private User borrower;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rent_id", nullable = false)
    private Rent rent;

    @Column(name = "owner_last_read_message_id")
    private Long ownerLastReadMessageId;

    @Column(name = "borrower_last_read_message_id")
    private Long borrowerLastReadMessageId;

    public void updateLastRead(User user, Long messageId) {
        if (messageId == null) return;
        if (owner.getId().equals(user.getId())) {
            if (ownerLastReadMessageId == null || messageId > ownerLastReadMessageId) {
                ownerLastReadMessageId = messageId;
            }
        } else if (borrower.getId().equals(user.getId())) {
            if (borrowerLastReadMessageId == null || messageId > borrowerLastReadMessageId) {
                borrowerLastReadMessageId = messageId;
            }
        } else {
            throw new IllegalArgumentException("채팅방에 속하지 않은 사용자");
        }
    }

    public Long getLastReadOf(User user) {
        if (owner.getId().equals(user.getId())) return ownerLastReadMessageId;
        if (borrower.getId().equals(user.getId())) return borrowerLastReadMessageId;
        return null;
    }
}
