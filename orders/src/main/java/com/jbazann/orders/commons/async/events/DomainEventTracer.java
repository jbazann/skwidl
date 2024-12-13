package com.jbazann.orders.commons.async.events;

import com.jbazann.orders.commons.async.rabbitmq.RabbitPublisher;
import com.jbazann.orders.commons.identity.ApplicationMember;

import static com.jbazann.orders.commons.async.events.DomainEvent.Type.*;

/**
 * Provides utility methods to publish well-formed events that
 * are sent in response to previous ones.
 */
public class DomainEventTracer {

    private final ApplicationMember member;
    private final RabbitPublisher publisher;

    public DomainEventTracer(ApplicationMember member, RabbitPublisher publisher) {
        this.member = member;
        this.publisher = publisher;
    }

    public void request(DomainEvent event, String context) {
        publisher.publish(DomainEvent.copyOf(event)
                .sentBy(member)
                .type(REQUEST)
                .context(context));
    }

    public void acknowledge(DomainEvent event, String context) {
        publisher.publish(DomainEvent.copyOf(event)
                .sentBy(member)
                .type(ACK)
                .context(context));
    }

    public void reject(DomainEvent event, String context) {
        publisher.publish(DomainEvent.copyOf(event)
                .sentBy(member)
                .type(REJECT)
                .context(context));
    }

    public void rollback(DomainEvent event, String context) {
        publisher.publish(DomainEvent.copyOf(event)
                .sentBy(member)
                .type(ROLLBACK)
                .context(context));
    }

    public void accept(DomainEvent event, String context) {
        publisher.publish(DomainEvent.copyOf(event)
                .sentBy(member)
                .type(ACCEPT)
                .context(context));
    }

    public void commit(DomainEvent event, String context) {
        publisher.publish(DomainEvent.copyOf(event)
                .sentBy(member)
                .type(COMMIT)
                .context(context));
    }

    public void error(DomainEvent event, String context) {
        publisher.publish(DomainEvent.copyOf(event)
                .sentBy(member)
                .type(ERROR)
                .context(context));
    }

    public void discard(DomainEvent event, String context) {
        publisher.publish(DiscardedEvent.discard(event, context)
                .sentBy(member));
    }



}
