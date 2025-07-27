package com.ecommerce.assignment.service;

import com.ecommerce.assignment.dto.UserRegistDTO;
import com.ecommerce.assignment.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Optional;

public interface UserService extends UserDetailsService {
    User save(UserRegistDTO  registDTO);
    boolean isEmailAlreadyRegistered(String email);
    User findByUsername(String username);



}
