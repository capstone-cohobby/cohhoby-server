package com.backthree.cohobby.domain.hobby.service;

import com.backthree.cohobby.domain.hobby.dto.response.HobbyStatsResponse;
import com.backthree.cohobby.domain.hobby.entity.Hobby;
import com.backthree.cohobby.domain.hobby.repository.HobbyRepository;
import com.backthree.cohobby.domain.rent.entity.Rent;
import com.backthree.cohobby.domain.rent.entity.RentStatus;
import com.backthree.cohobby.domain.rent.repository.RentRepository;
import com.backthree.cohobby.domain.user.entity.User;
import com.backthree.cohobby.domain.user.repository.UserRepository;
import com.backthree.cohobby.global.common.response.status.ErrorStatus;
import com.backthree.cohobby.global.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HobbyService {
    private final HobbyRepository hobbyRepository;
    private final RentRepository rentRepository;
    private final UserRepository userRepository;

    public Hobby findHobbyById(Long hobbyId) {
        return hobbyRepository.findById(hobbyId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.HOBBY_NOT_FOUND));
    }

    public boolean existsById(Long hobbyId) {
        // Repository의 existsById를 그대로 호출하여 가장 효율적으로 확인합니다.
        return hobbyRepository.existsById(hobbyId);
    }

    @Transactional(readOnly = true)
    public HobbyStatsResponse getMyHobbyStats(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        // 총 경험치: user의 score
        Integer totalExperience = user.getScore();

        // 대여한 물품 개수: userId가 borrower인 rent 중 status가 COMPLETED인 개수
        List<Rent> completedRents = rentRepository.findByBorrowerAndStatus(user, RentStatus.COMPLETED);
        Integer totalRentedItems = completedRents.size();

        // 기여한 취미: rent에서 post를 보고, 해당 hobby를 집합으로 했을 때 개수 (COMPLETED인 것만)
        // LAZY 로딩을 위해 post와 hobby를 미리 로드
        Set<Long> contributedHobbyIds = completedRents.stream()
                .map(rent -> {
                    // Post와 Hobby 엔티티 로딩 (LAZY 로딩을 위해)
                    rent.getPost().getHobby().getId();
                    return rent.getPost().getHobby().getId();
                })
                .collect(Collectors.toSet());
        Integer contributedHobbiesCount = contributedHobbyIds.size();

        // 모든 취미 조회
        List<Hobby> allHobbies = hobbyRepository.findAll();

        // 각 취미별 정보 생성 (모든 취미 표시)
        List<HobbyStatsResponse.HobbyInfo> hobbyInfos = allHobbies.stream()
                .map(hobby -> {
                    // 취미의 score (null이면 0으로 처리)
                    Integer hobbyScore = hobby.getScore() != null ? hobby.getScore() : 0;
                    
                    // 진척도 계산 (score / 1000000 * 100)
                    double progress = (hobbyScore.doubleValue() / 1000000.0) * 100.0;
                    if (progress > 100.0) progress = 100.0;
                    
                    // 내가 대여 완료한 취미인지 확인
                    boolean contributed = contributedHobbyIds.contains(hobby.getId());
                    
                    // Category 엔티티 로딩 (LAZY 로딩을 위해)
                    String categoryName = null;
                    if (hobby.getCategory() != null) {
                        categoryName = hobby.getCategory().getName();
                    }
                    
                    return HobbyStatsResponse.HobbyInfo.builder()
                            .hobbyId(hobby.getId())
                            .name(hobby.getName())
                            .categoryName(categoryName)
                            .score(hobbyScore)
                            .progress(progress)
                            .contributed(contributed)
                            .build();
                })
                .collect(Collectors.toList());

        return HobbyStatsResponse.builder()
                .totalExperience(totalExperience)
                .totalRentedItems(totalRentedItems)
                .contributedHobbiesCount(contributedHobbiesCount)
                .hobbies(hobbyInfos)
                .build();
    }
}
