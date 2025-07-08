package com.ecommerce.assignment.controller;

import com.ecommerce.assignment.model.CartItem;
import com.ecommerce.assignment.model.Product;
import com.ecommerce.assignment.service.ProductService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class CartController {

    public final ProductService productService;

    @GetMapping("/Add-cart{ProductId}")
    public String addCart(@PathVariable("ProductId") UUID productId, HttpSession session){
        List<CartItem> cartItems  = (List<CartItem>) session.getAttribute("cartItem");
        if(cartItems == null){
            cartItems = new ArrayList<>();
        }

        Product product = productService.findProductByIdAndState(productId, true);

        if(product != null){
            boolean itemExist = false;

            for(CartItem cartItem : cartItems){
                if(cartItem.getProduct().getId().equals(productId)){
                    cartItem.setQuantity(cartItem.getQuantity() + 1);
                    itemExist = true;
                    break;
                }
            }

            if(!itemExist){
                cartItems.add(new CartItem(product,1));
            }
            session.setAttribute("cartItem", cartItems);

        }
     return "redirect:/cart";
    }

    @GetMapping("/cart")
    public String showCart(HttpSession session, Model model){

        return null;
    }


}
