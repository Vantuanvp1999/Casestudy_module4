package com.codegym.casestudy.service;

import com.codegym.casestudy.dto.CartItemDto;
import com.codegym.casestudy.entity.Order;
import com.codegym.casestudy.entity.OrderDetail;
import com.codegym.casestudy.entity.User;
import com.codegym.casestudy.repository.OrderRepository;
import com.codegym.casestudy.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CartService cartService; // Inject CartService để lấy thông tin giỏ hàng

    public OrderService(OrderRepository orderRepository, UserRepository userRepository, CartService cartService) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.cartService = cartService;
    }

    @Transactional // Đảm bảo tất cả các thao tác CSDL trong phương thức này là một giao dịch
    public Order createOrder(String customerName, String shippingAddress, String phoneNumber, String notes, String username) {
        // Lấy thông tin người dùng hiện tại
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        // Lấy các sản phẩm từ giỏ hàng
        List<CartItemDto> cartItems = cartService.getCartItems();
        if (cartItems.isEmpty()) {
            throw new IllegalStateException("Giỏ hàng trống!");
        }

        // Tạo đối tượng Order
        Order order = new Order();
        order.setUser(user);
        order.setCustomerName(customerName);
        order.setShippingAddress(shippingAddress);
        order.setPhoneNumber(phoneNumber);
        order.setNotes(notes);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("PENDING"); // Trạng thái ban đầu
        order.setTotalPrice(cartService.getTotalPrice());

        // Tạo các đối tượng OrderDetail từ cartItems
        for (CartItemDto cartItem : cartItems) {
            OrderDetail detail = new OrderDetail();
            detail.setOrder(order); // Liên kết với Order
            detail.setProduct(cartItem.getProduct());
            detail.setQuantity(cartItem.getQuantity());
            // Lưu giá tại thời điểm đặt hàng
            detail.setPrice(cartItem.getProduct().getPrice());
            order.getOrderDetails().add(detail);
        }

        // Lưu Order (và OrderDetails nhờ cascade) vào CSDL
        Order savedOrder = orderRepository.save(order);

        // Xóa giỏ hàng sau khi đã đặt hàng thành công
        cartService.clearCart();

        return savedOrder;
    }

    // Lấy danh sách đơn hàng của một người dùng
    public List<Order> getOrdersByUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return orderRepository.findByUserOrderByOrderDateDesc(user);
    }

    // Lấy chi tiết một đơn hàng (có thể dùng để kiểm tra quyền sở hữu)
    public Order getOrderById(Long id) {
        return orderRepository.findById(id).orElse(null);
    }
}