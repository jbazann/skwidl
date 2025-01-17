package dev.jbazann.skwidl.orders.order.services;

import dev.jbazann.skwidl.orders.order.entities.DangerousIllegalBadSinfulOrderNumberRange;
import jakarta.validation.constraints.NotNull;

public interface OrderNumberRemoteServiceInterface {

    DangerousIllegalBadSinfulOrderNumberRange requestNextRange(@NotNull DangerousIllegalBadSinfulOrderNumberRange current);

}
