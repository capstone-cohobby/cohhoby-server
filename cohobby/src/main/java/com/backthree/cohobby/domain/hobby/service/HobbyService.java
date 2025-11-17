package com.backthree.cohobby.domain.hobby.service;

import com.backthree.cohobby.domain.hobby.dto.response.GetHobbyResponse;
import com.backthree.cohobby.domain.hobby.entity.Hobby;
import com.backthree.cohobby.domain.hobby.repository.HobbyRepository;
import com.backthree.cohobby.global.common.response.status.ErrorStatus;
import com.backthree.cohobby.global.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HobbyService {
    private final HobbyRepository hobbyRepository;

    public Hobby findHobbyById(Long hobbyId) {
        return hobbyRepository.findById(hobbyId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.HOBBY_NOT_FOUND));
    }

    public boolean existsById(Long hobbyId) {
        // Repository의 existsById를 그대로 호출하여 가장 효율적으로 확인합니다.
        return hobbyRepository.existsById(hobbyId);
    }

    public List<GetHobbyResponse> findHobbyReferenceById(Long hobbyId) {
        return hobbyRepository.findAll()
                .stream()
                .map(GetHobbyResponse::fromEntity)
                .toList();
    }

    public Hobby getHobbyOrThrow(String category, String hobbyName) {
        return hobbyRepository
                .findByCategoryAndName(category, hobbyName)
                .orElseThrow(() -> new CustomException(ErrorStatus.HOBBY_NOT_FOUND));
    }
}
