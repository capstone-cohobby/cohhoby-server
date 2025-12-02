package com.backthree.cohobby.global.config.security;

import com.backthree.cohobby.domain.user.repository.RefreshTokenRepository;
import com.backthree.cohobby.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final DefaultOAuth2UserService customOauth2UserService;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final OAuth2LoginFailureHandler oAuth2LoginFailureHandler;

    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                //세션을 stateless로 설정(jwt 사용을 위해)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/", "/login", "/oauth2/**", "/favicon.ico", "/css/**", "/js/**").permitAll()
                        .requestMatchers("/auth/token/refresh").permitAll() //토큰 갱신은 누구나 접근 가능해야 함
                        .requestMatchers("/auth/test/**").permitAll() // 테스트용 회원가입/로그인 API
                        .requestMatchers("/swagger-ui/**", "/docs/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll() // Swagger UI는 인증 필요
                        .requestMatchers("/ws-stomp/**").permitAll() // WebSocket 핸드셰이크 허용 (실제 인증은 STOMP CONNECT에서 처리)
                        .requestMatchers("/auth/signup-extra").authenticated() //추가 정보 api는 인증 필요
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        // .loginPage("/login")
                        .userInfoEndpoint(userInfo ->
                                userInfo.userService(customOauth2UserService)
                        )
                        .successHandler(oAuth2LoginSuccessHandler())
                        .failureHandler(oAuth2LoginFailureHandler)
                )
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(authenticationEntryPoint())
                )
                //jwt 인증 필터를 oauth2 로그인 필터 실행 전에 배치
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);


        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler oAuth2LoginSuccessHandler() {
        return new OAuth2LoginSuccessHandler(jwtService, refreshTokenRepository, userRepository);
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authException) -> {
            // Swagger UI 접근 시 인증 실패하면 OAuth2 로그인 페이지로 리다이렉트
            String requestPath = request.getRequestURI();
            if (requestPath != null && (requestPath.startsWith("/swagger-ui") ||
                    requestPath.startsWith("/docs/swagger-ui") ||
                    requestPath.startsWith("/v3/api-docs"))) {
                // 현재 요청의 호스트와 포트를 동적으로 가져와서 리다이렉트 URL 생성
                String scheme = request.getScheme(); // http 또는 https
                String serverName = request.getServerName(); // 호스트명 또는 IP
                int serverPort = request.getServerPort(); // 포트 번호

                // 포트가 기본 포트(80, 443)가 아니면 포트 번호 포함
                String baseUrl = (serverPort == 80 || serverPort == 443)
                    ? scheme + "://" + serverName
                    : scheme + "://" + serverName + ":" + serverPort;

                // Swagger 경로 결정 (요청 경로에 따라)
                String swaggerPath = requestPath.startsWith("/docs/swagger-ui")
                    ? "/docs/swagger-ui/index.html"
                    : "/swagger-ui/index.html";

                String redirectUrl = "/oauth2/authorization/kakao?redirect_to=" + 
                    java.net.URLEncoder.encode(baseUrl + swaggerPath, "UTF-8");
                response.sendRedirect(redirectUrl);
            } else {
                // 다른 경로는 401 응답
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"error\":\"인증이 필요합니다.\",\"message\":\"" + authException.getMessage() + "\"}");
            }
        };
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // 환경변수에서 프론트엔드 URL 가져오기 (없으면 기본값: localhost:3000)
        List<String> allowedOrigins = new ArrayList<>();
        allowedOrigins.add("http://localhost:3000");
        allowedOrigins.add("http://127.0.0.1:3000");
        
        // 환경변수로 설정된 프론트엔드 URL 추가
        if (frontendUrl != null && !frontendUrl.isEmpty()) {
            allowedOrigins.add(frontendUrl);
        }

        config.setAllowedOrigins(allowedOrigins);
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(Arrays.asList("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}
