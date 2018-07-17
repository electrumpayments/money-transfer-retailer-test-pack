package io.electrum.moneytransfer.server.backend.records;

public abstract class MoneyTransferRecord {

   protected String recordId;

   public void setRecordId(String recordId) {
      this.recordId = recordId;
   }

   public String getRecordId() {
      return recordId;
   }
}
