package io.electrum.moneytransfer.server.backend.records;

import io.electrum.moneytransfer.model.MoneyTransferRedeemRequest;

public class RedemptionRecord extends RequestRecord {

   private final MoneyTransferRedeemRequest redeemRequest;

   public RedemptionRecord(String recordId, MoneyTransferRedeemRequest redeemRequest) {
      super(recordId);
      this.redeemRequest = redeemRequest;
   }

   public MoneyTransferRedeemRequest getRedeemRequest() {
      return redeemRequest;
   }
}
