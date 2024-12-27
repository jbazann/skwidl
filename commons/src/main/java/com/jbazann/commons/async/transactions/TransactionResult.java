package com.jbazann.commons.async.transactions;

import com.jbazann.commons.async.transactions.api.implement.Transaction;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true, fluent = true)
public class TransactionResult {

    private SimpleResult simpleResult;
    private Transaction data;
    private String context;

    public enum SimpleResult {
        SUCCESS, WARNED_SUCCESS, FAILURE, CRITICAL_FAILURE, REGISTRY_FAILURE
    }

}
