package io.electrum.moneytransfer.server.backend.records;

import io.electrum.moneytransfer.model.MoneyTransferReversal;

public class RedemptionReversalRecord extends RequestRecord {

   private final MoneyTransferReversal reversal;

   public RedemptionReversalRecord(String recordId, MoneyTransferReversal reversal) {
      super(recordId);
      this.reversal = reversal;
   }

   public MoneyTransferReversal getReversal() {
      return reversal;
   }
}
