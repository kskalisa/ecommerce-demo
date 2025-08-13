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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final ProductService productService;
    private final CategoryService categoryService;

    @GetMapping("/")
    public String index(@RequestParam(value = "category", required = false) String category, Model model) {
        List<Product> products;
        List<com.ecommerce.assignment.model.Category> categories = categoryService.findAllCategories();
        if (category != null && !category.isEmpty()) {
            com.ecommerce.assignment.model.Category selectedCategory = categories.stream()
                .filter(cat -> cat.getName().equalsIgnoreCase(category))
                .findFirst()
                .orElse(null);
            products = (selectedCategory != null && selectedCategory.getProducts() != null)
                ? selectedCategory.getProducts()
                : List.of();
        } else {
            products = productService.findProductByStockStateAndState(EStockState.AVAILABLE, Boolean.TRUE);
        }
        model.addAttribute("products", products);
        model.addAttribute("categories", categories);
        model.addAttribute("category", category);
        return "main/index";
    }
    @GetMapping("/userdash")
    public String showDashboard(){
        return "user/dashboard";
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

    // Live search endpoint for AJAX requests
    @GetMapping("/search/products")
    @ResponseBody
    public ResponseEntity<List<Product>> searchProducts(@RequestParam(value = "query", required = false) String query,
                                                       @RequestParam(value = "categoryId", required = false) UUID categoryId) {
        // Only return AVAILABLE products
        List<Product> products;
        if ((query != null && !query.trim().isEmpty()) || categoryId != null) {
            products = productService.findProductsWithFilters(
                query,
                categoryId,
                EStockState.AVAILABLE,
                null,
                null,
                org.springframework.data.domain.Pageable.unpaged()
            ).getContent();
        } else {
            products = productService.findProductByStockStateAndState(EStockState.AVAILABLE, Boolean.TRUE);
        }
        return new ResponseEntity<>(products, HttpStatus.OK);
    }



}
