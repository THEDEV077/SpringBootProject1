package com.myfullstackproject.springbootproject1.controller;

import com.myfullstackproject.springbootproject1.exception.ResourceNotFoundException;
import com.myfullstackproject.springbootproject1.model.Product;
import com.myfullstackproject.springbootproject1.repository.ProductRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/produits")
@CrossOrigin(origins = "http://localhost:5173") // port Vite par défaut
@Tag(name = "Products", description = "Public product APIs for browsing products")
public class ProductController {

    private final ProductRepository productRepository;

    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Operation(summary = "Get all products", description = "Retrieve a list of all available products")
    @GetMapping
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Operation(summary = "Get product by ID", description = "Retrieve detailed information about a specific product")
    @GetMapping("/{id}")
    public Product getProductById(@PathVariable Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produit introuvable avec l'ID: " + id));
    }
}
