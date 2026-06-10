package com.enviro.assessment.junior.lufunonemudzivhadi.dto.response;

import com.enviro.assessment.junior.lufunonemudzivhadi.model.ProductType;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvestmentProductResponse {
    private Long productId;
    private String productName;
    private ProductType productType;
    private BigDecimal balance;
}
