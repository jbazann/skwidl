package com.jbazann.commons.async.transactions;

import com.jbazann.commons.async.transactions.data.TransientTransaction;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true, fluent = true)
public class TransactionResult {

    private SimpleResult simpleResult;
    private TransientTransaction data;
    private String context;

    public enum SimpleResult {
        SUCCESS, FAILURE, CRITICAL_FAILURE, REGISTRY_FAILURE
    }

}
