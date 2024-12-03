package com.jbazann.orders.testdata.entities;

import java.math.BigDecimal;

public class StandardDatasetDataBuilder {

    private final StandardDatasetData target;

    public StandardDatasetDataBuilder(StandardDatasetData target) {
        this.target = target;
    }

    public static StandardDatasetDataBuilder startFrom(StandardDatasetData target) {
        return new StandardDatasetDataBuilder(target);
    }

    public StandardDatasetData build() {
        return target;
    }

    public StandardDatasetDataBuilder setCustomerBudget(BigDecimal budget) {
        if (target.customer() == null) {
            target.customer(CustomerMock.nonNull()
                    .id(target.order().customer()));
        }
        target.customer()
                .budget(budget)
                .budgetResetValue(budget);
        return this;
    }

}
