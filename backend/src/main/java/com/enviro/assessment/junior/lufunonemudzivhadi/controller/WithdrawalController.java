package com.enviro.assessment.junior.lufunonemudzivhadi.controller;

import com.enviro.assessment.junior.lufunonemudzivhadi.dto.request.WithdrawalRequest;
import com.enviro.assessment.junior.lufunonemudzivhadi.dto.response.WithdrawalResponse;
import com.enviro.assessment.junior.lufunonemudzivhadi.service.WithdrawalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/v1/portfolio")
@RequiredArgsConstructor
public class WithdrawalController {
    private final WithdrawalService withdrawalService;

    @PostMapping("/withdrawals")
    public ResponseEntity<WithdrawalResponse> processWithdrawal(@Valid @RequestBody WithdrawalRequest request) {

        // Logging the incoming withdrawal request
        log.info("Processing withdrawal request for productId={}, amount={}",
                request.getProductId(), request.getAmount());

        WithdrawalResponse response = withdrawalService.processWithdrawal(request);

        // Logging the withdrawal response
        log.info("Withdrawal processed successfully. withdrawalId={}", response.getWithdrawalId());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
