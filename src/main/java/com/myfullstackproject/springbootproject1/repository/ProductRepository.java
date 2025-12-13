package com.myfullstackproject.springbootproject1.repository;

import com.myfullstackproject.springbootproject1.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Exemple de méthodes de recherche (on pourra les utiliser plus tard)
    // List<Product> findByCategory(String category);
    // List<Product> findByTitleContainingIgnoreCase(String keyword);
}