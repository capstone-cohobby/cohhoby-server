package com.backthree.cohobby.domain.chatting.controller;

import com.backthree.cohobby.domain.chatting.dto.ReadReceiptDto;
import com.backthree.cohobby.domain.chatting.service.ChattingReadService;
import com.backthree.cohobby.domain.user.entity.User;
import com.backthree.cohobby.global.annotation.CurrentUser;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chatting")
public class ChattingReadRestController {

    private final ChattingReadService chattingReadService;

    @GetMapping("/rooms/{roomId}/read-status")
    public ReadReceiptDto getReadStatus(
            @PathVariable Long roomId,
            @Parameter(hidden = true) @CurrentUser User user
    ) {
        return chattingReadService.getReadStatus(roomId, user.getId());
    }
}
