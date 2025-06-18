package dev.jbazann.skwidl.orders.testdata.entities;

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
        if (target.getCustomer() == null) {
            target.setCustomer(CustomerMock.nonNull()
                    .setId(target.getOrder().getCustomer()));
        }
        target.getCustomer()
                .setBudget(budget)
                .setBudgetResetValue(budget);
        return this;
    }

}
