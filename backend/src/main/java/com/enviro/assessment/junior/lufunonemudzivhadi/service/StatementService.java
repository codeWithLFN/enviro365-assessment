package com.enviro.assessment.junior.lufunonemudzivhadi.service;

import com.enviro.assessment.junior.lufunonemudzivhadi.model.WithdrawalNotice;
import com.enviro.assessment.junior.lufunonemudzivhadi.repository.WithdrawalNoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatementService {
    private final WithdrawalNoticeRepository withdrawalNoticeRepository;

    public byte[] exportStatementsToCsv(Long investorId, Long productId, String status, String fromDate, String toDate) {

        // Retrieve all withdrawal notices from the database
        List<WithdrawalNotice> notices = withdrawalNoticeRepository.findAll();

        // Create the CSV header
        StringBuilder csv = new StringBuilder();
        csv.append("withdrawalId,investorId,investorName,productId,productName,amount,status\n");

        // Filter withdrawal notices based on the provided criteria
        for (WithdrawalNotice notice : notices) {
            if (investorId != null && !notice.getInvestmentProduct().getInvestor().getId().equals(investorId)) {
                continue;
            }

            if (productId != null && !notice.getInvestmentProduct().getId().equals(productId)) {
                continue;
            }

            if (status != null && !notice.getStatus().name().equalsIgnoreCase(status)) {
                continue;
            }

            // Add the withdrawal notice to the CSV
            csv.append(notice.getId()).append(",")
                    .append(notice.getInvestmentProduct().getInvestor().getId()).append(",")
                    .append(escapeCsv(notice.getInvestmentProduct().getInvestor().getFirstname() + " "
                            + notice.getInvestmentProduct().getInvestor().getLastname())).append(",")
                    .append(notice.getInvestmentProduct().getId()).append(",")
                    .append(escapeCsv(notice.getInvestmentProduct().getProductName())).append(",")
                    .append(notice.getAmount()).append(",")
                    .append(notice.getStatus()).append("\n");
        }

        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }

    // Method to escape CSV values
    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
