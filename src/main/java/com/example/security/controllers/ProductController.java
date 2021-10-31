package com.example.security.controllers;

import com.example.security.services.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(path = "/api/v1/products")
public class ProductController {
    private final ProductService productService;
    private static final Map<String, Object> RESPONSE = new LinkedHashMap<>();

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('product:read')")
    public Object getProducts() {
        return productService.getAllProducts();
    }

    @PostMapping(path = "/add")
    @PreAuthorize("hasAuthority('product:create')")
    public ResponseEntity<Map<String, Object>> addNewProduct(@RequestBody Map<String, Object> request) {
        String message = productService.addProduct(request);
        log.info("Message: {}", message);
        if (message.equals("Success")) {
            RESPONSE.put("status", "Success");
            RESPONSE.put("message", "User is added successfully");
        } else {
            RESPONSE.put("status", "Failure");
            RESPONSE.put("message", message);
        }
        return new ResponseEntity<>(RESPONSE, HttpStatus.OK);
    }


    @DeleteMapping(path = "/delete/{productId}")
    @PreAuthorize("hasAuthority('product:delete')")
    public ResponseEntity<Map<String, Object>> deleteBook(@PathVariable("productId") Long productId) {
        String message = productService.deleteProduct(productId);
        log.info("Message: {}", message);
        if (message.equals("Success")) {
            RESPONSE.put("status", "Success");
            RESPONSE.put("message", "Product is deleted");
        } else {
            RESPONSE.put("status", "Failure");
            RESPONSE.put("message", message);
        }
        return new ResponseEntity<>(RESPONSE, HttpStatus.OK);
    }

    @PutMapping(path = "/update/{productId}")
    @PreAuthorize("hasAuthority('product:update')")
    public ResponseEntity<Map<String, Object>> updateBook(@RequestBody Map<String, Object> request) {
        log.info("Your request: {}", request);
        String message = productService.updateProduct(request);
        log.info("Message: {}", message);
        if (message.equals("Success")) {
            RESPONSE.put("status", "Success");
            RESPONSE.put("message", "Product is updated");
        } else {
            RESPONSE.put("status", "Failure");
            RESPONSE.put("message", message);
        }
        return new ResponseEntity<>(RESPONSE, HttpStatus.OK);
    }
}
