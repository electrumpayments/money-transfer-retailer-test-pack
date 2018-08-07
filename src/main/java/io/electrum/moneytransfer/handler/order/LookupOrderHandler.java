package io.electrum.moneytransfer.handler.order;

import java.util.UUID;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import io.electrum.moneytransfer.handler.BaseHandler;
import io.electrum.moneytransfer.model.ErrorDetail;
import io.electrum.moneytransfer.model.MoneyTransferAuthRequest;
import io.electrum.moneytransfer.model.MoneyTransferLookupResponse;
import io.electrum.moneytransfer.model.MoneyTransferRedeemRequest;
import io.electrum.moneytransfer.resource.impl.MoneyTransferTestServer;
import io.electrum.moneytransfer.server.util.RequestKey;

public class LookupOrderHandler extends BaseHandler {

   public LookupOrderHandler(HttpHeaders httpHeaders, UriInfo uriInfo) {
      super(httpHeaders, uriInfo);
   }

   public Response handle(String orderRedeemRef, String merchantId, String originatorInstId, String receiverId) {

      if (!checkBasicAuth(receiverId)) {
         return buildErrorDetailResponse(
               UUID.randomUUID().toString(),
               null,
               ErrorDetail.ErrorTypeEnum.AUTHENTICATION_ERROR,
               "ReceiverId must match basic auth username");
      }

      String requestId = MoneyTransferTestServer.getOrderRedeemRef().get(orderRedeemRef);
      RequestKey requestKey = new RequestKey(username, password, RequestKey.REDEEM_ORDER_RESOURCE, requestId);
      MoneyTransferRedeemRequest redeemRequest = MoneyTransferTestServer.getRedeemRequestRecords().get(requestKey);
      if (redeemRequest != null) {
         MoneyTransferLookupResponse lookupResponse = new MoneyTransferLookupResponse();
         lookupResponse.setOriginator(redeemRequest.getOriginator());
         lookupResponse.setReceiver(redeemRequest.getReceiver());
         lookupResponse.setAmount(redeemRequest.getAmount());

         if (redeemRequest.getTime().minusMonths(1).isAfter(System.currentTimeMillis())) {
            lookupResponse.setStatus(MoneyTransferLookupResponse.StatusEnum.EXPIRED);
            return Response.ok(lookupResponse).build();
         }

         requestKey = new RequestKey(username, password, RequestKey.CONFIRM_REDEEM_RESOURCE, requestId);
         if (MoneyTransferTestServer.getRedeemConfirmationRecords().get(requestKey) != null) {
            lookupResponse.setStatus(MoneyTransferLookupResponse.StatusEnum.REDEEMED);
         } else {
            lookupResponse.setStatus(MoneyTransferLookupResponse.StatusEnum.UNREDEEMED);
         }
         return Response.ok(lookupResponse).build();
      }

      requestKey = new RequestKey(username, password, RequestKey.CREATE_ORDER_RESOURCE, requestId);
      MoneyTransferAuthRequest authRequest = MoneyTransferTestServer.getAuthRequestRecords().get(requestKey);
      if (authRequest != null) {
         MoneyTransferLookupResponse lookupResponse = new MoneyTransferLookupResponse();
         lookupResponse.setReceiver(authRequest.getReceiver());
         lookupResponse.setOriginator(authRequest.getOriginator());
         lookupResponse.setAmount(authRequest.getAmount());

         if (authRequest.getTime().minusMonths(1).isAfter(System.currentTimeMillis())) {
            lookupResponse.setStatus(MoneyTransferLookupResponse.StatusEnum.EXPIRED);
            return Response.ok(lookupResponse).build();
         }

         requestKey = new RequestKey(username, password, RequestKey.REVERSE_PAYMENT_RESOURCE, requestId);
         if (MoneyTransferTestServer.getAuthReversalRecords().get(requestKey) != null) {
            lookupResponse.setStatus(MoneyTransferLookupResponse.StatusEnum.CANCELLED);
            return Response.ok(lookupResponse).build();
         }

         requestKey = new RequestKey(username, password, RequestKey.CONFIRM_PAYMENT_RESOURCE, requestId);
         if (MoneyTransferTestServer.getAuthConfirmationRecords().get(requestKey) != null) {
            lookupResponse.setStatus(MoneyTransferLookupResponse.StatusEnum.UNREDEEMED);
            return Response.ok(lookupResponse).build();
         }

         requestKey = new RequestKey(username, password, RequestKey.CREATE_ORDER_RESOURCE, requestId);
         if (MoneyTransferTestServer.getAuthRequestRecords().get(requestKey) != null) {
            lookupResponse.setStatus(MoneyTransferLookupResponse.StatusEnum.UNREDEEMED);
            return Response.ok(lookupResponse).build();
         }
      }

      return buildErrorDetailResponse(
            ErrorDetail.ErrorTypeEnum.UNABLE_TO_LOCATE_RECORD,
            "Order could not be located, check using correct orderRedeemRef");
   }
}
