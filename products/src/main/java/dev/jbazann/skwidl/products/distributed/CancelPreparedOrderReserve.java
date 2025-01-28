package dev.jbazann.skwidl.products.distributed;

import dev.jbazann.skwidl.commons.async.events.DomainEvent;
import dev.jbazann.skwidl.commons.async.events.specialized.CancelPreparedOrderEvent;
import dev.jbazann.skwidl.commons.async.transactions.TransactionResult;
import dev.jbazann.skwidl.commons.async.transactions.api.Stage;
import dev.jbazann.skwidl.commons.async.transactions.api.TransactionLifecycleActions;
import dev.jbazann.skwidl.commons.async.transactions.api.TransactionStage;
import dev.jbazann.skwidl.commons.async.transactions.api.TransactionStageBean;
import dev.jbazann.skwidl.commons.async.transactions.api.locking.EntityLock;
import dev.jbazann.skwidl.commons.async.transactions.entities.Transaction;
import dev.jbazann.skwidl.products.product.Product;
import dev.jbazann.skwidl.products.product.ProductLifecycleActions;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.*;

@TransactionStageBean(
        value = "CancelPreparedOrderReserve",
        eventClass = CancelPreparedOrderEvent.class,
        stage = Stage.RESERVE
)
public class CancelPreparedOrderReserve implements TransactionStage {

    private final ProductLifecycleActions actions;
    private final TransactionLifecycleActions transactions;

    public CancelPreparedOrderReserve(ProductLifecycleActions actions, TransactionLifecycleActions transactions) {
        this.actions = actions;
        this.transactions = transactions;
    }

    @Override
    public List<EntityLock> getRequiredLocks(DomainEvent domainEvent) {
        return ((CancelPreparedOrderEvent) domainEvent).returnedStock().keySet().stream()
                .map(k -> new EntityLock(k.toString(), Product.class))
                .toList();
    }

    @Override
    public TransactionResult runStage(@NotNull @Valid DomainEvent domainEvent,
                                      @NotNull @Valid Transaction transaction) {
        if (!(domainEvent instanceof CancelPreparedOrderEvent event))
            throw new IllegalArgumentException("Wrong DomainEvent type.");

        final Collection<Product> products = actions.fetchAll(event.returnedStock().keySet());
        final Map<UUID, Integer> stock = event.returnedStock();

        if (stock.size() != products.size()) {
            StringBuilder message = new StringBuilder("Could not find products for ID(s): ");
            List<UUID> foundIds = products.stream().map(Product::id).toList();
            stock.keySet().stream().filter(k -> !foundIds.contains(k))
                    .forEach(k -> message.append(k).append(", "));
            message.setLength(message.length() - 2);
            message.append('.');
            transaction = transactions.reject(transaction);
            return new TransactionResult()
                    .data(transaction)
                    .simpleResult(TransactionResult.SimpleResult.FAILURE)
                    .context(message.toString());
        }

        products.forEach(p -> p.currentStock(p.currentStock() + stock.get(p.id())));
        actions.saveAll(products);
        transaction = transactions.accept(transaction);
        return new TransactionResult()
                .data(transaction)
                .simpleResult(TransactionResult.SimpleResult.SUCCESS)
                .context("Stock returned.");
    }
}
