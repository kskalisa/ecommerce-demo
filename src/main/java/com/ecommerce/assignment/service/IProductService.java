package com.ecommerce.assignment.service;

import com.ecommerce.assignment.model.Product;
import com.ecommerce.assignment.util.EStockState;

import java.util.List;
import java.util.UUID;

public interface IProductService {
    Product createProduct(Product theProduct);
    Product updateProduct(Product theProduct);
    Product deleteProduct(Product theProduct);
    Product findProductByIdAndState(UUID id, Boolean active);
    List<Product> findProductByState(Boolean active);
    List<Product> findProductByStockStateAndState(EStockState state, Boolean active);
    List<Product> findProductsByStockStatesAndState(List<EStockState> stockStates , Boolean active);
}
