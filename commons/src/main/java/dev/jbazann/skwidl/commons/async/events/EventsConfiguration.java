package dev.jbazann.skwidl.commons.async.events;

import dev.jbazann.skwidl.commons.async.rabbitmq.RabbitConfiguration;
import dev.jbazann.skwidl.commons.async.rabbitmq.RabbitPublisher;
import dev.jbazann.skwidl.commons.async.transactions.api.Transaction;
import dev.jbazann.skwidl.commons.identity.ApplicationMemberRegistry;
import dev.jbazann.skwidl.commons.identity.IdentityConfiguration;
import dev.jbazann.skwidl.commons.internal.CommonsBeanFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.*;
import org.springframework.lang.Nullable;

import java.util.function.Supplier;

@Configuration
@Import({
        RabbitConfiguration.class,
        IdentityConfiguration.class
})
public class EventsConfiguration {

    @Bean("BlankDomainEventBuilder")
    @Scope("prototype")
    @Lazy()
    public DomainEventBuilder<?> blankDomainEventBuilder(
            ApplicationMemberRegistry memberRegistry,
            Supplier<Transaction> transactionSupplier,
            @Nullable Class<? extends DomainEvent> c
    ) {
        if (c == null) {
            return new DomainEventBuilder<>(memberRegistry.SELF, memberRegistry.DEFAULT_COORDINATOR, GenericDomainEvent.class, transactionSupplier);
        }
        return new DomainEventBuilder<>(memberRegistry.SELF, memberRegistry.DEFAULT_COORDINATOR, c, transactionSupplier);
    }

    @Bean("WrapperDomainEventBuilder")
    @Scope("prototype")
    @Lazy()
    public <T extends DomainEvent> DomainEventBuilder<?> wrapperDomainEventBuilder(
            ApplicationMemberRegistry memberRegistry,
            Supplier<Transaction> transactionSupplier,
            @Nullable T event
    ) {
        if (event == null) {
            return new DomainEventBuilder<>(memberRegistry.SELF, memberRegistry.DEFAULT_COORDINATOR, new GenericDomainEvent(), transactionSupplier);
        }
        return new DomainEventBuilder<>(memberRegistry.SELF, memberRegistry.DEFAULT_COORDINATOR, event, transactionSupplier);
    }

    @Bean("BlankDomainEventBuilderBeanFactory")
    @Scope("prototype")
    @Lazy()
    public CommonsBeanFactory<DomainEventBuilder<?>> blankDomainEventBuilderBeanFactory(
            @Qualifier("BlankDomainEventBuilder") ObjectProvider<DomainEventBuilder<?>> provider
    ) {
        return new CommonsBeanFactory<>(provider);
    }

    @Bean("WrapperDomainEventBuilderBeanFactory")
    @Scope("prototype")
    @Lazy()
    public CommonsBeanFactory<DomainEventBuilder<?>> wrapperDomainEventBuilderBeanFactory(
            @Qualifier("WrapperDomainEventBuilder") ObjectProvider<DomainEventBuilder<?>> provider
    ) {
        return new CommonsBeanFactory<>(provider);
    }

    @Bean("DomainEventBuilderFactory")
    @ConditionalOnMissingBean
    public DomainEventBuilderFactory DomainEventBuilderBeanFactory(
            @Qualifier("BlankDomainEventBuilderBeanFactory") CommonsBeanFactory<DomainEventBuilder<?>> blankBeanFactory,
            @Qualifier("WrapperDomainEventBuilderBeanFactory") CommonsBeanFactory<DomainEventBuilder<?>> wrapperBeanFactory,
            ApplicationMemberRegistry memberRegistry,
            Supplier<Transaction> transactionSupplier
    ) {
        return new DomainEventBuilderFactory(blankBeanFactory,wrapperBeanFactory,memberRegistry,transactionSupplier);
    }

    @Bean
    @ConditionalOnMissingBean
    public EventRequestPublisher eventRequestPublisher(RabbitPublisher publisher, DomainEventBuilderFactory factory) {
        return new EventRequestPublisher(publisher, factory);
    }

}
