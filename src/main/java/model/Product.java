package model;

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

    @Column(name = "code_asin")
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
    private Integer ratingCount;
}