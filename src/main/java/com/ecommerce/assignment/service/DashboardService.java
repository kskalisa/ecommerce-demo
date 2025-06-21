package com.ecommerce.assignment.service;

import com.ecommerce.assignment.model.Product;

import java.util.List;

public interface DashboardService {
    long countCustomers();
    long countProducts();
    List<Product> getTopProducts();
}
