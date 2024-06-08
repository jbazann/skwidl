package com.jbazann.customers.domain.entities;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Accessors(chain = true, fluent = true)
@Entity
@Table(name = "customer", schema = "customers")
public class Customer {

    @Column @Id private UUID id;
    @Column private String name;
    @Column private String email;
    @Column private String cuit;
    @Column private int maxDebt;
    @Column private int maxActiveSites;

}
