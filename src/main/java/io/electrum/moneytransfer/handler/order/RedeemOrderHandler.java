package io.electrum.moneytransfer.handler.order;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import io.electrum.moneytransfer.handler.BaseHandler;
import io.electrum.moneytransfer.model.ErrorDetail;
import io.electrum.moneytransfer.model.MoneyTransferAuthRequest;
import io.electrum.moneytransfer.model.MoneyTransferRedeemRequest;
import io.electrum.moneytransfer.model.MoneyTransferRedeemResponse;
import io.electrum.moneytransfer.resource.impl.MoneyTransferTestServer;
import io.electrum.moneytransfer.server.util.MoneyTransferUtils;
import io.electrum.moneytransfer.server.util.RequestKey;
import io.electrum.moneytransfer.server.util.Status;

public class RedeemOrderHandler extends BaseHandler {

   public RedeemOrderHandler(HttpHeaders httpHeaders, UriInfo uriInfo) {
      super(httpHeaders, uriInfo);
   }

   public Response handle(MoneyTransferRedeemRequest body) {

      if (!checkBasicAuth(body.getReceiver().getId())) {
         return buildErrorDetailResponse(
               body.getId(),
               null,
               ErrorDetail.ErrorTypeEnum.AUTHENTICATION_ERROR,
               "ReceiverId must match basic auth username");
      }

      if (MoneyTransferTestServer.getIdCache().get(body.getId()) != null) {
         return buildErrorDetailResponse(
               body.getId(),
               null,
               ErrorDetail.ErrorTypeEnum.DUPLICATE_RECORD,
               "Id already in use");
      }

      // Get original request
      RequestKey requestKey =
            new RequestKey(
                  username,
                  password,
                  RequestKey.CREATE_ORDER_RESOURCE,
                  MoneyTransferTestServer.getOrderRedeemRef().get(body.getOrderRedeemRef()));
      MoneyTransferAuthRequest authRequest = MoneyTransferTestServer.getAuthRequestRecords().get(requestKey);
      if (authRequest == null) {
         return buildErrorDetailResponse(
               body.getId(),
               null,
               ErrorDetail.ErrorTypeEnum.UNABLE_TO_LOCATE_RECORD,
               "The orderRedeemRef could be incorrect, original create order could not be found");
      }

      // Get confirmation for the order
      requestKey = new RequestKey(username, password, RequestKey.CONFIRM_PAYMENT_RESOURCE, authRequest.getId());
      if (MoneyTransferTestServer.getAuthConfirmationRecords().get(requestKey) == null) {
         return buildErrorDetailResponse(
               body.getId(),
               null,
               ErrorDetail.ErrorTypeEnum.UNABLE_TO_REDEEM,
               "Order has not been confirmed yet");
      }

      // Check that order has not already been redeemed
      requestKey = new RequestKey(username, password, RequestKey.CONFIRM_REDEEM_RESOURCE, body.getId());
      if (MoneyTransferTestServer.getRedeemConfirmationRecords().get(requestKey) != null) {
         return buildErrorDetailResponse(
               body.getId(),
               null,
               ErrorDetail.ErrorTypeEnum.ALREADY_REDEEMED,
               "Order has already been redeemed, create a new order to redeem");
      }

      // Redeeming more or less than available to redeem.
      if (authRequest.getAmount().getAmount() < body.getAmount().getAmount()) {
         return buildErrorDetailResponse(
               body.getId(),
               null,
               ErrorDetail.ErrorTypeEnum.INVALID_AMOUNT,
               "Cannot redeem for more than the order has been created for");
      } else if (authRequest.getAmount().getAmount() > body.getAmount().getAmount()) {
         return buildErrorDetailResponse(
               body.getId(),
               null,
               ErrorDetail.ErrorTypeEnum.INVALID_AMOUNT,
               "Cannot redeem for less than the order has been created for");
      }

      MoneyTransferTestServer.getIdCache().put(body.getId(), Status.REDEEM);

      // Authenticate the pin with the create order request
      if (!authRequest.getPin().getPinBlock().equals(body.getPin().getPinBlock())) {
         Integer retries = MoneyTransferTestServer.getAuthRequestPinRetries().get(authRequest.getId());
         if (retries > 3) {
            return buildErrorDetailResponse(
                  body.getId(),
                  null,
                  ErrorDetail.ErrorTypeEnum.PIN_RETRIES_EXCEEDED,
                  "PinBlock did not match create orders pinBlock more than 3 times");
         }
         MoneyTransferTestServer.getAuthRequestPinRetries().put(authRequest.getId(), retries + 1);
         return buildErrorDetailResponse(
               body.getId(),
               null,
               ErrorDetail.ErrorTypeEnum.INCORRECT_PIN,
               "PinBlock does not match create orders pinBlock");
      }

      requestKey = new RequestKey(username, password, RequestKey.REDEEM_ORDER_RESOURCE, body.getId());
      MoneyTransferTestServer.getRedeemRequestRecords().put(requestKey, body);
      MoneyTransferRedeemResponse redeemResponse =
            MoneyTransferUtils.copyClass(body, MoneyTransferRedeemRequest.class, MoneyTransferRedeemResponse.class);
      if (redeemResponse == null) {
         return buildErrorDetailResponse(
               ErrorDetail.ErrorTypeEnum.SYSTEM_ERROR,
               "RedeemOrderHandler has failed to complete your request");
      }

      redeemResponse.setAmount(authRequest.getAmount());
      redeemResponse.setOrderId(authRequest.getId());
      redeemResponse.getThirdPartyIdentifiers()
            .add(MoneyTransferUtils.getRandomThirdPartyIdentifier(body.getReceiver().getId()));

      requestKey = new RequestKey(username, password, RequestKey.REDEEM_ORDER_RESOURCE, body.getId());
      MoneyTransferTestServer.getRedeemResponseRecords().put(requestKey, redeemResponse);
      MoneyTransferTestServer.getOrderRedeemRef().put(body.getOrderRedeemRef(), body.getId());

      return Response.created(uriInfo.getRequestUri()).entity(redeemResponse).build();
   }
}
