package com.myfullstackproject.springbootproject1.controller;

import com.myfullstackproject.springbootproject1.exception.ResourceNotFoundException;
import com.myfullstackproject.springbootproject1.model.Product;
import com.myfullstackproject.springbootproject1.repository.ProductRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

    @Operation(summary = "Search products", description = "Search and filter products by various criteria")
    @GetMapping("/search")
    public List<Product> searchProducts(
            @Parameter(description = "Category ID to filter by") @RequestParam(required = false) Long categoryId,
            @Parameter(description = "Search keyword for product title") @RequestParam(required = false) String keyword,
            @Parameter(description = "Minimum price") @RequestParam(required = false) Double minPrice,
            @Parameter(description = "Maximum price") @RequestParam(required = false) Double maxPrice,
            @Parameter(description = "Minimum rating") @RequestParam(required = false) Double minRating) {
        
        return productRepository.searchProducts(categoryId, keyword, minPrice, maxPrice, minRating);
    }

    @Operation(summary = "Get products by category", description = "Retrieve all products in a specific category")
    @GetMapping("/category/{categoryId}")
    public List<Product> getProductsByCategory(@PathVariable Long categoryId) {
        return productRepository.findByCategorie_Id(categoryId);
    }

    @Operation(summary = "Get available products", description = "Retrieve products that are currently in stock")
    @GetMapping("/available")
    public List<Product> getAvailableProducts() {
        return productRepository.findByQuantityAvailableGreaterThan(0);
    }
}
