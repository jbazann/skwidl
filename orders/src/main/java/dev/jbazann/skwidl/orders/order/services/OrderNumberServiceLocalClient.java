package dev.jbazann.skwidl.orders.order.services;

import dev.jbazann.skwidl.orders.order.entities.DangerousIllegalBadSinfulOrderNumberRange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Abstracts the generation of unique sequential numbers in
 * a distributed environment with optimal network overhead.
 */
@Service
@Validated
public class OrderNumberServiceLocalClient { //TODO wtf are these classes

    private AtomicLong lastUsedIndex;
    private AtomicLong base;
    private long max;

    private final OrderNumberServiceClient remoteService;

    @Autowired
    public OrderNumberServiceLocalClient(OrderNumberServiceClient remoteService) {
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
                        .setBase(base.get())
                        .setMax(max)
        );
        base.set(next.getBase());
        lastUsedIndex.set(0);
        max = next.getMax();
    }


}
