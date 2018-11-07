package io.electrum.moneytransfer.server.backend.records;

import io.electrum.moneytransfer.model.MoneyTransferAdminMessage;

public class AdminRecord extends MoneyTransferRecord {

   private final MoneyTransferAdminMessage adminMessage;

   public AdminRecord(String recordId, MoneyTransferAdminMessage adminMessage) {
      super(recordId);
      this.adminMessage = adminMessage;
   }

   public MoneyTransferAdminMessage getAdminMessage() {
      return adminMessage;
   }
}
