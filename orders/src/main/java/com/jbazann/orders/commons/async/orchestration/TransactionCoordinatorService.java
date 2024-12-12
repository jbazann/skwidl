package com.jbazann.orders.commons.async.orchestration;

import com.jbazann.orders.commons.async.events.DomainEvent;
import com.jbazann.orders.commons.async.events.DomainEventTracer;
import com.jbazann.orders.commons.async.rabbitmq.RabbitPublisher;
import com.jbazann.orders.commons.async.transactions.TransactionCoordinatorData;
import com.jbazann.orders.commons.async.transactions.TransactionCoordinatorDataRepository;
import com.jbazann.orders.commons.identity.ApplicationMember;

import static com.jbazann.orders.commons.async.transactions.TransactionCoordinatorData.TransactionStatus.*;

public class TransactionCoordinatorService {

    private final ApplicationMember member;
    private final RabbitPublisher publisher;
    private final TransactionCoordinatorDataRepository repository;
    private final DomainEventTracer tracer;

    public TransactionCoordinatorService(ApplicationMember member, RabbitPublisher publisher, TransactionCoordinatorDataRepository repository, DomainEventTracer tracer) {
        this.member = member;
        this.publisher = publisher;
        this.repository = repository;
        this.tracer = tracer;
    }

    public void coordinate(DomainEvent event) {
        if(!member.equals(event.transactionQuorum().coordinator())) return;

        TransactionCoordinatorData transaction = repository.findByIdOrCreate(event.transactionId());
        if(UNINITIALIZED.equals(transaction.transactionStatus())) transaction = register(event, transaction);

        if(transaction.expired()) {
            expired(event, transaction);
            return;
        }
        if(transaction.concluded()) {
            publishAck(event, transaction);
            return;
        }

        switch(event.type()) {
            case ACCEPT -> transaction = accept(event, transaction);
            case REJECT -> transaction = reject(event, transaction);
            case ROLLBACK -> transaction = rollback(event, transaction);
            case ACK -> transaction = ackCommit(event, transaction);
        }

        if(transaction.allAccepted() && transaction.notRejected()) transaction = commit(event, transaction);
        publishAck(event, transaction);
    }

    private TransactionCoordinatorData register(DomainEvent event, TransactionCoordinatorData transaction) {
        if(!UNINITIALIZED.equals(transaction.transactionStatus())) return transaction;
        transaction.setQuorum(event.transactionQuorum().quorum())
                .expires(event.expires())
                .transactionStatus(STARTED);
        return repository.persist(transaction);
    }

    private void expired(DomainEvent event, TransactionCoordinatorData transaction) {
        if(!CONCLUDED_EXPIRED.equals(transaction.transactionStatus()))
            repository.persist(transaction.transactionStatus(CONCLUDED_EXPIRED));
        publisher.publish(tracer.reject(event, "Transactional operation timed out."));
    }

    private TransactionCoordinatorData commit(DomainEvent event, TransactionCoordinatorData transaction) {
        if (!transaction.isCommitted()) {
            publisher.publish(tracer.commit(event, "Accepted by full quorum."));
            // Only persist after publishing to protect against double write inconsistencies.
            return repository.persist(transaction.isCommitted(true));
        }
        return transaction;
    }

    private TransactionCoordinatorData reject(DomainEvent event, TransactionCoordinatorData transaction) {
        transaction.reject(event.sentBy());
        if (transaction.allRejected())
            return repository.persist(transaction.transactionStatus(CONCLUDED_REJECT));
        return transaction;
    }

    private TransactionCoordinatorData accept(DomainEvent event, TransactionCoordinatorData transaction) {
        transaction.accept(event.sentBy());
        return repository.persist(transaction.transactionStatus(ACCEPTED));
    }

    private TransactionCoordinatorData rollback(DomainEvent event, TransactionCoordinatorData transaction) {
        transaction.rollback(event.sentBy());
        if(transaction.allRejected())
            transaction.transactionStatus(CONCLUDED_REJECT);
        return repository.persist(transaction);
    }

    private TransactionCoordinatorData ackCommit(DomainEvent event, TransactionCoordinatorData transaction) {
        if(!transaction.isCommitted()) return transaction;// TODO maybe something should happen here.
        transaction.commit(event.sentBy());
        if(transaction.allCommitted()) transaction.transactionStatus(CONCLUDED_COMMIT);
        return repository.persist(transaction);
    }

    private void publishAck(DomainEvent event, TransactionCoordinatorData transaction) {
        publisher.publish(tracer.acknowledge(event, "Processed by coordinator."));
    }

}
