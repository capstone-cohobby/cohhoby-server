package com.backthree.cohobby.domain.chatting.controller;

import com.backthree.cohobby.domain.chatting.dto.ChattingDto;
import com.backthree.cohobby.domain.chatting.dto.ChattingRoomDto;
import com.backthree.cohobby.domain.chatting.service.ChattingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PostMapping("/room")
    public ChattingRoomDto createRoom(@RequestParam Long user1Id, @RequestParam Long user2Id) {
        System.out.println("Creating room between users: " + user1Id + " and " + user2Id);
        return chattingService.createRoom(user1Id, user2Id);
    }
}
