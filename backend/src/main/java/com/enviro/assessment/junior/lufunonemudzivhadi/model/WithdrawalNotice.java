package com.enviro.assessment.junior.lufunonemudzivhadi.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "withdrawal_notices")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class WithdrawalNotice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private BigDecimal amount;
    private LocalDate noticeDate;
    @Enumerated(EnumType.STRING)
    private Status status;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "investment_product_id", nullable = false)
    private InvestmentProduct investmentProduct;
}
