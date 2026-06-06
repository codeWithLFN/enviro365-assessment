package com.enviro.assessment.junior.lufunonemudzivhadi.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "investment_products")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class InvestmentProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String productName;
    @Enumerated(EnumType.STRING)
    private ProductType productType;
    private BigDecimal balance;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "investor_id", nullable = false)
    private Investor investor;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "investmentProduct")
    private List<WithdrawalNotice> withdrawalNotices;

}
