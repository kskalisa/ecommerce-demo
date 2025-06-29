package com.ecommerce.assignment.service;

import com.ecommerce.assignment.model.Customer;
import com.ecommerce.assignment.repository.ICustomerRepo;
import lombok.RequiredArgsConstructor;
import org.hibernate.ObjectNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerService implements ICustomerService {

    private final ICustomerRepo customerRepo;


    @Override
    public Customer registerCustomer(Customer theCustomer) {
        return customerRepo.save(theCustomer);
    }

    @Override
    public Customer updateCustomer(Customer theCustomer) {
        Customer found = findCustomerByIdAndState(theCustomer.getId(), Boolean.TRUE);
        if (Objects.nonNull(found)) {
            found.setFirstName(theCustomer.getFirstName());
            found.setLastName(theCustomer.getLastName());
            found.setEmail(theCustomer.getEmail());
            found.setPhoneNumber(theCustomer.getPhoneNumber());
            return customerRepo.save(found);
        }
        throw new ObjectNotFoundException(Customer.class, "Customer not found");
    }

    @Override
    public Customer deleteCustomer(Customer theCustomer) {
        Customer found = findCustomerByIdAndState(theCustomer.getId(), Boolean.TRUE);
        if (Objects.nonNull(found)) {
            found.setActive(Boolean.FALSE);
            return customerRepo.save(found);
        }
       throw new ObjectNotFoundException(Customer.class, "Customer not found");
    }

    @Override
    public Customer findCustomerByIdAndState(UUID id, Boolean state) {
        Customer theCustomer = customerRepo.findCustomerByIdAndActive(id, state)
                .orElseThrow(() -> new ObjectNotFoundException(Customer.class, "Customer not found"));
        return theCustomer;
    }

    @Override
    public Customer findCustomerByEmailAndState(String email, Boolean state) {
        Customer theCustomer = customerRepo.findByEmailAndActive(email, state)
                .orElseThrow(() -> new ObjectNotFoundException(Customer.class, "Customer not found"));
        return theCustomer;
    }

    @Override
    public List<Customer> findCustomersByState(Boolean state) {
        return customerRepo.findAllByActive(state);
    }
}
