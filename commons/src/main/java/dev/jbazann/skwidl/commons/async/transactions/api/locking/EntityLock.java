package dev.jbazann.skwidl.commons.async.transactions.api.locking;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Getter
@Accessors(chain = true, fluent = true)
@RequiredArgsConstructor
@AllArgsConstructor
public class EntityLock {

    @NotNull
    private final String entityId;
    @NotNull
    private final Class<?> entityClass;

    public String toString() {
        return entityClass.getName() + '@' + entityId;
    }

}
