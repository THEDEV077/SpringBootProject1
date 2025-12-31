package com.myfullstackproject.springbootproject1.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "avis_produits")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnoreProperties({"produits", "ratings"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur utilisateur;

    @JsonIgnoreProperties({"categorie", "utilisateur", "ratings"})
    @ManyToOne
    @JoinColumn(name = "produit_id")
    private com.myfullstackproject.springbootproject1.model.Product product;

    @Column(name = "nb_etoiles")
    private Integer stars;   // 1 à 5

    @Column(name = "commentaire")
    private String comment;

    @Column(name = "date_creation")
    private LocalDateTime createdAt;
}