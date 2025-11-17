package com.backthree.cohobby.domain.chatting.controller;

import com.backthree.cohobby.domain.chatting.dto.ChattingDto;
import com.backthree.cohobby.domain.chatting.dto.ChattingRoomDto;
import com.backthree.cohobby.domain.chatting.service.ChattingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/chatting")
@RequiredArgsConstructor
public class ChattingController {
    private final ChattingService chattingService;

    @GetMapping("/{roomId}")
    public List<ChattingDto> getChattingByRoom(@PathVariable Long roomId) {
        return chattingService.getChattingByRoomId(roomId);
    }

    @GetMapping("/room")
    public List<ChattingRoomDto> getRooms(@RequestParam Long userId) {
        return chattingService.getRoomsByUser(userId);
    }


    //request body로 수정
    @PostMapping(value = "/room", consumes = "application/json")
    public ChattingRoomDto createRoom(@RequestBody Map<String, Object> request) {
        Long postId = Long.valueOf(request.get("postId").toString());
        Long borrowerId = Long.valueOf(request.get("borrowerId").toString());
        return chattingService.createRoom(postId, borrowerId);
    }
}
