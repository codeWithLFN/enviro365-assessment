package com.enviro.assessment.junior.lufunonemudzivhadi.repository;

import com.enviro.assessment.junior.lufunonemudzivhadi.model.InvestmentProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvestmentProductRepository extends JpaRepository<InvestmentProduct, Long> {
}
