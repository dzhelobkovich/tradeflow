package com.dynamiconlineshopping.backend.dto.loyalty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BonusTransactionDto {
    private Long id;
    private Long orderId;
    private String type;
    private Integer amount;
    private String description;
    private Instant createdAt;
}
