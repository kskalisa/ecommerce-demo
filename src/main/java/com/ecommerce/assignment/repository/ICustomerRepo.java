package com.ecommerce.assignment.repository;

import com.ecommerce.assignment.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ICustomerRepo extends JpaRepository<Customer, UUID> {

    @Query(value = "SELECT c FROM Customer c WHERE c.id = :id AND c.active = :active")
    Optional<Customer> findCustomerByIdWithNamedQuery(@Param("id") UUID id, @Param("active") Boolean active);
    Optional<Customer> findCustomerByIdAndActive(@Param("id") UUID id, Boolean active);
    Optional<Customer> findByEmailAndActive(String email, Boolean active);
    List<Customer> findAllByActive(Boolean active);
}
