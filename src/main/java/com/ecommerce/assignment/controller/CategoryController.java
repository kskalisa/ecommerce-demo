package com.ecommerce.assignment.controller;

import com.ecommerce.assignment.model.Category;
import com.ecommerce.assignment.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping("/All")
    public String getAllCategories(Model model){
        List<Category> categories = categoryService.findAllCategories();
        model.addAttribute("categories", categories);
        model.addAttribute("category", new Category());
        return "category";
    }

    @PostMapping("/addCategory")
    public String addCategory(@ModelAttribute("category") Category category, Model model){
        categoryService.createCategory(category);
        model.addAttribute("message"," Category Added Successfully");
        return "redirect:/categories/All";
    }

}
