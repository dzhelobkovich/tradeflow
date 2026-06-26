package com.dynamiconlineshopping.backend.entity.cart;

import com.dynamiconlineshopping.backend.entity.product.Product;
import com.dynamiconlineshopping.backend.entity.user.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cart_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItem {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private User user;

    @ManyToOne(optional = false)
    private Product product;

    private Integer quantity;
}
