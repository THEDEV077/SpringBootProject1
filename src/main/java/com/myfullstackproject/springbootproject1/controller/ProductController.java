package com.myfullstackproject.springbootproject1.controller;

import com.myfullstackproject.springbootproject1.model.Product;
import com.myfullstackproject.springbootproject1.model.Rating;
import com.myfullstackproject.springbootproject1.model.Utilisateur;
import com.myfullstackproject.springbootproject1.repository.ProductRepository;
import com.myfullstackproject.springbootproject1.repository.RatingRepository;
import com.myfullstackproject.springbootproject1.repository.UtilisateurRepository;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/produits")
@CrossOrigin(origins = "http://localhost:5173") // port Vite par défaut
public class ProductController {

    private final ProductRepository productRepository;
    private final RatingRepository ratingRepository;
    private final UtilisateurRepository utilisateurRepository;

    private static final Long DEMO_USER_ID = 1L; // Utilisateur test ID=1

    public ProductController(ProductRepository productRepository,
                           RatingRepository ratingRepository,
                           UtilisateurRepository utilisateurRepository) {
        this.productRepository = productRepository;
        this.ratingRepository = ratingRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    // 1) Liste de tous les produits
    @GetMapping
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // 2) Détail d’un produit par id
    @GetMapping("/{id}")
    public Product getProductById(@PathVariable Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produit introuvable"));
    }

    // 3) Get all reviews for a product
    @GetMapping("/{productId}/reviews")
    public List<Rating> getProductReviews(@PathVariable Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Produit introuvable"));
        return ratingRepository.findByProduct(product);
    }

    // 4) Add new review
    @PostMapping("/{productId}/reviews")
    public Rating addReview(@PathVariable Long productId, @RequestBody Rating review) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Produit introuvable"));

        Utilisateur user = utilisateurRepository.findById(DEMO_USER_ID)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        // Set the product and user for the review
        review.setProduct(product);
        review.setUtilisateur(user);
        review.setCreatedAt(LocalDateTime.now());

        return ratingRepository.save(review);
    }
}
