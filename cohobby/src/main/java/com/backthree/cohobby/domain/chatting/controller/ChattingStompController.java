package com.backthree.cohobby.domain.chatting.controller;

import com.backthree.cohobby.domain.chatting.dto.ChattingDto;
import com.backthree.cohobby.domain.chatting.entity.Chatting;
import com.backthree.cohobby.domain.chatting.entity.ChattingRoom;
import com.backthree.cohobby.domain.chatting.repository.ChattingRepository;
import com.backthree.cohobby.domain.chatting.repository.ChattingRoomRepository;
import com.backthree.cohobby.domain.user.entity.User;
import com.backthree.cohobby.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.LocalDateTime;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChattingStompController {

    private final ChattingRepository chattingRepository;
    private final ChattingRoomRepository chattingRoomRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    // 클라이언트에서 /pub/chatting/send 로 전송
    @MessageMapping("/chatting/send")
    @Transactional
    public void sendMessage(ChattingDto message) {
        if (message.getRoomId() == null ||
                message.getSenderId() == null ||
                message.getReceiverId() == null ||
                message.getText() == null ||
                message.getText().isBlank()) {
            log.warn("잘못된 메시지 payload: {}", message);
            return;
        }

        ChattingRoom room = chattingRoomRepository.findById(message.getRoomId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채팅방"));
        User sender = userRepository.findById(message.getSenderId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 발신자"));
        User receiver = userRepository.findById(message.getReceiverId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 수신자"));

        // 참여자 검증
        boolean senderInRoom = room.getUser1().getId().equals(sender.getId()) ||
                room.getUser2().getId().equals(sender.getId());
        boolean receiverInRoom = room.getUser1().getId().equals(receiver.getId()) ||
                room.getUser2().getId().equals(receiver.getId());

        if (!senderInRoom) {
            throw new IllegalArgumentException("채팅방에 속하지 않은 사용자");
        }
        if (!receiverInRoom) {
            throw new IllegalArgumentException("채팅방에 속하지 않은 사용자");
        }

        Chatting chat = chattingRepository.save(Chatting.builder()
                .room(room)
                .sender(sender)
                .receiver(receiver)
                .text(message.getText())
                .time(LocalDateTime.now())
                .build());

        ChattingDto payload = ChattingDto.builder()
                .id(chat.getId())
                .roomId(room.getId())
                .senderId(sender.getId())
                .receiverId(receiver.getId())
                .text(chat.getText())
                .time(chat.getTime())
                .build();

        String destination = "/sub/chatting/room/" + room.getId();
        messagingTemplate.convertAndSend(destination, payload);
        log.debug("메시지 전송 -> {} : {}", destination, payload);
    }
}
