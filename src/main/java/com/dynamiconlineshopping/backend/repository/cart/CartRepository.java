package com.dynamiconlineshopping.backend.repository.cart;

import com.dynamiconlineshopping.backend.entity.cart.CartItem;
import com.dynamiconlineshopping.backend.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByUser(User user);
    void deleteByUserAndProductId(User user, Long productId);
}
