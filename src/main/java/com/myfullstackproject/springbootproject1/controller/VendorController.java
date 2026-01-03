package com.myfullstackproject.springbootproject1.controller;

import com.myfullstackproject.springbootproject1.dto.ProductRequest;
import com.myfullstackproject.springbootproject1.dto.ProductStatsResponse;
import com.myfullstackproject.springbootproject1.exception.ResourceNotFoundException;
import com.myfullstackproject.springbootproject1.exception.UnauthorizedException;
import com.myfullstackproject.springbootproject1.exception.ValidationException;
import com.myfullstackproject.springbootproject1.model.*;
import com.myfullstackproject.springbootproject1.repository.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/vendeur")
@CrossOrigin(origins = "http://localhost:5173")
@Tag(name = "Vendor", description = "Vendor/Seller APIs for managing products and viewing statistics")
public class VendorController {

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final CategorieRepository categorieRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final RatingRepository ratingRepository;

    private static final Long DEMO_VENDOR_ID = 1L; // ID du vendeur démo

    public VendorController(ProductRepository productRepository,
                           ProductImageRepository productImageRepository,
                           CategorieRepository categorieRepository,
                           UtilisateurRepository utilisateurRepository,
                           RatingRepository ratingRepository) {
        this.productRepository = productRepository;
        this.productImageRepository = productImageRepository;
        this.categorieRepository = categorieRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.ratingRepository = ratingRepository;
    }

    // ========== GESTION DES PRODUITS ==========

    @Operation(summary = "Add a new product", description = "Create a new product as a vendor")
    @PostMapping("/produits")
    public ResponseEntity<Product> addProduct(@Valid @RequestBody ProductRequest request) {
        // Récupérer le vendeur
        Utilisateur vendor = utilisateurRepository.findById(DEMO_VENDOR_ID)
                .orElseThrow(() -> new ResourceNotFoundException("Vendeur introuvable"));

        // Récupérer la catégorie si fournie
        Categorie categorie = null;
        if (request.getCategorieId() != null) {
            categorie = categorieRepository.findById(request.getCategorieId())
                    .orElseThrow(() -> new ResourceNotFoundException("Catégorie introuvable"));
        }

        // Créer le produit
        Product product = Product.builder()
                .asin(request.getAsin() != null ? request.getAsin() : generateAsin())
                .title(request.getTitle())
                .description(request.getDescription())
                .price(request.getPrice())
                .quantityAvailable(request.getQuantityAvailable())
                .categorie(categorie)
                .utilisateur(vendor)
                .rating(0.0)
                .ratingCount(0L)
                .build();

        product = productRepository.save(product);

        // Ajouter les images si fournies
        if (request.getImageUrls() != null && !request.getImageUrls().isEmpty()) {
            for (int i = 0; i < request.getImageUrls().size(); i++) {
                ProductImage image = ProductImage.builder()
                        .product(product)
                        .imageUrl(request.getImageUrls().get(i))
                        .isPrimary(i == 0)
                        .displayOrder(i)
                        .build();
                productImageRepository.save(image);
            }
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }

    @Operation(summary = "Update an existing product", description = "Update product details")
    @PutMapping("/produits/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produit introuvable"));

        // Vérifier que le produit appartient au vendeur
        if (!product.getUtilisateur().getId().equals(DEMO_VENDOR_ID)) {
            throw new UnauthorizedException("Vous n'êtes pas autorisé à modifier ce produit");
        }

        // Mettre à jour les champs
        if (request.getTitle() != null) {
            product.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            product.setDescription(request.getDescription());
        }
        if (request.getPrice() != null) {
            product.setPrice(request.getPrice());
        }
        if (request.getQuantityAvailable() != null) {
            product.setQuantityAvailable(request.getQuantityAvailable());
        }
        if (request.getCategorieId() != null) {
            Categorie categorie = categorieRepository.findById(request.getCategorieId())
                    .orElseThrow(() -> new ResourceNotFoundException("Catégorie introuvable"));
            product.setCategorie(categorie);
        }

        product = productRepository.save(product);
        return ResponseEntity.ok(product);
    }

    @Operation(summary = "Delete a product", description = "Remove a product from the catalog")
    @DeleteMapping("/produits/{id}")
    public ResponseEntity<Map<String, String>> deleteProduct(@PathVariable Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produit introuvable"));

        // Vérifier que le produit appartient au vendeur
        if (!product.getUtilisateur().getId().equals(DEMO_VENDOR_ID)) {
            throw new UnauthorizedException("Vous n'êtes pas autorisé à supprimer ce produit");
        }

        productRepository.delete(product);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Produit supprimé avec succès");
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get all vendor products", description = "List all products belonging to the vendor")
    @GetMapping("/produits")
    public ResponseEntity<List<Product>> getVendorProducts() {
        List<Product> products = productRepository.findByUtilisateur_Id(DEMO_VENDOR_ID);
        return ResponseEntity.ok(products);
    }

    @Operation(summary = "Get product details", description = "Get detailed information about a specific product")
    @GetMapping("/produits/{id}")
    public ResponseEntity<Product> getProductDetails(@PathVariable Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produit introuvable"));

        // Vérifier que le produit appartient au vendeur
        if (!product.getUtilisateur().getId().equals(DEMO_VENDOR_ID)) {
            throw new UnauthorizedException("Vous n'êtes pas autorisé à consulter ce produit");
        }

        return ResponseEntity.ok(product);
    }

    // ========== GESTION DES IMAGES ==========

    @Operation(summary = "Add images to product", description = "Add one or more images to a product")
    @PostMapping("/produits/{productId}/images")
    public ResponseEntity<List<ProductImage>> addProductImages(
            @PathVariable Long productId,
            @RequestBody List<String> imageUrls) {

        if (imageUrls == null || imageUrls.isEmpty()) {
            throw new ValidationException("Au moins une URL d'image est requise");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Produit introuvable"));

        // Vérifier que le produit appartient au vendeur
        if (!product.getUtilisateur().getId().equals(DEMO_VENDOR_ID)) {
            throw new UnauthorizedException("Vous n'êtes pas autorisé à modifier ce produit");
        }

        // Récupérer les images existantes pour déterminer l'ordre
        List<ProductImage> existingImages = productImageRepository.findByProduct_IdOrderByDisplayOrderAsc(productId);
        int startOrder = existingImages.size();

        // Ajouter les nouvelles images
        for (int i = 0; i < imageUrls.size(); i++) {
            ProductImage image = ProductImage.builder()
                    .product(product)
                    .imageUrl(imageUrls.get(i))
                    .isPrimary(existingImages.isEmpty() && i == 0)
                    .displayOrder(startOrder + i)
                    .build();
            productImageRepository.save(image);
        }

        List<ProductImage> allImages = productImageRepository.findByProduct_IdOrderByDisplayOrderAsc(productId);
        return ResponseEntity.status(HttpStatus.CREATED).body(allImages);
    }

    @Operation(summary = "Delete product image", description = "Remove an image from a product")
    @DeleteMapping("/produits/{productId}/images/{imageId}")
    public ResponseEntity<Map<String, String>> deleteProductImage(
            @PathVariable Long productId,
            @PathVariable Long imageId) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Produit introuvable"));

        // Vérifier que le produit appartient au vendeur
        if (!product.getUtilisateur().getId().equals(DEMO_VENDOR_ID)) {
            throw new UnauthorizedException("Vous n'êtes pas autorisé à modifier ce produit");
        }

        ProductImage image = productImageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Image introuvable"));

        if (!image.getProduct().getId().equals(productId)) {
            throw new ValidationException("Cette image n'appartient pas à ce produit");
        }

        productImageRepository.delete(image);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Image supprimée avec succès");
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get product images", description = "Retrieve all images for a product")
    @GetMapping("/produits/{productId}/images")
    public ResponseEntity<List<ProductImage>> getProductImages(@PathVariable Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Produit introuvable"));

        // Vérifier que le produit appartient au vendeur
        if (!product.getUtilisateur().getId().equals(DEMO_VENDOR_ID)) {
            throw new UnauthorizedException("Vous n'êtes pas autorisé à consulter ce produit");
        }

        List<ProductImage> images = productImageRepository.findByProduct_IdOrderByDisplayOrderAsc(productId);
        return ResponseEntity.ok(images);
    }

    // ========== AVIS CLIENTS ==========

    @Operation(summary = "Get product reviews", description = "View all reviews for a vendor's product")
    @GetMapping("/produits/{productId}/reviews")
    public ResponseEntity<List<Rating>> getProductReviews(@PathVariable Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Produit introuvable"));

        // Vérifier que le produit appartient au vendeur
        if (!product.getUtilisateur().getId().equals(DEMO_VENDOR_ID)) {
            throw new UnauthorizedException("Vous n'êtes pas autorisé à consulter les avis de ce produit");
        }

        List<Rating> reviews = ratingRepository.findByProduct(product);
        return ResponseEntity.ok(reviews);
    }

    @Operation(summary = "Get product statistics", description = "Get detailed statistics and ratings breakdown for a product")
    @GetMapping("/produits/{productId}/stats")
    public ResponseEntity<ProductStatsResponse> getProductStats(@PathVariable Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Produit introuvable"));

        // Vérifier que le produit appartient au vendeur
        if (!product.getUtilisateur().getId().equals(DEMO_VENDOR_ID)) {
            throw new UnauthorizedException("Vous n'êtes pas autorisé à consulter les statistiques de ce produit");
        }

        List<Rating> reviews = ratingRepository.findByProduct(product);

        // Calculer les statistiques
        long totalReviews = reviews.size();
        double averageRating = reviews.stream()
                .mapToInt(Rating::getStars)
                .average()
                .orElse(0.0);

        long fiveStar = reviews.stream().filter(r -> r.getStars() == 5).count();
        long fourStar = reviews.stream().filter(r -> r.getStars() == 4).count();
        long threeStar = reviews.stream().filter(r -> r.getStars() == 3).count();
        long twoStar = reviews.stream().filter(r -> r.getStars() == 2).count();
        long oneStar = reviews.stream().filter(r -> r.getStars() == 1).count();

        ProductStatsResponse stats = ProductStatsResponse.builder()
                .productId(product.getId())
                .productTitle(product.getTitle())
                .totalReviews(totalReviews)
                .averageRating(Math.round(averageRating * 10.0) / 10.0)
                .fiveStarCount(fiveStar)
                .fourStarCount(fourStar)
                .threeStarCount(threeStar)
                .twoStarCount(twoStar)
                .oneStarCount(oneStar)
                .build();

        return ResponseEntity.ok(stats);
    }

    // Méthode utilitaire pour générer un code ASIN unique
    private String generateAsin() {
        return "VEND" + System.currentTimeMillis();
    }
}
