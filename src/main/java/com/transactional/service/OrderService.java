package com.transactional.service;

import com.transactional.entity.Customer;
import com.transactional.entity.Order;
import com.transactional.entity.Product;
import com.transactional.repository.CustomerRepository;
import com.transactional.repository.OrderRepository;
import com.transactional.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Transactional
    public Order placeOrder(Long customerId, List<Long> productIds) throws Exception {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new Exception("Customer not found"));

        List<Product> products = productRepository.findAllById(productIds);

        BigDecimal totalAmount = products.stream()
                .map(Product::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (customer.getBalance().compareTo(totalAmount) < 0) {
            throw new Exception("Insufficient balance");
        }

        Order order = new Order();
        order.setCustomer(customer);
        order.setProducts(products);
        order.setTotalAmount(totalAmount);

        customer.setBalance(customer.getBalance().subtract(totalAmount));
        customerRepository.save(customer);

        for (Product product : products) {
            if (product.getQuantity() < 1) {
                throw new Exception("Product out of stock: " + product.getProductName());
            }
            product.setQuantity(product.getQuantity() - 1);
            productRepository.save(product);
        }

        return orderRepository.save(order);
    }
}


