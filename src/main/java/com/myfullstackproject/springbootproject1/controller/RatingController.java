package com.myfullstackproject.springbootproject1.controller;

import com.myfullstackproject.springbootproject1.dto.ReviewRequest;
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
@CrossOrigin(origins = "http://localhost:5173")
public class RatingController {

    private final ProductRepository productRepository;
    private final RatingRepository ratingRepository;
    private final UtilisateurRepository utilisateurRepository;

    private static final Long DEMO_USER_ID = 1L; // Utilisateur test ID=1

    public RatingController(ProductRepository productRepository,
                           RatingRepository ratingRepository,
                           UtilisateurRepository utilisateurRepository) {
        this.productRepository = productRepository;
        this.ratingRepository = ratingRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    // Get all reviews for a product
    @GetMapping("/{productId}/reviews")
    public List<Rating> getProductReviews(@PathVariable Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Produit introuvable"));
        return ratingRepository.findByProduct(product);
    }

    // Add new review
    @PostMapping("/{productId}/reviews")
    public Rating addReview(@PathVariable Long productId, @RequestBody ReviewRequest reviewRequest) {
        // Validate input
        if (reviewRequest.getStars() == null || reviewRequest.getStars() < 1 || reviewRequest.getStars() > 5) {
            throw new RuntimeException("Les étoiles doivent être entre 1 et 5");
        }
        if (reviewRequest.getComment() == null || reviewRequest.getComment().trim().isEmpty()) {
            throw new RuntimeException("Le commentaire ne peut pas être vide");
        }
        if (reviewRequest.getComment().length() > 500) {
            throw new RuntimeException("Le commentaire ne peut pas dépasser 500 caractères");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Produit introuvable"));

        Utilisateur user = utilisateurRepository.findById(DEMO_USER_ID)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

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
