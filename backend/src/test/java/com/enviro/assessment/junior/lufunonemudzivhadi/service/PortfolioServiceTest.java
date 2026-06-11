package com.enviro.assessment.junior.lufunonemudzivhadi.service;

import com.enviro.assessment.junior.lufunonemudzivhadi.dto.response.PortfolioResponse;
import com.enviro.assessment.junior.lufunonemudzivhadi.model.InvestmentProduct;
import com.enviro.assessment.junior.lufunonemudzivhadi.model.Investor;
import com.enviro.assessment.junior.lufunonemudzivhadi.model.ProductType;
import com.enviro.assessment.junior.lufunonemudzivhadi.repository.InvestorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PortfolioServiceTest {

    @Mock
    private InvestorRepository investorRepository;

    @InjectMocks
    private PortfolioService portfolioService;

    private Investor investor;

    @BeforeEach
    void setUp() {
        InvestmentProduct product = new InvestmentProduct();
        product.setId(1L);
        product.setProductName("Retirement Annuity Fund");
        product.setProductType(ProductType.RETIREMENT);
        product.setBalance(BigDecimal.valueOf(50000));

        investor = new Investor();
        investor.setId(1L);
        investor.setFirstname("Lufuno");
        investor.setLastname("Nemudzivhadi");
        investor.setEmail("lufuno@example.com");
        investor.setDateOfBirth(LocalDate.of(1990, 5, 10));
        investor.setInvestmentProducts(List.of(product));
    }

    @Test
    @DisplayName("Should return portfolio response for valid investor ID")
    void shouldReturnPortfolio_whenInvestorExists() {
        when(investorRepository.findById(1L)).thenReturn(Optional.of(investor));

        PortfolioResponse response = portfolioService.getPortfolioByInvestorId(1L);

        assertNotNull(response);
        assertEquals(1L, response.getInvestorId());
        assertEquals("Lufuno", response.getFirstName());
        assertEquals("Nemudzivhadi", response.getLastName());
        assertEquals("lufuno@example.com", response.getEmail());
        assertEquals(1, response.getInvestmentProducts().size());

        verify(investorRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when investor does not exist")
    void shouldThrowException_whenInvestorNotFound() {
        when(investorRepository.findById(99L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> portfolioService.getPortfolioByInvestorId(99L)
        );

        assertTrue(exception.getMessage().toLowerCase().contains("investor"));
    }

    @Test
    @DisplayName("Should return correct number of investment products")
    void shouldReturnCorrectProducts_inPortfolioResponse() {
        when(investorRepository.findById(1L)).thenReturn(Optional.of(investor));

        PortfolioResponse response = portfolioService.getPortfolioByInvestorId(1L);

        assertFalse(response.getInvestmentProducts().isEmpty());
        assertEquals("Retirement Annuity Fund", response.getInvestmentProducts().get(0).getProductName());
    }
}
