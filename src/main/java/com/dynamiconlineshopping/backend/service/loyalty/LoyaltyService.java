package com.dynamiconlineshopping.backend.service.loyalty;

import com.dynamiconlineshopping.backend.dto.loyalty.BonusBalanceDto;
import com.dynamiconlineshopping.backend.dto.loyalty.BonusCalculationResult;
import com.dynamiconlineshopping.backend.dto.loyalty.BonusTransactionDto;
import com.dynamiconlineshopping.backend.dto.loyalty.LoyaltyRuleDto;
import com.dynamiconlineshopping.backend.dto.loyalty.LoyaltyStatsDto;
import com.dynamiconlineshopping.backend.dto.loyalty.UpdateLoyaltyRuleRequest;
import com.dynamiconlineshopping.backend.entity.order.Order;
import com.dynamiconlineshopping.backend.entity.user.User;

import java.util.List;

public interface LoyaltyService {

    BonusBalanceDto getCurrentUserBalance();

    List<BonusTransactionDto> getCurrentUserHistory();

    LoyaltyRuleDto getActiveRule();

    LoyaltyRuleDto updateRule(UpdateLoyaltyRuleRequest request);

    BonusCalculationResult calculateBonusWriteOff(User user, Double orderAmount, Integer requestedBonuses);

    void writeOffBonuses(User user, Order order, Integer bonusesToWriteOff);

    void refundBonuses(User user, Order order, Integer bonusesToRefund);

    void accrueBonuses(User user, Order order);

    LoyaltyStatsDto getStats();
}
