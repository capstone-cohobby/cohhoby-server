package com.backthree.cohobby.domain.chatting.service;

import com.backthree.cohobby.domain.chatting.dto.ReadReceiptDto;
import com.backthree.cohobby.domain.chatting.entity.ChattingRoom;
import com.backthree.cohobby.domain.chatting.repository.ChattingRoomRepository;
import com.backthree.cohobby.domain.user.entity.User;
import com.backthree.cohobby.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChattingReadService {

    private final ChattingRoomRepository roomRepo;
    private final UserRepository userRepo;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public void markRead(Long roomId, Long userId, Long lastMessageId) {
        ChattingRoom room = roomRepo.findById(roomId).orElseThrow();
        User user = userRepo.findById(userId).orElseThrow();
        room.updateLastRead(user, lastMessageId);

        ReadReceiptDto payload = ReadReceiptDto.builder()
                .roomId(roomId)
                .userId(userId)
                .lastReadMessageId(room.getLastReadOf(user))
                .build();

        messagingTemplate.convertAndSend("/sub/chatting/room/" + roomId + "/read", payload);
    }

    @Transactional(readOnly = true)
    public ReadReceiptDto getReadStatus(Long roomId, Long userId) {
        ChattingRoom room = roomRepo.findById(roomId).orElseThrow();
        User user = userRepo.findById(userId).orElseThrow();
        return ReadReceiptDto.builder()
                .roomId(roomId)
                .userId(userId)
                .lastReadMessageId(room.getLastReadOf(user))
                .build();
    }

    @Transactional(readOnly = true)
    public ReadReceiptDto getPeerReadStatus(Long roomId, Long userId) {
        ChattingRoom room = roomRepo.findById(roomId).orElseThrow();
        User user = userRepo.findById(userId).orElseThrow();
        
        // 상대방 찾기
        User peer;
        if (room.getOwner().getId().equals(userId)) {
            peer = room.getBorrower();
        } else if (room.getBorrower().getId().equals(userId)) {
            peer = room.getOwner();
        } else {
            throw new IllegalArgumentException("채팅방에 속하지 않은 사용자");
        }
        
        return ReadReceiptDto.builder()
                .roomId(roomId)
                .userId(peer.getId())
                .lastReadMessageId(room.getLastReadOf(peer))
                .build();
    }
}
