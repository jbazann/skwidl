package dev.jbazann.skwidl.commons.async.transactions.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Getter
@Accessors(chain = true, fluent = true)
@RequiredArgsConstructor
@AllArgsConstructor
public class EntityLock {

    private final String entityId;
    private final Class<?> entityClass;

    public String toString() {
        return entityClass.getName() + '@' + entityId;
    }

}
