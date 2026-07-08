package com.dynamiconlineshopping.backend.repository.loyalty;

import com.dynamiconlineshopping.backend.entity.order.Order;
import com.dynamiconlineshopping.backend.entity.loyalty.BonusTransaction;
import com.dynamiconlineshopping.backend.entity.loyalty.LoyaltyAccount;
import com.dynamiconlineshopping.backend.enums.loyalty.BonusTransactionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BonusTransactionRepository extends JpaRepository<BonusTransaction, Long> {

    List<BonusTransaction> findByLoyaltyAccountOrderByCreatedAtDesc(LoyaltyAccount loyaltyAccount);

    Optional<BonusTransaction> findByOrderAndType(Order order, BonusTransactionType type);

    List<BonusTransaction> findAllByOrderByCreatedAtDesc();
}
