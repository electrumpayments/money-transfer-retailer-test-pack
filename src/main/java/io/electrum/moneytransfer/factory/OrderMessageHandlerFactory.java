package io.electrum.moneytransfer.factory;

import io.electrum.moneytransfer.handler.ConfirmPaymentHandler;
import io.electrum.moneytransfer.handler.ConfirmRedeemHandler;
import io.electrum.moneytransfer.handler.CreateOrderHandler;
import io.electrum.moneytransfer.handler.LookupOrderHandler;
import io.electrum.moneytransfer.handler.RedeemOrderHandler;
import io.electrum.moneytransfer.handler.ReversePaymentHandler;
import io.electrum.moneytransfer.handler.ReverseRedeemHandler;

public class OrderMessageHandlerFactory {
   public static ConfirmPaymentHandler getConfirmPaymentHandler() {
      return new ConfirmPaymentHandler();
   }

   public static ConfirmRedeemHandler getConfirmRedeemHandler() {
      return new ConfirmRedeemHandler();
   }

   public static CreateOrderHandler getCreateOrderHandler() {
      return new CreateOrderHandler();
   }

   public static LookupOrderHandler getLookupOrderHandler() {
      return new LookupOrderHandler();
   }

   public static RedeemOrderHandler getRedeemOrderHandler() {
      return new RedeemOrderHandler();
   }

   public static ReversePaymentHandler getReversePaymentHandler() {
      return new ReversePaymentHandler();
   }

   public static ReverseRedeemHandler getReverseRedeemHandler() {
      return new ReverseRedeemHandler();
   }
}
