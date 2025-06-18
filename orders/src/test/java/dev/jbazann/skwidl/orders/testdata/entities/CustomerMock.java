package dev.jbazann.skwidl.orders.testdata.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
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
@Accessors(chain = true)
@AllArgsConstructor
public class CustomerMock {

    private UUID id;
    private BigDecimal budget;
    private BigDecimal budgetResetValue;

    public CustomerMock copy() {
        return new CustomerMock(id, budget, budgetResetValue);
    }

    /**
     * For null safety where specific values aren't needed. This
     * should reduce the refactoring load when adding new fields.
     * Randomly regenerated fields should be replaced with values from a seeded
     * sequence for test consistency.
     * @return an instance with no null fields.
     */
    public static CustomerMock nonNull() {
        return new CustomerMock(UUID.randomUUID(), BigDecimal.ZERO, BigDecimal.ZERO);
    }

    public boolean bill(BigDecimal amount) {
        if (budget == null ||
                budget.compareTo(BigDecimal.ZERO) < 0 ||
                amount.compareTo(BigDecimal.ZERO) < 0 ||
                budget.compareTo(amount) < 0) return false;
        budget = budget.subtract(amount);
        return true;
    }
    
    public void resetBudget() {
        budget = budgetResetValue;
    }

}
