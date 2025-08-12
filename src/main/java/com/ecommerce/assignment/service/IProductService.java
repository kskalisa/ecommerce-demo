package com.ecommerce.assignment.service;

import com.ecommerce.assignment.model.Product;
import com.ecommerce.assignment.util.EStockState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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


    // NEW: Enhanced search and filtering
    Page<Product> findProductsWithFilters(
            String search,
            UUID categoryId,
            EStockState stockState,
            Double minPrice,
            Double maxPrice,
            Pageable pageable
    );

    // NEW: Search suggestions for autocomplete
    List<String> getProductNameSuggestions(String query);

    // NEW: Bulk operations
    void bulkDeleteProducts(List<UUID> productIds);
    void bulkUpdateStockState(List<UUID> productIds, EStockState stockState);

    // NEW: Statistics and reporting
    long getTotalProductCount();
    long getProductCountByState(EStockState stockState);
    long getLowStockProductCount();
    long getLowStockProductCount(int threshold); // products with quantity < threshold

    // NEW: Export functionality
    byte[] exportProductsToCSV(String search, UUID categoryId, EStockState stockState);

    // NEW: Quick update for inline editing
    boolean quickUpdateProduct(UUID productId, String field, String value);

    // NEW: Advanced queries
    List<Product> findTopSellingProducts(int limit);
    List<Product> findLowStockProducts(int threshold);
    List<Product> findProductsByPriceRange(double minPrice, double maxPrice);

    // NEW: Category-based operations
    List<Product> findProductsByCategory(UUID categoryId);
    long getProductCountByCategory(UUID categoryId);
}
