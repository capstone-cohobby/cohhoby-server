package com.backthree.cohobby.domain.user.service;

import com.backthree.cohobby.domain.user.entity.User;
import com.backthree.cohobby.domain.user.repository.UserRepository;
import com.backthree.cohobby.global.common.response.status.ErrorStatus;
import com.backthree.cohobby.global.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    public User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
    }

    public boolean existsById(Long userId) {
        // DB에 가장 가벼운 쿼리를 보내 존재 여부만 확인합니다.
        return userRepository.existsById(userId);
    }


    //참조(프록시)만 가져오는 함수
    public User findUserReferenceById(Long userId) {
        return userRepository.getReferenceById(userId);
    }
}
