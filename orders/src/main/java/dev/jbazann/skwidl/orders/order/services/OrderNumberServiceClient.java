package dev.jbazann.skwidl.orders.order.services;

import dev.jbazann.skwidl.orders.order.entities.DangerousIllegalBadSinfulOrderNumberRange;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

@Validated //TODO wtf are these classes
public interface OrderNumberServiceClient {

    DangerousIllegalBadSinfulOrderNumberRange requestNextRange(@NotNull DangerousIllegalBadSinfulOrderNumberRange current);

}
