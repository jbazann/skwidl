package dev.jbazann.skwidl.commons.async.orchestration;

import dev.jbazann.skwidl.commons.async.events.DomainEvent;
import dev.jbazann.skwidl.commons.async.events.DomainEventBuilder;
import dev.jbazann.skwidl.commons.async.events.EventAnswerPublisher;
import dev.jbazann.skwidl.commons.async.transactions.entities.CoordinatedTransaction;
import dev.jbazann.skwidl.commons.async.transactions.entities.CoordinatedTransactionRepository;
import dev.jbazann.skwidl.commons.identity.ApplicationMember;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

public class TransactionCoordinatorService {

    private final ApplicationMember member;
    private final CoordinatedTransactionRepository repository;
    private final EventAnswerPublisher publisher;
    private final DomainEventBuilder builder;


    public TransactionCoordinatorService(ApplicationMember member, CoordinatedTransactionRepository repository, EventAnswerPublisher publisher, DomainEventBuilder builder) {
        this.member = member;
        this.repository = repository;
        this.publisher = publisher;
        this.builder = builder;
    }

    @RabbitListener(queues = "${jbazann.rabbit.queues.event}")
    public void coordinate(DomainEvent event) {
        if(!member.equals(event.transaction().quorum().coordinator())) return;

        CoordinatedTransaction transaction = getForEvent(event);
        if(transaction.isExpired()) {
            expired(event, transaction);
            return;
        }
        if(transaction.isConcluded()) {
            acknowledge(event, transaction);
            return;
        }

        switch(event.type()) {
            case ACCEPT -> transaction = accept(event, transaction);
            case REJECT -> transaction = reject(event, transaction);
            case ROLLBACK -> transaction = rollback(event, transaction);
            case ACK -> transaction = ackCommit(event, transaction);
        }

        if(transaction.isFullyAccepted() && !transaction.isRejected()) transaction = commit(event, transaction);
        acknowledge(event, transaction);
    }

    private CoordinatedTransaction getForEvent(DomainEvent event) {
        CoordinatedTransaction transaction = repository.findById(event.transaction().id()).orElse(null);
        if(transaction == null) transaction = repository.save(CoordinatedTransaction.from(event));
        return transaction;
    }

    private void expired(DomainEvent event, CoordinatedTransaction transaction) {
        if(!CoordinatedTransaction.TransactionStatus.CONCLUDED_EXPIRED.equals(transaction.status()))
            repository.save(transaction.status(CoordinatedTransaction.TransactionStatus.CONCLUDED_EXPIRED));
        publisher.reject(builder.answer(event),"Transactional operation timed out.");
    }

    private CoordinatedTransaction commit(DomainEvent event, CoordinatedTransaction transaction) {
        if (!transaction.isCommitted()) {
            publisher.commit(builder.answer(event),"Accepted by full quorum.");
            // Only persist after publishing to protect against double write inconsistencies.
            // Duplicated commit events are acceptable. TODO are they *actually*?
            return repository.save(transaction.isCommitted(true));
        }
        return transaction;
    }

    private CoordinatedTransaction reject(DomainEvent event, CoordinatedTransaction transaction) {
        transaction.addReject(event.sentBy());
        if (transaction.isFullyRejected())
            return repository.save(transaction.status(CoordinatedTransaction.TransactionStatus.CONCLUDED_REJECT));
        return transaction;
    }

    private CoordinatedTransaction accept(DomainEvent event, CoordinatedTransaction transaction) {
        transaction.addAccept(event.sentBy());
        return repository.save(transaction.status(CoordinatedTransaction.TransactionStatus.ACCEPTED));
    }

    private CoordinatedTransaction rollback(DomainEvent event, CoordinatedTransaction transaction) {
        transaction.addRollback(event.sentBy());
        if(transaction.isFullyRejected())
            transaction.status(CoordinatedTransaction.TransactionStatus.CONCLUDED_REJECT);
        return repository.save(transaction);
    }

    private CoordinatedTransaction ackCommit(DomainEvent event, CoordinatedTransaction transaction) {
        if(!transaction.isCommitted()) return transaction;// TODO maybe something should happen here.
        transaction.addCommit(event.sentBy());
        if(transaction.isFullyCommitted()) transaction.status(CoordinatedTransaction.TransactionStatus.CONCLUDED_COMMIT);
        return repository.save(transaction);
    }

    private void acknowledge(DomainEvent event, CoordinatedTransaction transaction) {
        publisher.acknowledge(builder.answer(event), "Processed by coordinator.");
    }

}
