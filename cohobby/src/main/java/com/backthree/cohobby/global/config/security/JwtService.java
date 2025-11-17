package com.backthree.cohobby.global.config.security;

import com.backthree.cohobby.domain.user.dto.TokenDTO;
import com.backthree.cohobby.domain.user.entity.User;
import com.backthree.cohobby.domain.user.repository.RefreshTokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKeyPlain;
    private Key secretKey;

    private final long ACCESS_TOKEN_EXPIRATION = 1000*60*60; //1시간
    private final long REFRESH_TOKEN_EXPIRATION = 1000*60*60*24*7; //일주일

    //임시 회원가입 토큰 만료 시간
    private final long SIGNUP_TOKEN_EXPIRATION = 1000*60*10;

    private final RefreshTokenRepository refreshTokenRepository;

    @PostConstruct
    protected void init() {
        this.secretKey = Keys.hmacShaKeyFor(secretKeyPlain.getBytes());
    }

    //최초 로그인 시점에 액세스, 리프레시 토큰 모두 발급
    //유저의 이메일을 파라미터로 사용
    public TokenDTO createTokenDTO(String email) {
        String accessToken = createAccessToken(email);
        String refreshToken = createRefreshToken(email);
        return TokenDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    //액세스 토큰 생성
    public String createAccessToken(String email){
        return createToken(email, ACCESS_TOKEN_EXPIRATION);
    }

    //리프레시 토큰 생성
    public String createRefreshToken(String email){
        return createToken(email, REFRESH_TOKEN_EXPIRATION);
    }

    //토큰 생성 공통 로직
    private String createToken(String email, long expiration) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis()+expiration))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    // 임시 토큰 생성 메소드 (providerId 기반)
    public String createSignupToken(String providerId) {
        return Jwts.builder()
                .setSubject(providerId) // providerId를 subject로
                .claim("auth", "ROLE_SIGNUP") // 임시 토큰임을 증명하는 '권한'
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + SIGNUP_TOKEN_EXPIRATION))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    // 임시 토큰 검증 및 providerId 추출
    public String validateAndExtractProviderId(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            // 'auth' 클레임 확인
            String auth = claims.get("auth", String.class);
            if (!"ROLE_SIGNUP".equals(auth)) {
                throw new JwtException("Not a valid signup token (invalid auth claim)");
            }

            return claims.getSubject(); // providerId 반환
        } catch (Exception e) { // (ExpiredJwtException, MalformedJwtException 등 모두 포함)
            // JwtException을 사용하지 않고 SecurityException을 throw
            throw new SecurityException("Invalid or expired signup token", e);
        }
    }

    //토큰에서 이메일 추출
    public String extractEmail(String token) {
        try{
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch(JwtException e){
            return null;
        }
    }

    //토큰 유효성 검사
    public boolean validateToken(String token) {
        try{
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch(JwtException e){
            return false;
        }
    }

}
