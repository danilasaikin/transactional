package com.transactional.controller;

import com.transactional.entity.Order;
import com.transactional.repository.OrderRepository;
import com.transactional.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class OrderController {

    @Autowired
    private OrderService orderService;


    @PostMapping("/orders")
    public Order placeOrder(@RequestParam Long customerId, @RequestParam List<Long> productIds) throws Exception {
        return orderService.placeOrder(customerId, productIds);
    }

}
