package dev.jbazann.skwidl.commons.async.transactions.entities;

import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface CoordinatedTransactionRepository extends CrudRepository<CoordinatedTransaction, UUID> {

}
