package com.ecommerce.assignment.controller;

import com.ecommerce.assignment.model.CartItem;
import com.ecommerce.assignment.model.CartSummary;
import com.ecommerce.assignment.model.Product;
import com.ecommerce.assignment.service.ProductService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class CartController {

    public final ProductService productService;

    @PostMapping("/add-cart")
    public String addCart(@RequestParam("productId") UUID productId
            ,@RequestParam("quantity") int quantity, HttpSession session){

        List<CartItem> cartItems  = (List<CartItem>) session.getAttribute("cartItem");
        if(cartItems == null){
            cartItems = new ArrayList<>();
        }

        Product product = productService.findProductByIdAndState(productId, true);

        if(product != null){
            boolean itemExist = false;

            for(CartItem cartItem : cartItems){
                if(cartItem.getProduct().getId().equals(productId)){
                    cartItem.setQuantity(cartItem.getQuantity() + quantity);
                    itemExist = true;
                    break;
                }
            }

            if(!itemExist){
                cartItems.add(new CartItem(product,quantity));
            }
            session.setAttribute("cartItem", cartItems);

        }
     return "redirect:/cart";
    }

    @GetMapping("/cart")
    public String showCart(HttpSession session, Model model) {

        List<CartItem> cartItems = (List<CartItem>) session.getAttribute("cartItem");
        if (cartItems == null) {
            cartItems = new ArrayList<>();

        }

        CartSummary cartSummary = new CartSummary(cartItems);

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("cartSummary", cartSummary);

        return "main/cart";

    }

    @PostMapping("/remove-cart")
    public String removeCartItem(@RequestParam("productId") UUID productId, HttpSession session){
        List<CartItem> cartItems = (List<CartItem>) session.getAttribute("cartItem");
        if(cartItems != null){
            cartItems.removeIf(cartItem -> cartItem.getProduct().getId().equals(productId));

        }
        return "redirect:/cart";
    }

    @PostMapping("/clear")
    public String clearCart(HttpSession session){
        session.removeAttribute("cartItem");
        return "redirect:/cart";
    }

}
