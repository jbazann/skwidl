package dev.jbazann.skwidl.orders.order.entities;

import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.mapping.Document;


/**
 * As long as this entity exists in the code, this service cannot be
 * replicated. It is intended to support the assignation of a range unique
 * to a service replica, in which sequentially generated order numbers
 * must be guaranteed to be unique.
 * Dealing with this, either by actually solving the problem or by properly documenting
 * it, sounds like a problem too big for a toy project in this state. I most
 * likely won't ever return to fix this.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
@Document
@ToString
public class DangerousIllegalBadSinfulOrderNumberRange {

    private long base;
    private long max;

}
