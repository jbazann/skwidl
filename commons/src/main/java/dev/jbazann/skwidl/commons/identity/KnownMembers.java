package dev.jbazann.skwidl.commons.identity;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

import java.util.Arrays;
import java.util.List;

@Validated
public enum KnownMembers {
    ORDERS("orders"),
    PRODUCTS("products"),
    CUSTOMERS("customers"),
    ;

    @Valid @NotNull
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
