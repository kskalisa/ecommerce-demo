package com.ecommerce.assignment.service;

import com.ecommerce.assignment.model.Category;
import com.ecommerce.assignment.repository.CategoryRepo;
import lombok.RequiredArgsConstructor;
import org.hibernate.ObjectNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryService implements ICategoryService{

    private final CategoryRepo categoryRepo;

    @Override
    public List<Category> findAllCategories() {
        return categoryRepo.findAll();
    }

    @Override
    public Category createCategory(Category theCategory) {
        return categoryRepo.save(theCategory);
    }

    @Override
    public Category updateCategory(Category theCategory) {
        Category found = findCategoryById(theCategory.getId());
        if (Objects.nonNull(found)) {
            found.setName(theCategory.getName());
            found.setDescription(theCategory.getDescription());
            found.setProducts(theCategory.getProducts());
            return categoryRepo.save(found);
        }
        throw new ObjectNotFoundException(Category.class, "Category not found");
    }

    @Override
    public Category deleteCategory(Category theCategory) {
        Category found = findCategoryById(theCategory.getId());
        if (Objects.nonNull(found)) {
            categoryRepo.delete(found);
            return found;
        }
        throw new ObjectNotFoundException(Category.class, "Category not found");
    }

    @Override
    public Category findCategoryById(UUID id) {
        return categoryRepo.findById(id).orElseThrow(() -> new ObjectNotFoundException(Category.class, "Category not found"));
    }

    @Override
    public List<Category> findCategoryByName(String name) {
        return categoryRepo.findByName(name)
                .map(List::of)
                .orElseThrow(() -> new ObjectNotFoundException(Category.class, "Category not found"));
    }


}