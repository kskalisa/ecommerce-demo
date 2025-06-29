package com.ecommerce.assignment.model;

import com.ecommerce.assignment.base.AbstractBaseEntity;
import com.ecommerce.assignment.util.EStockState;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Product extends AbstractBaseEntity {
    private String productName;
    private String description;
    private String category;
    private double price;
    private int quantity;
    private String image;

    @Enumerated(EnumType.STRING)
    private EStockState stockState;
}