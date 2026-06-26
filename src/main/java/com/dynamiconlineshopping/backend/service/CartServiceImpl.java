package com.dynamiconlineshopping.backend.service;

import com.dynamiconlineshopping.backend.dto.cart.CartItemDto;
import com.dynamiconlineshopping.backend.dto.product.ProductDto;
import com.dynamiconlineshopping.backend.dto.promotion.PromotionPriceResult;
import com.dynamiconlineshopping.backend.entity.cart.CartItem;
import com.dynamiconlineshopping.backend.entity.product.Product;
import com.dynamiconlineshopping.backend.entity.user.User;
import com.dynamiconlineshopping.backend.enums.promotion.PromotionSegment;
import com.dynamiconlineshopping.backend.exception.ResourceNotFoundException;
import com.dynamiconlineshopping.backend.repository.cart.CartRepository;
import com.dynamiconlineshopping.backend.repository.product.ProductRepository;
import com.dynamiconlineshopping.backend.repository.user.UserRepository;
import com.dynamiconlineshopping.backend.service.promotion.ProductPromotionPriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ProductPromotionPriceService productPromotionPriceService;

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private CartItemDto toDto(CartItem cartItem) {
        Product product = cartItem.getProduct();

        PromotionPriceResult priceResult = productPromotionPriceService.calculatePrice(
                product,
                PromotionSegment.B2C
        );

        ProductDto productDto = ProductDto.builder()
                .id(product.getId())
                .title(product.getTitle())
                .description(product.getDescription())
                .price(product.getPrice())
                .b2bPrice(product.getB2bPrice())
                .finalPrice(priceResult.getFinalPrice())
                .discountAmount(priceResult.getDiscountAmount())
                .discountPercent(priceResult.getDiscountPercent())
                .hasActivePromotion(priceResult.getHasActivePromotion())
                .promotionTitle(priceResult.getPromotionTitle())
                .stock(product.getStock())
                .sku(product.getSku())
                .imageUrl(product.getImageUrl())
                .active(product.getActive())
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : null)
                .categorySlug(product.getCategory() != null ? product.getCategory().getSlug() : null)
                .build();

        return CartItemDto.builder()
                .id(cartItem.getId())
                .productId(product.getId())
                .quantity(cartItem.getQuantity())
                .product(productDto)
                .build();
    }

    @Override
    public List<CartItemDto> getCartForCurrentUser() {
        User user = getCurrentUser();

        return cartRepository.findByUser(user)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<CartItemDto> addItem(CartItemDto dto) {
        validateCartItemRequest(dto);

        User user = getCurrentUser();

        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (Boolean.FALSE.equals(product.getActive())) {
            throw new IllegalStateException("Cannot add inactive product to cart");
        }

        if (product.getStock() == null || product.getStock() < dto.getQuantity()) {
            throw new IllegalStateException("Not enough product stock");
        }

        CartItem existingItem = cartRepository.findByUser(user)
                .stream()
                .filter(item -> item.getProduct() != null
                        && item.getProduct().getId().equals(product.getId()))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            int newQuantity = existingItem.getQuantity() + dto.getQuantity();

            if (product.getStock() < newQuantity) {
                throw new IllegalStateException("Not enough product stock");
            }

            existingItem.setQuantity(newQuantity);
            cartRepository.save(existingItem);
        } else {
            CartItem item = CartItem.builder()
                    .user(user)
                    .product(product)
                    .quantity(dto.getQuantity())
                    .build();

            cartRepository.save(item);
        }

        return getCartForCurrentUser();
    }

    @Override
    @Transactional
    public List<CartItemDto> updateItem(Long cartItemId, CartItemDto dto) {
        if (dto == null || dto.getQuantity() == null || dto.getQuantity() <= 0) {
            throw new IllegalArgumentException("Cart item quantity must be greater than zero");
        }

        User user = getCurrentUser();

        CartItem item = cartRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        if (!item.getUser().getId().equals(user.getId())) {
            throw new IllegalStateException("Cart item does not belong to current user");
        }

        Product product = item.getProduct();

        if (product.getStock() == null || product.getStock() < dto.getQuantity()) {
            throw new IllegalStateException("Not enough product stock");
        }

        item.setQuantity(dto.getQuantity());
        cartRepository.save(item);

        return getCartForCurrentUser();
    }

    @Override
    @Transactional
    public List<CartItemDto> removeItem(Long cartItemId) {
        User user = getCurrentUser();

        CartItem item = cartRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        if (!item.getUser().getId().equals(user.getId())) {
            throw new IllegalStateException("Cart item does not belong to current user");
        }

        cartRepository.delete(item);

        return getCartForCurrentUser();
    }

    private void validateCartItemRequest(CartItemDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Cart item request must not be null");
        }

        if (dto.getProductId() == null) {
            throw new IllegalArgumentException("Product id is required");
        }

        if (dto.getQuantity() == null || dto.getQuantity() <= 0) {
            throw new IllegalArgumentException("Cart item quantity must be greater than zero");
        }
    }
}
