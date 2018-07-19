package io.electrum.moneytransfer.handler.order;

import io.electrum.moneytransfer.handler.BaseHandler;
import io.electrum.moneytransfer.model.ErrorDetail;
import io.electrum.moneytransfer.model.ErrorDetail.ErrorTypeEnum;
import io.electrum.moneytransfer.model.MoneyTransferAuthRequest;
import io.electrum.moneytransfer.model.MoneyTransferAuthResponse;
import io.electrum.moneytransfer.resource.impl.MoneyTransferTestServer;
import io.electrum.moneytransfer.server.model.DetailMessage;
import io.electrum.moneytransfer.server.util.MoneyTransferUtils;
import io.electrum.moneytransfer.server.util.OrderUtils;
import io.electrum.moneytransfer.server.util.RequestKey;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateOrderHandler extends BaseHandler {

   public CreateOrderHandler(HttpHeaders httpHeaders, UriInfo uriInfo) {
      super(httpHeaders, uriInfo);
   }

   public Response handle(MoneyTransferAuthRequest request) {
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
            return Response.status(400).entity(errorDetail).build();
         }

         RequestKey key = new RequestKey(username, password, RequestKey.CREATE_ORDER_RESOURCE, request.getId());
         rsp = OrderUtils.canCreateOrder(request.getId(), username, password);
         if (rsp != null) {
            return rsp;
         }
         MoneyTransferTestServer.getAuthRequestRecords().put(key, request);
         MoneyTransferAuthResponse voucherRsp = OrderUtils.authRspFromReq(request);
         MoneyTransferTestServer.getAuthResponseRecords().put(key, voucherRsp);
         return Response.created(uriInfo.getRequestUri()).entity(voucherRsp).build();
      } catch (Exception e) {
         log.debug("error processing CreateOrder", e);
         return Response.serverError().entity(e.getMessage()).build();
      }
   }
}
