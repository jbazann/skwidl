package dev.jbazann.skwidl.commons.identity;

import dev.jbazann.skwidl.commons.async.transactions.entities.TransactionQuorum;
import jakarta.validation.constraints.NotBlank;

/**
 * Represents the role a given artifact has in the distributed application.
 * Services that make use of certain generic mechanisms from the 'commons' module
 * must declare exactly one Spring bean of this type. Beans defined in different
 * instances of the same service must be equal.
 * <br>
 * Instances of this class may also be used to represent other artifacts,
 * as seen in {@link TransactionQuorum}.
 *
 * @param id A unique ID for each different service. The same for
 *           replicas of the same service.
 */
public record ApplicationMember(@NotBlank String id) {}
