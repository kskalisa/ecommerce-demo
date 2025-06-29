package com.ecommerce.assignment.controller;

import com.ecommerce.assignment.model.Customer;
import com.ecommerce.assignment.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("/customer/")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;

    @GetMapping("/search/all")
    public String getAllCustomers(Model model) {
        List<Customer> customers = customerService.findCustomersByState(Boolean.TRUE);
        model.addAttribute("customers", customers);
        return "customers";

    }

    @GetMapping("register")
    public String getCustomerRegistrationForm(Model model) {
        model.addAttribute("customer", new Customer());
        return "customer/customer-form";
    }

    @PostMapping("/register")
    public String registerCustomer(@ModelAttribute("customer") Customer theCustomer, Model model) {
        if (Objects.nonNull(theCustomer)){
            customerService.registerCustomer(theCustomer);
            model.addAttribute("message", "Data saved successfully");
        }
        else{
            model.addAttribute("error", "Data not saved successfully");
        }
        return "customer/customer-form";
    }
}
