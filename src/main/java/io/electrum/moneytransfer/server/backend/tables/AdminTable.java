package io.electrum.moneytransfer.server.backend.tables;

import io.electrum.moneytransfer.server.backend.records.AdminRecord;

import java.util.Enumeration;

public class AdminTable extends MoneyTransferTable<AdminRecord> {

   public AdminRecord getAdminRecordWithCustomerProfileId(String customerProfileId) {
      Enumeration<AdminRecord> adminRecords = getRecords();
      while (adminRecords.hasMoreElements()) {
         AdminRecord adminRecord = adminRecords.nextElement();
         if (adminRecord.getAdminMessage().getCustomerProfileId().equals(customerProfileId)) {
            return adminRecord;
         }
      }
      return null;
   }

}
