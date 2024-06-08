package com.jbazann.customers.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true, fluent = true)
@Entity
@Table(name = "site", schema = "customers")
public class Site {

    @Column @Id private UUID id;
    @Column private String address;
    @Column private String coordinates;
    @Column @ManyToOne private Customer customer;

}
