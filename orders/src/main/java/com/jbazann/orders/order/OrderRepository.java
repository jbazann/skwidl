package com.jbazann.orders.order;

import com.jbazann.orders.order.entities.Order;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface OrderRepository extends MongoRepository<Order, String> {
}
