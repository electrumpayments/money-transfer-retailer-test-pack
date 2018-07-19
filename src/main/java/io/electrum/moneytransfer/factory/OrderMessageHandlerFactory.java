package io.electrum.moneytransfer.factory;

import javax.swing.*;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriInfo;

import io.electrum.moneytransfer.handler.order.ConfirmPaymentHandler;
import io.electrum.moneytransfer.handler.order.ConfirmRedeemHandler;
import io.electrum.moneytransfer.handler.order.CreateOrderHandler;
import io.electrum.moneytransfer.handler.order.LookupOrderHandler;
import io.electrum.moneytransfer.handler.order.RedeemOrderHandler;
import io.electrum.moneytransfer.handler.order.ReversePaymentHandler;
import io.electrum.moneytransfer.handler.order.ReverseRedeemHandler;

public class OrderMessageHandlerFactory {
   public static ConfirmPaymentHandler getConfirmPaymentHandler(HttpHeaders httpHeaders, UriInfo uriInfo) {
      return new ConfirmPaymentHandler(httpHeaders, uriInfo);
   }

   public static ConfirmRedeemHandler getConfirmRedeemHandler(HttpHeaders httpHeaders, UriInfo uriInfo) {
      return new ConfirmRedeemHandler(httpHeaders, uriInfo);
   }

   public static CreateOrderHandler getCreateOrderHandler(HttpHeaders httpHeaders, UriInfo uriInfo) {
      return new CreateOrderHandler(httpHeaders, uriInfo);
   }

   public static LookupOrderHandler getLookupOrderHandler(HttpHeaders httpHeaders, UriInfo uriInfo) {
      return new LookupOrderHandler(httpHeaders, uriInfo);
   }

   public static RedeemOrderHandler getRedeemOrderHandler(HttpHeaders httpHeaders, UriInfo uriInfo) {
      return new RedeemOrderHandler(httpHeaders, uriInfo);
   }

   public static ReversePaymentHandler getReversePaymentHandler(HttpHeaders httpHeaders, UriInfo uriInfo) {
      return new ReversePaymentHandler(httpHeaders, uriInfo);
   }

   public static ReverseRedeemHandler getReverseRedeemHandler(HttpHeaders httpHeaders, UriInfo uriInfo) {
      return new ReverseRedeemHandler(httpHeaders, uriInfo);
   }
}
