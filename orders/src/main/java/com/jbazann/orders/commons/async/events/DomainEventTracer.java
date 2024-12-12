package com.jbazann.orders.commons.async.events;

import com.jbazann.orders.commons.identity.ApplicationMember;

import static com.jbazann.orders.commons.async.events.DomainEvent.Type.*;

public class DomainEventTracer {

    private final ApplicationMember member;

    public DomainEventTracer(ApplicationMember member) {
        this.member = member;
    }

    public DomainEvent request(DomainEvent event, String context) {
        return DomainEvent.reEmit(event)
                .sentBy(member)
                .type(REQUEST)
                .context(context)
                .setRouting();
    }

    public DomainEvent acknowledge(DomainEvent event, String context) {
        return DomainEvent.reEmit(event)
                .sentBy(member)
                .type(ACK)
                .context(context)
                .setRouting();
    }

    public DomainEvent reject(DomainEvent event, String context) {
        return DomainEvent.reEmit(event)
                .sentBy(member)
                .type(REJECT)
                .context(context)
                .setRouting();
    }

    public DomainEvent rollback(DomainEvent event, String context) {
        return DomainEvent.reEmit(event)
                .sentBy(member)
                .type(ROLLBACK)
                .context(context)
                .setRouting();
    }

    public DomainEvent accept(DomainEvent event, String context) {
        return DomainEvent.reEmit(event)
                .sentBy(member)
                .type(ACCEPT)
                .context(context)
                .setRouting();
    }

    public DomainEvent commit(DomainEvent event, String context) {
        return DomainEvent.reEmit(event)
                .sentBy(member)
                .type(COMMIT)
                .context(context)
                .setRouting();
    }

    public DomainEvent error(DomainEvent event, String context) {
        return DomainEvent.reEmit(event)
                .sentBy(member)
                .type(ERROR)
                .context(context)
                .setRouting();
    }

    public DomainEvent discard(DomainEvent event, String context) {
        return DiscardedEvent.from(event)
                .sentBy(member)
                .context(context)
                .setRouting();
    }



}
