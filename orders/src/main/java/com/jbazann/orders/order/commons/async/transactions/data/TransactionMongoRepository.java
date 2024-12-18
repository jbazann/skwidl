package com.jbazann.orders.order.commons.async.transactions.data;

import com.jbazann.commons.async.transactions.data.Transaction;
import com.jbazann.commons.async.transactions.data.TransactionRepository;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TransactionMongoRepository extends TransactionRepository, MongoRepository<Transaction, String> {
}
