package com.dynamiconlineshopping.backend.service.loyalty;

import com.dynamiconlineshopping.backend.dto.loyalty.BonusBalanceDto;
import com.dynamiconlineshopping.backend.dto.loyalty.BonusCalculationResult;
import com.dynamiconlineshopping.backend.dto.loyalty.LoyaltyRuleDto;
import com.dynamiconlineshopping.backend.dto.loyalty.LoyaltyStatsDto;
import com.dynamiconlineshopping.backend.dto.loyalty.UpdateLoyaltyRuleRequest;
import com.dynamiconlineshopping.backend.entity.loyalty.BonusTransaction;
import com.dynamiconlineshopping.backend.entity.loyalty.LoyaltyAccount;
import com.dynamiconlineshopping.backend.entity.loyalty.LoyaltyRule;
import com.dynamiconlineshopping.backend.entity.order.Order;
import com.dynamiconlineshopping.backend.entity.user.User;
import com.dynamiconlineshopping.backend.enums.loyalty.BonusTransactionType;
import com.dynamiconlineshopping.backend.repository.loyalty.BonusTransactionRepository;
import com.dynamiconlineshopping.backend.repository.loyalty.LoyaltyAccountRepository;
import com.dynamiconlineshopping.backend.repository.loyalty.LoyaltyRuleRepository;
import com.dynamiconlineshopping.backend.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoyaltyServiceImplTest {

    @Mock
    private LoyaltyAccountRepository loyaltyAccountRepository;

    @Mock
    private LoyaltyRuleRepository loyaltyRuleRepository;

    @Mock
    private BonusTransactionRepository bonusTransactionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private LoyaltyServiceImpl loyaltyService;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "client@test.com",
                        null,
                        List.of()
                )
        );
    }

    @Test
    @DisplayName("Should return current user bonus balance")
    void shouldReturnCurrentUserBonusBalance() {
        User user = User.builder()
                .id(1L)
                .email("client@test.com")
                .build();
        LoyaltyAccount account = LoyaltyAccount.builder()
                .id(1L)
                .user(user)
                .bonusBalance(500)
                .isActive(true)
                .build();
        when(userRepository.findByEmail("client@test.com")).thenReturn(Optional.of(user));
        when(loyaltyAccountRepository.findByUser(user)).thenReturn(Optional.of(account));

        BonusBalanceDto result = loyaltyService.getCurrentUserBalance();

        assertThat(result).isNotNull();
        assertThat(result.getBalance()).isEqualTo(500);
        assertThat(result.getActive()).isTrue();
    }

    @Test
    @DisplayName("Should calculate bonus write off successfully")
    void shouldCalculateBonusWriteOffSuccessfully() {
        User user = User.builder()
                .id(1L)
                .email("client@test.com")
                .build();
        LoyaltyRule rule = LoyaltyRule.builder()
                .id(1L)
                .maxWriteOffPercent(30)
                .accrualPercent(5)
                .minOrderAmountForAccrual(1.0)
                .isActive(true)
                .build();
        LoyaltyAccount account = LoyaltyAccount.builder()
                .id(1L)
                .user(user)
                .bonusBalance(1000)
                .isActive(true)
                .build();

        when(loyaltyRuleRepository.findFirstByIsActiveTrue()).thenReturn(Optional.of(rule));
        when(loyaltyAccountRepository.findByUser(user)).thenReturn(Optional.of(account));
        BonusCalculationResult result = loyaltyService.calculateBonusWriteOff(
                        user,
                        2000.0,
                        300);

        assertThat(result).isNotNull();
        assertThat(result.getBonusesToWriteOff()).isEqualTo(300);
        assertThat(result.getDiscountAmount()).isEqualTo(300.0);
        assertThat(result.getFinalAmount()).isEqualTo(1700.0);
    }

    @Test
    @DisplayName("Should throw exception when bonus balance is insufficient")
    void shouldThrowExceptionWhenBonusBalanceInsufficient() {
        User user = User.builder()
                .id(1L)
                .email("client@test.com")
                .build();
        LoyaltyRule rule = LoyaltyRule.builder()
                .id(1L)
                .maxWriteOffPercent(50)
                .isActive(true)
                .build();
        LoyaltyAccount account = LoyaltyAccount.builder()
                .id(1L)
                .user(user)
                .bonusBalance(100)
                .isActive(true)
                .build();

        when(loyaltyRuleRepository.findFirstByIsActiveTrue()).thenReturn(Optional.of(rule));
        when(loyaltyAccountRepository.findByUser(user)).thenReturn(Optional.of(account));
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                        () -> loyaltyService.calculateBonusWriteOff(user, 1000.0, 500));
        assertThat(exception.getMessage()).isEqualTo("Not enough bonus balance");
    }

    @Test
    @DisplayName("Should write off bonuses successfully")
    void shouldWriteOffBonusesSuccessfully() {
        User user = User.builder()
                .id(1L)
                .email("client@test.com")
                .build();
        Order order = Order.builder()
                .id(10L)
                .build();
        LoyaltyAccount account = LoyaltyAccount.builder()
                .id(1L)
                .user(user)
                .bonusBalance(1000)
                .isActive(true)
                .build();

        when(loyaltyAccountRepository.findByUser(user)).thenReturn(Optional.of(account));
        loyaltyService.writeOffBonuses(
                user,
                order,
                200);

        assertThat(account.getBonusBalance()).isEqualTo(800);
        verify(loyaltyAccountRepository).save(account);
        verify(bonusTransactionRepository).save(any(BonusTransaction.class));
        ArgumentCaptor<BonusTransaction> captor = ArgumentCaptor.forClass(BonusTransaction.class);
        verify(bonusTransactionRepository).save(captor.capture());
        BonusTransaction transaction = captor.getValue();
        assertThat(transaction.getType()).isEqualTo(BonusTransactionType.WRITE_OFF);
        assertThat(transaction.getAmount()).isEqualTo(200);
    }

    @Test
    @DisplayName("Should refund bonuses successfully")
    void shouldRefundBonusesSuccessfully() {
        User user = User.builder()
                .id(1L)
                .email("client@test.com")
                .build();
        Order order = Order.builder()
                .id(15L)
                .build();
        LoyaltyAccount account = LoyaltyAccount.builder()
                .id(1L)
                .user(user)
                .bonusBalance(300)
                .isActive(true)
                .build();
        when(bonusTransactionRepository.findByOrderAndType(order, BonusTransactionType.REFUND)
        ).thenReturn(Optional.empty());
        when(loyaltyAccountRepository.findByUser(user)).thenReturn(Optional.of(account));

        loyaltyService.refundBonuses(user, order, 100);

        assertThat(account.getBonusBalance()).isEqualTo(400);
        verify(loyaltyAccountRepository).save(account);
        verify(bonusTransactionRepository).save(any(BonusTransaction.class));
    }

    @Test
    @DisplayName("Should accrue bonuses successfully")
    void shouldAccrueBonusesSuccessfully() {
        User user = User.builder()
                .id(1L)
                .email("client@test.com")
                .build();
        Order order = Order.builder()
                .id(20L)
                .totalAmount(2000.0)
                .accruedBonusAmount(0)
                .build();
        LoyaltyRule rule = LoyaltyRule.builder()
                .id(1L)
                .accrualPercent(10)
                .minOrderAmountForAccrual(100.0)
                .isActive(true)
                .build();
        LoyaltyAccount account = LoyaltyAccount.builder()
                .id(1L)
                .user(user)
                .bonusBalance(500)
                .isActive(true)
                .build();

        when(bonusTransactionRepository.findByOrderAndType(order, BonusTransactionType.ACCRUAL)
        ).thenReturn(Optional.empty());
        when(loyaltyRuleRepository.findFirstByIsActiveTrue()).thenReturn(Optional.of(rule));
        when(loyaltyAccountRepository.findByUser(user)).thenReturn(Optional.of(account));

        loyaltyService.accrueBonuses(user, order);

        assertThat(account.getBonusBalance()).isEqualTo(700);
        assertThat(order.getAccruedBonusAmount()).isEqualTo(200);
        verify(loyaltyAccountRepository).save(account);
        verify(bonusTransactionRepository).save(any(BonusTransaction.class));
    }

    @Test
    @DisplayName("Should update loyalty rule successfully")
    void shouldUpdateLoyaltyRuleSuccessfully() {
        LoyaltyRule rule = LoyaltyRule.builder()
                .id(1L)
                .accrualPercent(5)
                .maxWriteOffPercent(30)
                .minOrderAmountForAccrual(1.0)
                .isActive(true)
                .build();
        UpdateLoyaltyRuleRequest request = UpdateLoyaltyRuleRequest.builder()
                        .accrualPercent(10)
                        .maxWriteOffPercent(50)
                        .minOrderAmountForAccrual(100.0)
                        .active(true)
                        .build();

        when(loyaltyRuleRepository.findFirstByIsActiveTrue()).thenReturn(Optional.of(rule));
        when(loyaltyRuleRepository.save(any(LoyaltyRule.class))).thenAnswer(invocation -> invocation.getArgument(0));

        LoyaltyRuleDto result = loyaltyService.updateRule(request);

        assertThat(result).isNotNull();
        assertThat(result.getAccrualPercent()).isEqualTo(10);
        assertThat(result.getMaxWriteOffPercent()).isEqualTo(50);
        assertThat(result.getMinOrderAmountForAccrual()).isEqualTo(100.0);
    }

    @Test
    @DisplayName("Should return loyalty statistics")
    void shouldReturnLoyaltyStatistics() {
        Order order = Order.builder()
                .id(1L)
                .build();
        BonusTransaction accrual = BonusTransaction.builder()
                        .type(BonusTransactionType.ACCRUAL)
                        .amount(300)
                        .order(order)
                        .build();
        BonusTransaction writeOff = BonusTransaction.builder()
                        .type(BonusTransactionType.WRITE_OFF)
                        .amount(100)
                        .order(order)
                        .build();
        BonusTransaction refund = BonusTransaction.builder()
                        .type(BonusTransactionType.REFUND)
                        .amount(50)
                        .order(order)
                        .build();

        when(bonusTransactionRepository.findAll()).thenReturn(List.of(accrual, writeOff, refund));
        when(loyaltyAccountRepository.count()).thenReturn(10L);

        LoyaltyStatsDto result = loyaltyService.getStats();

        assertThat(result).isNotNull();
        assertThat(result.getTotalClientsInLoyaltyProgram()).isEqualTo(10L);
        assertThat(result.getTotalBonusesAccrued()).isEqualTo(300L);
        assertThat(result.getTotalBonusesWrittenOff()).isEqualTo(100L);
        assertThat(result.getTotalBonusesRefunded()).isEqualTo(50L);
        assertThat(result.getTotalOrdersWithBonuses()).isEqualTo(1L);
    }
}
