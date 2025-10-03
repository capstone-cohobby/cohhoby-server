package com.backthree.cohobby.domain.chatting.controller;

import com.backthree.cohobby.domain.chatting.dto.ChattingDto;
import com.backthree.cohobby.domain.chatting.dto.ChattingMessage;
import com.backthree.cohobby.domain.chatting.dto.ChattingRoomDto;
import com.backthree.cohobby.domain.chatting.service.ChattingService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/chatting")
@RequiredArgsConstructor
public class ChattingController {
    private final ChattingService chattingService;
    private final SimpMessagingTemplate messagingTemplate;

    @GetMapping("/{roomId}")
    public List<ChattingDto> getChattingByRoom(@PathVariable Long roomId) {
        return chattingService.getChattingByRoomId(roomId);
    }

    @GetMapping("/room")
    public List<ChattingRoomDto> getRooms(@RequestParam Long userId) {
        return chattingService.getRoomsByUser(userId);
    }

    @PostMapping("/room")
    public ChattingRoomDto createRoom(@RequestParam Long user1Id, @RequestParam Long user2Id) {
        System.out.println("Creating room between users: " + user1Id + " and " + user2Id);
        return chattingService.createRoom(user1Id, user2Id);
    }

    // 1) 클라이언트가 /pub/chatting/message 로 publish
    @MessageMapping("/chatting/message")
    public void handleChatMessage(@Payload ChattingMessage message) {
        if (message == null || message.getRoomId() == null) return;
        if (message.getCreatedAt() == null) message.setCreatedAt(LocalDateTime.now());
        // 서버가 /sub/chatting/room/{roomId} 구독자에게 전달
        messagingTemplate.convertAndSend("/sub/chatting/room/" + message.getRoomId(), message);
    }

    // 2) 채팅방 입장 시 클라이언트가 /sub/chatting/room/{roomId}를 구독하면 1회성 초기 응답
    @SubscribeMapping("/chatting/room/{roomId}")
    public ChattingMessage onSubscribe(@DestinationVariable Long roomId) {
        return ChattingMessage.builder()
                .roomId(roomId)
                .content("SUBSCRIBED")
                .createdAt(LocalDateTime.now())
                .build();
    }
}
