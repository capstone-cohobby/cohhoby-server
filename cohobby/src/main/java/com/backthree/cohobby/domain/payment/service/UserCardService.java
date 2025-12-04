package com.backthree.cohobby.domain.payment.service;

import com.backthree.cohobby.domain.payment.dto.request.CardRegisterRequest;
import com.backthree.cohobby.domain.payment.dto.response.CardRegisterResponse;
import com.backthree.cohobby.domain.payment.dto.response.UserCardResponse;
import com.backthree.cohobby.domain.payment.entity.UserCard;
import com.backthree.cohobby.domain.payment.repository.UserCardRepository;
import com.backthree.cohobby.domain.user.entity.User;
import com.backthree.cohobby.global.config.payments.PaymentsConfig;
import com.backthree.cohobby.domain.rent.repository.RentRepository;
import com.backthree.cohobby.domain.rent.entity.RentStatus;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserCardService {
    private final UserCardRepository userCardRepository;
    private final RentRepository rentRepository;
    private final PaymentsConfig paymentsConfig;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 카드 등록 (빌링키 발급)
    public CardRegisterResponse registerCard(CardRegisterRequest request, User user) {
        // 이미 등록된 카드가 있으면 삭제 (하나의 카드만 등록 가능)
        userCardRepository.findByUser(user).ifPresent(userCardRepository::delete);

        // customerKey는 서버에서 자동 생성 (사용자 ID 기반)
        String customerKey = "customer_" + user.getId();

        // 토스페이먼츠 빌링키 발급 API 호출
        HttpHeaders headers = createAuthHeaders();
        Map<String, Object> body = Map.of(
                "customerKey", customerKey,
                "authKey", request.authKey()
        );

        try {
            String url = paymentsConfig.getToss().getBillingAuthUrl();
            log.info("토스페이먼츠 빌링키 발급 API 호출: url={}, customerKey={}", url, customerKey);
            
            String response = restTemplate.postForObject(
                    url,
                    new HttpEntity<>(body, headers),
                    String.class
            );

            if (response == null || response.trim().isEmpty()) {
                log.error("토스페이먼츠 API 응답이 비어있습니다: userId={}", user.getId());
                throw new RuntimeException("토스페이먼츠 API 응답이 비어있습니다.");
            }

            log.debug("토스페이먼츠 API 응답: {}", response);

            // 응답 파싱
            JsonNode jsonNode;
            try {
                jsonNode = objectMapper.readTree(response);
            } catch (Exception e) {
                log.error("토스페이먼츠 API 응답 파싱 실패: userId={}, response={}, error={}", 
                        user.getId(), response, e.getMessage());
                throw new RuntimeException("토스페이먼츠 API 응답을 파싱할 수 없습니다: " + e.getMessage());
            }

            // 에러 응답 확인
            if (jsonNode.has("code") || jsonNode.has("message")) {
                String errorCode = jsonNode.has("code") ? jsonNode.get("code").asText() : "UNKNOWN";
                String errorMessage = jsonNode.has("message") ? jsonNode.get("message").asText() : "알 수 없는 오류";
                log.error("토스페이먼츠 API 에러 응답: userId={}, code={}, message={}", 
                        user.getId(), errorCode, errorMessage);
                throw new RuntimeException("토스페이먼츠 API 오류: " + errorMessage + " (코드: " + errorCode + ")");
            }

            // billingKey 확인
            if (!jsonNode.has("billingKey") || jsonNode.get("billingKey").isNull()) {
                log.error("토스페이먼츠 API 응답에 billingKey가 없습니다: userId={}, response={}", 
                        user.getId(), response);
                throw new RuntimeException("토스페이먼츠 API 응답에 billingKey가 없습니다.");
            }
            String billingKey = jsonNode.get("billingKey").asText();

            // card 정보 확인
            if (!jsonNode.has("card") || jsonNode.get("card").isNull()) {
                log.error("토스페이먼츠 API 응답에 card 정보가 없습니다: userId={}, response={}", 
                        user.getId(), response);
                throw new RuntimeException("토스페이먼츠 API 응답에 card 정보가 없습니다.");
            }
            JsonNode cardNode = jsonNode.get("card");

            // cardNumber 추출 (여러 가능한 필드명 확인)
            String cardNumber;
            if (cardNode.has("number")) {
                cardNumber = cardNode.get("number").asText();
            } else if (cardNode.has("cardNumber")) {
                cardNumber = cardNode.get("cardNumber").asText();
            } else {
                log.warn("카드 번호를 찾을 수 없습니다. 기본값 사용: userId={}", user.getId());
                cardNumber = "****-****-****-****";
            }

            // cardCompany 추출 (여러 가능한 필드명 확인)
            String cardCompany;
            if (cardNode.has("company")) {
                cardCompany = cardNode.get("company").asText();
            } else if (cardNode.has("issuerCode")) {
                cardCompany = cardNode.get("issuerCode").asText();
            } else if (cardNode.has("issuer")) {
                cardCompany = cardNode.get("issuer").asText();
            } else {
                log.warn("카드사 정보를 찾을 수 없습니다. 기본값 사용: userId={}", user.getId());
                cardCompany = "UNKNOWN";
            }

            // cardType 추출 (여러 가능한 필드명 확인)
            String cardType;
            if (cardNode.has("cardType")) {
                cardType = cardNode.get("cardType").asText();
            } else if (cardNode.has("type")) {
                cardType = cardNode.get("type").asText();
            } else if (cardNode.has("cardTypeCode")) {
                cardType = cardNode.get("cardTypeCode").asText();
            } else {
                log.warn("카드 타입을 찾을 수 없습니다. 기본값 사용: userId={}", user.getId());
                cardType = "CREDIT";
            }

            // UserCard 엔티티 생성 및 저장
            UserCard userCard = UserCard.builder()
                    .user(user)
                    .billingKey(billingKey)
                    .cardNumber(cardNumber)
                    .cardCompany(cardCompany)
                    .cardType(cardType)
                    .isDefault(true)
                    .build();
            userCardRepository.save(userCard);

            log.info("카드 등록 완료: userId={}, billingKey={}, cardCompany={}, cardType={}", 
                    user.getId(), billingKey, cardCompany, cardType);
            return CardRegisterResponse.from(userCard);

        } catch (org.springframework.web.client.HttpClientErrorException e) {
            // HTTP 4xx 에러 처리
            String errorBody = e.getResponseBodyAsString();
            log.error("토스페이먼츠 API HTTP 에러: userId={}, status={}, body={}", 
                    user.getId(), e.getStatusCode(), errorBody);
            
            try {
                JsonNode errorNode = objectMapper.readTree(errorBody);
                String errorMessage = errorNode.has("message") 
                        ? errorNode.get("message").asText() 
                        : "카드 등록에 실패했습니다.";
                throw new RuntimeException("토스페이먼츠 API 오류: " + errorMessage);
            } catch (Exception parseException) {
                throw new RuntimeException("카드 등록에 실패했습니다: " + e.getMessage());
            }
        } catch (org.springframework.web.client.HttpServerErrorException e) {
            // HTTP 5xx 에러 처리
            String errorBody = e.getResponseBodyAsString();
            log.error("토스페이먼츠 API 서버 에러: userId={}, status={}, body={}", 
                    user.getId(), e.getStatusCode(), errorBody);
            throw new RuntimeException("토스페이먼츠 서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
        } catch (RuntimeException e) {
            // 이미 처리된 RuntimeException은 그대로 전달
            throw e;
        } catch (Exception e) {
            log.error("카드 등록 실패: userId={}, error={}", user.getId(), e.getMessage(), e);
            throw new RuntimeException("카드 등록에 실패했습니다: " + e.getMessage());
        }
    }

    // 사용자 카드 조회
    @Transactional(readOnly = true)
    public UserCardResponse getUserCard(User user) {
        UserCard userCard = userCardRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("등록된 카드가 없습니다."));
        boolean deletable = isCardDeletable(user);
        return UserCardResponse.from(userCard, deletable);
    }

    // 카드 삭제
    public void deleteCard(User user) {
        if (!isCardDeletable(user)) {
            throw new IllegalStateException("진행 중인 대여가 있어 카드를 삭제할 수 없습니다.");
        }
        UserCard userCard = userCardRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("등록된 카드가 없습니다."));
        userCardRepository.delete(userCard);
        log.info("카드 삭제 완료: userId={}", user.getId());
    }

    private HttpHeaders createAuthHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(paymentsConfig.getToss().getSecretKey(), "");
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return headers;
    }

    private boolean isCardDeletable(User user) {
        List<RentStatus> activeStatuses = List.of(RentStatus.CONFIRMED, RentStatus.ONGOING);
        return !rentRepository.existsByBorrowerAndStatusIn(user, activeStatuses);
    }
}

