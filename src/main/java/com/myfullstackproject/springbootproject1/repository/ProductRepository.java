package com.myfullstackproject.springbootproject1.repository;

import com.myfullstackproject.springbootproject1.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Find products by vendor/seller
    List<Product> findByUtilisateur_Id(Long vendorId);

    // Exemple de méthodes de recherche (on pourra les utiliser plus tard)
    // List<Product> findByCategory(String category);
    // List<Product> findByTitleContainingIgnoreCase(String keyword);
}