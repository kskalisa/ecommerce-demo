package com.ecommerce.assignment.repository;

import com.ecommerce.assignment.model.Customer;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepo extends JpaRepository<Customer, UUID> {
}
