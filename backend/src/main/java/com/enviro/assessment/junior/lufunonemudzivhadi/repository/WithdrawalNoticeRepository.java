package com.enviro.assessment.junior.lufunonemudzivhadi.repository;

import com.enviro.assessment.junior.lufunonemudzivhadi.model.WithdrawalNotice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WithdrawalNoticeRepository extends JpaRepository<WithdrawalNotice, Long> {
}
