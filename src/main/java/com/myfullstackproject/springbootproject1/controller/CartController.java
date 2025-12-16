package com.myfullstackproject.springbootproject1.controller;

import com.myfullstackproject.springbootproject1.model.CartItem;
import com.myfullstackproject.springbootproject1.model.Product;
import com.myfullstackproject.springbootproject1.model.Utilisateur;
import com.myfullstackproject.springbootproject1.repository.CartItemRepository;
import com.myfullstackproject.springbootproject1.repository.ProductRepository;
import com.myfullstackproject.springbootproject1.repository.UtilisateurRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/panier")
@CrossOrigin(origins = "http://localhost:5173")
public class CartController {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UtilisateurRepository utilisateurRepository;

    private static final Long DEMO_USER_ID = 1L; // Utilisateur test ID=1

    public CartController(CartItemRepository cartItemRepository,
                          ProductRepository productRepository,
                          UtilisateurRepository utilisateurRepository) {
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    // 1. AJOUTER produit au panier
    @PostMapping("/add/{productId}")
    public CartItem addToCart(@PathVariable Long productId,
                              @RequestParam(defaultValue = "1") int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Produit introuvable"));

        Utilisateur user = utilisateurRepository.findById(DEMO_USER_ID)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        // Chercher si produit existe déjà
        List<CartItem> itemsUser = cartItemRepository.findByUtilisateur_Id(DEMO_USER_ID);
        CartItem item = itemsUser.stream()
                .filter(ci -> ci.getProduct().getId().equals(productId))
                .findFirst()
                .orElse(null);

        if (item == null) {
            item = CartItem.builder()
                    .utilisateur(user)
                    .product(product)
                    .quantity(quantity)
                    .build();
        } else {
            item.setQuantity(item.getQuantity() + quantity);
        }

        return cartItemRepository.save(item);
    }

    // 2. LISTER panier
    @GetMapping
    public List<CartItem> getCart() {
        return cartItemRepository.findByUtilisateur_Id(DEMO_USER_ID);
    }

    // 3. MODIFIER quantité
    @PutMapping("/{cartItemId}")
    public CartItem updateCartItemQuantity(@PathVariable Long cartItemId,
                                           @RequestParam int quantity) {
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Item panier introuvable"));

        // Vérifier propriétaire
        if (!item.getUtilisateur().getId().equals(DEMO_USER_ID)) {
            throw new RuntimeException("Accès non autorisé");
        }

        item.setQuantity(quantity);
        return cartItemRepository.save(item);
    }

    // 4. SUPPRIMER item
    @DeleteMapping("/{cartItemId}")
    public void deleteCartItem(@PathVariable Long cartItemId) {
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Item panier introuvable"));

        if (!item.getUtilisateur().getId().equals(DEMO_USER_ID)) {
            throw new RuntimeException("Accès non autorisé");
        }

        cartItemRepository.delete(item);
    }
}
