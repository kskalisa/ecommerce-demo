package com.ecommerce.assignment.model;

import com.ecommerce.assignment.base.AbstractBaseEntity;
import com.ecommerce.assignment.util.EOrderStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class Order extends AbstractBaseEntity {
    private LocalDateTime orderDate;
    @Enumerated(EnumType.STRING)
    private EOrderStatus status;
    private String shippingAddress;
    private String paymentMethod;
    private String phoneNumber;

    @ManyToOne
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> items;

    public double getTotalAmount() {
        if (items == null) return 0.0;
        return items.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
    }

}
