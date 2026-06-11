package com.enviro.assessment.junior.lufunonemudzivhadi.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import com.enviro.assessment.junior.lufunonemudzivhadi.dto.request.WithdrawalRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.enviro.assessment.junior.lufunonemudzivhadi.model.InvestmentProduct;
import com.enviro.assessment.junior.lufunonemudzivhadi.model.Investor;
import com.enviro.assessment.junior.lufunonemudzivhadi.model.ProductType;
import com.enviro.assessment.junior.lufunonemudzivhadi.repository.InvestmentProductRepository;
import com.enviro.assessment.junior.lufunonemudzivhadi.repository.WithdrawalNoticeRepository;

@ExtendWith(MockitoExtension.class)
public class WithdrawalServiceTest {

    @Mock
    private InvestmentProductRepository productRepository;

    @Mock
    private WithdrawalNoticeRepository withdrawalNoticeRepository;

    @InjectMocks
    private WithdrawalService withdrawalService;

    private Investor youngInvestor;
    private Investor retiredInvestor;
    private InvestmentProduct retirementProduct;
    private InvestmentProduct savingsProduct;

    @BeforeEach
    void setUp() {
        youngInvestor = new Investor();
        youngInvestor.setId(1L);
        youngInvestor.setFirstname("Lufuno");
        youngInvestor.setLastname("Nemudzivhadi");
        youngInvestor.setDateOfBirth(LocalDate.of(1990, 5, 10));

        retiredInvestor = new Investor();
        retiredInvestor.setId(2L);
        retiredInvestor.setFirstname("Sarah");
        retiredInvestor.setLastname("Nkosi");
        retiredInvestor.setDateOfBirth(LocalDate.of(1955, 3, 22));

        retirementProduct = new InvestmentProduct();
        retirementProduct.setId(1L);
        retirementProduct.setProductName("Retirement Annuity Fund");
        retirementProduct.setProductType(ProductType.RETIREMENT);
        retirementProduct.setBalance(BigDecimal.valueOf(50000));
        retirementProduct.setInvestor(youngInvestor);

        savingsProduct = new InvestmentProduct();
        savingsProduct.setId(2L);
        savingsProduct.setProductName("Tax Free Savings Account");
        savingsProduct.setProductType(ProductType.SAVINGS);
        savingsProduct.setBalance(BigDecimal.valueOf(14000));
        savingsProduct.setInvestor(youngInvestor);
    }

     @Test
    @DisplayName("Should throw exception when investor under 65 withdraws from retirement product")
    void shouldThrowException_whenInvestorUnder65WithdrawsFromRetirementProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(retirementProduct));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> withdrawalService.processWithdrawal(
                        WithdrawalRequest.builder()
                                .productId(1L)
                                .amount(BigDecimal.valueOf(1000))
                                .build()
                )
        );

        assertTrue(exception.getMessage().toLowerCase().contains("65"));
    }

    @Test
    @DisplayName("Should throw exception when withdrawal amount exceeds balance")
    void shouldThrowException_whenWithdrawalAmountExceedsBalance() {
        savingsProduct.setInvestor(youngInvestor);
        when(productRepository.findById(2L)).thenReturn(Optional.of(savingsProduct));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> withdrawalService.processWithdrawal(
                        WithdrawalRequest.builder()
                                .productId(1L)
                                .amount(BigDecimal.valueOf(15000))
                                .build()
                )
        );

        assertNotNull(exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when withdrawal exceeds 90% of balance")
    void shouldThrowException_whenWithdrawalExceeds90PercentOfBalance() {
        savingsProduct.setInvestor(youngInvestor);
        when(productRepository.findById(2L)).thenReturn(Optional.of(savingsProduct));

        // 90% of 14000 = 12600, so 13000 should fail
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> withdrawalService.processWithdrawal(
                        WithdrawalRequest.builder()
                                .productId(1L)
                                .amount(BigDecimal.valueOf(13000))
                                .build()
                )
        );

        assertNotNull(exception.getMessage());
    }

    @Test
    @DisplayName("Should allow retirement withdrawal when investor is 65 or older")
    void shouldAllowWithdrawal_whenInvestorIs65OrOlder() {
        retirementProduct.setInvestor(retiredInvestor);
        when(productRepository.findById(1L)).thenReturn(Optional.of(retirementProduct));
        when(withdrawalNoticeRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        assertDoesNotThrow(() ->withdrawalService.processWithdrawal(
                WithdrawalRequest.builder()
                        .productId(1L)
                        .amount(BigDecimal.valueOf(1000))
                        .build()
        ));

        verify(withdrawalNoticeRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Should throw exception when product is not found")
    void shouldThrowException_whenProductNotFound() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(
                IllegalArgumentException.class,
                () -> withdrawalService.processWithdrawal(
                        WithdrawalRequest.builder()
                                .productId(1L)
                                .amount(BigDecimal.valueOf(1000))
                                .build()
                )
        );
    }

    @Test
    @DisplayName("Should allow valid savings withdrawal within limits")
    void shouldProcessWithdrawal_whenAmountIsValid() {
        savingsProduct.setInvestor(youngInvestor);
        when(productRepository.findById(2L)).thenReturn(Optional.of(savingsProduct));
        when(withdrawalNoticeRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        assertDoesNotThrow(() -> withdrawalService.processWithdrawal(
                WithdrawalRequest.builder()
                        .productId(1L)
                        .amount(BigDecimal.valueOf(5000))
                        .build()
        ));

        verify(withdrawalNoticeRepository, times(1)).save(any());
    }
    
}
