package com.ecommerce.assignment.repository;

import com.ecommerce.assignment.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepo extends JpaRepository<Product, Integer> {
}
