package com.dynamiconlineshopping.backend.entity.loyalty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "loyalty_rules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoyaltyRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "accrual_percent", nullable = false)
    private Integer accrualPercent;

    @Column(name = "max_write_off_percent", nullable = false)
    private Integer maxWriteOffPercent;

    @Column(name = "min_order_amount_for_accrual", nullable = false)
    private Double minOrderAmountForAccrual;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;
}
