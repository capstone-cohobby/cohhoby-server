package com.backthree.cohobby.domain.chatting.alert.entity;

import com.backthree.cohobby.domain.chatting.entity.Chatting;
import com.backthree.cohobby.domain.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChattingAlert extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 127, nullable = false)
    private String text;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chattingId", nullable = false)
    private Chatting chatting;
}