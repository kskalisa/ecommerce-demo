package com.ecommerce.assignment.model;

import com.ecommerce.assignment.base.AbstractBaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
public class Category extends AbstractBaseEntity {
    private String name;
    private String description;
    @OneToMany(mappedBy = "category")
    private List<Product> products;

}
