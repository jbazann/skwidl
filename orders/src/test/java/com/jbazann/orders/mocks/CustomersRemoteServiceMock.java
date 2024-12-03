package com.jbazann.orders.mocks;

import com.jbazann.orders.order.exceptions.CustomerNotFoundException;
import com.jbazann.orders.order.services.CustomersRemoteServiceInterface;
import com.jbazann.orders.testdata.StandardDataset;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class CustomersRemoteServiceMock implements CustomersRemoteServiceInterface {

    private final CustomersRemoteServiceInterface mock = Mockito.mock();

    public CustomersRemoteServiceMock() {
        System.out.println("TPDAN: ### Building mocks: CustomerRemoteServiceMock.");// TODO print

        //
        // billFor() mocks here.
        //
        when(mock.billFor(any(UUID.class),any(BigDecimal.class)))
                .thenReturn(false);
        Arrays.stream(StandardDataset.values())
                .filter(StandardDataset::hasCustomer)
                .map(StandardDataset::getCustomer)
                .forEach(customer -> when(mock.billFor(eq(customer.id()),any(BigDecimal.class)))
                        .thenAnswer((Answer<Boolean>) (invocationOnMock) ->
                                customer.bill(invocationOnMock.getArgument(1))
                        )
                );

        //
        // validateCustomerAndFetchBudget() mocks here.
        //
        when(mock.validateCustomerAndFetchBudget(any(UUID.class)))
                .thenReturn(
                        // All unknown Customers are valid but have no budget.
                        CompletableFuture.completedFuture(BigDecimal.ZERO)
                );
        Arrays.stream(StandardDataset.values())
                .filter(StandardDataset::hasCustomer)
                .map(StandardDataset::getCustomer)
                .forEach(customer -> when(mock.validateCustomerAndFetchBudget(eq(customer.id())))
                        .thenAnswer((Answer<CompletableFuture<BigDecimal>>) (invocationOnMock) ->
                                CompletableFuture.completedFuture(customer.budget())
                        )
                );
        Assertions.assertTrue(StandardDataset.INVALID_CUSTOMER.hasCustomer());
        when(mock.validateCustomerAndFetchBudget(eq(StandardDataset.INVALID_CUSTOMER.customerId())))
                .thenThrow(new CustomerNotFoundException("Testing Exception."));

    }

    @Override
    public Boolean billFor(UUID id, BigDecimal amount) {
        return mock.billFor(id, amount);
    }

    @Override
    public CompletableFuture<BigDecimal> validateCustomerAndFetchBudget(UUID id) {
        return mock.validateCustomerAndFetchBudget(id);
    }
}
