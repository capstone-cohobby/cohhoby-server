package com.backthree.cohobby.domain.payment.controller;

import com.backthree.cohobby.domain.payment.dto.request.CardRegisterRequest;
import com.backthree.cohobby.domain.payment.dto.response.CardRegisterResponse;
import com.backthree.cohobby.domain.payment.dto.response.UserCardResponse;
import com.backthree.cohobby.domain.payment.service.UserCardService;
import com.backthree.cohobby.domain.user.entity.User;
import com.backthree.cohobby.global.annotation.CurrentUser;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments/cards")
@RequiredArgsConstructor
@Slf4j
public class UserCardController {
    private final UserCardService userCardService;

    // 카드 등록
    @PostMapping
    public ResponseEntity<CardRegisterResponse> registerCard(
            @RequestBody CardRegisterRequest request,
            @Parameter(hidden = true) @CurrentUser User user
    ) {
        CardRegisterResponse response = userCardService.registerCard(request, user);
        return ResponseEntity.ok(response);
    }

    // 카드 조회
    @GetMapping
    public ResponseEntity<UserCardResponse> getUserCard(
            @Parameter(hidden = true) @CurrentUser User user
    ) {
        UserCardResponse response = userCardService.getUserCard(user);
        return ResponseEntity.ok(response);
    }

    // 카드 삭제
    @DeleteMapping
    public ResponseEntity<Void> deleteCard(
            @Parameter(hidden = true) @CurrentUser User user
    ) {
        userCardService.deleteCard(user);
        return ResponseEntity.ok().build();
    }
}

