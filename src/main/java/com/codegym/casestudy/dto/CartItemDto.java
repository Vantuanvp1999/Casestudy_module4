package com.codegym.casestudy.dto;

import com.codegym.casestudy.entity.Product;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CartItemDto {
    private Product product;
    private int quantity;

    public BigDecimal getSubtotal() {
        return product.getPrice().multiply(BigDecimal.valueOf(quantity));
    }
}
