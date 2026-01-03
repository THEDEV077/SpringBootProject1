package com.myfullstackproject.springbootproject1.controller;

import com.myfullstackproject.springbootproject1.dto.ReviewRequest;
import com.myfullstackproject.springbootproject1.exception.ResourceNotFoundException;
import com.myfullstackproject.springbootproject1.exception.ValidationException;
import com.myfullstackproject.springbootproject1.model.Product;
import com.myfullstackproject.springbootproject1.model.Rating;
import com.myfullstackproject.springbootproject1.model.Utilisateur;
import com.myfullstackproject.springbootproject1.repository.ProductRepository;
import com.myfullstackproject.springbootproject1.repository.RatingRepository;
import com.myfullstackproject.springbootproject1.repository.UtilisateurRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/produits")
@CrossOrigin(origins = "http://localhost:5173")
@Tag(name = "Product Reviews", description = "APIs for product reviews and ratings")
public class RatingController {

    private final ProductRepository productRepository;
    private final RatingRepository ratingRepository;
    private final UtilisateurRepository utilisateurRepository;

    // TODO: Remove hardcoded demo user ID and implement proper authentication
    // This is temporary for testing without authentication
    private static final Long DEMO_USER_ID = 1L; // Utilisateur test ID=1

    public RatingController(ProductRepository productRepository,
                           RatingRepository ratingRepository,
                           UtilisateurRepository utilisateurRepository) {
        this.productRepository = productRepository;
        this.ratingRepository = ratingRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    @Operation(summary = "Get all reviews for a product")
    @GetMapping("/{productId}/reviews")
    public List<Rating> getProductReviews(@PathVariable Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Produit introuvable"));
        return ratingRepository.findByProduct(product);
    }

    @Operation(summary = "Add a new review for a product")
    @PostMapping("/{productId}/reviews")
    public Rating addReview(@PathVariable Long productId, @Valid @RequestBody ReviewRequest reviewRequest) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Produit introuvable"));

        Utilisateur user = utilisateurRepository.findById(DEMO_USER_ID)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));

        // Create rating from request
        Rating review = Rating.builder()
                .product(product)
                .utilisateur(user)
                .stars(reviewRequest.getStars())
                .comment(reviewRequest.getComment())
                .createdAt(LocalDateTime.now())
                .build();

        return ratingRepository.save(review);
    }
}
