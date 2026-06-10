package com.enviro.assessment.junior.lufunonemudzivhadi.controller;

import com.enviro.assessment.junior.lufunonemudzivhadi.dto.response.PortfolioResponse;
import com.enviro.assessment.junior.lufunonemudzivhadi.service.PortfolioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/portfolio")
@RequiredArgsConstructor
public class PortfolioController {

    private final PortfolioService portfolioService;

    @GetMapping("/{investorId}")
    public ResponseEntity<PortfolioResponse> getPortfolioByInvestorId(@PathVariable Long investorId) {
        // retrieve the portfolio for the given investorId
        PortfolioResponse response = portfolioService.getPortfolioByInvestorId(investorId);
        // Logging the response for debugging purposes
        log.info("Portfolio retrieved for investorId={}", investorId);
        return ResponseEntity.ok(response);
    }
}
