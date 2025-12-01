package com.backthree.cohobby.domain.chatting.controller;

import com.backthree.cohobby.domain.chatting.dto.ChattingDto;
import com.backthree.cohobby.domain.chatting.dto.ChattingRoomDto;
import com.backthree.cohobby.domain.chatting.service.ChattingService;
import com.backthree.cohobby.domain.user.entity.User;
import com.backthree.cohobby.global.annotation.CurrentUser;
import io.swagger.v3.oas.annotations.Parameter;
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
    public List<ChattingRoomDto> getRooms(@CurrentUser User user) {
        return chattingService.getRoomsByUser(user.getId());
    }


    //request body로 수정
    @PostMapping(value = "/room", consumes = "application/json")
    public ChattingRoomDto createRoom(
            @RequestBody Map<String, Object> request,
            @Parameter(hidden = true) @CurrentUser User user
    ) {
        Long postId = Long.valueOf(request.get("postId").toString());
        Long borrowerId = user.getId(); // 현재 로그인한 사용자가 borrower
        
        // 날짜와 가격 정보 추출 (선택적)
        String startDate = request.get("startDate") != null ? request.get("startDate").toString() : null;
        String endDate = request.get("endDate") != null ? request.get("endDate").toString() : null;
        Integer totalPrice = null;
        if (request.get("totalPrice") != null) {
            Object priceObj = request.get("totalPrice");
            if (priceObj instanceof Number) {
                totalPrice = ((Number) priceObj).intValue();
            } else {
                totalPrice = Integer.valueOf(priceObj.toString());
            }
        }
        
        return chattingService.createRoom(postId, borrowerId, startDate, endDate, totalPrice);
    }
}
