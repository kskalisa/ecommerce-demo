package com.ecommerce.assignment.controller.user;

import com.ecommerce.assignment.model.User;
import com.ecommerce.assignment.model.Order;
import com.ecommerce.assignment.util.EOrderStatus;
import com.ecommerce.assignment.service.UserService;
import com.ecommerce.assignment.repository.OrderRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserDashboardController {
    @PostMapping("/orders/cancel/{orderId}")
    public String cancelOrder(@PathVariable Long orderId, Principal principal, Model model) {
        if (principal == null) {
            return "redirect:/auth/signin";
        }
        Order order = null;
        try {
            order = orderRepo.findById(UUID.fromString(orderId.toString())).orElse(null);
        } catch (Exception e) {
            model.addAttribute("message", "Invalid order ID.");
            return "user/dashboard";
        }
        if (order == null || order.getStatus() == null || order.getStatus().name().equals("CANCELLED") || order.getStatus().name().equals("DELIVERED")) {
            model.addAttribute("message", "Order cannot be cancelled.");
            return "user/dashboard";
        }
        order.setStatus(EOrderStatus.CANCELLED);
        orderRepo.save(order);
        model.addAttribute("message", "Order cancelled successfully.");
        return "user/dashboard";
    }

    private final UserService userService;
    private final OrderRepo orderRepo;

    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/auth/signin";
        }
        String email = principal.getName();
        User user = userService.findByEmail(email);
        if (user == null) {
            return "redirect:/auth/signin";
        }
        List<Order> userOrders = orderRepo.findAll().stream()
            .filter(order -> order.getUser() != null && user.getId() != null && order.getUser().getId().equals(user.getId()))
            .toList();
        model.addAttribute("user", user);
        model.addAttribute("userOrders", userOrders);
        // Add recentOrders (last 5 orders)
        List<Order> recentOrders = userOrders.stream()
            .sorted((o1, o2) -> o2.getOrderDate().compareTo(o1.getOrderDate()))
            .limit(5)
            .toList();
        model.addAttribute("recentOrders", recentOrders);

        // Add dummy orderStats
        model.addAttribute("orderStats", new OrderStats(userOrders));

        // Add dummy wishlistStats
        model.addAttribute("wishlistStats", new WishlistStats());

        return "user/dashboard";
    }

    // Dummy stats classes for template compatibility
    public static class OrderStats {
        public int totalOrders;
        public double totalSpent;
        public int pendingOrders;

        public OrderStats(List<Order> orders) {
            this.totalOrders = orders.size();
            this.totalSpent = orders.stream().mapToDouble(Order::getTotalAmount).sum();
            this.pendingOrders = (int) orders.stream().filter(o -> o.getStatus() != null && o.getStatus().name().equals("PENDING")).count();
        }
    }

    public static class WishlistStats {
        public int itemCount = 0;
    }
    }
