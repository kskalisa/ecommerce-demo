package com.ecommerce.assignment.controller;

import com.ecommerce.assignment.model.CartItem;
import com.ecommerce.assignment.model.Order;
import com.ecommerce.assignment.model.OrderItem;
import com.ecommerce.assignment.model.User;
import com.ecommerce.assignment.repository.OrderRepo;
import com.ecommerce.assignment.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {

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
}
