package io.electrum.moneytransfer.handler.order;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import io.electrum.moneytransfer.handler.BaseHandler;
import io.electrum.moneytransfer.model.ErrorDetail;
import io.electrum.moneytransfer.model.ErrorDetail.ErrorTypeEnum;
import io.electrum.moneytransfer.model.MoneyTransferAdminMessage;
import io.electrum.moneytransfer.model.MoneyTransferAuthRequest;
import io.electrum.moneytransfer.resource.impl.MoneyTransferTestServer;
import io.electrum.moneytransfer.server.util.OrderUtils;
import io.electrum.moneytransfer.server.util.RandomData;
import io.electrum.moneytransfer.server.util.RequestKey;

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

      switch (OrderUtils.getOrderStatus(request.getId(), null, username, password)) {
      case UNABLE_TO_LOCATE_RECORD:
         // Order could not be found to it is safe to create
         break;
      case ORDER_CREATED:
         return buildErrorDetailResponse(
               request.getId(),
               null,
               ErrorTypeEnum.DUPLICATE_RECORD,
               "Order with this id is already created");
      case ORDER_REVERSED:
      case ORDER_CONFIRMED:
      case REDEEM:
      case REDEEM_REVERSED:
      case REDEEM_CONFIRMED:
         return buildErrorDetailResponse(
               ErrorTypeEnum.DUPLICATE_RECORD,
               "Id not unique, all requests need unique Id (Try use UUID's)");
      default:
         return buildErrorDetailResponse(ErrorTypeEnum.SYSTEM_ERROR, "Could not get status of the order");
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

      if (request.getNewCustomer()) {
         MoneyTransferAdminMessage moneyTransferAdminMessage = new MoneyTransferAdminMessage();
         moneyTransferAdminMessage.setOriginator(request.getOriginator());
         moneyTransferAdminMessage.setReceiver(request.getReceiver());
         moneyTransferAdminMessage.setCustomerDetails(request.getRecipientDetails());
         moneyTransferAdminMessage.setCustomerProfileId(RandomData.random09AZ(10));
         RequestKey key =
               new RequestKey(
                     username,
                     password,
                     RequestKey.CUSTOMER_RESOURCE,
                     moneyTransferAdminMessage.getCustomerDetails().getIdNumber());
         MoneyTransferTestServer.getAdminRecords().put(key, moneyTransferAdminMessage);
      }

      RequestKey key = new RequestKey(username, password, RequestKey.CREATE_ORDER_RESOURCE, request.getId());
      MoneyTransferTestServer.getAuthRequestRecords().put(key, request);





      return null;
      // rsp = OrderUtils.canCreateOrder(request.getId(), username, password);
      // if (rsp != null) {
      // return rsp;
      // }
      // MoneyTransferTestServer.getAuthRequestRecords().put(key, request);
      // MoneyTransferAuthResponse voucherRsp = OrderUtils.authRspFromReq(request);
      // MoneyTransferTestServer.getAuthResponseRecords().put(key, voucherRsp);
      // return Response.created(uriInfo.getRequestUri()).entity(voucherRsp).build();

   }


}
