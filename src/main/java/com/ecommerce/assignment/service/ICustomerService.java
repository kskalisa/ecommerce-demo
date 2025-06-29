package com.ecommerce.assignment.service;

import com.ecommerce.assignment.model.Customer;

import java.util.List;
import java.util.UUID;

public interface ICustomerService {
    Customer registerCustomer(Customer theCustomer);
    Customer updateCustomer(Customer theCustomer);
    Customer deleteCustomer(Customer theCustomer);
    Customer findCustomerByIdAndState(UUID id , Boolean state);
    Customer findCustomerByEmailAndState(String email , Boolean state);
    List<Customer> findCustomersByState(Boolean state);
}
