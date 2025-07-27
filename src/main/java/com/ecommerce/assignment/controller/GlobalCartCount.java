package com.ecommerce.assignment.controller;

import com.ecommerce.assignment.model.CartItem;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@ControllerAdvice
public class GlobalCartCount {
    @ModelAttribute("cartCount")
    public int getCartCount(HttpSession session) {
        List<CartItem> cartItems = (List<CartItem>) session.getAttribute("cartItem");
        if(cartItems != null){
            return cartItems.stream().mapToInt(CartItem::getQuantity).sum();

        }
        return 0;
    }
}
