package dev.jbazann.skwidl.commons.async.transactions;

import dev.jbazann.skwidl.commons.async.events.DomainEvent;
import dev.jbazann.skwidl.commons.async.rabbitmq.RabbitPublisher;
import dev.jbazann.skwidl.commons.async.transactions.coordination.TransactionCoordinatorStrategy;
import dev.jbazann.skwidl.commons.async.transactions.coordination.TransactionCoordinatorStrategyResult;
import dev.jbazann.skwidl.commons.async.transactions.entities.CoordinatedTransaction;
import dev.jbazann.skwidl.commons.async.transactions.entities.CoordinatedTransactionRepository;
import dev.jbazann.skwidl.commons.identity.ApplicationMember;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

@Validated
public class TransactionCoordinatorService {

    private final ApplicationMember member;
    private final CoordinatedTransactionRepository repository;
    private final TransactionCoordinatorStrategySelector strategies;
    private final RabbitPublisher publisher;

    public TransactionCoordinatorService(ApplicationMember member, CoordinatedTransactionRepository repository, TransactionCoordinatorStrategySelector strategies, RabbitPublisher publisher) {
        this.member = member;
        this.repository = repository;
        this.strategies = strategies;
        this.publisher = publisher;
    }

    public void handle(@NotNull @Valid DomainEvent event) {
        CoordinatedTransaction transaction = repository.findById(event.getTransaction().getId())
                .orElse(repository.save(CoordinatedTransaction.from(event)));

        TransactionCoordinatorStrategy strategy = strategies.getStrategy(event, transaction);

        TransactionCoordinatorStrategyResult result = strategy.getResult();

        if (result.response().isPresent()) {
            publisher.publish(result.response().get());
        }

        repository.save(result.transaction());
    }

    public boolean isCoordinatorFor(DomainEvent event) {
        return member.equals(event.getTransaction().getQuorum().getCoordinator());
    }

}
