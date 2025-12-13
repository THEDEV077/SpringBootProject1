package model;

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

    @Column(name = "acheteur_id")
    private String buyerId;

    @ManyToOne
    @JoinColumn(name = "produit_id")
    private model.Product product;

    @Column(name = "quantite")
    private Integer quantity;
}