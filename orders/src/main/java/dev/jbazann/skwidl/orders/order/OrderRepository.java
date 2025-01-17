package dev.jbazann.skwidl.orders.order;

import dev.jbazann.skwidl.orders.order.entities.Order;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrderRepository extends MongoRepository<Order, String> {
}
