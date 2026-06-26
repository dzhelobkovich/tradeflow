package com.dynamiconlineshopping.backend.dto.loyalty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoyaltyRuleDto {
    private Long id;
    private Integer accrualPercent;
    private Integer maxWriteOffPercent;
    private Double minOrderAmountForAccrual;
    private Boolean active;
}
