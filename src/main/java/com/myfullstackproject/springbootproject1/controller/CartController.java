package com.myfullstackproject.springbootproject1.controller;

import com.myfullstackproject.springbootproject1.model.CartItem;
import com.myfullstackproject.springbootproject1.model.Product;
import com.myfullstackproject.springbootproject1.repository.CartItemRepository;
import com.myfullstackproject.springbootproject1.repository.ProductRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/panier")
@CrossOrigin(origins = "http://localhost:5174") // adapte le port si besoin
public class CartController {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    private static final String DEMO_BUYER = "demo-buyer";

    public CartController(CartItemRepository cartItemRepository,
                          ProductRepository productRepository) {
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
    }

    // Ajouter un produit au panier (quantité par défaut = 1)
    @PostMapping("/add/{productId}")
    public CartItem addToCart(@PathVariable Long productId,
                              @RequestParam(defaultValue = "1") int quantity) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Produit introuvable"));

        // Chercher si ce produit existe déjà dans le panier du buyer
        List<CartItem> itemsBuyer = cartItemRepository.findByBuyerId(DEMO_BUYER);

        CartItem item = itemsBuyer.stream()
                .filter(ci -> ci.getProduct().getId().equals(productId))
                .findFirst()
                .orElse(null);

        if (item == null) {
            item = CartItem.builder()
                    .buyerId(DEMO_BUYER)
                    .product(product)
                    .quantity(quantity)
                    .build();
        } else {
            item.setQuantity(item.getQuantity() + quantity);
        }

        return cartItemRepository.save(item);
    }

    // Récupérer le panier du buyer fixe
    @GetMapping
    public List<CartItem> getCart() {
        return cartItemRepository.findByBuyerId(DEMO_BUYER);
    }
}