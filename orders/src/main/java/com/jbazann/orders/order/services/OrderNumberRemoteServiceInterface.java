package com.jbazann.orders.order.services;

import com.jbazann.orders.order.entities.DangerousIllegalBadSinfulOrderNumberRange;
import jakarta.validation.constraints.NotNull;

public interface OrderNumberRemoteServiceInterface {

    DangerousIllegalBadSinfulOrderNumberRange requestNextRange(@NotNull DangerousIllegalBadSinfulOrderNumberRange current);

}
