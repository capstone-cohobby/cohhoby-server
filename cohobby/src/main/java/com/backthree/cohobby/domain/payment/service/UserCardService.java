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
            String response = restTemplate.postForObject(
                    url,
                    new HttpEntity<>(body, headers),
                    String.class
            );

            // 응답 파싱
            JsonNode jsonNode = objectMapper.readTree(response);
            String billingKey = jsonNode.get("billingKey").asText();
            JsonNode cardNode = jsonNode.get("card");
            String cardNumber = cardNode.get("number").asText();
            // card.company 또는 card.issuerCode 사용 (토스페이먼츠 API 응답에 따라 다를 수 있음)
            String cardCompany = cardNode.has("company") 
                    ? cardNode.get("company").asText() 
                    : (cardNode.has("issuerCode") ? cardNode.get("issuerCode").asText() : "UNKNOWN");
            String cardType = cardNode.has("cardType") 
                    ? cardNode.get("cardType").asText() 
                    : (cardNode.has("type") ? cardNode.get("type").asText() : "CREDIT");

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

            log.info("카드 등록 완료: userId={}, billingKey={}, cardCompany={}", 
                    user.getId(), billingKey, cardCompany);
            return CardRegisterResponse.from(userCard);

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

