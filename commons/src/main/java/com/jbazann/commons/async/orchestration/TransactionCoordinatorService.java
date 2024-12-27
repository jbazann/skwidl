package com.jbazann.commons.async.orchestration;

import com.jbazann.commons.async.events.DomainEvent;
import com.jbazann.commons.async.events.DomainEventBuilder;
import com.jbazann.commons.async.events.EventAnswerPublisher;
import com.jbazann.commons.async.transactions.api.implement.CoordinatedTransactionRepository;
import com.jbazann.commons.async.transactions.TransientCoordinatedTransaction;
import com.jbazann.commons.identity.ApplicationMember;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

import static com.jbazann.commons.async.transactions.TransientCoordinatedTransaction.TransactionStatus.*;

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

        TransientCoordinatedTransaction transaction = getForEvent(event);
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

    private TransientCoordinatedTransaction getForEvent(DomainEvent event) {
        TransientCoordinatedTransaction transaction = (TransientCoordinatedTransaction)
                repository.findById(event.transaction().id());
        if(transaction == null) transaction = (TransientCoordinatedTransaction)
                repository.save(TransientCoordinatedTransaction.from(event));
        return transaction;
    }

    private void expired(DomainEvent event, TransientCoordinatedTransaction transaction) {
        if(!CONCLUDED_EXPIRED.equals(transaction.status()))
            repository.save(transaction.status(CONCLUDED_EXPIRED));
        publisher.reject(builder.answer(event),"Transactional operation timed out.");
    }

    private TransientCoordinatedTransaction commit(DomainEvent event, TransientCoordinatedTransaction transaction) {
        if (!transaction.isCommitted()) {
            publisher.commit(builder.answer(event),"Accepted by full quorum.");
            // Only persist after publishing to protect against double write inconsistencies.
            // Duplicated commit events are acceptable. TODO are they *actually*?
            return (TransientCoordinatedTransaction) repository.save(transaction.isCommitted(true));
        }
        return transaction;
    }

    private TransientCoordinatedTransaction reject(DomainEvent event, TransientCoordinatedTransaction transaction) {
        transaction.addReject(event.sentBy());
        if (transaction.isFullyRejected())
            return (TransientCoordinatedTransaction) repository.save(transaction.status(CONCLUDED_REJECT));
        return transaction;
    }

    private TransientCoordinatedTransaction accept(DomainEvent event, TransientCoordinatedTransaction transaction) {
        transaction.addAccept(event.sentBy());
        return (TransientCoordinatedTransaction) repository.save(transaction.status(ACCEPTED));
    }

    private TransientCoordinatedTransaction rollback(DomainEvent event, TransientCoordinatedTransaction transaction) {
        transaction.addRollback(event.sentBy());
        if(transaction.isFullyRejected())
            transaction.status(CONCLUDED_REJECT);
        return (TransientCoordinatedTransaction) repository.save(transaction);
    }

    private TransientCoordinatedTransaction ackCommit(DomainEvent event, TransientCoordinatedTransaction transaction) {
        if(!transaction.isCommitted()) return transaction;// TODO maybe something should happen here.
        transaction.addCommit(event.sentBy());
        if(transaction.isFullyCommitted()) transaction.status(CONCLUDED_COMMIT);
        return (TransientCoordinatedTransaction) repository.save(transaction);
    }

    private void acknowledge(DomainEvent event, TransientCoordinatedTransaction transaction) {
        publisher.acknowledge(builder.answer(event), "Processed by coordinator.");
    }

}
