package com.enviro.assessment.junior.lufunonemudzivhadi.controller;

import com.enviro.assessment.junior.lufunonemudzivhadi.service.StatementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/statements")
@RequiredArgsConstructor
public class StatementController {

    private final StatementService statementService;

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportStatementsToCsv(
            @RequestParam(required = false) Long investorId,
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate
    ) {
        byte[] csvData = statementService.exportStatementsToCsv(
                investorId, productId, status, fromDate, toDate
        );

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=statements.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csvData);
    }
}
