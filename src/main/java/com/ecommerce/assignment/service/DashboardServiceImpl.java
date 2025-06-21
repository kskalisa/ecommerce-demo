package com.ecommerce.assignment.service;

import com.ecommerce.assignment.model.Product;
import com.ecommerce.assignment.repository.CustomerRepo;
import com.ecommerce.assignment.repository.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private CustomerRepo customerRepo;

    @Autowired
    private ProductRepo productRepo;

    @Override
    public long countCustomers() {
        return customerRepo.count();
    }

    @Override
    public long countProducts() {
        return productRepo.count();
    }

    @Override
    public List<Product> getTopProducts() {
        return productRepo.findAll(PageRequest.of(0, 4, Sort.by("sales").descending())).getContent();
    }
}
