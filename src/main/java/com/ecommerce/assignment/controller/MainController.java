package com.ecommerce.assignment.controller;

import com.ecommerce.assignment.model.Product;
import com.ecommerce.assignment.service.CategoryService;
import com.ecommerce.assignment.service.ProductService;
import com.ecommerce.assignment.util.EStockState;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final ProductService productService;

    @GetMapping("/")
    public String index(Model model) {
        List<Product> products = productService.findProductByStockStateAndState(EStockState.AVAILABLE, Boolean.TRUE);
        model.addAttribute("products", products);
        return "main/index";
    }

    @GetMapping("/product-details/{productId}")
    public String productDetails(@PathVariable("productId") UUID productId, Model model) {
        Product product = productService.findProductByIdAndState(productId, true);
        if (product != null) {
            model.addAttribute("product", product);
            return "main/product-details";
        }
        model.addAttribute("error", "Product not found");
        return "redirect:/";
    }



}
