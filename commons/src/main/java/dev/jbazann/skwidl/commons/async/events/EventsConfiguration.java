package dev.jbazann.skwidl.commons.async.events;

import dev.jbazann.skwidl.commons.async.rabbitmq.RabbitConfiguration;
import dev.jbazann.skwidl.commons.async.rabbitmq.RabbitPublisher;
import dev.jbazann.skwidl.commons.identity.ApplicationMember;
import dev.jbazann.skwidl.commons.identity.IdentityConfiguration;
import dev.jbazann.skwidl.commons.internal.CommonsBeanFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.*;
import org.springframework.lang.Nullable;

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
            @Qualifier("identity") ApplicationMember identity,
            @Qualifier("defaultCoordinator") ApplicationMember defaultCoordinator,
            @Nullable Class<? extends DomainEvent> c
    ) {
        if (c == null) {
            return new DomainEventBuilder<>(identity, defaultCoordinator, GenericDomainEvent.class);
        }
        return new DomainEventBuilder<>(identity, defaultCoordinator, c);
    }

    @Bean("WrapperDomainEventBuilder")
    @Scope("prototype")
    @Lazy()
    public <T extends DomainEvent> DomainEventBuilder<?> wrapperDomainEventBuilder(
            @Qualifier("identity") ApplicationMember identity,
            @Qualifier("defaultCoordinator") ApplicationMember defaultCoordinator,
            @Nullable T event
    ) {
        if (event == null) {
            return new DomainEventBuilder<>(identity, defaultCoordinator, new GenericDomainEvent());
        }
        return new DomainEventBuilder<>(identity, defaultCoordinator, event);
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
            @Qualifier("identity") ApplicationMember identity,
            @Qualifier("defaultCoordinator") ApplicationMember defaultCoordinator
    ) {
        return new DomainEventBuilderFactory(blankBeanFactory,wrapperBeanFactory,identity,defaultCoordinator);
    }

    @Bean
    @ConditionalOnMissingBean
    public EventRequestPublisher eventRequestPublisher(RabbitPublisher publisher, DomainEventBuilderFactory factory) {
        return new EventRequestPublisher(publisher, factory);
    }

}
