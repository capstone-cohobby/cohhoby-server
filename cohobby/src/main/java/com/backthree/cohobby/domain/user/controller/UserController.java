package com.backthree.cohobby.domain.user.controller;

import com.backthree.cohobby.domain.user.dto.UserResponseDTO;
import com.backthree.cohobby.domain.user.service.UserService;
import com.backthree.cohobby.global.common.response.status.ErrorStatus;
import com.backthree.cohobby.global.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDTO> getUserProfile(@PathVariable Long userId) {
        return ResponseEntity.ok(UserResponseDTO.from(userService.findUserById(userId)));
    }
}

