package com.ecommerce.assignment.controller;

import com.ecommerce.assignment.dto.CustomerDTO;
import com.ecommerce.assignment.model.User;
import com.ecommerce.assignment.model.Order;
import com.ecommerce.assignment.repository.UserRepo;
import com.ecommerce.assignment.repository.OrderRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class CustomerController {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private OrderRepo orderRepo;

    @GetMapping("/customers")
    public String customers(Model model, 
                          @RequestParam(required = false) String search,
                          @RequestParam(required = false) String filter) {
        
        List<User> users = userRepo.findAll().stream()
                .filter(user -> "ROLE_USER".equals(user.getRole()))
                .collect(Collectors.toList());

        // Apply search filter
        if (search != null && !search.trim().isEmpty()) {
            String searchLower = search.toLowerCase();
            users = users.stream()
                    .filter(user -> user.getUsername().toLowerCase().contains(searchLower) ||
                                   user.getEmail().toLowerCase().contains(searchLower))
                    .collect(Collectors.toList());
        }

        // Apply additional filters
        if (filter != null && !filter.trim().isEmpty()) {
            switch (filter.toLowerCase()) {
                case "new":
                    users = users.stream()
                            .filter(user -> {
                                List<Order> userOrders = orderRepo.findAll().stream()
                                        .filter(order -> order.getUser() != null && 
                                                       order.getUser().getId().equals(user.getId()))
                                        .collect(Collectors.toList());
                                return userOrders.isEmpty();
                            })
                            .collect(Collectors.toList());
                    break;
                case "active":
                    users = users.stream()
                            .filter(user -> {
                                List<Order> userOrders = orderRepo.findAll().stream()
                                        .filter(order -> order.getUser() != null && 
                                                       order.getUser().getId().equals(user.getId()))
                                        .collect(Collectors.toList());
                                return !userOrders.isEmpty();
                            })
                            .collect(Collectors.toList());
                    break;
            }
        }

        List<CustomerDTO> customerDTOs = users.stream()
                .map(user -> {
                    List<Order> userOrders = orderRepo.findAll().stream()
                            .filter(order -> order.getUser() != null && 
                                           order.getUser().getId().equals(user.getId()))
                            .collect(Collectors.toList());
                    
                    int orderCount = userOrders.size();
                    double totalSpent = userOrders.stream()
                            .mapToDouble(Order::getTotalAmount)
                            .sum();
                    
                    CustomerDTO dto = new CustomerDTO();
                    dto.setId(user.getId());
                    dto.setUsername(user.getUsername());
                    dto.setEmail(user.getEmail());
                    dto.setOrderCount(orderCount);
                    dto.setTotalSpent(totalSpent);
                    return dto;
                })
                .collect(Collectors.toList());

        model.addAttribute("customers", customerDTOs);
        model.addAttribute("search", search);
        model.addAttribute("filter", filter);
        return "customers";
    }

    @GetMapping("/customers/view/{id}")
    public String viewCustomer(@PathVariable UUID id, Model model) {
        User user = userRepo.findById(id).orElse(null);
        if (user == null || !"ROLE_USER".equals(user.getRole())) {
            return "redirect:/admin/customers";
        }

        List<Order> userOrders = orderRepo.findAll().stream()
                .filter(order -> order.getUser() != null && 
                               order.getUser().getId().equals(user.getId()))
                .collect(Collectors.toList());
        
        CustomerDTO dto = new CustomerDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setOrderCount(userOrders.size());
        dto.setTotalSpent(userOrders.stream()
                .mapToDouble(Order::getTotalAmount)
                .sum());
        
        model.addAttribute("customer", dto);
        model.addAttribute("orders", userOrders);
        return "customer-details";
    }

    @GetMapping("/customers/edit/{id}")
    public String editCustomerForm(@PathVariable UUID id, Model model) {
        User user = userRepo.findById(id).orElse(null);
        if (user == null || !"ROLE_USER".equals(user.getRole())) {
            return "redirect:/admin/customers";
        }
        
        model.addAttribute("customer", user);
        return "customer-edit";
    }

    @PostMapping("/customers/edit/{id}")
    public String updateCustomer(@PathVariable UUID id, 
                               @ModelAttribute User userDetails) {
        User user = userRepo.findById(id).orElse(null);
        if (user == null || !"ROLE_USER".equals(user.getRole())) {
            return "redirect:/admin/customers";
        }
        
        user.setUsername(userDetails.getUsername());
        user.setEmail(userDetails.getEmail());
        userRepo.save(user);
        
        return "redirect:/admin/customers";
    }

    @GetMapping("/customers/api/{id}")
    @ResponseBody
    public ResponseEntity<CustomerDTO> getCustomerApi(@PathVariable UUID id) {
        User user = userRepo.findById(id).orElse(null);
        if (user == null || !"ROLE_USER".equals(user.getRole())) {
            return ResponseEntity.notFound().build();
        }

        List<Order> userOrders = orderRepo.findAll().stream()
                .filter(order -> order.getUser() != null && 
                               order.getUser().getId().equals(user.getId()))
                .collect(Collectors.toList());
        
        CustomerDTO dto = new CustomerDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setOrderCount(userOrders.size());
        dto.setTotalSpent(userOrders.stream()
                .mapToDouble(Order::getTotalAmount)
                .sum());
        
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/customers/api/{id}")
    @ResponseBody
    public ResponseEntity<CustomerDTO> updateCustomerApi(@PathVariable UUID id, 
                                                      @RequestBody CustomerDTO customerDTO) {
        User user = userRepo.findById(id).orElse(null);
        if (user == null || !"ROLE_USER".equals(user.getRole())) {
            return ResponseEntity.notFound().build();
        }
        
        user.setUsername(customerDTO.getUsername());
        user.setEmail(customerDTO.getEmail());
        userRepo.save(user);
        
        // Return updated customer
        List<Order> userOrders = orderRepo.findAll().stream()
                .filter(order -> order.getUser() != null && 
                               order.getUser().getId().equals(user.getId()))
                .collect(Collectors.toList());
        
        CustomerDTO updatedDto = new CustomerDTO();
        updatedDto.setId(user.getId());
        updatedDto.setUsername(user.getUsername());
        updatedDto.setEmail(user.getEmail());
        updatedDto.setOrderCount(userOrders.size());
        updatedDto.setTotalSpent(userOrders.stream()
                .mapToDouble(Order::getTotalAmount)
                .sum());
        
        return ResponseEntity.ok(updatedDto);
    }

    @PostMapping("/customers/delete/{id}")
    public String deleteCustomer(@PathVariable UUID id) {
        User user = userRepo.findById(id).orElse(null);
        if (user != null && "ROLE_USER".equals(user.getRole())) {
            // First delete associated orders
            List<Order> userOrders = orderRepo.findAll().stream()
                    .filter(order -> order.getUser() != null && 
                                   order.getUser().getId().equals(user.getId()))
                    .collect(Collectors.toList());
            orderRepo.deleteAll(userOrders);
            
            // Then delete the user
            userRepo.delete(user);
        }
        return "redirect:/admin/customers";
    }
}
