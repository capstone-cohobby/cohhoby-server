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
public class DepositAutoPaymentScheduler {
    private final RentRepository rentRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentService paymentService;

    // 매일 자정에 실행 (대여 종료일이 오늘인 대여의 보증금 자동결제)
    @Scheduled(cron = "0 0 0 * * *") // 매일 00:00:00에 실행
    @Transactional
    public void processDepositAutoPayments() {
        LocalDate today = LocalDate.now();
        log.info("보증금 자동결제 스케줄러 실행: date={}", today);

        // 대여 종료일이 오늘이고 상태가 CONFIRMED 또는 ONGOING인 대여 조회
        List<Rent> rentsDueToday = rentRepository.findByDuedateDateAndStatus(
                today, 
                RentStatus.ONGOING
        );

        // CONFIRMED 상태도 추가로 조회
        List<Rent> confirmedRents = rentRepository.findByDuedateDateAndStatus(
                today,
                RentStatus.CONFIRMED
        );
        rentsDueToday.addAll(confirmedRents);

        log.info("보증금 자동결제 대상 대여 수: {}", rentsDueToday.size());

        for (Rent rent : rentsDueToday) {
            try {
                // 이미 보증금 결제가 완료되었는지 확인
                boolean depositAlreadyPaid = paymentRepository.findByRent(rent).stream()
                        .anyMatch(payment -> payment.getPaymentType() == PaymentType.DEPOSIT
                                && payment.getStatus() == PaymentStatus.CAPTURED);

                if (depositAlreadyPaid) {
                    log.info("보증금이 이미 결제되었습니다. rentId={}", rent.getId());
                    continue;
                }

                // 보증금 자동결제 실행
                Payment payment = paymentService.processDepositAutoPayment(rent);
                log.info("보증금 자동결제 성공: rentId={}, paymentId={}, amount={}",
                        rent.getId(), payment.getId(), rent.getDeposit());

            } catch (Exception e) {
                log.error("보증금 자동결제 실패: rentId={}, error={}", rent.getId(), e.getMessage(), e);
                // 실패해도 다음 대여 처리 계속 진행
            }
        }

        log.info("보증금 자동결제 스케줄러 완료: 처리된 대여 수={}", rentsDueToday.size());
    }
}

