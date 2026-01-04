package com.myfullstackproject.springbootproject1.controller;

import com.myfullstackproject.springbootproject1.dto.ProductRequest;
import com.myfullstackproject.springbootproject1.dto.ProductStatsResponse;
import com.myfullstackproject.springbootproject1.model.*;
import com.myfullstackproject.springbootproject1.repository.*;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
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
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final EntityManager entityManager;

    private static final Long DEMO_VENDOR_ID = 1L; // ID du vendeur démo

    public VendorController(ProductRepository productRepository,
                           ProductImageRepository productImageRepository,
                           CategorieRepository categorieRepository,
                           UtilisateurRepository utilisateurRepository,
                           RatingRepository ratingRepository,
                           OrderRepository orderRepository,
                           OrderItemRepository orderItemRepository,
                           EntityManager entityManager) {
        this.productRepository = productRepository;
        this.productImageRepository = productImageRepository;
        this.categorieRepository = categorieRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.ratingRepository = ratingRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.entityManager = entityManager;
    }

    // ========== GESTION DES PRODUITS ==========

    /**
     * Ajouter un nouveau produit
     * POST /api/vendeur/produits
     */
    @Transactional
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
            // Flush to ensure images are persisted before reloading
            entityManager.flush();
            // Clear to force a fresh load from database
            entityManager.clear();
            // Reload product with images eagerly fetched
            product = productRepository.findByIdWithImages(product.getId())
                    .orElse(product);
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
        List<Product> products = productRepository.findByUtilisateur_Id(DEMO_VENDOR_ID);
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

    // ========== GESTION DES VENTES ==========

    /**
     * Récupérer toutes les ventes du vendeur
     * GET /api/vendeur/ventes
     */
    @GetMapping("/ventes")
    public ResponseEntity<List<Map<String, Object>>> getVendorSales() {
        // Récupérer tous les produits du vendeur
        List<Product> vendorProducts = productRepository.findByUtilisateur_Id(DEMO_VENDOR_ID);
        
        if (vendorProducts.isEmpty()) {
            return ResponseEntity.ok(List.of());
        }

        // Récupérer toutes les commandes contenant les produits du vendeur
        List<OrderItem> vendorOrderItems = orderItemRepository.findByProduct_Utilisateur_Id(DEMO_VENDOR_ID);

        // Créer une liste de ventes avec les détails
        List<Map<String, Object>> sales = vendorOrderItems.stream()
                .map(orderItem -> {
                    Map<String, Object> sale = new HashMap<>();
                    sale.put("id", orderItem.getId());
                    sale.put("orderId", orderItem.getOrder().getId());
                    sale.put("productName", orderItem.getProduct().getTitle());
                    sale.put("productId", orderItem.getProduct().getId());
                    sale.put("quantity", orderItem.getQuantity());
                    sale.put("unitPrice", orderItem.getUnitPrice());
                    sale.put("totalPrice", orderItem.getQuantity() * orderItem.getUnitPrice());
                    sale.put("orderDate", orderItem.getOrder().getCreatedAt());
                    sale.put("buyerName", orderItem.getOrder().getUtilisateur().getNom());
                    return sale;
                })
                .toList();

        return ResponseEntity.ok(sales);
    }

    /**
     * Récupérer les statistiques de ventes du vendeur
     * GET /api/vendeur/ventes/stats
     */
    @GetMapping("/ventes/stats")
    public ResponseEntity<Map<String, Object>> getVendorSalesStats() {
        // Récupérer tous les items de commande pour les produits du vendeur
        List<OrderItem> vendorOrderItems = orderItemRepository.findByProduct_Utilisateur_Id(DEMO_VENDOR_ID);

        // Calculer les statistiques
        int totalOrders = (int) vendorOrderItems.stream()
                .map(item -> item.getOrder().getId())
                .distinct()
                .count();

        int totalProductsSold = vendorOrderItems.stream()
                .mapToInt(OrderItem::getQuantity)
                .sum();

        double totalRevenue = vendorOrderItems.stream()
                .mapToDouble(item -> item.getQuantity() * item.getUnitPrice())
                .sum();

        double averageOrderValue = totalOrders > 0 ? totalRevenue / totalOrders : 0.0;

        // Top 5 produits les plus vendus
        Map<Long, Integer> productSales = new HashMap<>();
        Map<Long, String> productNames = new HashMap<>();
        for (OrderItem item : vendorOrderItems) {
            Long productId = item.getProduct().getId();
            productSales.put(productId, productSales.getOrDefault(productId, 0) + item.getQuantity());
            productNames.put(productId, item.getProduct().getTitle());
        }

        List<Map<String, Object>> topProducts = productSales.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .limit(5)
                .map(entry -> {
                    Map<String, Object> product = new HashMap<>();
                    product.put("productId", entry.getKey());
                    product.put("productName", productNames.get(entry.getKey()));
                    product.put("quantitySold", entry.getValue());
                    return product;
                })
                .toList();

        // Créer la réponse
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalOrders", totalOrders);
        stats.put("totalProductsSold", totalProductsSold);
        stats.put("totalRevenue", totalRevenue);
        stats.put("averageOrderValue", averageOrderValue);
        stats.put("topProducts", topProducts);

        return ResponseEntity.ok(stats);
    }

    /**
     * Récupérer les détails d'une vente spécifique
     * GET /api/vendeur/ventes/{orderId}
     */
    @GetMapping("/ventes/{orderId}")
    public ResponseEntity<Map<String, Object>> getVendorSaleDetails(@PathVariable Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Commande introuvable"));

        // Récupérer uniquement les items du vendeur dans cette commande
        List<OrderItem> vendorItems = order.getItems().stream()
                .filter(item -> item.getProduct().getUtilisateur().getId().equals(DEMO_VENDOR_ID))
                .toList();

        if (vendorItems.isEmpty()) {
            throw new RuntimeException("Aucun produit de ce vendeur dans cette commande");
        }

        // Calculer le total pour le vendeur
        double vendorTotal = vendorItems.stream()
                .mapToDouble(item -> item.getQuantity() * item.getUnitPrice())
                .sum();

        // Créer la réponse
        Map<String, Object> saleDetails = new HashMap<>();
        saleDetails.put("orderId", order.getId());
        saleDetails.put("orderDate", order.getCreatedAt());
        saleDetails.put("buyerName", order.getUtilisateur().getNom());
        saleDetails.put("vendorTotal", vendorTotal);
        saleDetails.put("items", vendorItems.stream()
                .map(item -> {
                    Map<String, Object> itemMap = new HashMap<>();
                    itemMap.put("productName", item.getProduct().getTitle());
                    itemMap.put("quantity", item.getQuantity());
                    itemMap.put("unitPrice", item.getUnitPrice());
                    itemMap.put("totalPrice", item.getQuantity() * item.getUnitPrice());
                    return itemMap;
                })
                .toList());

        return ResponseEntity.ok(saleDetails);
    }

    // Méthode utilitaire pour générer un code ASIN unique
    private String generateAsin() {
        return "VEND" + System.currentTimeMillis();
    }
}
