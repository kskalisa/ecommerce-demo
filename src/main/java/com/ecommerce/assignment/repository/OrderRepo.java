package com.ecommerce.assignment.repository;

import com.ecommerce.assignment.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderRepo extends JpaRepository<Order, UUID> {
}
