package com.jbazann.commons.identity;

import java.util.Arrays;
import java.util.List;

public enum KnownMembers {
    ORDERS("orders"),
    PRODUCTS("products"),
    CUSTOMERS("customers"),
    ;

    private final ApplicationMember identity;

    public ApplicationMember identity() {
        return identity;
    }

    KnownMembers(String name) {
        this.identity = new ApplicationMember(name);
    }

    public static List<ApplicationMember> memberList(KnownMembers... members) {
        return Arrays.stream(members).map(KnownMembers::identity).toList();
    }

}
