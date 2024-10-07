package com.jbazann.customers.commons.events;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data()
@Accessors(chain = true, fluent = true)
@EqualsAndHashCode(callSuper = true)
public class AddAllowedUserFailureEvent extends AddAllowedUserEvent {

    public String cause;

}
