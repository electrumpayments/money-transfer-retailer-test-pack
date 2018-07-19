package io.electrum.moneytransfer.resource.impl;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.electrum.moneytransfer.api.IOrdersResource;
import io.electrum.moneytransfer.api.OrdersResource;
import io.electrum.moneytransfer.factory.OrderMessageHandlerFactory;
import io.electrum.moneytransfer.model.MoneyTransferAuthRequest;
import io.electrum.moneytransfer.model.MoneyTransferConfirmation;
import io.electrum.moneytransfer.model.MoneyTransferRedeemRequest;
import io.electrum.moneytransfer.model.MoneyTransferReversal;
import io.swagger.annotations.Api;
import io.swagger.annotations.Authorization;

@Path("/moneytransfer/v2/order")
@Api(description = "the Money Transfer Retailer Order API", authorizations = { @Authorization("httpBasic") })
public class OrdersResourceImpl extends OrdersResource implements IOrdersResource {
   private static OrdersResourceImpl instance = null;
   private static final Logger log = LoggerFactory.getLogger(MoneyTransferTestServer.class.getPackage().getName());

   @Override
   protected IOrdersResource getResourceImplementation() {
      if (instance == null) {
         return new OrdersResourceImpl();
      }
      return instance;
   }

   @Override
   public void confirmPayment(
         MoneyTransferConfirmation body,
         SecurityContext securityContext,
         Request request,
         HttpHeaders httpHeaders,
         AsyncResponse asyncResponse,
         UriInfo uriInfo,
         HttpServletRequest httpServletRequest) {
      log.info(String.format("%s %s", httpServletRequest.getMethod(), uriInfo.getPath()));
      log.debug(String.format("%s %s\n%s", httpServletRequest.getMethod(), uriInfo.getPath(), body));
      Response rsp = OrderMessageHandlerFactory.getConfirmPaymentHandler(httpHeaders, uriInfo).handle(body);
      log.debug(String.format("Entity returned:\n%s", rsp.getEntity()));

      asyncResponse.resume(rsp);
   }

   @Override
   public void confirmRedeem(
         MoneyTransferConfirmation body,
         SecurityContext securityContext,
         Request request,
         HttpHeaders httpHeaders,
         AsyncResponse asyncResponse,
         UriInfo uriInfo,
         HttpServletRequest httpServletRequest) {
      log.info(String.format("%s %s", httpServletRequest.getMethod(), uriInfo.getPath()));
      log.debug(String.format("%s %s\n%s", httpServletRequest.getMethod(), uriInfo.getPath(), body));
      Response rsp = OrderMessageHandlerFactory.getConfirmRedeemHandler(httpHeaders, uriInfo).handle(body);
      log.debug(String.format("Entity returned:\n%s", rsp.getEntity()));

      asyncResponse.resume(rsp);
   }

   @Override
   public void createOrder(
         MoneyTransferAuthRequest body,
         SecurityContext securityContext,
         Request request,
         HttpHeaders httpHeaders,
         AsyncResponse asyncResponse,
         UriInfo uriInfo,
         HttpServletRequest httpServletRequest) {
      log.info(String.format("%s %s", httpServletRequest.getMethod(), uriInfo.getPath()));
      log.debug(String.format("%s %s\n%s", httpServletRequest.getMethod(), uriInfo.getPath(), body));
      Response rsp = OrderMessageHandlerFactory.getCreateOrderHandler(httpHeaders, uriInfo).handle(body);
      log.debug(String.format("Entity returned:\n%s", rsp.getEntity()));

      asyncResponse.resume(rsp);
   }

   @Override
   public void lookupOrder(
         String orderRedeemRef,
         String merchantId,
         String originatorInstId,
         String receiverId,
         SecurityContext securityContext,
         Request request,
         HttpHeaders httpHeaders,
         AsyncResponse asyncResponse,
         UriInfo uriInfo,
         HttpServletRequest httpServletRequest) {
      log.info(String.format("%s %s", httpServletRequest.getMethod(), uriInfo.getPath()));
      log.debug(
            String.format(
                  "%s %s\nOrder Redeem Reference: %s\nMerchant ID: %s\nOriginator Institute ID: %s\nReceiver ID: %s",
                  httpServletRequest.getMethod(),
                  uriInfo.getPath(),
                  orderRedeemRef,
                  merchantId,
                  originatorInstId,
                  receiverId));
      Response rsp =
            OrderMessageHandlerFactory.getLookupOrderHandler(httpHeaders, uriInfo)
                  .handle(orderRedeemRef, merchantId, originatorInstId, receiverId);
      log.debug(String.format("Entity returned:\n%s", rsp.getEntity()));

      asyncResponse.resume(rsp);
   }

   @Override
   public void redeemOrder(
         MoneyTransferRedeemRequest body,
         SecurityContext securityContext,
         Request request,
         HttpHeaders httpHeaders,
         AsyncResponse asyncResponse,
         UriInfo uriInfo,
         HttpServletRequest httpServletRequest) {
      log.info(String.format("%s %s", httpServletRequest.getMethod(), uriInfo.getPath()));
      log.debug(String.format("%s %s\n%s", httpServletRequest.getMethod(), uriInfo.getPath(), body));
      Response rsp = OrderMessageHandlerFactory.getRedeemOrderHandler(httpHeaders, uriInfo).handle(body);
      log.debug(String.format("Entity returned:\n%s", rsp.getEntity()));

      asyncResponse.resume(rsp);
   }

   @Override
   public void reversePayment(
         MoneyTransferReversal body,
         SecurityContext securityContext,
         Request request,
         HttpHeaders httpHeaders,
         AsyncResponse asyncResponse,
         UriInfo uriInfo,
         HttpServletRequest httpServletRequest) {
      log.info(String.format("%s %s", httpServletRequest.getMethod(), uriInfo.getPath()));
      log.debug(String.format("%s %s\n%s", httpServletRequest.getMethod(), uriInfo.getPath(), body));
      Response rsp = OrderMessageHandlerFactory.getReversePaymentHandler(httpHeaders, uriInfo).handle(body);
      log.debug(String.format("Entity returned:\n%s", rsp.getEntity()));

      asyncResponse.resume(rsp);
   }

   @Override
   public void reverseRedeem(
         MoneyTransferReversal body,
         SecurityContext securityContext,
         Request request,
         HttpHeaders httpHeaders,
         AsyncResponse asyncResponse,
         UriInfo uriInfo,
         HttpServletRequest httpServletRequest) {
      log.info(String.format("%s %s", httpServletRequest.getMethod(), uriInfo.getPath()));
      log.debug(String.format("%s %s\n%s", httpServletRequest.getMethod(), uriInfo.getPath(), body));
      Response rsp = OrderMessageHandlerFactory.getReverseRedeemHandler(httpHeaders, uriInfo).handle(body);
      log.debug(String.format("Entity returned:\n%s", rsp.getEntity()));

      asyncResponse.resume(rsp);
   }
}