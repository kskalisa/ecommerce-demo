package com.ecommerce.assignment.model;

import com.ecommerce.assignment.base.AbstractBaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Customer extends AbstractBaseEntity {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;

}
