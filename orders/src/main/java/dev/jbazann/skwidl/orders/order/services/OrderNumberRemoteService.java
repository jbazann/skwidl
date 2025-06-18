package dev.jbazann.skwidl.orders.order.services;

import dev.jbazann.skwidl.orders.order.entities.DangerousIllegalBadSinfulOrderNumberRange;
import org.springframework.stereotype.Service;

/**
 * This is a placeholder implementation. It should be replaced with
 * a solution that guarantees exclusive use of assigned ranges
 * in an environment with multiple replicas, both in the service and
 * database layers. Possibly but not necessarily with a centralized
 * singleton service that provides an atomic "nextRange" endpoint.
 */
@Service //TODO wtf are these classes
public class OrderNumberRemoteService implements OrderNumberServiceClient {

    @Override
    public DangerousIllegalBadSinfulOrderNumberRange requestNextRange(DangerousIllegalBadSinfulOrderNumberRange current) {
        return new DangerousIllegalBadSinfulOrderNumberRange()
                .base(current.base() + current.max())
                .max(current.max());
    }
}
