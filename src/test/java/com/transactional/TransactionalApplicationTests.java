package com.transactional;

import com.transactional.entity.Customer;
import com.transactional.entity.Order;
import com.transactional.entity.Product;
import com.transactional.repository.CustomerRepository;
import com.transactional.repository.ProductRepository;
import com.transactional.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class TransactionalApplicationTests {

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private OrderService orderService;

	@Test
	public void testCreateCustomer() {
		Customer customer = new Customer();
		customer.setUserName("testuser");
		customer.setBalance(new BigDecimal("100.00"));
		customer = customerRepository.save(customer);

		assertNotNull(customer.getId());
		assertEquals("testuser", customer.getUserName());
	}

	@Test
	public void testCreateProduct() {
		Product product = new Product();
		product.setProductName("testproduct");
		product.setPrice(new BigDecimal("10.00"));
		product.setQuantity(100);
		product = productRepository.save(product);

		assertNotNull(product.getId());
		assertEquals("testproduct", product.getProductName());
	}

	@Test
	@Transactional
	public void testPlaceOrderSuccess() throws Exception {
		Customer customer = new Customer();
		customer.setUserName("orderuser");
		customer.setBalance(new BigDecimal("100.00"));
		customer = customerRepository.save(customer);

		Product product1 = new Product();
		product1.setProductName("product1");
		product1.setPrice(new BigDecimal("20.00"));
		product1.setQuantity(50);
		product1 = productRepository.save(product1);

		Product product2 = new Product();
		product2.setProductName("product2");
		product2.setPrice(new BigDecimal("30.00"));
		product2.setQuantity(50);
		product2 = productRepository.save(product2);

		Order order = orderService.placeOrder(customer.getId(), Arrays.asList(product1.getId(), product2.getId()));

		assertNotNull(order.getId());
		assertEquals(2, order.getProducts().size());
		assertEquals(new BigDecimal("50.00"), order.getTotalAmount());

		Customer updatedCustomer = customerRepository.findById(customer.getId()).orElseThrow();
		assertEquals(new BigDecimal("50.00"), updatedCustomer.getBalance());

		Product updatedProduct1 = productRepository.findById(product1.getId()).orElseThrow();
		assertEquals(49, updatedProduct1.getQuantity());

		Product updatedProduct2 = productRepository.findById(product2.getId()).orElseThrow();
		assertEquals(49, updatedProduct2.getQuantity());
	}

	@Test
	@Transactional
	public void testPlaceOrderInsufficientBalance() {
		Customer customer = new Customer();
		customer.setUserName("orderuser2");
		customer.setBalance(new BigDecimal("10.00"));
		customer = customerRepository.save(customer);

		Product product = new Product();
		product.setProductName("product3");
		product.setPrice(new BigDecimal("20.00"));
		product.setQuantity(50);
		product = productRepository.save(product);

		Customer finalCustomer = customer;
		Product finalProduct = product;
		Exception exception = assertThrows(Exception.class, () -> {
			orderService.placeOrder(finalCustomer.getId(), Arrays.asList(finalProduct.getId()));
		});

		assertEquals("Insufficient balance", exception.getMessage());

		Customer updatedCustomer = customerRepository.findById(customer.getId()).orElseThrow();
		assertEquals(new BigDecimal("10.00"), updatedCustomer.getBalance());

		Product updatedProduct = productRepository.findById(product.getId()).orElseThrow();
		assertEquals(50, updatedProduct.getQuantity());
	}
}

