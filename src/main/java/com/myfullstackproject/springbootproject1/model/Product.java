package com.myfullstackproject.springbootproject1.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "produits")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code_asin", nullable = false, unique = true)
    private String asin;

    @Column(name = "titre")
    private String title;

    @Column(name = "categorie")
    private String category;

    @Column(name = "prix")
    private Double price;

    @Column(name = "note_moyenne")
    private Double rating;

    @Column(name = "nombre_notes")
    private Long ratingCount;  // Changé en Long pour Reviews Count (gros chiffres)

    @Column(name = "rang_amazon")
    private Integer rank;  // Changé en Integer pour le champ Rank du CSV

    @Column(name = "product_link")
    private String productLink;  // Nouveau pour Product Link du CSV

    // Les autres champs restent pour compatibilité/évolutions futures
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "nombre_avis")
    private Integer reviewsCount;
}