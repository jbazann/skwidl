package com.jbazann.customers.commons.events;

import lombok.*;
import lombok.experimental.Accessors;

import java.util.UUID;

@Data()
@Accessors(chain = true, fluent = true)
@EqualsAndHashCode(callSuper = true)
public class AddAllowedUserEvent extends DomainEvent {

    public UUID customerId;
    public UUID userId;

}
