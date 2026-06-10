package com.enviro.assessment.junior.lufunonemudzivhadi.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WithdrawalRequest {
    @NotNull(message = "productId must be provided")
    private Long productId;
    @NotNull(message = "amount must be provided")
    @DecimalMin(value = "0.01", message = "amount must be a positive value")
    private BigDecimal amount;
}
