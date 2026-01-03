package com.myfullstackproject.springbootproject1.repository;

import com.myfullstackproject.springbootproject1.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Find products by vendor/seller
    List<Product> findByUtilisateur_Id(Long vendorId);

    // Find products by category
    List<Product> findByCategorie_Id(Long categoryId);
    
    // Search products by title (case-insensitive)
    List<Product> findByTitleContainingIgnoreCase(String keyword);
    
    // Find products within price range
    List<Product> findByPriceBetween(Double minPrice, Double maxPrice);
    
    // Find products with minimum rating
    List<Product> findByRatingGreaterThanEqual(Double minRating);
    
    // Find available products (quantity > 0)
    List<Product> findByQuantityAvailableGreaterThan(Integer quantity);
    
    // Find by ASIN
    Optional<Product> findByAsin(String asin);
    
    // Combined search query
    @Query("SELECT p FROM Product p WHERE " +
           "(:categoryId IS NULL OR p.categorie.id = :categoryId) AND " +
           "(:keyword IS NULL OR LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
           "(:maxPrice IS NULL OR p.price <= :maxPrice) AND " +
           "(:minRating IS NULL OR p.rating >= :minRating)")
    List<Product> searchProducts(
        @Param("categoryId") Long categoryId,
        @Param("keyword") String keyword,
        @Param("minPrice") Double minPrice,
        @Param("maxPrice") Double maxPrice,
        @Param("minRating") Double minRating
    );
}
