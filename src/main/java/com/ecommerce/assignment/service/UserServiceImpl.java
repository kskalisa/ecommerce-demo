package com.ecommerce.assignment.service;

import com.ecommerce.assignment.dto.UserRegistDTO;
import com.ecommerce.assignment.model.User;
import com.ecommerce.assignment.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.User.UserBuilder;

import java.util.Objects;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;



    @Override
    public User save(UserRegistDTO registDTO) {
        User user = new User();
        user.setUsername(registDTO.getUsername());
        user.setEmail(registDTO.getEmail());
        user.setPassword(passwordEncoder.encode(registDTO.getPassword()));
        user.setRole("ROLE_USER");
        return userRepo.save(user);
    }

    @Override
    public boolean isEmailAlreadyRegistered(String email) {
        return userRepo.findByEmail(email).isPresent();
    }

    @Override
    public User findByUsername(String username) {
        Optional<User> optionalUser = userRepo.findByUsername(username);
        return optionalUser.orElse(null);
    }

    @Override
    public User findByEmail(String email) {
        Optional<User> optionalUser = userRepo.findByEmail(email);
        return optionalUser.orElse(null);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        System.out.println("=== UserService loadUserByUsername called with: " + email);

        Optional<User> user = userRepo.findByEmail(email);
        if (user.isEmpty()){
            throw new UsernameNotFoundException("User not found with email : " + email);
        }
       UserBuilder builder = org.springframework.security.core.userdetails.User.withUsername(email);
        builder.password(user.get().getPassword());
        builder.roles(user.get().getRole().replace("ROLE_", ""));

        return builder.build();
    }
}
