package io.electrum.moneytransfer.handler.order;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import io.electrum.moneytransfer.handler.BaseHandler;
import io.electrum.moneytransfer.model.ErrorDetail;
import io.electrum.moneytransfer.model.MoneyTransferRedeemRequest;
import io.electrum.moneytransfer.model.MoneyTransferRedeemResponse;
import io.electrum.moneytransfer.server.backend.records.AuthRecord;
import io.electrum.moneytransfer.server.backend.records.RedemptionRecord;
import io.electrum.moneytransfer.server.backend.records.RequestRecord;
import io.electrum.moneytransfer.server.util.MoneyTransferUtils;

public class RedeemOrderHandler extends BaseHandler {

   public RedeemOrderHandler(HttpHeaders httpHeaders, UriInfo uriInfo) {
      super(httpHeaders, uriInfo);
   }

   public Response handle(MoneyTransferRedeemRequest body) {

      if (!checkBasicAuth(body.getClient().getId())) {
         return buildErrorDetailResponse(
               body.getId(),
               null,
               ErrorDetail.ErrorTypeEnum.AUTHENTICATION_ERROR,
               "ClientId must match basic auth username");
      }

      if (moneyTransferDb.doesUuidExist(body.getId())) {
         return buildErrorDetailResponse(
               body.getId(),
               null,
               ErrorDetail.ErrorTypeEnum.DUPLICATE_RECORD,
               "Id already in use");
      }

      // Get original request
      AuthRecord authRecord = moneyTransferDb.getAuthTable().getWithOrderRedeemRef(body.getOrderRedeemRef());
      if (authRecord == null) {
         return buildErrorDetailResponse(
               body.getId(),
               null,
               ErrorDetail.ErrorTypeEnum.UNABLE_TO_LOCATE_RECORD,
               "The orderRedeemRef could be incorrect, original create order could not be found.");
      }

      // Get confirmation for the order
      if (!authRecord.getState().equals(RequestRecord.State.CONFIRMED)) {
         return buildErrorDetailResponse(
               body.getId(),
               null,
               ErrorDetail.ErrorTypeEnum.UNABLE_TO_REDEEM,
               "Order has not been confirmed yet");
      }

      // Check that order has not already been redeemed
      if (moneyTransferDb.getRedemptionTable().getRecord(body.getId()) != null) {
         return buildErrorDetailResponse(
               body.getId(),
               null,
               ErrorDetail.ErrorTypeEnum.ALREADY_REDEEMED,
               "Order has already been redeemed, create a new order to redeem");
      }

      // Redeeming more or less than available to redeem.
      if (authRecord.getAuthRequest().getAmount().getAmount() < body.getAmount().getAmount()) {
         return buildErrorDetailResponse(
               body.getId(),
               null,
               ErrorDetail.ErrorTypeEnum.INVALID_AMOUNT,
               "Cannot redeem for more than the order has been created for");
      } else if (authRecord.getAuthRequest().getAmount().getAmount() > body.getAmount().getAmount()) {
         return buildErrorDetailResponse(
               body.getId(),
               null,
               ErrorDetail.ErrorTypeEnum.INVALID_AMOUNT,
               "Cannot redeem for less than the order has been created for");
      }

      // Authenticate the pin with the create order request
      if (!authRecord.getAuthRequest().getPin().getPinBlock().equals(body.getPin().getPinBlock())) {
         if (authRecord.getPinRetries() >= 3) {
            return buildErrorDetailResponse(
                  body.getId(),
                  null,
                  ErrorDetail.ErrorTypeEnum.PIN_RETRIES_EXCEEDED,
                  "PinBlock did not match create orders pinBlock more than 3 times");
         }
         authRecord.incorrectPinEntry();
         return buildErrorDetailResponse(
               body.getId(),
               null,
               ErrorDetail.ErrorTypeEnum.INCORRECT_PIN,
               "PinBlock does not match create orders pinBlock");
      }

      MoneyTransferRedeemResponse redeemResponse =
            MoneyTransferUtils.copyClass(body, MoneyTransferRedeemRequest.class, MoneyTransferRedeemResponse.class);
      if (redeemResponse == null) {
         return buildErrorDetailResponse(
               ErrorDetail.ErrorTypeEnum.SYSTEM_ERROR,
               "RedeemOrderHandler has failed to complete your request");
      }

      moneyTransferDb.getRedemptionTable().putRecord(new RedemptionRecord(body.getId(), body));
      redeemResponse.setAmount(authRecord.getAuthRequest().getAmount());
      redeemResponse.setOrderId(authRecord.getAuthRequest().getId());
      redeemResponse.getThirdPartyIdentifiers()
            .add(MoneyTransferUtils.getRandomThirdPartyIdentifier(body.getReceiver().getId()));

      return Response.created(uriInfo.getRequestUri()).entity(redeemResponse).build();
   }
}
