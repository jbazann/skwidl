package dev.jbazann.skwidl.commons.async.transactions.api;

public enum LockingStrategies {
    /**
     * An ephemeral lock is released immediately after
     * executing the transaction stage that acquired it.
     */
    EPHEMERAL,
    /**
     * A multi-stage lock is acquired by one stage, and
     * only released when expired or upon finishing
     * the execution of a later stage.
     */
    MULTISTAGE;
}
