package com.backthree.cohobby.domain.payment.scheduler;

import com.backthree.cohobby.domain.payment.entity.Payment;
import com.backthree.cohobby.domain.payment.entity.PaymentStatus;
import com.backthree.cohobby.domain.payment.entity.PaymentType;
import com.backthree.cohobby.domain.payment.repository.PaymentRepository;
import com.backthree.cohobby.domain.payment.service.PaymentService;
import com.backthree.cohobby.domain.rent.entity.Rent;
import com.backthree.cohobby.domain.rent.entity.RentStatus;
import com.backthree.cohobby.domain.rent.repository.RentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class RentalFeeAutoPaymentScheduler {
    private final RentRepository rentRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentService paymentService;

    // 매일 자정에 실행 (대여 시작일이 오늘인 대여의 대여료 자동결제)
    @Scheduled(cron = "0 0 0 * * *") // 매일 00:00:00에 실행
    @Transactional
    public void processRentalFeeAutoPayments() {
        LocalDate today = LocalDate.now();
        log.info("대여료 자동결제 스케줄러 실행: date={}", today);

        // 대여 시작일이 오늘이고 상태가 CONFIRMED인 대여 조회
        List<Rent> rentsStartingToday = rentRepository.findByStartAtDateAndStatus(
                today,
                RentStatus.CONFIRMED
        );

        log.info("대여료 자동결제 대상 대여 수: {}", rentsStartingToday.size());

        for (Rent rent : rentsStartingToday) {
            try {
                // 이미 대여료 결제가 완료되었는지 확인
                boolean rentalFeeAlreadyPaid = paymentRepository.findByRent(rent).stream()
                        .anyMatch(payment -> payment.getPaymentType() == PaymentType.RENTAL_FEE
                                && payment.getStatus() == PaymentStatus.CAPTURED);

                if (rentalFeeAlreadyPaid) {
                    log.info("대여료가 이미 결제되었습니다. rentId={}", rent.getId());
                    continue;
                }

                // 대여료 자동결제 실행
                Payment payment = paymentService.processRentalFeeAutoPayment(rent);
                log.info("대여료 자동결제 성공: rentId={}, paymentId={}, amount={}",
                        rent.getId(), payment.getId(), rent.getTotalPrice());

            } catch (Exception e) {
                log.error("대여료 자동결제 실패: rentId={}, error={}", rent.getId(), e.getMessage(), e);
                // 실패해도 다음 대여 처리 계속 진행
            }
        }

        log.info("대여료 자동결제 스케줄러 완료: 처리된 대여 수={}", rentsStartingToday.size());
    }
}

