package com.jbazann.orders.order;

import com.jbazann.orders.commons.async.events.CancelAcceptedOrderEvent;
import com.jbazann.orders.commons.async.events.CancelPreparedOrderEvent;
import com.jbazann.orders.commons.async.events.DeliverOrderEvent;
import com.jbazann.orders.commons.async.events.DomainEvent;
import com.jbazann.orders.order.services.AsyncOrderService;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RabbitListener(queues = "${jbazann.rabbit.queues.event}")
public class OrdersEventListener {

    private final AsyncOrderService asyncOrderService;

    @Autowired
    public OrdersEventListener(AsyncOrderService asyncOrderService) {
        this.asyncOrderService = asyncOrderService;
    }

    @RabbitHandler
    public void eventMessage(DomainEvent event) {
        switch(event) {
            case CancelAcceptedOrderEvent e -> asyncOrderService.cancelAcceptedOrder(e);
            case CancelPreparedOrderEvent e -> asyncOrderService.cancelPreparedOrder(e);
            case DeliverOrderEvent e -> asyncOrderService.deliverOrder(e);
            case DomainEvent e -> asyncOrderService.discardEvent(e);
        }
    }

}
