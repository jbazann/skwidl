package com.jbazann.products.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Accessors(chain = true, fluent = true)
@Entity
@Table(name = "product",schema = "products")
public class Product {

    @Column @Id private UUID id;
    @Column private String name;
    @Column private String description;
    @Column private Double price;//TODO float money
    @Column private int currentStock;
    @Column private int minimumStock;
    @Column @ManyToOne private Category category;

}
