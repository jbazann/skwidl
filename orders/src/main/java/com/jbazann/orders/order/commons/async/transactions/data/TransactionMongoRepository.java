package com.jbazann.orders.order.commons.async.transactions.data;

import com.jbazann.commons.async.transactions.api.implement.Transaction;
import com.jbazann.commons.async.transactions.api.implement.TransactionRepository;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TransactionMongoRepository extends TransactionRepository, MongoRepository<Transaction, String> {
}
