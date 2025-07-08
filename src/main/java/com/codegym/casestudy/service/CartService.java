package com.codegym.casestudy.service;


import com.codegym.casestudy.dto.CartItemDto;
import com.codegym.casestudy.entity.Product;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@SessionScope // Mỗi session (mỗi người dùng) sẽ có một CartService riêng
public class CartService {
    private List<CartItemDto> cartItems = new ArrayList<>();

    public void addToCart(Product product, int quantity) {
        Optional<CartItemDto> existingItem = cartItems.stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItemDto item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
        } else {
            CartItemDto newItem = new CartItemDto();
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            cartItems.add(newItem);
        }
    }

    public List<CartItemDto> getCartItems() {
        return cartItems;
    }

    public void removeFromCart(Long productId) {
        cartItems.removeIf(item -> item.getProduct().getId().equals(productId));
    }

    public void updateCart(Long productId, int quantity) {
        cartItems.stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .ifPresent(item -> item.setQuantity(quantity));
    }

    public BigDecimal getTotalPrice() {
        return cartItems.stream()
                .map(CartItemDto::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void clearCart() {
        cartItems.clear();
    }
}
