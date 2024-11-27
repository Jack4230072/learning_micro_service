package com.programmingtech.product.controller;

import com.programmingtech.product.dto.ProductRequest;
import com.programmingtech.product.dto.ProductResponse;
import com.programmingtech.product.repository.ProductService;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createProduct(@RequestBody ProductRequest productRequest) {
        productService.createProduct(productRequest);
    }

    @GetMapping
    public List<ProductResponse> getAllProducts() {
       return productService.getAllProducts();
    }
}
