package com.backthree.cohobby.domain.chatting.service;

import com.backthree.cohobby.domain.chatting.dto.ChattingDto;
import com.backthree.cohobby.domain.chatting.dto.ChattingRoomDto;
import com.backthree.cohobby.domain.chatting.entity.Chatting;
import com.backthree.cohobby.domain.chatting.entity.ChattingRoom;
import com.backthree.cohobby.domain.chatting.repository.ChattingRepository;
import com.backthree.cohobby.domain.chatting.repository.ChattingRoomRepository;
import com.backthree.cohobby.domain.user.entity.User;
import com.backthree.cohobby.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChattingService {
    private final ChattingRepository chattingRepository;
    private final ChattingRoomRepository chattingRoomRepository;
    private final UserRepository userRepository;

    public List<ChattingDto> getChattingByRoomId(Long roomId) {
        return chattingRepository.findByRoomId(roomId).stream()
                .map(chat -> ChattingDto.builder()
                        .id(chat.getId())
                        .roomId(chat.getRoom().getId())
                        .senderId(chat.getSender().getId())
                        .receiverId(chat.getReceiver().getId())
                        .text(chat.getText())
                        .time(chat.getTime())
                        .build())
                .collect(Collectors.toList());
    }

    public List<ChattingRoomDto> getRoomsByUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        return chattingRoomRepository.findByUser1OrUser2(user, user).stream()
                .map(room -> ChattingRoomDto.builder()
                        .id(room.getId())
                        .user1Id(room.getUser1().getId())
                        .user2Id(room.getUser2().getId())
                        .name(room.getName())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public ChattingRoomDto createRoom(Long user1Id, Long user2Id) {
        User user1 = userRepository.findById(user1Id).orElseThrow();
        User user2 = userRepository.findById(user2Id).orElseThrow();

        // 필요 시 중복 구분용 suffix 예: + " #" + System.currentTimeMillis()
        String roomName = user1.getNickname() + " & " + user2.getNickname();

        ChattingRoom room = chattingRoomRepository.save(ChattingRoom.builder()
                .user1(user1)
                .user2(user2)
                .name(roomName)
                .build());

        return ChattingRoomDto.builder()
                .id(room.getId())
                .user1Id(room.getUser1().getId())
                .user2Id(room.getUser2().getId())
                .name(room.getName())
                .build();
    }
}
