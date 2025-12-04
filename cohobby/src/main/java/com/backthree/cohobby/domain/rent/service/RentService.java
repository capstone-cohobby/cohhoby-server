package com.backthree.cohobby.domain.rent.service;

import com.backthree.cohobby.domain.rent.dto.request.UpdateDetailRequest;
import com.backthree.cohobby.domain.rent.dto.response.MyRentalHistoryResponse;
import com.backthree.cohobby.domain.rent.dto.response.RentDetailResponse;
import com.backthree.cohobby.domain.rent.dto.response.UpdateDetailResponse;
import com.backthree.cohobby.domain.rent.entity.Rent;
import com.backthree.cohobby.domain.rent.entity.RentStatus;
import com.backthree.cohobby.domain.rent.repository.RentRepository;
import com.backthree.cohobby.domain.user.entity.User;
import com.backthree.cohobby.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RentService {

    private final RentRepository rentRepository;
    private final UserRepository userRepository;

    /**
     * 대여 기간이 종료되었는지 확인하고, 종료되었다면 상태를 COMPLETED로 변경
     * @param rent 확인할 대여 정보
     */
    @Transactional
    private void checkAndUpdateRentStatusIfExpired(Rent rent) {
        if (rent == null || rent.getDuedate() == null) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        RentStatus currentStatus = rent.getStatus();

        // 대여 기간이 종료되었고, 상태가 ONGOING 또는 CONFIRMED인 경우 COMPLETED로 변경
        if (now.isAfter(rent.getDuedate()) && 
            (currentStatus == RentStatus.ONGOING || currentStatus == RentStatus.CONFIRMED)) {
            rent.updateStatus(RentStatus.COMPLETED);
            rentRepository.save(rent);
            log.info("대여 기간 종료로 상태 변경: rentId={}, status={} -> COMPLETED", 
                    rent.getId(), currentStatus);
        }
    }

    @Transactional
    public RentDetailResponse getDetail(Long roomId, Long userId) {
        Rent rent = rentRepository.findByChattingRoomId(roomId)
                .orElseThrow(() -> new IllegalArgumentException("대여 정보를 찾을 수 없습니다. roomId=" + roomId));
        
        // 권한 검증: owner 또는 borrower만 조회 가능
        if (!rent.getOwner().getId().equals(userId) && !rent.getBorrower().getId().equals(userId)) {
            throw new IllegalArgumentException("대여 정보를 조회할 권한이 없습니다.");
        }

        // 대여 기간 종료 확인 및 상태 업데이트
        checkAndUpdateRentStatusIfExpired(rent);

        return RentDetailResponse.fromEntity(rent);
    }

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
        boolean hasDailyPrice = request.getDailyPrice() != null;

        if (hasStart || hasDue) {
            if (!(hasStart && hasDue) || hasRule || hasDailyPrice) {
                throw new IllegalArgumentException("대여 시작/종료 날짜는 함께 제공되어야 하며, 규칙이나 일일 대여료와 함께 제공되면 안됩니다.");
            }
            LocalDateTime startAt = request.getStartAt().atStartOfDay();
            LocalDateTime duedate = request.getDuedate().atStartOfDay();
            rent.updateDates(startAt, duedate);
            // 날짜 변경 시 totalPrice 재계산
            rent.calculateAndUpdateTotalPrice();
        } else if (hasRule) {
            if (hasDailyPrice) {
                throw new IllegalArgumentException("규칙과 일일 대여료는 함께 업데이트할 수 없습니다.");
            }
            rent.updateRule(request.getRule());
        } else if (hasDailyPrice) {
            rent.updateDailyPrice(request.getDailyPrice());
            // 일일 대여료 변경 시 totalPrice 재계산
            rent.calculateAndUpdateTotalPrice();
        } else {
            throw new IllegalArgumentException("업데이트할 값이 없습니다. (startAt+duedate 한 쌍, rule, dailyPrice 중 하나 필요)");
        }

        rentRepository.save(rent);
        return UpdateDetailResponse.fromEntity(rent);
    }

    @Transactional
    public List<MyRentalHistoryResponse> getMyRentalHistory(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. userId=" + userId));
        
        List<Rent> rents = rentRepository.findByBorrowerOrderByCreatedAtDesc(user);
        
        // 대여 기간 종료 확인 및 상태 업데이트
        rents.forEach(this::checkAndUpdateRentStatusIfExpired);
        
        // Image 엔티티 로딩 (LAZY 로딩을 위해)
        rents.forEach(rent -> {
            if (rent.getPost() != null && rent.getPost().getImages() != null) {
                rent.getPost().getImages().size();
            }
        });
        
        return rents.stream()
                .map(MyRentalHistoryResponse::fromEntity)
                .collect(Collectors.toList());
    }
}
