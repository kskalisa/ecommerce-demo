package com.ecommerce.assignment.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Checkout {
    private String shippingAddress;
    private String phoneNumber;
    private String paymentMethod;
}
