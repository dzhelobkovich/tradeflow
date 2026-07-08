package com.dynamiconlineshopping.backend.dto.cart;

import com.dynamiconlineshopping.backend.dto.product.ProductDto;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemDto {
    private Long id;
    private Long productId;
    private Integer quantity;
    private ProductDto product;
}
