package com.backthree.cohobby.domain.chatting.controller;

import com.backthree.cohobby.domain.chatting.dto.ReadRequest;
import com.backthree.cohobby.domain.chatting.service.ChattingReadService;
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

import java.security.Principal;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChattingReadStompController {

    private final ChattingReadService chattingReadService;
    private final UserRepository userRepository;

    @MessageMapping("/chatting/read")
    @Transactional
    public void read(ReadRequest req, SimpMessageHeaderAccessor headerAccessor) {
        // 현재 로그인된 사용자 가져오기
        User currentUser = getCurrentUser(headerAccessor);
        if (currentUser == null) {
            log.warn("인증되지 않은 사용자가 읽음 요청을 보내려고 시도했습니다.");
            throw new IllegalStateException("로그인이 필요합니다!");
        }

        // 필수 필드 검증
        if (req.getRoomId() == null || req.getLastMessageId() == null) {
            throw new IllegalArgumentException("잘못된 읽음 요청: roomId와 lastMessageId는 필수입니다.");
        }

        // 클라이언트에서 보낸 userId가 있으면 검증 (하위 호환성)
        if (req.getUserId() != null && !req.getUserId().equals(currentUser.getId())) {
            log.warn("클라이언트에서 보낸 userId({})와 현재 사용자 ID({})가 일치하지 않습니다. 현재 사용자 ID를 사용합니다.", 
                    req.getUserId(), currentUser.getId());
        }

        // 현재 사용자 ID로 읽음 처리
        chattingReadService.markRead(req.getRoomId(), currentUser.getId(), req.getLastMessageId());
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
