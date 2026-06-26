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
public class BonusBalanceDto {
    private Integer balance;
    private Boolean active;
}
