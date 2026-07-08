package com.dynamiconlineshopping.backend.service.loyalty;

import com.dynamiconlineshopping.backend.dto.loyalty.BonusBalanceDto;
import com.dynamiconlineshopping.backend.dto.loyalty.BonusCalculationResult;
import com.dynamiconlineshopping.backend.dto.loyalty.BonusTransactionDto;
import com.dynamiconlineshopping.backend.dto.loyalty.LoyaltyRuleDto;
import com.dynamiconlineshopping.backend.dto.loyalty.LoyaltyStatsDto;
import com.dynamiconlineshopping.backend.dto.loyalty.UpdateLoyaltyRuleRequest;
import com.dynamiconlineshopping.backend.entity.order.Order;
import com.dynamiconlineshopping.backend.entity.user.User;
import com.dynamiconlineshopping.backend.entity.loyalty.BonusTransaction;
import com.dynamiconlineshopping.backend.entity.loyalty.LoyaltyAccount;
import com.dynamiconlineshopping.backend.entity.loyalty.LoyaltyRule;
import com.dynamiconlineshopping.backend.enums.loyalty.BonusTransactionType;
import com.dynamiconlineshopping.backend.exception.ResourceNotFoundException;
import com.dynamiconlineshopping.backend.repository.user.UserRepository;
import com.dynamiconlineshopping.backend.repository.loyalty.BonusTransactionRepository;
import com.dynamiconlineshopping.backend.repository.loyalty.LoyaltyAccountRepository;
import com.dynamiconlineshopping.backend.repository.loyalty.LoyaltyRuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LoyaltyServiceImpl implements LoyaltyService {

    private final LoyaltyAccountRepository loyaltyAccountRepository;
    private final LoyaltyRuleRepository loyaltyRuleRepository;
    private final BonusTransactionRepository bonusTransactionRepository;
    private final UserRepository userRepository;

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private LoyaltyAccount getOrCreateAccount(User user) {
        return loyaltyAccountRepository.findByUser(user)
                .orElseGet(() -> loyaltyAccountRepository.save(
                        LoyaltyAccount.builder()
                                .user(user)
                                .bonusBalance(0)
                                .isActive(true)
                                .build()
                ));
    }

    private LoyaltyRule getRequiredActiveRule() {
        return loyaltyRuleRepository.findFirstByIsActiveTrue()
                .orElseThrow(() -> new ResourceNotFoundException("Active loyalty rule not found"));
    }

    private BonusTransactionDto mapToDto(BonusTransaction transaction) {
        return BonusTransactionDto.builder()
                .id(transaction.getId())
                .orderId(transaction.getOrder() != null ? transaction.getOrder().getId() : null)
                .type(transaction.getType().name())
                .amount(transaction.getAmount())
                .description(transaction.getDescription())
                .createdAt(transaction.getCreatedAt())
                .build();
    }

    @Override
    public BonusBalanceDto getCurrentUserBalance() {
        User user = getCurrentUser();
        LoyaltyAccount account = getOrCreateAccount(user);

        return BonusBalanceDto.builder()
                .balance(account.getBonusBalance())
                .active(account.getIsActive())
                .build();
    }

    @Override
    public List<BonusTransactionDto> getCurrentUserHistory() {
        User user = getCurrentUser();
        LoyaltyAccount account = getOrCreateAccount(user);

        return bonusTransactionRepository.findByLoyaltyAccountOrderByCreatedAtDesc(account)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public LoyaltyRuleDto getActiveRule() {
        LoyaltyRule rule = getRequiredActiveRule();

        return LoyaltyRuleDto.builder()
                .id(rule.getId())
                .accrualPercent(rule.getAccrualPercent())
                .maxWriteOffPercent(rule.getMaxWriteOffPercent())
                .minOrderAmountForAccrual(rule.getMinOrderAmountForAccrual())
                .active(rule.getIsActive())
                .build();
    }

    @Override
    @Transactional
    public LoyaltyRuleDto updateRule(UpdateLoyaltyRuleRequest request) {
        LoyaltyRule rule = loyaltyRuleRepository.findFirstByIsActiveTrue()
                .orElseGet(() -> loyaltyRuleRepository.save(
                        LoyaltyRule.builder()
                                .accrualPercent(5)
                                .maxWriteOffPercent(30)
                                .minOrderAmountForAccrual(1.0)
                                .isActive(true)
                                .build()
                ));

        if (request.getAccrualPercent() != null) {
            if (request.getAccrualPercent() < 0 || request.getAccrualPercent() > 100) {
                throw new IllegalArgumentException("Accrual percent must be between 0 and 100");
            }
            rule.setAccrualPercent(request.getAccrualPercent());
        }

        if (request.getMaxWriteOffPercent() != null) {
            if (request.getMaxWriteOffPercent() < 0 || request.getMaxWriteOffPercent() > 100) {
                throw new IllegalArgumentException("Max write-off percent must be between 0 and 100");
            }
            rule.setMaxWriteOffPercent(request.getMaxWriteOffPercent());
        }

        if (request.getMinOrderAmountForAccrual() != null) {
            if (request.getMinOrderAmountForAccrual() < 0) {
                throw new IllegalArgumentException("Minimum order amount must be non-negative");
            }
            rule.setMinOrderAmountForAccrual(request.getMinOrderAmountForAccrual());
        }

        if (request.getActive() != null) {
            rule.setIsActive(request.getActive());
        }

        LoyaltyRule saved = loyaltyRuleRepository.save(rule);

        return LoyaltyRuleDto.builder()
                .id(saved.getId())
                .accrualPercent(saved.getAccrualPercent())
                .maxWriteOffPercent(saved.getMaxWriteOffPercent())
                .minOrderAmountForAccrual(saved.getMinOrderAmountForAccrual())
                .active(saved.getIsActive())
                .build();
    }

    @Override
    public BonusCalculationResult calculateBonusWriteOff(User user, Double orderAmount, Integer requestedBonuses) {
        if (requestedBonuses == null || requestedBonuses <= 0) {
            return BonusCalculationResult.builder()
                    .bonusesToWriteOff(0)
                    .discountAmount(0.0)
                    .finalAmount(orderAmount)
                    .build();
        }

        LoyaltyRule rule = getRequiredActiveRule();
        LoyaltyAccount account = getOrCreateAccount(user);

        if (!Boolean.TRUE.equals(account.getIsActive())) {
            throw new IllegalStateException("Loyalty account is inactive");
        }

        if (requestedBonuses > account.getBonusBalance()) {
            throw new IllegalArgumentException("Not enough bonus balance");
        }

        int maxAllowedByPercent = (int) Math.floor(orderAmount * rule.getMaxWriteOffPercent() / 100.0);

        if (requestedBonuses > maxAllowedByPercent) {
            throw new IllegalArgumentException("Requested bonuses exceed the maximum allowed write-off");
        }

        double finalAmount = orderAmount - requestedBonuses;

        if (finalAmount < 0) {
            throw new IllegalArgumentException("Final amount cannot be negative");
        }

        return BonusCalculationResult.builder()
                .bonusesToWriteOff(requestedBonuses)
                .discountAmount((double) requestedBonuses)
                .finalAmount(finalAmount)
                .build();
    }

    @Override
    @Transactional
    public void writeOffBonuses(User user, Order order, Integer bonusesToWriteOff) {
        if (bonusesToWriteOff == null || bonusesToWriteOff <= 0) {
            return;
        }

        LoyaltyAccount account = getOrCreateAccount(user);

        if (account.getBonusBalance() < bonusesToWriteOff) {
            throw new IllegalArgumentException("Not enough bonus balance");
        }

        account.setBonusBalance(account.getBonusBalance() - bonusesToWriteOff);
        loyaltyAccountRepository.save(account);

        BonusTransaction transaction = BonusTransaction.builder()
                .loyaltyAccount(account)
                .order(order)
                .type(BonusTransactionType.WRITE_OFF)
                .amount(bonusesToWriteOff)
                .description("Bonuses written off for order #" + order.getId())
                .createdAt(Instant.now())
                .build();

        bonusTransactionRepository.save(transaction);
    }

    @Override
    @Transactional
    public void refundBonuses(User user, Order order, Integer bonusesToRefund) {
        if (bonusesToRefund == null || bonusesToRefund <= 0) {
            return;
        }

        boolean alreadyRefunded = bonusTransactionRepository
                .findByOrderAndType(order, BonusTransactionType.REFUND)
                .isPresent();

        if (alreadyRefunded) {
            return;
        }

        LoyaltyAccount account = getOrCreateAccount(user);
        account.setBonusBalance(account.getBonusBalance() + bonusesToRefund);
        loyaltyAccountRepository.save(account);

        BonusTransaction transaction = BonusTransaction.builder()
                .loyaltyAccount(account)
                .order(order)
                .type(BonusTransactionType.REFUND)
                .amount(bonusesToRefund)
                .description("Bonuses refunded for cancelled order #" + order.getId())
                .createdAt(Instant.now())
                .build();

        bonusTransactionRepository.save(transaction);
    }

    @Override
    @Transactional
    public void accrueBonuses(User user, Order order) {
        if (order.getAccruedBonusAmount() != null && order.getAccruedBonusAmount() > 0) {
            return;
        }

        boolean alreadyAccrued = bonusTransactionRepository
                .findByOrderAndType(order, BonusTransactionType.ACCRUAL)
                .isPresent();

        if (alreadyAccrued) {
            return;
        }

        LoyaltyRule rule = getRequiredActiveRule();
        LoyaltyAccount account = getOrCreateAccount(user);

        Double amountForAccrual = order.getTotalAmount();

        if (amountForAccrual == null || amountForAccrual < rule.getMinOrderAmountForAccrual()) {
            return;
        }

        int accrued = (int) Math.floor(amountForAccrual * rule.getAccrualPercent() / 100.0);

        if (accrued <= 0) {
            return;
        }

        account.setBonusBalance(account.getBonusBalance() + accrued);
        loyaltyAccountRepository.save(account);

        order.setAccruedBonusAmount(accrued);

        BonusTransaction transaction = BonusTransaction.builder()
                .loyaltyAccount(account)
                .order(order)
                .type(BonusTransactionType.ACCRUAL)
                .amount(accrued)
                .description("Bonuses accrued for completed order #" + order.getId())
                .createdAt(Instant.now())
                .build();

        bonusTransactionRepository.save(transaction);
    }

    @Override
    public LoyaltyStatsDto getStats() {
        List<BonusTransaction> allTransactions = bonusTransactionRepository.findAll();

        long accrued = allTransactions.stream()
                .filter(t -> t.getType() == BonusTransactionType.ACCRUAL)
                .mapToLong(BonusTransaction::getAmount)
                .sum();

        long writtenOff = allTransactions.stream()
                .filter(t -> t.getType() == BonusTransactionType.WRITE_OFF)
                .mapToLong(BonusTransaction::getAmount)
                .sum();

        long refunded = allTransactions.stream()
                .filter(t -> t.getType() == BonusTransactionType.REFUND)
                .mapToLong(BonusTransaction::getAmount)
                .sum();

        long ordersWithBonuses = allTransactions.stream()
                .filter(t -> t.getType() == BonusTransactionType.WRITE_OFF && t.getOrder() != null)
                .map(t -> t.getOrder().getId())
                .distinct()
                .count();

        return LoyaltyStatsDto.builder()
                .totalClientsInLoyaltyProgram(loyaltyAccountRepository.count())
                .totalOrdersWithBonuses(ordersWithBonuses)
                .totalBonusesAccrued(accrued)
                .totalBonusesWrittenOff(writtenOff)
                .totalBonusesRefunded(refunded)
                .build();
    }
}
