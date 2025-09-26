package com.backthree.cohobby.domain.inquiry.repository;

import com.backthree.cohobby.domain.inquiry.entity.Inquiry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {
}