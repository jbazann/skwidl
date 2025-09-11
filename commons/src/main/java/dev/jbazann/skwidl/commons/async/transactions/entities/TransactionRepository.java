package dev.jbazann.skwidl.commons.async.transactions.entities;

import dev.jbazann.skwidl.commons.async.transactions.api.Transaction;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.UUID;

@NoRepositoryBean
public interface TransactionRepository<T extends Transaction>
        extends CrudRepository<T, UUID>  {
}
