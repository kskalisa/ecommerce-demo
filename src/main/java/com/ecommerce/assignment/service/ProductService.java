package com.ecommerce.assignment.service;

import com.ecommerce.assignment.model.Customer;
import com.ecommerce.assignment.model.Product;
import com.ecommerce.assignment.repository.IProductRepo;
import com.ecommerce.assignment.util.EStockState;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.hibernate.ObjectNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ProductService implements IProductService {

    private final IProductRepo productRepo;

    @Override
    public Product createProduct(Product theProduct) {
        return productRepo.save(theProduct);
    }

    @Override
    public Product updateProduct(Product theProduct) {
        Product found = findProductByIdAndState(theProduct.getId(), Boolean.TRUE);

        if (Objects.nonNull(found)) {
            found.setProductName(theProduct.getProductName());
            found.setDescription(theProduct.getDescription());
            found.setPrice(theProduct.getPrice());
            found.setCategory(theProduct.getCategory());
            found.setQuantity(theProduct.getQuantity());
            found.setImage(theProduct.getImage());
            found.setStockState(theProduct.getStockState());
            return productRepo.save(found);
        }
        throw new ObjectNotFoundException(Customer.class, "Customer not found");

    }

    @Override
    public Product deleteProduct(Product theProduct) {

        Product found = findProductByIdAndState(theProduct.getId(), Boolean.TRUE);
        if (Objects.nonNull(found)) {
            found.setActive(Boolean.FALSE);
            return productRepo.save(found);
        }
        throw new ObjectNotFoundException(Customer.class, "Product  not found");
    }

    @Override
    public Product findProductByIdAndState(UUID id, Boolean active) {
        Product theProduct = productRepo.findByIdAndActive(id, active)
                .orElseThrow(() -> new ObjectNotFoundException(Product.class, "Product not Found"));

        return theProduct;
    }

    @Override
    public List<Product> findProductByState(Boolean active) {
        return productRepo.findAllByActive(active);
    }

    @Override
    public List<Product> findProductByStockStateAndState(EStockState state, Boolean active) {
        return productRepo.findAllByStockStateAndActive(state, active);
    }

    @Override
    public List<Product> findProductsByStockStatesAndState(List<EStockState> stockStates, Boolean active) {
        return productRepo.findAllByStockStateInAndActive(stockStates, active);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Product> findProductsWithFilters(String search, UUID categoryId, EStockState stockState, Double minPrice, Double maxPrice, Pageable pageable) {
        Specification<Product> spec = Specification.where(null);

        // Only active products
        spec = spec.and((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("active"), true));

        // Search in product name and description
        if (search != null && !search.trim().isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) -> {
                String searchPattern = "%" + search.toLowerCase() + "%";
                Predicate namePredicate = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("productName")), searchPattern);
                Predicate descPredicate = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("description")), searchPattern);
                return criteriaBuilder.or(namePredicate, descPredicate);
            });
        }

        // Filter by category
        if (categoryId != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("category").get("id"), categoryId));
        }

        // Filter by stock state
        if (stockState != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("stockState"), stockState));
        }

        // Filter by price range
        if (minPrice != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice));
        }

        if (maxPrice != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice));
        }

        return productRepo.findAll(spec, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getProductNameSuggestions(String query) {
        return productRepo.findDistinctProductNamesByProductNameContainingIgnoreCase(query)
                .stream()
                .limit(10)
                .collect(Collectors.toList());
    }

    @Override
    public void bulkDeleteProducts(List<UUID> productIds) {
        List<Product> products = productRepo.findAllById(productIds);
        products.forEach(product -> {
            product.setActive(false);
            product.setUpdatedAt(LocalDateTime.now());
        });
        productRepo.saveAll(products);

    }

    @Override
    public void bulkUpdateStockState(List<UUID> productIds, EStockState stockState) {
        List<Product> products = productRepo.findAllById(productIds);
        products.forEach(product -> {
            product.setStockState(stockState);
            product.setUpdatedAt(LocalDateTime.now());
        });
        productRepo.saveAll(products);

    }

    @Override
    @Transactional(readOnly = true)
    public long getTotalProductCount() {
        return productRepo.countByActive(true);
    }

    @Override
    public long getProductCountByState(EStockState stockState) {
        return productRepo.countByActiveAndStockState(true, stockState);
    }

    @Override
    @Transactional(readOnly = true)
    public long getLowStockProductCount() {
        return getLowStockProductCount(10); // Default threshold of 10
    }

    @Override
    @Transactional(readOnly = true)
    public long getLowStockProductCount(int threshold) {
        return productRepo.countByActiveAndQuantityLessThan(true, threshold);
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] exportProductsToCSV(String search, UUID categoryId, EStockState stockState) {
        try {
            // Get filtered products (without pagination for export)
            Specification<Product> spec = buildProductSpecification(search, categoryId, stockState, null, null);
            List<Product> products = productRepo.findAll(spec);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PrintWriter writer = new PrintWriter(outputStream);

            // CSV Header
            writer.println("ID,Product Name,Description,Category,Price,Quantity,Stock State,Created At");

            // CSV Data
            for (Product product : products) {
                writer.printf("%s,%s,%s,%s,%.2f,%d,%s,%s%n",
                        product.getId(),
                        escapeCSV(product.getProductName()),
                        escapeCSV(product.getDescription()),
                        product.getCategory() != null ? escapeCSV(product.getCategory().getName()) : "",
                        product.getPrice(),
                        product.getQuantity(),
                        product.getStockState(),
                        product.getCreatedAt()
                );
            }

            writer.flush();
            writer.close();

            return outputStream.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Failed to export products to CSV", e);
        }
    }

    @Override
    public boolean quickUpdateProduct(UUID productId, String field, String value) {
        try {
            Product product = productRepo.findById(productId).orElse(null);
            if (product == null || !product.isActive()) {
                return false;
            }

            switch (field) {
                case "productName":
                    if (value.trim().isEmpty()) return false;
                    product.setProductName(value.trim());
                    break;

                case "price":
                    Double price = Double.parseDouble(value);
                    if (price <= 0) return false;
                    product.setPrice(Double.valueOf(price));
                    break;

                case "quantity":
                    int quantity = Integer.parseInt(value);
                    if (quantity < 0) return false;
                    product.setQuantity(quantity);
                    break;

                default:
                    return false;
            }

            product.setUpdatedAt(LocalDateTime.now());
            productRepo.save(product);
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> findTopSellingProducts(int limit) {
        // This would typically join with order data
        // For now, returning products ordered by creation date
        return productRepo.findTopProductsByActive(true, limit);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> findLowStockProducts(int threshold) {
        return List.of();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> findProductsByPriceRange(double minPrice, double maxPrice) {
        return productRepo.findByActiveAndPriceBetweenOrderByPriceAsc(true, minPrice, maxPrice);
    }

    // NEW: Category-based operations

    @Override
    @Transactional(readOnly = true)
    public List<Product> findProductsByCategory(UUID categoryId) {
        return productRepo.findByActiveAndCategoryIdOrderByCreatedAtDesc(true, categoryId);
    }

    @Override
    @Transactional(readOnly = true)
    public long getProductCountByCategory(UUID categoryId) {
        return productRepo.countByActiveAndCategoryId(true, categoryId);
    }

    // Helper method to build specification (reusable)
    private Specification<Product> buildProductSpecification(String search, UUID categoryId,
                                                             EStockState stockState,
                                                             Double minPrice,
                                                             Double maxPrice) {
        Specification<Product> spec = Specification.where(null);

        // Only active products
        spec = spec.and((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("active"), true));

        // Search in product name and description
        if (search != null && !search.trim().isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) -> {
                String searchPattern = "%" + search.toLowerCase() + "%";
                Predicate namePredicate = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("productName")), searchPattern);
                Predicate descPredicate = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("description")), searchPattern);
                return criteriaBuilder.or(namePredicate, descPredicate);
            });
        }

        // Filter by category
        if (categoryId != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("category").get("id"), categoryId));
        }

        // Filter by stock state
        if (stockState != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("stockState"), stockState));
        }

        // Filter by price range
        if (minPrice != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice));
        }

        if (maxPrice != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice));
        }

        return spec;
    }

    // Helper method to escape CSV values
    private String escapeCSV(String value) {
        if (value == null) return "";

        // Escape quotes and wrap in quotes if contains comma, quote, or newline
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }

        return value;
    }
}
