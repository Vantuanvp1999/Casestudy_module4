package com.codegym.casestudy.controller;

import com.codegym.casestudy.entity.Order;
import com.codegym.casestudy.service.CartService;
import com.codegym.casestudy.service.OrderService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
public class OrderController {

    private final OrderService orderService;
    private final CartService cartService;

    public OrderController(OrderService orderService, CartService cartService) {
        this.orderService = orderService;
        this.cartService = cartService;
    }

    // Hiển thị form checkout
    @GetMapping("/checkout")
    public String checkout(Model model) {
        // Kiểm tra giỏ hàng có trống không
        if (cartService.getCartItems().isEmpty()) {
            return "redirect:/cart"; // Nếu trống thì quay lại giỏ hàng
        }
        model.addAttribute("cartItems", cartService.getCartItems());
        model.addAttribute("totalPrice", cartService.getTotalPrice());
        return "checkout/form"; // Trả về trang form checkout
    }

    // Xử lý việc đặt hàng
    @PostMapping("/order/place")
    public String placeOrder(@RequestParam String customerName,
                             @RequestParam String shippingAddress,
                             @RequestParam String phoneNumber,
                             @RequestParam String notes,
                             Principal principal, // Lấy thông tin user đã đăng nhập
                             RedirectAttributes redirectAttributes) {
        if (principal == null) {
            return "redirect:/login"; // Yêu cầu đăng nhập nếu chưa
        }

        try {
            Order order = orderService.createOrder(customerName, shippingAddress, phoneNumber, notes, principal.getName());
            // Dùng RedirectAttributes để gửi thông báo qua redirect
            redirectAttributes.addFlashAttribute("successMessage", "Đặt hàng thành công! Mã đơn hàng của bạn là #" + order.getId());
            return "redirect:/order/confirmation";
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/cart";
        }
    }

    // Hiển thị trang xác nhận đặt hàng thành công
    @GetMapping("/order/confirmation")
    public String orderConfirmation() {
        return "checkout/confirmation";
    }

    // Hiển thị lịch sử đơn hàng của người dùng
    @GetMapping("/orders")
    public String viewOrderHistory(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        List<Order> orders = orderService.getOrdersByUser(userDetails.getUsername());
        model.addAttribute("orders", orders);
        return "order/history";
    }

    // Xem chi tiết một đơn hàng cụ thể
    @GetMapping("/orders/{id}")
    public String viewOrderDetail(@PathVariable Long id, Model model, @AuthenticationPrincipal UserDetails userDetails) {
        Order order = orderService.getOrderById(id);

        // Kiểm tra xem đơn hàng có tồn tại và có thuộc về người dùng đang đăng nhập không
        if (order == null || !order.getUser().getUsername().equals(userDetails.getUsername())) {
            return "redirect:/orders?error=notfound"; // Hoặc trả về trang lỗi 404
        }

        model.addAttribute("order", order);
        return "order/detail";
    }
}