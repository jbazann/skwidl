package com.jbazann.commons.async.transactions.api.implement;

import com.jbazann.commons.async.transactions.TransientCoordinatedTransaction;
import com.jbazann.commons.identity.ApplicationMember;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface CoordinatedTransaction {

    // Getters
    UUID id();
    Map<ApplicationMember, TransientCoordinatedTransaction.QuorumStatus> quorumStatus();
    List<ApplicationMember> rollback();
    TransientCoordinatedTransaction.TransactionStatus status();
    boolean isCommitted();
    LocalDateTime expires();

    // Setters
    UUID id(UUID id);
    Map<ApplicationMember, TransientCoordinatedTransaction.QuorumStatus> quorumStatus(Map<ApplicationMember, TransientCoordinatedTransaction.QuorumStatus> quorumStatus);
    List<ApplicationMember> rollback(List<ApplicationMember> rollback);
    TransientCoordinatedTransaction.TransactionStatus status(TransientCoordinatedTransaction.TransactionStatus status);
    boolean isCommitted(boolean isCommitted);
    LocalDateTime expires(LocalDateTime expires);

}
