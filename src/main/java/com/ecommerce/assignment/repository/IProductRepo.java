package com.ecommerce.assignment.repository;

import com.ecommerce.assignment.model.Product;
import com.ecommerce.assignment.util.EStockState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    // NEW: Enhanced search and filtering support
    Page<Product> findAll(Specification<Product> spec, Pageable pageable);
    List<Product> findAll(Specification<Product> spec);

    // NEW: Search suggestions for autocomplete
    @Query("SELECT DISTINCT p.productName FROM Product p WHERE p.active = true AND LOWER(p.productName) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<String> findDistinctProductNamesByProductNameContainingIgnoreCase(@Param("query") String query);

    // NEW: Statistics queries
    long countByActive(Boolean active);
    long countByActiveAndStockState(Boolean active, EStockState stockState);
    long countByActiveAndQuantityLessThan(Boolean active, Integer threshold);
    long countByActiveAndCategoryId(Boolean active, UUID categoryId);

    // NEW: Advanced product queries
    @Query("SELECT p FROM Product p WHERE p.active = :active ORDER BY p.createdAt DESC")
    List<Product> findTopProductsByActive(@Param("active") Boolean active, Pageable pageable);

    default List<Product> findTopProductsByActive(Boolean active, int limit) {
        return findTopProductsByActive(active, Pageable.ofSize(limit));
    }

    // NEW: Low stock products
    List<Product> findByActiveAndQuantityLessThanOrderByQuantityAsc(Boolean active, Integer threshold);

    // NEW: Price range queries
    List<Product> findByActiveAndPriceBetweenOrderByPriceAsc(Boolean active, double minPrice, double maxPrice);

    // NEW: Category-based queries
    List<Product> findByActiveAndCategoryIdOrderByCreatedAtDesc(Boolean active, UUID categoryId);

    // NEW: Advanced search queries
    @Query("SELECT p FROM Product p WHERE p.active = true AND " +
            "(LOWER(p.productName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(p.description) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<Product> findBySearchTerm(@Param("search") String search);

    @Query("SELECT p FROM Product p WHERE p.active = true AND " +
            "(LOWER(p.productName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(p.description) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Product> findBySearchTerm(@Param("search") String search, Pageable pageable);

    // NEW: Complex filtering query
    @Query("SELECT p FROM Product p WHERE p.active = true " +
            "AND (:categoryId IS NULL OR p.category.id = :categoryId) " +
            "AND (:stockState IS NULL OR p.stockState = :stockState) " +
            "AND (:minPrice IS NULL OR p.price >= :minPrice) " +
            "AND (:maxPrice IS NULL OR p.price <= :maxPrice) " +
            "AND (:search IS NULL OR LOWER(p.productName) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "     OR LOWER(p.description) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Product> findProductsWithFilters(@Param("search") String search,
                                          @Param("categoryId") UUID categoryId,
                                          @Param("stockState") EStockState stockState,
                                          @Param("minPrice") double minPrice,
                                          @Param("maxPrice") double maxPrice,
                                          Pageable pageable);

    // NEW: Inventory management queries
    @Query("SELECT p FROM Product p WHERE p.active = true AND p.quantity <= :threshold ORDER BY p.quantity ASC")
    List<Product> findLowStockProducts(@Param("threshold") Integer threshold);

    @Query("SELECT p FROM Product p WHERE p.active = true AND p.stockState = 'OUT_OF_STOCK'")
    List<Product> findOutOfStockProducts();

    @Query("SELECT p FROM Product p WHERE p.active = true AND p.stockState = 'DAMAGED'")
    List<Product> findDamagedProducts();

    // NEW: Product analytics queries
    @Query("SELECT p.category.name, COUNT(p) FROM Product p WHERE p.active = true GROUP BY p.category.name")
    List<Object[]> getProductCountByCategory();

    @Query("SELECT p.stockState, COUNT(p) FROM Product p WHERE p.active = true GROUP BY p.stockState")
    List<Object[]> getProductCountByStockState();

    @Query("SELECT AVG(p.price) FROM Product p WHERE p.active = true")
    double getAverageProductPrice();

    @Query("SELECT SUM(p.quantity * p.price) FROM Product p WHERE p.active = true")
    double getTotalInventoryValue();

    // NEW: Recent products
    @Query("SELECT p FROM Product p WHERE p.active = true AND p.createdAt >= :date ORDER BY p.createdAt DESC")
    List<Product> findRecentProducts(@Param("date") java.time.LocalDateTime date);

}
