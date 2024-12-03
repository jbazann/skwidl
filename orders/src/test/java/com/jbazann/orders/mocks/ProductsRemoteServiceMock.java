package com.jbazann.orders.mocks;

import com.jbazann.orders.order.dto.OrderDTO;
import com.jbazann.orders.order.dto.ProductAmountDTO;
import com.jbazann.orders.order.services.ProductsRemoteService;
import com.jbazann.orders.order.services.ProductsRemoteServiceInterface;
import com.jbazann.orders.testdata.StandardDataset;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class ProductsRemoteServiceMock implements ProductsRemoteServiceInterface {

    private final ProductsRemoteServiceInterface mock = Mockito.mock();
    private final Function<UUID, BigDecimal> getTotalCost = Mockito.mock();
    private final Function<UUID, BigDecimal> getUnitCost = Mockito.mock();

    public ProductsRemoteServiceMock() {
        mockProductCost();
        when(mock.validateProductAndFetchCost(any()))
                .thenAnswer((Answer<CompletableFuture<Map<String, Object>>>) (invocationOnMock) -> {
                    final List<Map<String, Object>> batch = invocationOnMock.getArgument(0);
                    final Map<String, Object> response = new HashMap<>();
                    response.put(ProductsRemoteService.PRODUCTS_EXIST, Boolean.TRUE);
                    response.put(ProductsRemoteService.STOCK_AVAILABLE, Boolean.TRUE);
                    response.put(ProductsRemoteService.TOTAL_COST,
                            batch.stream().map(element -> element.get(ProductsRemoteService.PRODUCT_ID))
                                    .map(id -> (UUID) id)
                                    .map(getTotalCost)
                                    .reduce(BigDecimal::add)
                    );
                    batch.stream().map(element -> element.get(ProductsRemoteService.PRODUCT_ID))
                            .map(id -> (UUID) id)
                            .forEach(product -> response.put(product.toString(), getUnitCost.apply(product)));
                    return CompletableFuture.completedFuture(response);
                });
        when(mock.reserveProducts(any())).thenReturn(true);
    }

    @Override
    public CompletableFuture<Map<String, Object>> validateProductAndFetchCost(List<Map<String, Object>> batch) {
        return mock.validateProductAndFetchCost(batch);
    }

    @Override
    public Boolean reserveProducts(Map<UUID, ProductAmountDTO> products) {
        return mock.reserveProducts(products);
    }

    /**
     * This simulates the "FetchCost" part of {@link ProductsRemoteServiceInterface#validateProductAndFetchCost(List)}
     * by simply returning the calculated total cost from every relevant {@link com.jbazann.orders.order.entities.Detail}
     * in the test data.
     */
    private void mockProductCost() {
        when(getTotalCost.apply(any(UUID.class)))
                .thenReturn(new BigDecimal(0));
        when(getUnitCost.apply(any(UUID.class)))
                .thenReturn(new BigDecimal(0));
        Stream.of(StandardDataset.values())
                .filter(StandardDataset::hasOrder)
                .map(StandardDataset::asOrderDTO)
                .map(OrderDTO::detail)
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .forEach(detail -> {
                    when(getTotalCost.apply(eq(detail.product())))
                            .thenReturn(detail.totalCost());
                    when(getUnitCost.apply(eq(detail.product())))
                            .thenReturn(detail.unitCost());
                });
    }

}
