package com.jbazann.orders.order.commons.async.transactions.data;

import com.jbazann.commons.async.transactions.api.implement.CoordinatedTransaction;
import com.jbazann.commons.async.transactions.api.implement.CoordinatedTransactionRepository;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CoordinatedTransactionMongoRepository extends CoordinatedTransactionRepository, MongoRepository<CoordinatedTransaction, String> {

}
