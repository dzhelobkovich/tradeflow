package com.dynamiconlineshopping.backend.controller.loyalty;

import com.dynamiconlineshopping.backend.dto.loyalty.BonusBalanceDto;
import com.dynamiconlineshopping.backend.dto.loyalty.BonusTransactionDto;
import com.dynamiconlineshopping.backend.dto.loyalty.LoyaltyRuleDto;
import com.dynamiconlineshopping.backend.service.loyalty.LoyaltyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/loyalty")
@RequiredArgsConstructor
public class LoyaltyController {

    private final LoyaltyService loyaltyService;

    @GetMapping("/me/balance")
    public BonusBalanceDto getMyBalance() {
        return loyaltyService.getCurrentUserBalance();
    }

    @GetMapping("/me/history")
    public List<BonusTransactionDto> getMyHistory() {
        return loyaltyService.getCurrentUserHistory();
    }

    @GetMapping("/rules")
    public LoyaltyRuleDto getActiveRule() {
        return loyaltyService.getActiveRule();
    }
}
