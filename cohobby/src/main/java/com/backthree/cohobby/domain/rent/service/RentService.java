package com.backthree.cohobby.domain.rent.service;

import com.backthree.cohobby.domain.rent.dto.request.UpdateDetailRequest;
import com.backthree.cohobby.domain.rent.dto.response.UpdateDetailResponse;
import com.backthree.cohobby.domain.rent.entity.Rent;
import com.backthree.cohobby.domain.rent.repository.RentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RentService {

    private final RentRepository rentRepository;

    @Transactional
    public UpdateDetailResponse updateDetail(Long roomId, UpdateDetailRequest request, Long userId) {
        Rent rent = rentRepository.findByChattingRoomId(roomId)
                .orElseThrow(() -> new IllegalArgumentException("대여 정보를 찾을 수 없습니다. roomId=" + roomId));
        
        // 권한 검증: owner 또는 borrower만 수정 가능
        if (!rent.getOwner().getId().equals(userId) && !rent.getBorrower().getId().equals(userId)) {
            throw new IllegalArgumentException("대여 정보를 수정할 권한이 없습니다.");
        }

        boolean hasStart = request.getStartAt() != null;
        boolean hasDue = request.getDuedate() != null;
        boolean hasRule = request.getRule() != null && !request.getRule().isBlank();

        if (hasStart || hasDue) {
            if (!(hasStart && hasDue) || hasRule) {
                throw new IllegalArgumentException("대여 시작/종료 날짜는 함께 제공되어야 하며, 규칙과 함께 제공되면 안됩니다.");
            }
            LocalDateTime startAt = request.getStartAt().atStartOfDay();
            LocalDateTime duedate = request.getDuedate().atStartOfDay();
            rent.updateDates(startAt, duedate);
        } else if (hasRule) {
            rent.updateRule(request.getRule());
        } else {
            throw new IllegalArgumentException("업데이트할 값이 없습니다. (startAt+duedate 한 쌍 또는 rule 중 하나 필요)");
        }

        rentRepository.save(rent);
        return UpdateDetailResponse.fromEntity(rent);
    }
}
