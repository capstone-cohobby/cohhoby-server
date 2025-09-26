package com.backthree.cohobby.domain.rent.alert.repository;

import com.backthree.cohobby.domain.rent.alert.entity.RentAlert;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RentAlertRepository extends JpaRepository<RentAlert, Long> {
}