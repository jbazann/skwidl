package com.jbazann.commons.async.transactions;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

/**
 * Marks RESERVE {@link TransactionPhase} components so they can
 * be found by {@link TransactionPhaseRegistrar}.
 */
@Component
public @interface ReservePhase {

    @AliasFor(annotation=Component.class)
    String value();

}
