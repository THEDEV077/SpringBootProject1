package com.myfullstackproject.springbootproject1.controller;

import com.myfullstackproject.springbootproject1.dto.ProductRequest;
import com.myfullstackproject.springbootproject1.dto.ProductStatsResponse;
import com.myfullstackproject.springbootproject1.model.*;
import com.myfullstackproject.springbootproject1.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/vendeur")
@CrossOrigin(origins = "http://localhost:5173")
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

    /**
     * Ajouter un nouveau produit
     * POST /api/vendeur/produits
     */
    @PostMapping("/produits")
    public ResponseEntity<Product> addProduct(@RequestBody ProductRequest request) {
        // Validation
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new RuntimeException("Le nom du produit est requis");
        }
        if (request.getPrice() == null || request.getPrice() <= 0) {
            throw new RuntimeException("Le prix doit être supérieur à 0");
        }
        if (request.getQuantityAvailable() == null || request.getQuantityAvailable() < 0) {
            throw new RuntimeException("La quantité doit être supérieure ou égale à 0");
        }

        // Récupérer le vendeur
        Utilisateur vendor = utilisateurRepository.findById(DEMO_VENDOR_ID)
                .orElseThrow(() -> new RuntimeException("Vendeur introuvable"));

        // Récupérer la catégorie si fournie
        Categorie categorie = null;
        if (request.getCategorieId() != null) {
            categorie = categorieRepository.findById(request.getCategorieId())
                    .orElseThrow(() -> new RuntimeException("Catégorie introuvable"));
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

    /**
     * Modifier un produit existant
     * PUT /api/vendeur/produits/{id}
     */
    @PutMapping("/produits/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produit introuvable"));

        // Vérifier que le produit appartient au vendeur
        if (!product.getUtilisateur().getId().equals(DEMO_VENDOR_ID)) {
            throw new RuntimeException("Vous n'êtes pas autorisé à modifier ce produit");
        }

        // Mettre à jour les champs
        if (request.getTitle() != null) {
            product.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            product.setDescription(request.getDescription());
        }
        if (request.getPrice() != null) {
            if (request.getPrice() <= 0) {
                throw new RuntimeException("Le prix doit être supérieur à 0");
            }
            product.setPrice(request.getPrice());
        }
        if (request.getQuantityAvailable() != null) {
            if (request.getQuantityAvailable() < 0) {
                throw new RuntimeException("La quantité ne peut pas être négative");
            }
            product.setQuantityAvailable(request.getQuantityAvailable());
        }
        if (request.getCategorieId() != null) {
            Categorie categorie = categorieRepository.findById(request.getCategorieId())
                    .orElseThrow(() -> new RuntimeException("Catégorie introuvable"));
            product.setCategorie(categorie);
        }

        product = productRepository.save(product);
        return ResponseEntity.ok(product);
    }

    /**
     * Supprimer un produit
     * DELETE /api/vendeur/produits/{id}
     */
    @DeleteMapping("/produits/{id}")
    public ResponseEntity<Map<String, String>> deleteProduct(@PathVariable Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produit introuvable"));

        // Vérifier que le produit appartient au vendeur
        if (!product.getUtilisateur().getId().equals(DEMO_VENDOR_ID)) {
            throw new RuntimeException("Vous n'êtes pas autorisé à supprimer ce produit");
        }

        productRepository.delete(product);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Produit supprimé avec succès");
        return ResponseEntity.ok(response);
    }

    /**
     * Lister tous les produits du vendeur
     * GET /api/vendeur/produits
     */
    @GetMapping("/produits")
    public ResponseEntity<List<Product>> getVendorProducts() {
        Utilisateur vendor = utilisateurRepository.findById(DEMO_VENDOR_ID)
                .orElseThrow(() -> new RuntimeException("Vendeur introuvable"));

        List<Product> products = productRepository.findAll().stream()
                .filter(p -> p.getUtilisateur() != null && p.getUtilisateur().getId().equals(DEMO_VENDOR_ID))
                .toList();

        return ResponseEntity.ok(products);
    }

    /**
     * Consulter les détails d'un produit
     * GET /api/vendeur/produits/{id}
     */
    @GetMapping("/produits/{id}")
    public ResponseEntity<Product> getProductDetails(@PathVariable Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produit introuvable"));

        // Vérifier que le produit appartient au vendeur
        if (!product.getUtilisateur().getId().equals(DEMO_VENDOR_ID)) {
            throw new RuntimeException("Vous n'êtes pas autorisé à consulter ce produit");
        }

        return ResponseEntity.ok(product);
    }

    // ========== GESTION DES IMAGES ==========

    /**
     * Ajouter des images à un produit
     * POST /api/vendeur/produits/{productId}/images
     */
    @PostMapping("/produits/{productId}/images")
    public ResponseEntity<List<ProductImage>> addProductImages(
            @PathVariable Long productId,
            @RequestBody List<String> imageUrls) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Produit introuvable"));

        // Vérifier que le produit appartient au vendeur
        if (!product.getUtilisateur().getId().equals(DEMO_VENDOR_ID)) {
            throw new RuntimeException("Vous n'êtes pas autorisé à modifier ce produit");
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

    /**
     * Supprimer une image d'un produit
     * DELETE /api/vendeur/produits/{productId}/images/{imageId}
     */
    @DeleteMapping("/produits/{productId}/images/{imageId}")
    public ResponseEntity<Map<String, String>> deleteProductImage(
            @PathVariable Long productId,
            @PathVariable Long imageId) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Produit introuvable"));

        // Vérifier que le produit appartient au vendeur
        if (!product.getUtilisateur().getId().equals(DEMO_VENDOR_ID)) {
            throw new RuntimeException("Vous n'êtes pas autorisé à modifier ce produit");
        }

        ProductImage image = productImageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image introuvable"));

        if (!image.getProduct().getId().equals(productId)) {
            throw new RuntimeException("Cette image n'appartient pas à ce produit");
        }

        productImageRepository.delete(image);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Image supprimée avec succès");
        return ResponseEntity.ok(response);
    }

    /**
     * Consulter les images d'un produit
     * GET /api/vendeur/produits/{productId}/images
     */
    @GetMapping("/produits/{productId}/images")
    public ResponseEntity<List<ProductImage>> getProductImages(@PathVariable Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Produit introuvable"));

        // Vérifier que le produit appartient au vendeur
        if (!product.getUtilisateur().getId().equals(DEMO_VENDOR_ID)) {
            throw new RuntimeException("Vous n'êtes pas autorisé à consulter ce produit");
        }

        List<ProductImage> images = productImageRepository.findByProduct_IdOrderByDisplayOrderAsc(productId);
        return ResponseEntity.ok(images);
    }

    // ========== AVIS CLIENTS ==========

    /**
     * Consulter les avis d'un produit
     * GET /api/vendeur/produits/{productId}/reviews
     */
    @GetMapping("/produits/{productId}/reviews")
    public ResponseEntity<List<Rating>> getProductReviews(@PathVariable Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Produit introuvable"));

        // Vérifier que le produit appartient au vendeur
        if (!product.getUtilisateur().getId().equals(DEMO_VENDOR_ID)) {
            throw new RuntimeException("Vous n'êtes pas autorisé à consulter les avis de ce produit");
        }

        List<Rating> reviews = ratingRepository.findByProduct(product);
        return ResponseEntity.ok(reviews);
    }

    /**
     * Consulter les statistiques des avis d'un produit
     * GET /api/vendeur/produits/{productId}/stats
     */
    @GetMapping("/produits/{productId}/stats")
    public ResponseEntity<ProductStatsResponse> getProductStats(@PathVariable Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Produit introuvable"));

        // Vérifier que le produit appartient au vendeur
        if (!product.getUtilisateur().getId().equals(DEMO_VENDOR_ID)) {
            throw new RuntimeException("Vous n'êtes pas autorisé à consulter les statistiques de ce produit");
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
