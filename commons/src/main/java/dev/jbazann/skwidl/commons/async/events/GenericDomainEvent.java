package dev.jbazann.skwidl.commons.async.events;

import lombok.ToString;

@ToString
public class GenericDomainEvent extends DomainEvent {
    @Override
    protected DomainEvent copy() {
        return new GenericDomainEvent();
    }
}
