package com.ecommerce.assignment.repository;

import com.ecommerce.assignment.model.Product;
import com.ecommerce.assignment.util.EStockState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IProductRepo extends JpaRepository<Product, UUID> {
    Optional<Product> findByIdAndActive(UUID id, Boolean active);
    List<Product> findAllByActive(Boolean active);
    List<Product> findAllByStockStateAndActive(EStockState stock, Boolean active);
    List<Product> findAllByStockStateInAndActive(List<EStockState> stock, Boolean active);
}
