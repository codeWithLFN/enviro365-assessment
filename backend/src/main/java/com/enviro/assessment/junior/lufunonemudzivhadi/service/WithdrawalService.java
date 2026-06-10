package com.enviro.assessment.junior.lufunonemudzivhadi.service;

import com.enviro.assessment.junior.lufunonemudzivhadi.dto.request.WithdrawalRequest;
import com.enviro.assessment.junior.lufunonemudzivhadi.dto.response.WithdrawalResponse;
import com.enviro.assessment.junior.lufunonemudzivhadi.model.InvestmentProduct;
import com.enviro.assessment.junior.lufunonemudzivhadi.model.ProductType;
import com.enviro.assessment.junior.lufunonemudzivhadi.model.Status;
import com.enviro.assessment.junior.lufunonemudzivhadi.model.WithdrawalNotice;
import com.enviro.assessment.junior.lufunonemudzivhadi.repository.InvestmentProductRepository;
import com.enviro.assessment.junior.lufunonemudzivhadi.repository.WithdrawalNoticeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;

@Service
@RequiredArgsConstructor
@Transactional
public class WithdrawalService {

    private final WithdrawalNoticeRepository withdrawalNoticeRepository;
    private final InvestmentProductRepository investmentProductRepository;

    public WithdrawalResponse processWithdrawal(WithdrawalRequest request){
        InvestmentProduct product = investmentProductRepository.findById(request.getProductId())
                .orElseThrow( () -> new IllegalArgumentException("Product not found"));

        // Calculate the 90% of the balance and check if the withdrawal amount is valid
        BigDecimal amount = request.getAmount();
        BigDecimal balance = product.getBalance();
        BigDecimal ninetyPercentOfBalance = balance.multiply(BigDecimal.valueOf(0.9));

        if (amount.compareTo(balance) > 0) {
            throw new IllegalArgumentException("Withdrawal amount cannot exceed available balance");
        }

        if (amount.compareTo(ninetyPercentOfBalance) > 0) {
            throw new IllegalArgumentException("Withdrawal amount cannot exceed 90% of available balance");
        }

        // check if the withdrawal is allowed for retirement products
        if ( product.getProductType() == ProductType.RETIREMENT) {

            // calculating the age of the investor
            int age = Period.between(product.getInvestor().getDateOfBirth(), LocalDate.now()).getYears();

            if ( age <= 65){
                throw new IllegalArgumentException("The investor must be 65 years or older to withdraw from retirement products");
            }
        }

        // update the balance of the product
        BigDecimal remainingBalance = balance.subtract(amount);
        product.setBalance(remainingBalance);

        // save the updated product
        WithdrawalNotice withdrawalNotice = WithdrawalNotice.builder()
                                                            .amount(request.getAmount())
                                                            .noticeDate(LocalDate.now())
                                                            .status(Status.PENDING)
                                                            .investmentProduct(product)
                                                            .build();
       investmentProductRepository.save(product);

       // save the withdrawal notice
        WithdrawalNotice savedWithdrawalNotice = withdrawalNoticeRepository.save(withdrawalNotice);
        return WithdrawalResponse.builder()
                .withdrawalId(savedWithdrawalNotice.getId())
                .productId(product.getId())
                .productName(product.getProductName())
                .amount(savedWithdrawalNotice.getAmount())
                .status(savedWithdrawalNotice.getStatus())
                .noticeDate(savedWithdrawalNotice.getNoticeDate())
                .remainingBalance(product.getBalance())
                .build();
    }
}
