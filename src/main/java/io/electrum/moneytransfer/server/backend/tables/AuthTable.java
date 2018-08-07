package io.electrum.moneytransfer.server.backend.tables;

import io.electrum.moneytransfer.server.backend.records.AuthRecord;

import java.util.Enumeration;

public class AuthTable extends MoneyTransferTable<AuthRecord> {

   public AuthRecord getWithOrderRedeemRef(String orderRedeemRef) {
      Enumeration<AuthRecord> records = getRecords();
      while (records.hasMoreElements()) {
         AuthRecord record = records.nextElement();
         if (record.getOrderRedeemRef().equals(orderRedeemRef)) {
            return record;
         }
      }
      return null;
   }
}
