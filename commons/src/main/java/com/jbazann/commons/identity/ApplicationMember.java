package com.jbazann.commons.identity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * Represents the role a given artifact has in the distributed application.
 * Services that make use of certain generic mechanisms from the 'commons' module
 * must declare exactly one Spring bean of this type. Beans defined in different
 * instances of the same service must be equal.
 * <br>
 * Instances of this class may also be used to represent other artifacts,
 * as seen in {@link com.jbazann.commons.async.transactions.TransactionQuorum}.
 */
@Data
@Accessors(fluent = true)
@EqualsAndHashCode
public final class ApplicationMember {

    /**
     * A unique ID for each different service. The same for
     * replicas of the same service.
     */
    private final String id;

}
