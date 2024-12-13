package com.jbazann.commons.identity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    /**
     * A list of other services that may be referenced by certain
     * operations. It's main purpose is to isolate the definition
     * of {@link ApplicationMember} instances that represent other
     * services, reducing the awkwardness of using Strings to refer
     * to stable entities.
     * // TODO maybe this should be elsewhere.
     */
    public static Map<String, ApplicationMember> knownMembers;

    public static void setKnownMembers(List<String> ids) {
        Map<String, ApplicationMember> modifiable = new HashMap<String, ApplicationMember>();
        for (String id : ids) modifiable.put(id, new ApplicationMember(id));
        knownMembers = Map.copyOf(modifiable);
    }

    public static ApplicationMember getKnownMember(String id) {
        if(!knownMembers.containsKey(id)) throw new IllegalArgumentException("Member with id " + id + " not found.");
        return knownMembers.get(id);
    }

}
