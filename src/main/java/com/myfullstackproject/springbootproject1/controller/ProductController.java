package com.myfullstackproject.springbootproject1.controller;

import com.myfullstackproject.springbootproject1.model.Product;
import com.myfullstackproject.springbootproject1.repository.ProductRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/produits")
@CrossOrigin(origins = "http://localhost:5173") // port Vite par défaut
public class ProductController {

    private final ProductRepository productRepository;

    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // 1) Liste de tous les produits
    @GetMapping
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // 2) Détail d'un produit par id
    @GetMapping("/{id}")
    public Product getProductById(@PathVariable Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produit introuvable"));
    }

    // 3) Recherche par mot-clé (search)
    @GetMapping("/search")
    public List<Product> searchProducts(@RequestParam String keyword) {
        return productRepository.findByTitleContainingIgnoreCase(keyword);
    }

    // 4) Filtrage avancé avec plusieurs critères
    @GetMapping("/filter")
    public List<Product> filterProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Double minRating) {
        return productRepository.searchProducts(keyword, categoryId, minPrice, maxPrice, minRating);
    }

    // 5) Recherche par catégorie
    @GetMapping("/category/{categoryId}")
    public List<Product> getProductsByCategory(@PathVariable Long categoryId) {
        return productRepository.findByCategorie_Id(categoryId);
    }

    // 6) Recherche par ASIN
    @GetMapping("/asin/{asin}")
    public Product getProductByAsin(@PathVariable String asin) {
        return productRepository.findByAsin(asin)
                .orElseThrow(() -> new RuntimeException("Produit introuvable"));
    }

    // 7) Recherche par plage de prix
    @GetMapping("/price-range")
    public List<Product> getProductsByPriceRange(
            @RequestParam Double minPrice,
            @RequestParam Double maxPrice) {
        return productRepository.findByPriceBetween(minPrice, maxPrice);
    }

    // 8) Recherche par note minimale
    @GetMapping("/rating/{minRating}")
    public List<Product> getProductsByRating(@PathVariable Double minRating) {
        return productRepository.findByRatingGreaterThanEqual(minRating);
    }
}
