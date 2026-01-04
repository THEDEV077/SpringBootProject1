package com.myfullstackproject.springbootproject1.repository;

import com.myfullstackproject.springbootproject1.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    
    // Trouver tous les items de commande pour les produits d'un vendeur spécifique
    List<OrderItem> findByProduct_Utilisateur_Id(Long vendeurId);
}