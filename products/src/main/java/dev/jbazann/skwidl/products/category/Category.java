package dev.jbazann.skwidl.products.category;

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
@Setter
@Getter
@Accessors(chain = true, fluent = true)
@Entity
@Table(name = "category",schema = "categories")
public class Category {

    @Column @Id private UUID id;
    @Column private String name;

}
