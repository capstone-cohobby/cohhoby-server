package com.backthree.cohobby.domain.post.service;

import com.backthree.cohobby.domain.hobby.entity.Hobby;
import com.backthree.cohobby.domain.hobby.repository.HobbyRepository;
import com.backthree.cohobby.domain.post.dto.request.AiEstimateClientRequest;
import com.backthree.cohobby.domain.post.dto.request.AiEstimateRequest;
import com.backthree.cohobby.domain.post.dto.response.AiEstimateRawResponse;
import com.backthree.cohobby.domain.post.dto.response.AiEstimateResponse;
import com.backthree.cohobby.domain.post.entity.AiEstimateReport;
import com.backthree.cohobby.domain.post.entity.Post;
import com.backthree.cohobby.domain.post.repository.AiEstimateReportRepository;
import com.backthree.cohobby.domain.post.repository.PostRepository;
import com.backthree.cohobby.domain.user.service.UserService;
import com.backthree.cohobby.global.common.response.status.ErrorStatus;
import com.backthree.cohobby.global.exception.GeneralException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AIEstimateService {
    private final PostRepository postRepository;
    private final UserService userService;
    private final RestTemplate restTemplate;
    private final HobbyRepository hobbyRepository;
    private final AiEstimateReportRepository aiEstimateReportRepository;

    @Value("${ai.api.url}")
    private String aiApiUrl;

    @Transactional
    public AiEstimateResponse aiEstimate(AiEstimateClientRequest clientRequest, Long postId, Long userId) {

        // 1. 취미 ID로 취미 이름 조회
        Hobby hobby = hobbyRepository.findById(clientRequest.getHobbyId())
                .orElseThrow(() -> new GeneralException(ErrorStatus.HOBBY_NOT_FOUND)); // 적절한 에러코드 사용

        String hobbyName = hobby.getName(); // DB에서 가져온 실제 이름

        // 2. AI 서버용 DTO 생성 및 데이터 매핑
        AiEstimateRequest aiPayload = new AiEstimateRequest();
        aiPayload.setGoods(clientRequest.getGoods());
        aiPayload.setHobbyName(hobbyName); //  조회한 이름 주입 ("category")
        aiPayload.setPurchaseAt(clientRequest.getPurchaseAt());
        aiPayload.setDefectStatus(clientRequest.getDefectStatus());

        AiEstimateRawResponse raw = restTemplate.postForObject(
                aiApiUrl,
                aiPayload,
                AiEstimateRawResponse.class
        );

        if (raw == null) {
            throw new IllegalStateException("AI 서버 응답이 null입니다.");
        }

        // 변수 초기화
        Integer low = null;
        Integer point = null;
        Integer high = null;

        // Reference 정보 담을 변수 선언
        Integer referencePrice = null;
        String referenceUrl = null;
        String referenceType = null;

        Integer suggestedDeposit = null;
        String caution = null;
        String decision = null;
        Double confidence = null;

        String priceReason = null;
        String depositReason = null;
        String ruleReason = null;

        // 4. 가격 정보 매핑
        if (raw.getPrice() != null) {
            // PriceDetail 객체를 꺼내옵니다.
            var priceDetail = raw.getPrice().getPrice();

            if (priceDetail != null) {
                low = priceDetail.getLow();
                point = priceDetail.getPoint();
                high = priceDetail.getHigh();

                // ★ [핵심 수정] 여기서 Reference 정보를 변수에 미리 담습니다.
                referencePrice = priceDetail.getReferencePrice();
                referenceUrl = priceDetail.getReferenceUrl();
                referenceType = priceDetail.getReferenceType();
            }

            // 가격 사유 및 기타 정보 추출
            priceReason = raw.getPrice().getReasoning();
            decision = raw.getPrice().getDecision();
            confidence = raw.getPrice().getConfidence();
        }

        // 5. 보증금 정보 매핑
        if (raw.getDeposit() != null) {
            suggestedDeposit = raw.getDeposit().getDepositAmount();
            depositReason = raw.getDeposit().getReasoning();
        }

        // 6. 규칙 정보 매핑
        if (raw.getRules() != null) {
            if (raw.getRules().getRules() != null) {
                caution = String.join("\n", raw.getRules().getRules());
            }
            ruleReason = raw.getRules().getReasoning();
        }

        // 7. Evidence 리스트 변환
        List<AiEstimateResponse.EvidenceDto> evidenceList = new ArrayList<>();
        if (raw.getEvidence() != null) {
            evidenceList = raw.getEvidence().stream()
                    .map(e -> AiEstimateResponse.EvidenceDto.builder()
                            .title(e.getTitle())
                            .url(e.getUrl())
                            .build())
                    .collect(Collectors.toList()); // Java 버전에 따라 .toList() 가능
        }

        // Evidence 리스트를 JSON 문자열로 변환 (예: Jackson 사용)
        ObjectMapper objectMapper = new ObjectMapper();
        String evidenceJson = "";
        try {
            evidenceJson = objectMapper.writeValueAsString(evidenceList);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Evidence 직렬화 실패", e);
        }

        // 게시글 조회
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.POST_NOT_FOUND));

        // DB 저장
        AiEstimateReport report = AiEstimateReport.builder()
                .post(post)
                .suggestedLowPrice(low)
                .suggestedPointPrice(point)
                .suggestedHighPrice(high)
                .suggestedDeposit(suggestedDeposit)
                .caution(caution)
                .priceReason(priceReason)
                .depositReason(depositReason)
                .ruleReason(ruleReason)
                .decision(decision)
                .confidence(confidence)
                .referencePrice(referencePrice)
                .referenceUrl(referenceUrl)
                .referenceType(referenceType)
                .evidenceJson(evidenceJson)
                .build();

        aiEstimateReportRepository.save(report);

        // 8. 결과 반환
        return AiEstimateResponse.builder()
                .suggestedLowPrice(low)
                .suggestedPointPrice(point)
                .suggestedHighPrice(high)
                .suggestedDeposit(suggestedDeposit)
                .caution(caution)

                // ★ [수정] 위에서 추출해둔 변수를 사용 (priceDetail 접근 오류 해결)
                .referencePrice(referencePrice)
                .referenceUrl(referenceUrl)
                .referenceType(referenceType)

                .priceReason(priceReason)
                .depositReason(depositReason)
                .ruleReason(ruleReason)

                .decision(decision)
                .confidence(confidence)
                .evidence(evidenceList)
                .build();
    }

    @Transactional
    public AiEstimateResponse getEstimateByPostId(Long postId) {
        AiEstimateReport report = aiEstimateReportRepository.findByPostId(postId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.REPORT_NOT_FOUND));

        // JSON -> List<EvidenceDto> 변환
        ObjectMapper objectMapper = new ObjectMapper();
        List<AiEstimateResponse.EvidenceDto> evidenceList = new ArrayList<>();
        try {
            evidenceList = objectMapper.readValue(
                    report.getEvidenceJson(),
                    new TypeReference<List<AiEstimateResponse.EvidenceDto>>() {}
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Evidence JSON 역직렬화 실패", e);
        }

        return AiEstimateResponse.builder()
                .suggestedLowPrice(report.getSuggestedLowPrice())
                .suggestedPointPrice(report.getSuggestedPointPrice())
                .suggestedHighPrice(report.getSuggestedHighPrice())
                .suggestedDeposit(report.getSuggestedDeposit())
                .caution(report.getCaution())
                .priceReason(report.getPriceReason())
                .depositReason(report.getDepositReason())
                .ruleReason(report.getRuleReason())
                .decision(report.getDecision())
                .confidence(report.getConfidence())
                .referencePrice(report.getReferencePrice())
                .referenceUrl(report.getReferenceUrl())
                .referenceType(report.getReferenceType())
                .evidence(evidenceList)
                .build();
    }

}
