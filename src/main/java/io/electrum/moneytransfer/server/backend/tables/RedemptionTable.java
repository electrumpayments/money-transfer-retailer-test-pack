package io.electrum.moneytransfer.server.backend.tables;

import io.electrum.moneytransfer.server.backend.records.RedemptionRecord;

import java.util.Enumeration;

public class RedemptionTable extends MoneyTransferTable<RedemptionRecord> {

   public RedemptionRecord getRedemptionRecordWithOrderRedeemRef(String orderRedeemRef) {
      Enumeration<RedemptionRecord> redemptionRecords = getRecords();
      while (redemptionRecords.hasMoreElements()) {
         RedemptionRecord redemptionRecord = redemptionRecords.nextElement();
         if (redemptionRecord.getRedeemRequest().getOrderRedeemRef().equals(orderRedeemRef)) {
            return redemptionRecord;
         }
      }
      return null;
   }
}
