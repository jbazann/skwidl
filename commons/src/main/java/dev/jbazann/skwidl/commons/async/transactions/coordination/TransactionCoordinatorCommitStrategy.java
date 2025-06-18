package dev.jbazann.skwidl.commons.async.transactions.coordination;

import dev.jbazann.skwidl.commons.async.events.DomainEvent;
import dev.jbazann.skwidl.commons.async.events.DomainEventBuilder;
import dev.jbazann.skwidl.commons.async.events.DomainEventBuilderFactory;
import dev.jbazann.skwidl.commons.async.transactions.entities.CoordinatedTransaction;

import java.util.Optional;

public class TransactionCoordinatorCommitStrategy implements TransactionCoordinatorStrategy {

    private final CoordinatedTransaction transaction;
    private final DomainEvent event;
    private final DomainEventBuilderFactory events;

    public TransactionCoordinatorCommitStrategy(CoordinatedTransaction transaction, DomainEvent event, DomainEventBuilderFactory events) {
        this.transaction = transaction;
        this.event = event;
        this.events = events;
    }

    @Override
    public TransactionCoordinatorStrategyResult getResult() {
        if(DomainEvent.Type.COMMIT.equals(event.type())) transaction.addCommit(event.sentBy());

        if(transaction.isFullyCommitted())
            transaction.status(CoordinatedTransaction.TransactionStatus.CONCLUDED_COMMIT);

        DomainEvent response = !transaction.isFullyCommitted() ? null :
                events.create(event.getClass())
                        .answer(event)
                        .setType(DomainEvent.Type.ACK)
                        .setContext("Transaction concluded by commit.")
                        .build();

        return new TransactionCoordinatorStrategyResult(
                response == null ? Optional.empty() : Optional.of(response),
                transaction
        );
    }

}
