package com.ecommerce.assignment.controller;

import com.ecommerce.assignment.model.Product;
import com.ecommerce.assignment.service.FileStorageService;
import com.ecommerce.assignment.service.IProductService;
import com.ecommerce.assignment.util.EStockState;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final IProductService productService;
    private final FileStorageService fileStorageService;


//    @GetMapping("/All")
//    public String getProducts(Model model){
//        List<Product> products = productService.findProductByState(Boolean.TRUE);
//        model.addAttribute("products", products);
//        return "products";
//    }
//
//    @GetMapping("/allProduct")
//    public String showProducts(Model model){
//        model.addAttribute("product", new Product());
//        return "products";
//    }

    @GetMapping("/All")
    public String showProductPage(Model model) {
        List<Product> products = productService.findProductByState(Boolean.TRUE);
        model.addAttribute("products", products);
        model.addAttribute("product", new Product());
        return "products";
    }

    @PostMapping("/addProduct")
    public String registerProduct(@ModelAttribute("product") Product product,
                                  @RequestParam("imageFile") MultipartFile imageFile,
                                  Model model){
        try{
            if (!imageFile.isEmpty()) {
                String storedFileName = fileStorageService.storeFile(imageFile);
                product.setImage(storedFileName);
            }

            if(product.getStockState() == null){
                product.setStockState(EStockState.AVAILABLE);
            }

            productService.createProduct(product);
            model.addAttribute("message", "Product Added successfully");
            return "redirect:/products/All";


        }
        catch (Exception e){
            model.addAttribute("error", "Product not Added");
            return "redirect:/products/All";

        }

    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") UUID id, Model model) {
        Product product = productService.findProductByIdAndState(id, true);
        model.addAttribute("product", product);
        return "products"; // reuse the same modal or page for editing
    }

    @PostMapping("/updateProduct")
    public String updateProduct(@ModelAttribute("product") Product product,
                                @RequestParam("imageFile") MultipartFile imageFile,
                                Model model) {
        try {
            if (!imageFile.isEmpty()) {
                String fileName = StringUtils.cleanPath(Objects.requireNonNull(imageFile.getOriginalFilename()));
                product.setImage(fileName);
            }

            productService.updateProduct(product);
            model.addAttribute("message", "Product updated successfully");
            return "redirect:/products/All";
        } catch (Exception e) {
            model.addAttribute("error", "Product update failed");
            return "products";
        }
    }


    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable("id") UUID id) {
        Product product = productService.findProductByIdAndState(id, true);
        productService.deleteProduct(product);
        return "redirect:/products/All";
    }





}
