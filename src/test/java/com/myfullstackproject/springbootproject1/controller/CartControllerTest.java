package com.myfullstackproject.springbootproject1.controller;

import com.myfullstackproject.springbootproject1.model.CartItem;
import com.myfullstackproject.springbootproject1.model.Product;
import com.myfullstackproject.springbootproject1.repository.CartItemRepository;
import com.myfullstackproject.springbootproject1.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class CartControllerTest {

    @Autowired
    private CartController cartController;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    private Product testProduct;
    private static final String TEST_BUYER = "demo-buyer";

    @BeforeEach
    void setUp() {
        // Clean cart items
        cartItemRepository.deleteAll();
        
        // Create a test product
        testProduct = Product.builder()
                .asin("TEST123")
                .title("Test Product")
                .imageUrl("http://test.com/img.jpg")
                .productLink("http://test.com/product")
                .rating(4.5)
                .ratingCount(100L)
                .price(29.99)
                .category("Test Category")
                .rank(1)
                .reviewsCount(100)
                .build();
        testProduct = productRepository.save(testProduct);
    }

    @Test
    void testUpdateCartItemQuantity() {
        // Add item to cart
        CartItem item = cartController.addToCart(testProduct.getId(), 1);
        assertNotNull(item);
        assertEquals(1, item.getQuantity());

        // Update quantity
        CartItem updatedItem = cartController.updateCartItemQuantity(item.getId(), 5);
        assertNotNull(updatedItem);
        assertEquals(5, updatedItem.getQuantity());
        assertEquals(item.getId(), updatedItem.getId());
    }

    @Test
    void testUpdateCartItemQuantityThrowsExceptionForInvalidQuantity() {
        // Add item to cart
        CartItem item = cartController.addToCart(testProduct.getId(), 1);

        // Try to update with invalid quantity
        assertThrows(IllegalArgumentException.class, () -> {
            cartController.updateCartItemQuantity(item.getId(), 0);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            cartController.updateCartItemQuantity(item.getId(), -1);
        });
    }

    @Test
    void testUpdateCartItemQuantityThrowsExceptionForNonExistentItem() {
        // Try to update non-existent item
        assertThrows(RuntimeException.class, () -> {
            cartController.updateCartItemQuantity(999L, 5);
        });
    }

    @Test
    void testDeleteCartItem() {
        // Add item to cart
        CartItem item = cartController.addToCart(testProduct.getId(), 1);
        assertNotNull(item);
        
        // Verify item exists
        List<CartItem> cartBefore = cartController.getCart();
        assertEquals(1, cartBefore.size());

        // Delete item
        cartController.deleteCartItem(item.getId());

        // Verify item was deleted
        List<CartItem> cartAfter = cartController.getCart();
        assertEquals(0, cartAfter.size());
    }

    @Test
    void testDeleteCartItemThrowsExceptionForNonExistentItem() {
        // Try to delete non-existent item
        assertThrows(RuntimeException.class, () -> {
            cartController.deleteCartItem(999L);
        });
    }

    @Test
    void testGetCart() {
        // Initially empty
        List<CartItem> emptyCart = cartController.getCart();
        assertEquals(0, emptyCart.size());

        // Add items
        cartController.addToCart(testProduct.getId(), 2);

        // Verify cart has items
        List<CartItem> cart = cartController.getCart();
        assertEquals(1, cart.size());
        assertEquals(2, cart.get(0).getQuantity());
    }

    @Test
    void testUpdateCartItemQuantityThrowsExceptionForUnauthorizedAccess() {
        // Add item to cart
        CartItem item = cartController.addToCart(testProduct.getId(), 1);
        
        // Modify the buyer ID to simulate unauthorized access
        item.setBuyerId("another-buyer");
        cartItemRepository.save(item);

        // Try to update the item - should throw exception
        assertThrows(RuntimeException.class, () -> {
            cartController.updateCartItemQuantity(item.getId(), 5);
        });
    }

    @Test
    void testDeleteCartItemThrowsExceptionForUnauthorizedAccess() {
        // Add item to cart
        CartItem item = cartController.addToCart(testProduct.getId(), 1);
        
        // Modify the buyer ID to simulate unauthorized access
        item.setBuyerId("another-buyer");
        cartItemRepository.save(item);

        // Try to delete the item - should throw exception
        assertThrows(RuntimeException.class, () -> {
            cartController.deleteCartItem(item.getId());
        });
    }
}
