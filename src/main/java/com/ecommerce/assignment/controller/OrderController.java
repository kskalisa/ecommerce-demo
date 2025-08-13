package com.ecommerce.assignment.controller;
import com.ecommerce.assignment.util.EOrderStatus;

import com.ecommerce.assignment.model.CartItem;
import com.ecommerce.assignment.model.Order;
import com.ecommerce.assignment.model.OrderItem;
import com.ecommerce.assignment.model.User;
import com.ecommerce.assignment.repository.OrderRepo;
import com.ecommerce.assignment.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {
    // ...existing code...

    private final UserService userService;
    private final OrderRepo orderRepo;

    @PostMapping("/place")
    public String placeOrder(@ModelAttribute("order") Order order,
                             HttpSession session, Principal principal){
        String username = principal.getName();
        User user = userService.findByUsername(username);

        List<CartItem> cartItems = (List<CartItem>) session.getAttribute("cartItem");

        if (cartItems == null || cartItems.isEmpty()) {
            return "redirect:/cart?error=empty";
        }

        // Set user and order details
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());

        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            OrderItem item = new OrderItem();
            item.setProduct(cartItem.getProduct());
            item.setQuantity(cartItem.getQuantity());
            item.setPrice(cartItem.getProduct().getPrice());
            item.setOrder(order);
            orderItems.add(item);
        }

        order.setItems(orderItems);

        orderRepo.save(order);

        session.setAttribute("order", order);

        // Clear cart
        session.removeAttribute("cartItem");

        return "redirect:/order/confirmation";
    }


    @GetMapping("all")
    public String getAllOrder(Model model){
        List<Order> orders = orderRepo.findAll();
        model.addAttribute("orders", orders);

        return "orders";
    }

    // User portal: view own order history
    @GetMapping("/my")
    public String getUserOrders(Model model, Principal principal) {
        String username = principal.getName();
        User user = userService.findByUsername(username);
        List<Order> orders = orderRepo.findAll().stream()
            .filter(order -> order.getUser() != null && order.getUser().getId().equals(user.getId()))
            .toList();
        model.addAttribute("orders", orders);
        return "orders";
    }

    // Cancel order (user-initiated)
    @PostMapping("/cancel/{orderId}")
    @ResponseBody
    public ResponseEntity<String> cancelOrder(@PathVariable("orderId") UUID orderId, Principal principal) {
        if (principal == null) {
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        String email = principal.getName();
        User user = userService.findByEmail(email);
        if (user == null) {
            return new ResponseEntity<>("User not found", HttpStatus.FORBIDDEN);
        }
        Order order = orderRepo.findById(orderId).orElse(null);
        if (order == null || order.getUser() == null || !order.getUser().getId().equals(user.getId())) {
            return new ResponseEntity<>("Order not found or not authorized", HttpStatus.FORBIDDEN);
        }
        if (order.getStatus() == null || order.getStatus().name().equals("CANCELLED") || order.getStatus().name().equals("DELIVERED")) {
            return new ResponseEntity<>("Order cannot be cancelled", HttpStatus.BAD_REQUEST);
        }
        order.setStatus(com.ecommerce.assignment.util.EOrderStatus.CANCELLED);
        orderRepo.save(order);
        return new ResponseEntity<>("Order cancelled successfully", HttpStatus.OK);
    }
}
