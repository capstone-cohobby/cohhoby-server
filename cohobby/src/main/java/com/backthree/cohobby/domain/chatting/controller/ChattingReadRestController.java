package com.backthree.cohobby.domain.chatting.controller;

import com.backthree.cohobby.domain.chatting.dto.ReadReceiptDto;
import com.backthree.cohobby.domain.chatting.service.ChattingReadService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chatting")
public class ChattingReadRestController {

    private final ChattingReadService chattingReadService;

    @GetMapping("/rooms/{roomId}/read-status/{userId}")
    public ReadReceiptDto getReadStatus(@PathVariable Long roomId, @PathVariable Long userId) {
        return chattingReadService.getReadStatus(roomId, userId);
    }
}
