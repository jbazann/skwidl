package dev.jbazann.skwidl.commons.async.transactions;

import dev.jbazann.skwidl.commons.async.transactions.api.Transaction;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true, fluent = true)
public class TransactionResult {

    @NotNull
    private SimpleResult simpleResult;
    @NotNull @Valid
    private Transaction data;
    @NotNull
    private String context;

    public enum SimpleResult {
        SUCCESS, WARNED_SUCCESS, FAILURE, CRITICAL_FAILURE, REGISTRY_FAILURE
    }

}
