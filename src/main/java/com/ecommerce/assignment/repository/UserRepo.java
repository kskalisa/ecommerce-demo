package com.ecommerce.assignment.repository;

import com.ecommerce.assignment.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepo extends JpaRepository<User, UUID> {
   Optional<User> findByEmail(String email);
   Optional<User> findByUsername(String username);
}
