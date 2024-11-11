package com.jbazann.orders.order.services;

import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;

@Service
public class OrderNumberService {

    private AtomicLong index;
    private AtomicLong base;
    private long max;

    public OrderNumberService() {
        index = new AtomicLong(0);// TODO range table
        base = new AtomicLong(0);
        max = 500L;
    }

    public synchronized long next() {
        if( index.get()+1 >= max ) nextRange();
        return index.incrementAndGet() + base.get();
    }

    private synchronized void nextRange() {
        //TODO range table
        index = new AtomicLong(0);
    }


}
