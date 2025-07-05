package com.ecommerce.assignment.service;

import com.ecommerce.assignment.model.Customer;
import com.ecommerce.assignment.model.Product;
import com.ecommerce.assignment.repository.IProductRepo;
import com.ecommerce.assignment.util.EStockState;
import lombok.RequiredArgsConstructor;
import org.hibernate.ObjectNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

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
}
