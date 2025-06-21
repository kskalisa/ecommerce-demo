package com.ecommerce.assignment.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private int popularity;
    private int sales;

    public Product(String name, int popularity, int sales) {
        this.name = name;
        this.popularity = popularity;
        this.sales = sales;
    }

    public Product() {
    }
}






