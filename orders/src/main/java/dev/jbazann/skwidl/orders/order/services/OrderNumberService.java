package dev.jbazann.skwidl.orders.order.services;

import dev.jbazann.skwidl.orders.order.entities.DangerousIllegalBadSinfulOrderNumberRange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Abstracts the generation of unique sequential numbers in
 * a distributed environment with optimal network overhead.
 */
@Service
public class OrderNumberService {

    private AtomicLong lastUsedIndex;
    private AtomicLong base;
    private long max;

    private final OrderNumberRemoteServiceInterface remoteService;

    @Autowired
    public OrderNumberService(OrderNumberRemoteServiceInterface remoteService) {
        this.remoteService = remoteService;
        lastUsedIndex = new AtomicLong(0);
        base = new AtomicLong(0);
        max = 0;
    }

    public synchronized long next() {
        if( lastUsedIndex.get()+1 >= max ) requestNextRange();
        return lastUsedIndex.incrementAndGet() + base.get();
    }

    private synchronized void requestNextRange() {
        if(max == 0) max = 500L;
        DangerousIllegalBadSinfulOrderNumberRange next = remoteService.requestNextRange(
                new DangerousIllegalBadSinfulOrderNumberRange()
                        .base(base.get())
                        .max(max)
        );
        base.set(next.base());
        lastUsedIndex.set(0);
        max = next.max();
    }


}
