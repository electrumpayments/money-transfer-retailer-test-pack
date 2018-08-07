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
import io.electrum.moneytransfer.resource.impl.MoneyTransferTestServer;
import io.electrum.moneytransfer.server.util.MoneyTransferUtils;
import io.electrum.moneytransfer.server.util.RandomData;
import io.electrum.moneytransfer.server.util.RequestKey;
import io.electrum.moneytransfer.server.util.Status;

public class CreateOrderHandler extends BaseHandler {

   public CreateOrderHandler(HttpHeaders httpHeaders, UriInfo uriInfo) {
      super(httpHeaders, uriInfo);
   }

   public Response handle(MoneyTransferAuthRequest request) {

      if (!checkBasicAuth(request.getReceiver().getId())) {
         return buildErrorDetailResponse(
               request.getId(),
               null,
               ErrorDetail.ErrorTypeEnum.AUTHENTICATION_ERROR,
               "ReceiverId must match basic auth username");
      }

      if (MoneyTransferTestServer.getIdCache().get(request.getId()) != null) {
         return buildErrorDetailResponse(request.getId(), null, ErrorTypeEnum.DUPLICATE_RECORD, "Id already in use");
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
         return buildErrorDetailResponse(
               ErrorTypeEnum.SYSTEM_ERROR,
               "CreateOrderHandler has failed to complete your request");
      }

      MoneyTransferTestServer.getIdCache().put(request.getId(), Status.ORDER_CREATED);

      if (request.getNewCustomer()) {
         if (request.getSenderDetails() == null) {
            return buildErrorDetailResponse(
                  request.getId(),
                  null,
                  ErrorTypeEnum.FORMAT_ERROR,
                  "Sender details can not be null if new customer.");
         }
         MoneyTransferAdminMessage moneyTransferAdminMessage = new MoneyTransferAdminMessage();
         moneyTransferAdminMessage.setOriginator(request.getOriginator());
         moneyTransferAdminMessage.setReceiver(request.getReceiver());
         moneyTransferAdminMessage.setCustomerDetails(request.getSenderDetails());
         moneyTransferAdminMessage.setCustomerProfileId(RandomData.random09AZ(10));

         MoneyTransferTestServer.getCustomerIdToIdRecords()
               .put(moneyTransferAdminMessage.getCustomerProfileId(), request.getSenderDetails().getIdNumber());

         RequestKey key =
               new RequestKey(
                     username,
                     password,
                     RequestKey.CUSTOMER_RESOURCE,
                     moneyTransferAdminMessage.getCustomerDetails().getIdNumber());
         MoneyTransferTestServer.getAdminRecords().put(key, moneyTransferAdminMessage);
      } else {
         if (request.getCustomerProfileId() == null) {
            return buildErrorDetailResponse(
                  request.getId(),
                  null,
                  ErrorTypeEnum.FORMAT_ERROR,
                  "Existing customerProfileId cannot be null if newCustomer is false, sender needs to be registered.");
         }
         String customerId = MoneyTransferTestServer.getCustomerIdToIdRecords().get(request.getCustomerProfileId());
         if (customerId == null) {
            return buildErrorDetailResponse(request.getId(), null, ErrorTypeEnum.UNABLE_TO_LOCATE_RECORD, "Customer profile not found for sending customer");
         }
         RequestKey key = new RequestKey(username, password, RequestKey.CUSTOMER_RESOURCE, customerId);
         moneyTransferAuthResponse
               .setSenderDetails(MoneyTransferTestServer.getAdminRecords().get(key).getCustomerDetails());
      }

      RequestKey key = new RequestKey(username, password, RequestKey.CREATE_ORDER_RESOURCE, request.getId());
      MoneyTransferTestServer.getAuthRequestRecords().put(key, request);

      moneyTransferAuthResponse.setOrderId(request.getId());
      moneyTransferAuthResponse.setOrderRedeemRef(RandomData.random09AZ(20));
      moneyTransferAuthResponse.getThirdPartyIdentifiers()
            .add(MoneyTransferUtils.getRandomThirdPartyIdentifier(request.getReceiver().getId()));
      moneyTransferAuthResponse.getThirdPartyIdentifiers()
            .add(MoneyTransferUtils.getRandomThirdPartyIdentifier(request.getSettlementEntity().getId()));
      MoneyTransferTestServer.getAuthResponseRecords().put(key, moneyTransferAuthResponse); // TODO Maybe remove this?
      MoneyTransferTestServer.getOrderRedeemRef().put(moneyTransferAuthResponse.getOrderRedeemRef(), request.getId());
      MoneyTransferTestServer.getAuthRequestPinRetries().put(request.getId(), 0);

      return Response.created(uriInfo.getRequestUri()).entity(moneyTransferAuthResponse).build();
   }
}
