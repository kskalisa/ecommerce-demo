package com.ecommerce.assignment.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CartSummary {

    List<CartItem> cartItems;
    private double subTotal;
    private double discount;
    private double deliveryFee;
    private double total;

    public CartSummary(List<CartItem> cartItems) {
        this.subTotal = calculateSubtotal(cartItems);
        this.discount = calculateDiscount(subTotal);
        this.deliveryFee = calculateDeliveryFee(subTotal, cartItems.size());
        this.total = subTotal - discount + deliveryFee;
    }


    private double calculateSubtotal(List<CartItem> cartItems){
        return cartItems.stream()
                .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();

    }
    private double calculateDeliveryFee( double subTotal, int item){
        if (subTotal >= 500) {
            return 0.0;
        }
        else if(item >= 5){
            return 10.0;
        }
        else{
            return 20.0;
        }
    }

    public double getSubtotal(){
        return subTotal;
    }

    private double calculateDiscount(double subTotal){
        return subTotal * 0.2;
    }


}
