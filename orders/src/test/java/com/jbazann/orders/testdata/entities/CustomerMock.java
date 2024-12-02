package com.jbazann.orders.testdata.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.UUID;


/**
 * A <em>minimal</em> state representation of a remote entity meant to simplify mock logic semantics.
 * It should only contain data and logic to easily simulate contextual conditions relevant to the testing.
 * It should NOT attempt to simulate the entity's actual behavior as managed by the remote service.
 */
@Getter
@Setter
@Accessors(chain = true, fluent = true)
@AllArgsConstructor
@NoArgsConstructor
public class CustomerMock {

    private UUID id;
    private BigDecimal budget;
    private BigDecimal budgetResetValue;

    public CustomerMock copy() {
        return new CustomerMock(id, budget, budgetResetValue);
    }

    public boolean bill(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) < 0) return false;
        if (budget.compareTo(amount) < 0) return false;
        budget = budget.subtract(amount);
        return true;
    }
    
    public void resetBudget() {
        budget = budgetResetValue;
    }

}
