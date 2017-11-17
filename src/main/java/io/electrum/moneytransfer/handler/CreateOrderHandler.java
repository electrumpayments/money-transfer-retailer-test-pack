package io.electrum.moneytransfer.handler;

import io.electrum.moneytransfer.model.ErrorDetail;
import io.electrum.moneytransfer.model.ErrorDetail.ErrorTypeEnum;
import io.electrum.moneytransfer.model.MoneyTransferAuthRequest;
import io.electrum.moneytransfer.model.MoneyTransferAuthResponse;
import io.electrum.moneytransfer.resource.impl.MoneyTransferTestServer;
import io.electrum.moneytransfer.server.MoneyTransferTestServerRunner;
import io.electrum.moneytransfer.server.model.DetailMessage;
import io.electrum.moneytransfer.server.util.MoneyTransferUtils;
import io.electrum.moneytransfer.server.util.OrderUtils;
import io.electrum.moneytransfer.server.util.RequestKey;

import java.util.concurrent.ConcurrentHashMap;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateOrderHandler {
   private static final Logger log = LoggerFactory.getLogger(MoneyTransferTestServer.class.getPackage().getName());

   public Response handle(MoneyTransferAuthRequest request, HttpHeaders httpHeaders, UriInfo uriInfo) {
      try {
         String authString = MoneyTransferUtils.getAuthString(httpHeaders.getHeaderString(HttpHeaders.AUTHORIZATION));
         String username = MoneyTransferUtils.getUsernameFromAuth(authString);
         String password = MoneyTransferUtils.getPasswordFromAuth(authString);

         Response rsp = OrderUtils.validateCreateOrderRequest(request);
         if (rsp != null) {
            return rsp;
         }

         if (!request.getClient().getId().equals(username)) {
            ErrorDetail errorDetail =
                  new ErrorDetail().id(request.getId()).errorType(ErrorTypeEnum.FORMAT_ERROR).errorMessage(
                        "Incorrect username");
            DetailMessage detailMessage = new DetailMessage();
            detailMessage.setFreeString(
                  "The HTTP Basic Authentication username (" + username
                        + ") is not the same as the value in the Client.Id field (" + request.getClient().getId()
                        + ").");
            detailMessage.setClient(request.getClient());
            errorDetail.setDetailMessage(detailMessage);
            rsp = Response.status(400).entity(errorDetail).build();
            return rsp;
         }

         RequestKey key = new RequestKey(username, password, RequestKey.CREATE_ORDER_RESOURCE, request.getId());
         rsp = OrderUtils.canCreateOrder(request.getId(), username, password);
         if (rsp != null) {
            return rsp;
         }
         ConcurrentHashMap<RequestKey, MoneyTransferAuthRequest> authRequestRecords =
               MoneyTransferTestServerRunner.getTestServer().getAuthRequestRecords();
         authRequestRecords.put(key, request);
         MoneyTransferAuthResponse voucherRsp = OrderUtils.authRspFromReq(request);
         ConcurrentHashMap<RequestKey, MoneyTransferAuthResponse> authResponseRecords =
               MoneyTransferTestServerRunner.getTestServer().getAuthResponseRecords();
         authResponseRecords.put(key, voucherRsp);
         rsp = Response.created(uriInfo.getRequestUri()).entity(voucherRsp).build();
         return rsp;
      } catch (Exception e) {
         log.debug("error processing CreateOrder", e);
         Response rsp = Response.serverError().entity(e.getMessage()).build();
         return rsp;
      }
   }
}
