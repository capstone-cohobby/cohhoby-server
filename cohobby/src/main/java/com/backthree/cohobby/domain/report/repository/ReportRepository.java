package com.backthree.cohobby.domain.report.repository;

import com.backthree.cohobby.domain.report.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {
}