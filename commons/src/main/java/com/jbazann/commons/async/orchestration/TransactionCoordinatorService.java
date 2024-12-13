package com.jbazann.commons.async.orchestration;

import com.jbazann.commons.async.events.DomainEvent;
import com.jbazann.commons.async.events.DomainEventTracer;
import com.jbazann.commons.async.transactions.TransactionCoordinatorData;
import com.jbazann.commons.async.transactions.TransactionCoordinatorDataRepository;
import com.jbazann.commons.identity.ApplicationMember;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

import static com.jbazann.commons.async.transactions.TransactionCoordinatorData.TransactionStatus.*;

public class TransactionCoordinatorService {

    private final ApplicationMember member;
    private final TransactionCoordinatorDataRepository repository;
    private final DomainEventTracer tracer;

    public TransactionCoordinatorService(ApplicationMember member, TransactionCoordinatorDataRepository repository, DomainEventTracer tracer) {
        this.member = member;
        this.repository = repository;
        this.tracer = tracer;
    }

    @RabbitListener(queues = "${jbazann.rabbit.queues.event}")
    public void coordinate(DomainEvent event) {
        if(!member.equals(event.transaction().quorum().coordinator())) return;

        TransactionCoordinatorData transaction = repository.getForEvent(event);
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

    private void expired(DomainEvent event, TransactionCoordinatorData transaction) {
        if(!CONCLUDED_EXPIRED.equals(transaction.status()))
            repository.persist(transaction.status(CONCLUDED_EXPIRED));
        tracer.reject(event, "Transactional operation timed out.");
    }

    private TransactionCoordinatorData commit(DomainEvent event, TransactionCoordinatorData transaction) {
        if (!transaction.isCommitted()) {
            tracer.commit(event, "Accepted by full quorum.");
            // Only persist after publishing to protect against double write inconsistencies.
            return repository.persist(transaction.isCommitted(true));
        }
        return transaction;
    }

    private TransactionCoordinatorData reject(DomainEvent event, TransactionCoordinatorData transaction) {
        transaction.addReject(event.sentBy());
        if (transaction.isFullyRejected())
            return repository.persist(transaction.status(CONCLUDED_REJECT));
        return transaction;
    }

    private TransactionCoordinatorData accept(DomainEvent event, TransactionCoordinatorData transaction) {
        transaction.addAccept(event.sentBy());
        return repository.persist(transaction.status(ACCEPTED));
    }

    private TransactionCoordinatorData rollback(DomainEvent event, TransactionCoordinatorData transaction) {
        transaction.addRollback(event.sentBy());
        if(transaction.isFullyRejected())
            transaction.status(CONCLUDED_REJECT);
        return repository.persist(transaction);
    }

    private TransactionCoordinatorData ackCommit(DomainEvent event, TransactionCoordinatorData transaction) {
        if(!transaction.isCommitted()) return transaction;// TODO maybe something should happen here.
        transaction.addCommit(event.sentBy());
        if(transaction.isFullyCommitted()) transaction.status(CONCLUDED_COMMIT);
        return repository.persist(transaction);
    }

    private void acknowledge(DomainEvent event, TransactionCoordinatorData transaction) {
        tracer.acknowledge(event, "Processed by coordinator.");
    }

}
