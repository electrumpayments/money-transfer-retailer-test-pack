package io.electrum.moneytransfer.server.backend.records;

import io.electrum.moneytransfer.model.MoneyTransferAuthRequest;

public class AuthRecord extends RequestRecord {

   private final MoneyTransferAuthRequest authRequest;
   private final String orderRedeemRef;
   private int pinRetries;

   public AuthRecord(String recordId, MoneyTransferAuthRequest authRequest, String orderRedeemRef) {
      super(recordId);
      this.authRequest = authRequest;
      this.orderRedeemRef = orderRedeemRef;
      this.pinRetries = 0;
   }

   public MoneyTransferAuthRequest getAuthRequest() {
      return authRequest;
   }

   public String getOrderRedeemRef() {
      return orderRedeemRef;
   }

   public void incorrectPinEntry() {
      pinRetries += 1;
   }

   public int getPinRetries() {
      return pinRetries;
   }
}
