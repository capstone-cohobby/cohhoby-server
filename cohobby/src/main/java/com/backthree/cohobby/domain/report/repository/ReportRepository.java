package com.backthree.cohobby.domain.report.repository;

import com.backthree.cohobby.domain.report.entity.Report;
import com.backthree.cohobby.domain.report.entity.ReportStatus;
import com.backthree.cohobby.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findByUser(User user);
    List<Report> findByStatus(ReportStatus status);
}