package com.ecommerce.assignment.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class DashboardController {
    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }

    @GetMapping("/orders")
    public String orders() {
        return "orders"; // orders.html
    }

    @GetMapping("/customers")
    public String customers(){
        return "customers";
    }

    @GetMapping("/product-dashboard")
    public String products(){
        return "products";
    }
}