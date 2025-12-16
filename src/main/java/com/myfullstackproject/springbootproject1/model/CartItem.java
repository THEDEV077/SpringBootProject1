package com.myfullstackproject.springbootproject1.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "articles_panier")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur utilisateur;

    @ManyToOne
    @JoinColumn(name = "produit_id")
    private com.myfullstackproject.springbootproject1.model.Product product;

    @Column(name = "quantite")
    private Integer quantity;
}