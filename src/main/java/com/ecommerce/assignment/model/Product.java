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
    private double price;
    private int quantity;
    private String image;


    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @Enumerated(EnumType.STRING)
    private EStockState stockState;
}