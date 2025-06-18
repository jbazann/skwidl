package dev.jbazann.skwidl.products.category;

import dev.jbazann.skwidl.products.category.dto.CategoryDTO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.UUID;

@SuppressWarnings("JpaDataSourceORMInspection")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Accessors(chain = true)
@ToString
@Entity
@Table(name = "category",schema = "category")
public class Category {

    @Column @Id
    @NotNull
    private UUID id;
    @Column
    @NotBlank
    private String name;

    public CategoryDTO toDto() {
        return new CategoryDTO(id, name);
    }

}
