package com.enviro.assessment.junior.lufunonemudzivhadi.dto.response;

import com.enviro.assessment.junior.lufunonemudzivhadi.model.Status;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WithdrawalResponse {
    private Long withdrawalId;
    private Long productId;
    private String productName;
    private BigDecimal amount;
    private Status status;
    private LocalDate noticeDate;
    private BigDecimal remainingBalance;
}
