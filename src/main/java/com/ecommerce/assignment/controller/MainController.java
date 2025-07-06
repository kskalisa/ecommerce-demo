package com.ecommerce.assignment.controller;

import com.ecommerce.assignment.model.Product;
import com.ecommerce.assignment.service.CategoryService;
import com.ecommerce.assignment.service.ProductService;
import com.ecommerce.assignment.util.EStockState;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

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
}
