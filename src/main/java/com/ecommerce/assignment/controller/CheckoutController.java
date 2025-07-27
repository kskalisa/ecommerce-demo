package com.ecommerce.assignment.controller;

import com.ecommerce.assignment.dto.Checkout;
import com.ecommerce.assignment.model.CartItem;
import com.ecommerce.assignment.model.Order;
import com.ecommerce.assignment.model.OrderItem;
import com.ecommerce.assignment.model.User;
import com.ecommerce.assignment.repository.OrderRepo;
import com.ecommerce.assignment.repository.UserRepo;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping
@RequiredArgsConstructor
public class CheckoutController {


    private final UserRepo userService;
    private final OrderRepo orderRepo;

    @GetMapping("/checkout")
    public String checkoutPage(HttpSession session, Model model, Principal principal){
        if (principal == null) {
            return "redirect:/auth/signin";  // Only redirect if not logged in
        }
        List<CartItem> cartItems = (List<CartItem>) session.getAttribute("cartItem");
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("checkoutForm", new Checkout());


        return "checkout";
    }

    @PostMapping("/checkout")
    public String placeOrder(@ModelAttribute("order") Checkout checkout, HttpSession session, Principal principal) {
        if (principal == null) {
            return "redirect:/auth/signin";  // Not logged in
        }

        User user = userService.findByEmail(principal.getName()).orElseThrow();

        List<CartItem> cartItems = (List<CartItem>) session.getAttribute("cartItem");
        if (cartItems == null || cartItems.isEmpty()) {
            return "redirect:/cart"; // No items to checkout
        }

        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("Pending");
        order.setShippingAddress(checkout.getShippingAddress());
        order.setPhoneNumber(checkout.getPhoneNumber());
        order.setPaymentMethod(checkout.getPaymentMethod());

        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            OrderItem oi = new OrderItem();
            oi.setProduct(cartItem.getProduct());
            oi.setQuantity(cartItem.getQuantity());
            oi.setPrice(cartItem.getProduct().getPrice());
            oi.setOrder(order);
            orderItems.add(oi);
        }

        order.setItems(orderItems);
        orderRepo.save(order);

        session.setAttribute("order", order);


        session.removeAttribute("cartItem");

        return "redirect:/order-confirmation";
    }


    @GetMapping("/order-confirmation")
    public String orderConfirmation(Model model, HttpSession session) {
        Order order = (Order) session.getAttribute("order");
        if (order == null) {
            return "redirect:/"; // Or some fallback page if no order found in session
        }
        model.addAttribute("order", order);
        // Optionally, clear the order from session if you don't want it to persist
        session.removeAttribute("order");
        return "order-confirmation";
    }

}
