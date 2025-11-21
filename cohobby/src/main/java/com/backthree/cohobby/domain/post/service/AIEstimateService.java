package com.backthree.cohobby.domain.post.service;

import com.backthree.cohobby.domain.post.dto.request.AiEstimateRequest;
import com.backthree.cohobby.domain.post.dto.response.AiEstimateRawResponse;
import com.backthree.cohobby.domain.post.dto.response.AiEstimateResponse;
import com.backthree.cohobby.domain.post.repository.PostRepository;
import com.backthree.cohobby.domain.user.service.UserService;
import com.backthree.cohobby.global.common.response.status.ErrorStatus;
import com.backthree.cohobby.global.exception.GeneralException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class AIEstimateService {
    private final PostRepository postRepository;
    private final UserService userService;
    private final RestTemplate restTemplate;

    @Value("${ai.api.url}")
    private String aiApiUrl;

    @Transactional
    public AiEstimateResponse aiEstimate(AiEstimateRequest request, Long postId, Long userId) {
        AiEstimateRawResponse raw = restTemplate.postForObject(
                aiApiUrl,
                request,
                AiEstimateRawResponse.class
        );

        if (raw == null) {
            throw new IllegalStateException("AI 서버 응답이 null입니다.");
        }

        // 변수 초기화
        Integer low = null;
        Integer point = null;
        Integer high = null;
        Integer suggestedDeposit = null;
        String caution = null;

        // 💡 수정 포인트: 이유(reason)를 합치기 위한 StringBuilder 생성
        StringBuilder fullReason = new StringBuilder();

        String decision = null;
        Double confidence = null;

        // 1. 가격 정보 매핑
        if (raw.getPrice() != null) {
            // (1) 가격 데이터 추출
            if (raw.getPrice().getPrice() != null) {
                var p = raw.getPrice().getPrice();
                low = p.getLow();
                point = p.getPoint();
                high = p.getHigh();
            }

            // (2) 가격 이유 추가
            if (raw.getPrice().getReasoning() != null) {
                fullReason.append("[가격 사유]\n")
                        .append(raw.getPrice().getReasoning())
                        .append("\n\n");
            }

            // (3) 기타 정보
            decision = raw.getPrice().getDecision();
            confidence = raw.getPrice().getConfidence();
        }

        // 2. 보증금 정보 매핑
        if (raw.getDeposit() != null) {
            suggestedDeposit = raw.getDeposit().getDepositAmount();

            // (4) 보증금 이유 추가
            if (raw.getDeposit().getReasoning() != null) {
                fullReason.append("[보증금 사유]\n")
                        .append(raw.getDeposit().getReasoning())
                        .append("\n\n");
            }
        }

        // 3. 규칙 정보 매핑
        if (raw.getRules() != null) {
            if (raw.getRules().getRules() != null) {
                caution = String.join("\n", raw.getRules().getRules());
            }

            // (5) 규칙 이유 추가
            if (raw.getRules().getReasoning() != null) {
                fullReason.append("[규칙 사유]\n")
                        .append(raw.getRules().getReasoning());
            }
        }

        // 4. 결과 반환
        return AiEstimateResponse.builder()
                .suggestedLowPrice(low)
                .suggestedPointPrice(point)
                .suggestedHighPrice(high)
                .suggestedDeposit(suggestedDeposit)
                .caution(caution)
                .reason(fullReason.toString().trim()) // 💡 합쳐진 전체 이유를 반환
                .decision(decision)
                .confidence(confidence)
                .build();
    }
}
