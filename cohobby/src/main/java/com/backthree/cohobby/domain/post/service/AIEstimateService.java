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
    public AiEstimateResponse aiEstimate(AiEstimateRequest request, Long postId, Long userId){
        AiEstimateRawResponse raw = restTemplate.postForObject(
                aiApiUrl,
                request,
                AiEstimateRawResponse.class
        );

        if (raw == null) {
            throw new IllegalStateException("AI 서버 응답이 null입니다.");
        }

        Integer low = null;
        Integer point = null;
        Integer high = null;
        Integer suggestedDeposit = null;
        String caution = null;
        String reason = null;
        String decision = null;
        Double confidence = null;

        if (raw.getPrice() != null && raw.getPrice().getPrice() != null) {
            var p = raw.getPrice().getPrice();
            low = p.getLow();
            point = p.getPoint();
            high = p.getHigh();
        }

        if (raw.getDeposit() != null) {
            suggestedDeposit = raw.getDeposit().getDepositAmount();
        }

        if (raw.getRules() != null && raw.getRules().getRules() != null) {
            caution = String.join("\n", raw.getRules().getRules());
        }

        if (raw.getPrice() != null) {
            reason = raw.getPrice().getReasoning();
            decision = raw.getPrice().getDecision();
            confidence = raw.getPrice().getConfidence();
        }

        return AiEstimateResponse.builder()
                .suggestedLowPrice(low)
                .suggestedPointPrice(point)
                .suggestedHighPrice(high)
                .suggestedDeposit(suggestedDeposit)
                .caution(caution)
                .reason(reason)
                .decision(decision)
                .confidence(confidence)
                .build();
    }

}
