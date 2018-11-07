package io.electrum.moneytransfer.handler.order;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import io.electrum.moneytransfer.handler.BaseHandler;
import io.electrum.moneytransfer.model.ErrorDetail;
import io.electrum.moneytransfer.model.ErrorDetail.ErrorTypeEnum;
import io.electrum.moneytransfer.model.MoneyTransferAdminMessage;
import io.electrum.moneytransfer.model.MoneyTransferAuthRequest;
import io.electrum.moneytransfer.model.MoneyTransferAuthResponse;
import io.electrum.moneytransfer.server.backend.records.AdminRecord;
import io.electrum.moneytransfer.server.backend.records.AuthRecord;
import io.electrum.moneytransfer.server.util.MoneyTransferUtils;
import io.electrum.moneytransfer.server.util.RandomData;

public class AuthHandler extends BaseHandler {

   public AuthHandler(HttpHeaders httpHeaders, UriInfo uriInfo) {
      super(httpHeaders, uriInfo);
   }

   public Response handle(MoneyTransferAuthRequest request) {

      if (!checkBasicAuth(request.getClient().getId())) {
         return buildErrorDetailResponse(
               request.getId(),
               null,
               ErrorDetail.ErrorTypeEnum.AUTHENTICATION_ERROR,
               "ClientId must match basic auth username");
      }

      if (moneyTransferDb.doesUuidExist(request.getId())) {
         return buildErrorDetailResponse(request.getId(), null, ErrorTypeEnum.DUPLICATE_RECORD, "Id already in use");
      }

      if (request.getAmount().getAmount() <= 0) {
         return buildErrorDetailResponse(
               request.getId(),
               null,
               ErrorTypeEnum.INVALID_AMOUNT,
               "Amount cannot be negative");
      }

      if (request.getAmount().getAmount() > 500000) {
         if (request.getAmount().getAmount() > 1000000) {
            return buildErrorDetailResponse(
                  request.getId(),
                  null,
                  ErrorTypeEnum.MONTHLY_LIMIT_EXCEEDED,
                  "All amounts above R10000 will return with a MONTHLY LIMIT EXCEEDED");
         }
         return buildErrorDetailResponse(
               request.getId(),
               null,
               ErrorTypeEnum.DAILY_LIMIT_EXCEEDED,
               "All amounts above R5000 but less than R10000 will return with a MONTHLY LIMIT EXCEEDED");
      }

      MoneyTransferAuthResponse moneyTransferAuthResponse =
            MoneyTransferUtils.copyClass(request, MoneyTransferAuthRequest.class, MoneyTransferAuthResponse.class);

      if (moneyTransferAuthResponse == null) {
         return buildErrorDetailResponse(ErrorTypeEnum.SYSTEM_ERROR, "AuthHandler has failed to complete your request");
      }

      if (request.getNewCustomer()) {
         MoneyTransferAdminMessage moneyTransferAdminMessage = new MoneyTransferAdminMessage();
         moneyTransferAdminMessage.setOriginator(request.getOriginator());
         moneyTransferAdminMessage.setReceiver(request.getReceiver());
         moneyTransferAdminMessage.setCustomerDetails(request.getSenderDetails());
         moneyTransferAdminMessage.setCustomerProfileId(RandomData.random09AZ(10));

         moneyTransferDb.getAdminTable()
               .putRecord(new AdminRecord(request.getSenderDetails().getIdNumber(), moneyTransferAdminMessage));
      } else {
         if (request.getCustomerProfileId() != null) {
            AdminRecord cachedAdminRecord =
                  moneyTransferDb.getAdminTable().getAdminRecordWithCustomerProfileId(request.getCustomerProfileId());

            if (!cachedAdminRecord.getAdminMessage().getCustomerDetails().getIdNumber().equals(
                  request.getSenderDetails().getIdNumber())) {
               return buildErrorDetailResponse(
                     request.getId(),
                     null,
                     ErrorTypeEnum.CUSTOMER_CHECK_FAILED,
                     "CustomerProfileId does not match stored sending customer CustomerProfileId");
            }
         }
      }

      moneyTransferAuthResponse.setOrderId(request.getId());
      moneyTransferAuthResponse.setOrderRedeemRef(RandomData.random09AZ(20));
      moneyTransferAuthResponse.getThirdPartyIdentifiers()
            .add(MoneyTransferUtils.getRandomThirdPartyIdentifier(request.getReceiver().getId()));
      moneyTransferAuthResponse.getThirdPartyIdentifiers()
            .add(MoneyTransferUtils.getRandomThirdPartyIdentifier(request.getSettlementEntity().getId()));

      moneyTransferDb.getAuthTable()
            .putRecord(new AuthRecord(request.getId(), request, moneyTransferAuthResponse.getOrderRedeemRef()));
      return Response.created(uriInfo.getRequestUri()).entity(moneyTransferAuthResponse).build();
   }
}
