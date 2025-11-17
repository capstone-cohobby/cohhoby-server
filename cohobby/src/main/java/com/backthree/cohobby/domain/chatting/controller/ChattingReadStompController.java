package com.backthree.cohobby.domain.chatting.controller;

import com.backthree.cohobby.domain.chatting.dto.ReadRequest;
import com.backthree.cohobby.domain.chatting.service.ChattingReadService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

@Controller
@RequiredArgsConstructor
public class ChattingReadStompController {

    private final ChattingReadService chattingReadService;

    @MessageMapping("/chatting/read")
    @Transactional
    public void read(ReadRequest req) {
        if (req.getRoomId() == null || req.getUserId() == null || req.getLastMessageId() == null) {
            throw new IllegalArgumentException("잘못된 읽음 요청");
        }
        chattingReadService.markRead(req.getRoomId(), req.getUserId(), req.getLastMessageId());
    }
}
