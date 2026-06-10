package com.enviro.assessment.junior.lufunonemudzivhadi.service;

import com.enviro.assessment.junior.lufunonemudzivhadi.dto.response.InvestmentProductResponse;
import com.enviro.assessment.junior.lufunonemudzivhadi.dto.response.PortfolioResponse;
import com.enviro.assessment.junior.lufunonemudzivhadi.model.Investor;
import com.enviro.assessment.junior.lufunonemudzivhadi.repository.InvestorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class PortfolioService {
    private final InvestorRepository investorRepository;

    public PortfolioResponse getPortfolioByInvestorId(Long investorId){

        // find the investor by id
        Investor investor = investorRepository.findById(investorId).
                orElseThrow(() -> new IllegalArgumentException("Investor not found"));

        // map the investor's investment products to the response
        List<InvestmentProductResponse> investmentProducts = investor.getInvestmentProducts()
                .stream()
                .map(product -> InvestmentProductResponse.builder()
                        .productId(product.getId())
                        .productName(product.getProductName())
                        .productType(product.getProductType())
                        .balance(product.getBalance())
                        .build())
                .toList();

        // build and return the portfolio response
        return PortfolioResponse.builder()
                .investorId(investor.getId())
                .firstName(investor.getFirstname())
                .lastName(investor.getLastname())
                .email(investor.getEmail())
                .dateOfBirth(investor.getDateOfBirth())
                .investmentProducts(investmentProducts)
                .build();
    }
}
