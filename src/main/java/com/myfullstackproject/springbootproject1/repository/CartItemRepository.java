package com.myfullstackproject.springbootproject1.repository;

import com.myfullstackproject.springbootproject1.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findByUtilisateur_Id(Long utilisateurId);
}