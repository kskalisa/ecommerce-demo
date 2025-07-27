package com.ecommerce.assignment.config;

import com.ecommerce.assignment.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserService userDetailsService;

    public SecurityConfig(UserService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception{
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http
                .csrf( csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> {

                         auth.requestMatchers("/admin/**","/order/**","/dashboard",
                                         "/products/**","/categories/**").hasRole("ADMIN")
                            .requestMatchers( "/checkout").authenticated()
                            .requestMatchers(
                                    "/auth/signup", "/product-details/**","/auth/**",
                                    "/cart","/clear","/remove-cart","/", "/add-cart", "/","/images/**", "/products/**",
                                    "/css/**", "/js/**", "/img/**").permitAll()
                            .anyRequest().authenticated();
                })

                .formLogin(form -> {
                    form.loginPage("/auth/signin")
                            .loginProcessingUrl("/login")
                            .usernameParameter("email")
                            .defaultSuccessUrl("/auth/login-success", true)
                            .failureUrl("/auth/login-fail")
                            .permitAll();
                })

                .logout(logout -> {
                   logout
                           .logoutUrl("/auth/logout")
                           .logoutSuccessUrl("/auth/signin?logout")
                            .permitAll();
                });
        return http.build();

    }


}
