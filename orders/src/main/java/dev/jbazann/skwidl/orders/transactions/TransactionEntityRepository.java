package dev.jbazann.skwidl.orders.transactions;

import dev.jbazann.skwidl.commons.async.transactions.entities.TransactionRepository;
import org.springframework.stereotype.Repository;

public interface TransactionEntityRepository extends TransactionRepository<TransactionEntity> {
}
