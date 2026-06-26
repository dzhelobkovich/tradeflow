package com.dynamiconlineshopping.backend.dto.order;

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
public class OrderItemDto {
    private Long productId;
    private String productTitle;
    private String description;
    private String sku;
    private String categoryName;
    private Integer stock;
    private String imageUrl;
    private Integer quantity;
    private Double price;
    private Double subtotal;
}
