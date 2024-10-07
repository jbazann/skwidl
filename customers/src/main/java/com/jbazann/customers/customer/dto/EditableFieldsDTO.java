package com.jbazann.customers.customer.dto;

import com.jbazann.customers.customer.Customer;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Accessors(chain = true, fluent = true)
public class EditableFieldsDTO {

    private String name;
    private String email;
    private String cuit;

    /**
     * Utility method that calls setters on the passed entity with the
     * values of the non-null fields of the instance this was called on.
     * @param base a customer to update.
     * @return the updated customer.
     */
    public Customer update(@NotNull Customer base) {
        return base.name(name == null ? base.name() : name)
                .email(email == null ? base.email() : email)
                .cuit(cuit == null ? base.cuit() : cuit);
    }

}
