package io.electrum.moneytransfer.server.backend.records;

import io.electrum.moneytransfer.model.MoneyTransferConfirmation;

public class RedemptionConfirmationRecord extends RequestRecord {

   private final MoneyTransferConfirmation confirmation;

   public RedemptionConfirmationRecord(String recordId, MoneyTransferConfirmation confirmation) {
      super(recordId);
      this.confirmation = confirmation;
   }

   public MoneyTransferConfirmation getConfirmation() {
      return confirmation;
   }
}
