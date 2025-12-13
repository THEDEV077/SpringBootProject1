package com.myfullstackproject.springbootproject1.model;

import jakarta.persistence.*;
import lombok.*;
//import model.OrderItem;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "commandes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "acheteur_id")
    private String buyerId;

    @Column(name = "date_creation")
    private LocalDateTime createdAt;

    @Column(name = "montant_total")
    private Double totalAmount;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> items;
}