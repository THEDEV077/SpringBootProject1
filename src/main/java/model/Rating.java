package model;

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

    @Column(name = "acheteur_id")
    private String buyerId;

    @ManyToOne
    @JoinColumn(name = "produit_id")
    private model.Product product;

    @Column(name = "nb_etoiles")
    private Integer stars;   // 1 à 5

    @Column(name = "commentaire")
    private String comment;

    @Column(name = "date_creation")
    private LocalDateTime createdAt;
}