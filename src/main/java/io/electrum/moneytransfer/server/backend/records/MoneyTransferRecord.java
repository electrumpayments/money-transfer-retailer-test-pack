package io.electrum.moneytransfer.server.backend.records;

public abstract class MoneyTransferRecord {

   protected final String recordId;

   MoneyTransferRecord(String recordId) {
      this.recordId = recordId;
   }

   public String getRecordId() {
      return recordId;
   }

}
