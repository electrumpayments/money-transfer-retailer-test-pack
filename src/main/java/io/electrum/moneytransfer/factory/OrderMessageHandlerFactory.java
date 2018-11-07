package io.electrum.moneytransfer.factory;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriInfo;

import io.electrum.moneytransfer.handler.order.AuthHandler;
import io.electrum.moneytransfer.handler.order.ConfirmAuthHandler;
import io.electrum.moneytransfer.handler.order.ConfirmRedeemHandler;
import io.electrum.moneytransfer.handler.order.LookupOrderHandler;
import io.electrum.moneytransfer.handler.order.RedeemOrderHandler;
import io.electrum.moneytransfer.handler.order.ReverseAuthHandler;
import io.electrum.moneytransfer.handler.order.ReverseRedeemHandler;

public class OrderMessageHandlerFactory {
   public static ConfirmAuthHandler getConfirmPaymentHandler(HttpHeaders httpHeaders, UriInfo uriInfo) {
      return new ConfirmAuthHandler(httpHeaders, uriInfo);
   }

   public static ConfirmRedeemHandler getConfirmRedeemHandler(HttpHeaders httpHeaders, UriInfo uriInfo) {
      return new ConfirmRedeemHandler(httpHeaders, uriInfo);
   }

   public static AuthHandler getCreateOrderHandler(HttpHeaders httpHeaders, UriInfo uriInfo) {
      return new AuthHandler(httpHeaders, uriInfo);
   }

   public static LookupOrderHandler getLookupOrderHandler(HttpHeaders httpHeaders, UriInfo uriInfo) {
      return new LookupOrderHandler(httpHeaders, uriInfo);
   }

   public static RedeemOrderHandler getRedeemOrderHandler(HttpHeaders httpHeaders, UriInfo uriInfo) {
      return new RedeemOrderHandler(httpHeaders, uriInfo);
   }

   public static ReverseAuthHandler getReversePaymentHandler(HttpHeaders httpHeaders, UriInfo uriInfo) {
      return new ReverseAuthHandler(httpHeaders, uriInfo);
   }

   public static ReverseRedeemHandler getReverseRedeemHandler(HttpHeaders httpHeaders, UriInfo uriInfo) {
      return new ReverseRedeemHandler(httpHeaders, uriInfo);
   }
}
