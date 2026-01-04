package com.myfullstackproject.springbootproject1.controller;

import com.myfullstackproject.springbootproject1.model.*;
import com.myfullstackproject.springbootproject1.repository.*;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class OrderController {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final EntityManager entityManager;

    private static final Long DEMO_USER_ID = 1L; // Utilisateur demo

    public OrderController(OrderRepository orderRepository,
                          OrderItemRepository orderItemRepository,
                          CartItemRepository cartItemRepository,
                          ProductRepository productRepository,
                          UtilisateurRepository utilisateurRepository,
                          EntityManager entityManager) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.entityManager = entityManager;
    }

    /**
     * Créer une commande depuis le panier (Processus de paiement)
     * POST /api/ventes
     */
    @Transactional
    @PostMapping("/ventes")
    public ResponseEntity<Order> createOrder() {
        // Récupérer l'utilisateur
        Utilisateur user = utilisateurRepository.findById(DEMO_USER_ID)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        // Récupérer les items du panier
        List<CartItem> cartItems = cartItemRepository.findByUtilisateur_Id(DEMO_USER_ID);

        if (cartItems.isEmpty()) {
            throw new RuntimeException("Le panier est vide");
        }

        // Calculer le montant total
        double totalAmount = cartItems.stream()
                .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();

        // Créer la commande
        Order order = Order.builder()
                .utilisateur(user)
                .createdAt(LocalDateTime.now())
                .totalAmount(totalAmount)
                .build();

        order = orderRepository.save(order);

        // Créer les lignes de commande
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(cartItem.getProduct())
                    .quantity(cartItem.getQuantity())
                    .unitPrice(cartItem.getProduct().getPrice())
                    .build();
            orderItemRepository.save(orderItem);
        }

        // Vider le panier
        cartItemRepository.deleteAll(cartItems);

        // Flush pour s'assurer que tout est persisté
        entityManager.flush();
        entityManager.clear();

        // Recharger la commande avec les items
        order = orderRepository.findById(order.getId())
                .orElse(order);

        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    /**
     * Récupérer les commandes de l'utilisateur
     * GET /api/commandes
     */
    @GetMapping("/commandes")
    public List<Order> getUserOrders() {
        return orderRepository.findByUtilisateur_Id(DEMO_USER_ID);
    }

    /**
     * Récupérer une commande spécifique
     * GET /api/commandes/{id}
     */
    @GetMapping("/commandes/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Commande introuvable"));

        // Vérifier que c'est bien la commande de l'utilisateur
        if (!order.getUtilisateur().getId().equals(DEMO_USER_ID)) {
            throw new RuntimeException("Accès non autorisé");
        }

        return ResponseEntity.ok(order);
    }
}
