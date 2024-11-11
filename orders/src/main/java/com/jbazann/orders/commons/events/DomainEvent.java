package com.jbazann.orders.commons.events;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Accessors(fluent = true)
public abstract class DomainEvent {
 /*
 Intentionally not extending ApplicationEvent because lombok complains about constructors,
 and we don't care about source object anyway.
 */

    private final UUID id;
    private final LocalDateTime timestamp;
    private Type type;

    public DomainEvent() {
        this.id = UUID.randomUUID(); // TODO replace with safe alternative
        this.timestamp = LocalDateTime.now();
        this.type = Type.UNKNOWN;
    }

    public enum Type{
        /**
         * Used to trigger saga-pattern operations.
         */
        REQUEST,
        /**
         * Used to inform success of saga-pattern operations.
         */
        SUCCESS,
        /**
         * Used to inform failure of saga-pattern operations.
         */
        FAILURE,
        /**
         * Null-safe placeholder.
         */
        UNKNOWN;
    }

}
