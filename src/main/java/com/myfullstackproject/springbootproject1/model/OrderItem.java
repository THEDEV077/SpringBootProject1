package com.myfullstackproject.springbootproject1.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "lignes_commande")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "items"})
    @ManyToOne
    @JoinColumn(name = "commande_id")
    private Order order;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "categorie", "utilisateur", "images"})
    @ManyToOne
    @JoinColumn(name = "produit_id")
    private com.myfullstackproject.springbootproject1.model.Product product;

    @Column(name = "quantite")
    private Integer quantity;

    @Column(name = "prix_unitaire")
    private Double unitPrice;
}