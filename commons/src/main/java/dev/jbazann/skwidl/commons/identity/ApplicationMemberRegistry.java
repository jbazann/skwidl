package dev.jbazann.skwidl.commons.identity;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.Arrays;
import java.util.List;

public class ApplicationMemberRegistry {

    public final ApplicationMember DEFAULT_COORDINATOR;
    public final ApplicationMember SELF;

    public ApplicationMemberRegistry(ApplicationMember self, ApplicationMember defaultCoordinator) {
        this.SELF = self;
        this.DEFAULT_COORDINATOR = defaultCoordinator;
    }

    public enum Members {
        ORDERS("orders"),
        PRODUCTS("products"),
        CUSTOMERS("customers"),
        ;

        @Valid @NotNull
        private final ApplicationMember identity;

        public ApplicationMember identity() {
            return identity;
        }

        Members(String name) {
            this.identity = new ApplicationMember(name);
        }

        public static List<ApplicationMember> list(Members... members) {
            return Arrays.stream(members).map(Members::identity).toList();
        }

    }

}
