package com.dynamiconlineshopping.backend.repository.loyalty;

import com.dynamiconlineshopping.backend.entity.loyalty.LoyaltyRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LoyaltyRuleRepository extends JpaRepository<LoyaltyRule, Long> {
    Optional<LoyaltyRule> findFirstByIsActiveTrue();
}
