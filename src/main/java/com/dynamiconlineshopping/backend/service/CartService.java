package com.dynamiconlineshopping.backend.service;

import com.dynamiconlineshopping.backend.dto.cart.CartItemDto;

import java.util.List;

public interface CartService {
    List<CartItemDto> getCartForCurrentUser();
    List<CartItemDto> addItem(CartItemDto dto);
    List<CartItemDto> updateItem(Long cartItemId, CartItemDto dto);
    List<CartItemDto> removeItem(Long cartItemId);
}
