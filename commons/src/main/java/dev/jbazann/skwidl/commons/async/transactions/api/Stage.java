package dev.jbazann.skwidl.commons.async.transactions.api;

import dev.jbazann.skwidl.commons.async.events.DomainEvent;

public enum Stage {
    RESERVE(DomainEvent.Type.REQUEST),
    COMMIT(DomainEvent.Type.COMMIT),
    ROLLBACK(DomainEvent.Type.ROLLBACK),
    ;

    public final DomainEvent.Type trigger;
    private Stage(DomainEvent.Type trigger) {
        this.trigger = trigger;
    }

}
