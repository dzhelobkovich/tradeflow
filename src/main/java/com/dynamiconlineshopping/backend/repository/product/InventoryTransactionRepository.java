package com.dynamiconlineshopping.backend.repository.product;

import com.dynamiconlineshopping.backend.entity.product.InventoryTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryTransactionRepository extends JpaRepository<InventoryTransaction, Long> {
}
