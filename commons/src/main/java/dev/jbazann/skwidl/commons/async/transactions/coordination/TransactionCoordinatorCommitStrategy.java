package dev.jbazann.skwidl.commons.async.transactions.coordination;

import dev.jbazann.skwidl.commons.async.events.DomainEvent;
import dev.jbazann.skwidl.commons.async.events.DomainEventBuilder;
import dev.jbazann.skwidl.commons.async.transactions.entities.CoordinatedTransaction;

import java.util.Optional;

public class TransactionCoordinatorCommitStrategy implements TransactionCoordinatorStrategy {

    private final CoordinatedTransaction transaction;
    private final DomainEvent event;
    private final DomainEventBuilder builder;

    protected TransactionCoordinatorCommitStrategy(CoordinatedTransaction transaction, DomainEvent event, DomainEventBuilder builder) {
        this.transaction = transaction;
        this.event = event;
        this.builder = builder;
    }

    @Override
    public TransactionCoordinatorStrategyResult getResult() {
        transaction.addCommit(event.sentBy());

        if(transaction.isFullyCommitted())
            transaction.status(CoordinatedTransaction.TransactionStatus.CONCLUDED_COMMIT);

        DomainEvent response = !transaction.isFullyCommitted() ? null :
                builder.answer(event)
                        .withType(DomainEvent.Type.ACK)
                        .withContext("Transaction concluded by commit.")
                        .asDomainEvent();

        return new TransactionCoordinatorStrategyResult(
                response == null ? Optional.empty() : Optional.of(response),
                transaction
        );
    }

}
