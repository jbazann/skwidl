package dev.jbazann.skwidl.products.category;

import dev.jbazann.skwidl.products.category.dto.CategoryDTO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.UUID;

@SuppressWarnings("JpaDataSourceORMInspection")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Accessors(chain = true, fluent = true)
@Entity
@Table(name = "category",schema = "category")
public class Category {

    @Column @Id
    @NotNull
    private UUID id;
    @Column
    @NotNull @NotEmpty
    private String name;

    public CategoryDTO toDto() {
        return null;
    }
}
