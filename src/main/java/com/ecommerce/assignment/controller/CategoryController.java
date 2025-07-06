package com.ecommerce.assignment.controller;

import com.ecommerce.assignment.model.Category;
import com.ecommerce.assignment.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.hibernate.ObjectNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

    @GetMapping("/edit")
    public String showEditForm(@RequestParam("category_id") UUID id, Model model) {
        Category category = categoryService.findCategoryById(id);
        model.addAttribute("category", category);
        return "category";
    }

    @PostMapping("updateCategory")
    public String updateCategory(@ModelAttribute("category") Category category, Model model){
        categoryService.updateCategory(category);
        model.addAttribute("message"," Category Updated Successfully");
        return "redirect:/categories/All";
    }

    @GetMapping("/delete")
    public String deleteCategory(@RequestParam("categoryId") UUID id) {
        Category category = categoryService.findCategoryById(id);
        categoryService.deleteCategory(category);
        return "redirect:/categories/All";
    }


}
