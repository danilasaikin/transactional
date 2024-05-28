package com.transactional.controller;

import com.transactional.entity.Product;
import com.transactional.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public List<Product> getAllProducts() {
        return productService.findAll();
    }

    @PutMapping("/{productId}/quantity")
    public void updateProductQuantity(@PathVariable Long productId, @RequestParam Integer quantity) {
        productService.updateProductQuantity(productId, quantity);
    }
}
