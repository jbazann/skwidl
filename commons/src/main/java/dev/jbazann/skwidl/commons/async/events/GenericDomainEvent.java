package dev.jbazann.skwidl.commons.async.events;

public class GenericDomainEvent extends DomainEvent {
    @Override
    protected DomainEvent copy() {
        return this;
    }
}
