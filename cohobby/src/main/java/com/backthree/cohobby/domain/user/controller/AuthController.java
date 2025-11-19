package com.backthree.cohobby.domain.user.controller;

import com.backthree.cohobby.domain.user.dto.TokenDTO;
import com.backthree.cohobby.domain.user.dto.TokenRefreshRequestDTO;
import com.backthree.cohobby.domain.user.entity.User;
import com.backthree.cohobby.domain.user.repository.RefreshTokenRepository;
import com.backthree.cohobby.domain.user.repository.UserRepository;
import com.backthree.cohobby.global.annotation.CurrentUser;
import com.backthree.cohobby.global.config.security.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    /*@PostMapping("/signup-extra")
    public ResponseEntity<String> handleForm(@RequestBody SignupExtraDTO dto,
                             @AuthenticationPrincipal User user) {
        user.updateAdditionalInfo(
                Gender.valueOf(dto.getGender()),
                dto.getBirthYear(),
                dto.getBirthday(),
                dto.getPhoneNumber());
        userRepository.save(user);
        return ResponseEntity.ok("사용자 추가 정보가 성공적으로 업데이트되었습니다.");
    }*/

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@CurrentUser User user) throws IOException {
        refreshTokenRepository.findByUser(user)
                        .ifPresent(refreshTokenRepository::delete);
        return ResponseEntity.ok("로그아웃되었습니다.");
    }

    @PostMapping("/token/refresh")
    public ResponseEntity<TokenDTO> refreshToken(@RequestBody TokenRefreshRequestDTO requestDTO) throws IOException {
        String refreshToken = requestDTO.getRefreshToken();
        //리프레시 토큰 유효성 검증
        if(refreshToken == null || !jwtService.validateToken(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않은 리프레시 토큰입니다.");
        }
        //DB에 저장한 리프레시 토큰과 일치하는지 검증
        refreshTokenRepository.findByToken(refreshToken)
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않거나 폐기된 리프레시 토큰입니다."));
        //새로운 액세스 토큰 발급
        String email = jwtService.extractEmail(refreshToken);
        String newAccessToken = jwtService.createAccessToken(email);
        return ResponseEntity.ok(TokenDTO.builder().accessToken(newAccessToken).build());
    }

    @PostMapping("/kakao/unlink")
    public ResponseEntity<String> unlink(HttpServletRequest request, HttpServletResponse response) throws IOException {
        return ResponseEntity.ok().build();
    }

}
