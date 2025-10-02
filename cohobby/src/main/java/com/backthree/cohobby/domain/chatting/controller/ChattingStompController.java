package com.backthree.cohobby.domain.chatting.controller;

import com.backthree.cohobby.domain.chatting.dto.ChattingDto;
import com.backthree.cohobby.domain.chatting.entity.Chatting;
import com.backthree.cohobby.domain.chatting.entity.ChattingRoom;
import com.backthree.cohobby.domain.chatting.repository.ChattingRepository;
import com.backthree.cohobby.domain.chatting.repository.ChattingRoomRepository;
import com.backthree.cohobby.domain.user.entity.User;
import com.backthree.cohobby.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
public class ChattingStompController {
    private final ChattingRepository chattingRepository;
    private final ChattingRoomRepository chattingRoomRepository;
    private final UserRepository userRepository;

    @MessageMapping("/chatting/send")
    @SendTo("/sub/chat/room")
    public ChattingDto sendMessage(ChattingDto message) {
        ChattingRoom room = chattingRoomRepository.findById(message.getRoomId()).orElseThrow();
        User sender = userRepository.findById(message.getSenderId()).orElseThrow();
        User receiver = userRepository.findById(message.getReceiverId()).orElseThrow();

        Chatting chat = chattingRepository.save(Chatting.builder()
                .room(room)
                .sender(sender)
                .receiver(receiver)
                .text(message.getText())
                .time(LocalDateTime.now())
                .build());

        return ChattingDto.builder()
                .id(chat.getId())
                .roomId(room.getId())
                .senderId(sender.getId())
                .receiverId(receiver.getId())
                .text(chat.getText())
                .time(chat.getTime())
                .build();
    }
}
