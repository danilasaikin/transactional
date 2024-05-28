package com.transactional.controller;

import com.transactional.entity.Customer;
import com.transactional.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @GetMapping("/{customerId}")
    public Customer getCustomer(@PathVariable Long customerId) {
        return customerService.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
    }

    @PutMapping("/{customerId}/balance")
    public void updateBalance(@PathVariable Long customerId, @RequestParam BigDecimal amount) {
        customerService.updateBalance(customerId, amount);
    }
}
