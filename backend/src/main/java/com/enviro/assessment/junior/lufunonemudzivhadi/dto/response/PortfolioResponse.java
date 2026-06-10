package com.enviro.assessment.junior.lufunonemudzivhadi.dto.response;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PortfolioResponse {
    private Long investorId;
    private String firstName;
    private String lastName;
    private String email;
    private LocalDate dateOfBirth;
    private List<InvestmentProductResponse> investmentProducts;

}
