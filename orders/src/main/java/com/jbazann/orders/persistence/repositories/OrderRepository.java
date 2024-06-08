package com.jbazann.orders.persistence.repositories;

import com.jbazann.orders.domain.entities.Order;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface OrderRepository extends MongoRepository<Order, UUID> {
}
