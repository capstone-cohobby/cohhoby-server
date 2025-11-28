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
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;

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
    public void sendMessage(ChattingDto message, SimpMessageHeaderAccessor headerAccessor) {
        // 현재 로그인된 사용자 가져오기
        User sender = getCurrentUser(headerAccessor);
        if (sender == null) {
            log.warn("인증되지 않은 사용자가 메시지를 보내려고 시도했습니다.");
            throw new IllegalStateException("로그인이 필요합니다!");
        }

        // 필수 필드 검증
        if (message.getRoomId() == null ||
                message.getText() == null ||
                message.getText().isBlank()) {
            log.warn("잘못된 메시지 payload: {}", message);
            throw new IllegalArgumentException("필수 필드가 누락되었습니다.");
        }

        ChattingRoom room = chattingRoomRepository.findById(message.getRoomId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채팅방"));

        // sender가 채팅방에 속해있는지 검증
        boolean senderInRoom = room.getOwner().getId().equals(sender.getId()) ||
                room.getBorrower().getId().equals(sender.getId());

        if (!senderInRoom) {
            throw new IllegalArgumentException("채팅방에 속하지 않은 사용자");
        }

        // receiver는 채팅방의 다른 사용자로 자동 설정
        User receiver = room.getOwner().getId().equals(sender.getId()) 
                ? room.getBorrower() 
                : room.getOwner();

        // 클라이언트에서 보낸 receiverId가 있으면 검증 (하위 호환성)
        if (message.getReceiverId() != null && !message.getReceiverId().equals(receiver.getId())) {
            log.warn("클라이언트에서 보낸 receiverId({})와 실제 receiverId({})가 일치하지 않습니다. 실제 receiverId를 사용합니다.", 
                    message.getReceiverId(), receiver.getId());
        }

        Chatting chat = chattingRepository.save(Chatting.builder()
                .room(room)
                .sender(sender)
                .receiver(receiver)
                .text(message.getText())
                .time(LocalDateTime.now(ZoneId.of("Asia/Seoul")))
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

    /**
     * 현재 로그인된 사용자 가져오기
     */
    private User getCurrentUser(SimpMessageHeaderAccessor headerAccessor) {
        // 1. SecurityContext에서 가져오기 시도
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            return (User) authentication.getPrincipal();
        }

        // 2. Principal에서 User 가져오기 시도 (UserPrincipal인 경우)
        Principal principal = headerAccessor.getUser();
        if (principal instanceof com.backthree.cohobby.global.config.websocket.UserPrincipal) {
            return ((com.backthree.cohobby.global.config.websocket.UserPrincipal) principal).getUser();
        }

        // 3. 세션 속성에서 User 가져오기 시도 (WebSocketAuthInterceptor에서 저장한 값)
        Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
        if (sessionAttributes != null) {
            Object userObj = sessionAttributes.get("user");
            if (userObj instanceof User) {
                return (User) userObj;
            }
        }

        // 4. Principal의 이름으로 User 조회 시도
        if (principal != null && principal.getName() != null) {
            return userRepository.findByEmail(principal.getName()).orElse(null);
        }

        return null;
    }
}
