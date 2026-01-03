package com.myfullstackproject.springbootproject1.controller;

import com.myfullstackproject.springbootproject1.exception.ResourceNotFoundException;
import com.myfullstackproject.springbootproject1.exception.UnauthorizedException;
import com.myfullstackproject.springbootproject1.exception.ValidationException;
import com.myfullstackproject.springbootproject1.model.CartItem;
import com.myfullstackproject.springbootproject1.model.Product;
import com.myfullstackproject.springbootproject1.model.Utilisateur;
import com.myfullstackproject.springbootproject1.repository.CartItemRepository;
import com.myfullstackproject.springbootproject1.repository.ProductRepository;
import com.myfullstackproject.springbootproject1.repository.UtilisateurRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/panier")
@CrossOrigin(origins = "http://localhost:5173")
@Tag(name = "Shopping Cart", description = "APIs for managing shopping cart")
public class CartController {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UtilisateurRepository utilisateurRepository;

    // TODO: Remove hardcoded demo user ID and implement proper authentication
    // This is temporary for testing without authentication
    private static final Long DEMO_USER_ID = 1L; // Utilisateur test ID=1

    public CartController(CartItemRepository cartItemRepository,
                          ProductRepository productRepository,
                          UtilisateurRepository utilisateurRepository) {
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    @Operation(summary = "Add product to cart", description = "Add a product to the user's shopping cart")
    @PostMapping("/add/{productId}")
    public CartItem addToCart(@PathVariable Long productId,
                              @RequestParam(defaultValue = "1") int quantity) {
        if (quantity <= 0) {
            throw new ValidationException("La quantité doit être supérieure à 0");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Produit introuvable"));

        Utilisateur user = utilisateurRepository.findById(DEMO_USER_ID)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));

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

    @Operation(summary = "Get cart items", description = "Retrieve all items in the user's shopping cart")
    @GetMapping
    public List<CartItem> getCart() {
        return cartItemRepository.findByUtilisateur_Id(DEMO_USER_ID);
    }

    @Operation(summary = "Update cart item quantity", description = "Update the quantity of an item in the cart")
    @PutMapping("/{cartItemId}")
    public CartItem updateCartItemQuantity(@PathVariable Long cartItemId,
                                           @RequestParam int quantity) {
        if (quantity <= 0) {
            throw new ValidationException("La quantité doit être supérieure à 0");
        }

        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item panier introuvable"));

        // Vérifier propriétaire
        if (!item.getUtilisateur().getId().equals(DEMO_USER_ID)) {
            throw new UnauthorizedException("Accès non autorisé");
        }

        item.setQuantity(quantity);
        return cartItemRepository.save(item);
    }

    @Operation(summary = "Remove item from cart", description = "Remove an item from the shopping cart")
    @DeleteMapping("/{cartItemId}")
    public void deleteCartItem(@PathVariable Long cartItemId) {
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item panier introuvable"));

        if (!item.getUtilisateur().getId().equals(DEMO_USER_ID)) {
            throw new UnauthorizedException("Accès non autorisé");
        }

        cartItemRepository.delete(item);
    }
}
