package com.codegym.casestudy.controller;

import com.codegym.casestudy.entity.Product;
import com.codegym.casestudy.service.CartService;
import com.codegym.casestudy.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;
    private final ProductService productService;

    public CartController(CartService cartService, ProductService productService) {
        this.cartService = cartService;
        this.productService = productService;
    }

    @GetMapping
    public String showCart(Model model) {
        model.addAttribute("cartItems", cartService.getCartItems());
        model.addAttribute("totalPrice", cartService.getTotalPrice());
        return "cart/view";
    }

    @PostMapping("/add")
    public String addToCart(@RequestParam Long productId, @RequestParam int quantity) {
        Product product = productService.getProductById(productId);
        if (product != null) {
            cartService.addToCart(product, quantity);
        }
        return "redirect:/cart";
    }

    @GetMapping("/remove/{productId}")
    public String removeFromCart(@PathVariable Long productId) {
        cartService.removeFromCart(productId);
        return "redirect:/cart";
    }

    @PostMapping("/update")
    public String updateCart(@RequestParam Long productId, @RequestParam int quantity) {
        cartService.updateCart(productId, quantity);
        return "redirect:/cart";
    }
}