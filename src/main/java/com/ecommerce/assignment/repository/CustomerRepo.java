package com.ecommerce.assignment.repository;

import com.ecommerce.assignment.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepo extends JpaRepository<Customer, Integer> {
}
