package dev.jbazann.skwidl.commons.async.transactions.api.locking;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Accessors(chain = true, fluent = true)
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
