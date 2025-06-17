package dev.jbazann.skwidl.commons.async.events;

import dev.jbazann.skwidl.commons.internal.CommonsBeanFactory;

public class DomainEventBuilderFactory {

    private final CommonsBeanFactory<DomainEventBuilder<?>> blankBuilderFactory;
    private final CommonsBeanFactory<DomainEventBuilder<?>> wrapperBuilderFactory;

    public DomainEventBuilderFactory(
            CommonsBeanFactory<DomainEventBuilder<?>> blankBuilderFactory,
            CommonsBeanFactory<DomainEventBuilder<?>> wrapperBuilderFactory,
            Object... leadingArgs
    ) {
        this.blankBuilderFactory = blankBuilderFactory;
        this.wrapperBuilderFactory = wrapperBuilderFactory;
        if (leadingArgs != null && leadingArgs.length > 0) {
            blankBuilderFactory.leadingArgs(leadingArgs);
            wrapperBuilderFactory.leadingArgs(leadingArgs);
        }
    }

    public <T extends DomainEvent> DomainEventBuilder<T> create(Class<T> eventClass) {
        return blankBuilderFactory.create(eventClass);
    }

    public <T extends DomainEvent> DomainEventBuilder<T> create() {
        return blankBuilderFactory.create();
    }

    public <T extends DomainEvent> DomainEventBuilder<T> wrap(T event) {
        return wrapperBuilderFactory.create(event);
    }

}
