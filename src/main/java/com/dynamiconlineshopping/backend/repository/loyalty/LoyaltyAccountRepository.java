package com.dynamiconlineshopping.backend.repository.loyalty;

import com.dynamiconlineshopping.backend.entity.user.User;
import com.dynamiconlineshopping.backend.entity.loyalty.LoyaltyAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LoyaltyAccountRepository extends JpaRepository<LoyaltyAccount, Long> {
    Optional<LoyaltyAccount> findByUser(User user);
}
