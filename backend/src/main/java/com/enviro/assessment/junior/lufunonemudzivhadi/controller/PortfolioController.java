package com.enviro.assessment.junior.lufunonemudzivhadi.controller;

import com.enviro.assessment.junior.lufunonemudzivhadi.dto.response.PortfolioResponse;
import com.enviro.assessment.junior.lufunonemudzivhadi.service.PortfolioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/portfolio")
@RequiredArgsConstructor
public class PortfolioController {

    private final PortfolioService portfolioService;

    @GetMapping("/{investorId}")
    public ResponseEntity<PortfolioResponse> getPortfolioByInvestorId(@PathVariable Long investorId) {
        PortfolioResponse response = portfolioService.getPortfolioByInvestorId(investorId);
        return ResponseEntity.ok(response);
    }
}
