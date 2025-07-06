package com.ecommerce.assignment.service;

import com.ecommerce.assignment.model.Category;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ICategoryService {
    List<Category> findAllCategories();
    Category createCategory(Category theCategory);
    Category updateCategory(Category theCategory);
    Category deleteCategory(Category theCategory);
    Category findCategoryById(UUID id);
    List<Category> findCategoryByName(String name);
}
