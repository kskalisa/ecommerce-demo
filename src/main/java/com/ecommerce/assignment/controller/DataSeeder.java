package com.ecommerce.assignment.controller;

import com.ecommerce.assignment.model.Customer;
import com.ecommerce.assignment.model.Product;
import com.ecommerce.assignment.repository.CustomerRepo;
import com.ecommerce.assignment.repository.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
//
//@Component
//public class DataSeeder implements CommandLineRunner {
//
//    @Autowired
//    ProductRepo productRepo;
//
//    @Autowired
//    CustomerRepo customerRepo;
//
//    @Override
//    public void run(String... args) {
//        for (int i = 1; i <= 10; i++)
//            customerRepo.save(new Customer("Customer " + i, "email" + i + "@mail.com"));
//
//        productRepo.saveAll(List.of(
//                new Product("Home Decor Range", 82, 40),
//                new Product( "Disney Princess Dress", 90, 71),
//                new Product( "Bathroom Essentials", 75, 16),
//                new Product("Apple Smartwatch", 40, 29)
//        ));
//    }
//}
