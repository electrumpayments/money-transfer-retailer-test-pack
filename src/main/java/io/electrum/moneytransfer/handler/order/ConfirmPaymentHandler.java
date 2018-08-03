package io.electrum.moneytransfer.handler.order;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import io.electrum.moneytransfer.handler.BaseHandler;
import io.electrum.moneytransfer.model.ErrorDetail;
import io.electrum.moneytransfer.model.MoneyTransferAuthRequest;
import io.electrum.moneytransfer.model.MoneyTransferConfirmation;
import io.electrum.moneytransfer.resource.impl.MoneyTransferTestServer;
import io.electrum.moneytransfer.server.util.RequestKey;
import io.electrum.moneytransfer.server.util.Status;

public class ConfirmPaymentHandler extends BaseHandler {

   public ConfirmPaymentHandler(HttpHeaders httpHeaders, UriInfo uriInfo) {
      super(httpHeaders, uriInfo);
   }

   public Response handle(MoneyTransferConfirmation body) {
      RequestKey requestKey = new RequestKey(username, password, RequestKey.CREATE_ORDER_RESOURCE, body.getRequestId());
      MoneyTransferAuthRequest authRequest = MoneyTransferTestServer.getAuthRequestRecords().get(requestKey);
      if (authRequest == null) {
         return buildErrorDetailResponse(
               body.getId(),
               body.getRequestId(),
               ErrorDetail.ErrorTypeEnum.UNABLE_TO_LOCATE_RECORD,
               "No auth found for the confirmation");
      }

      if (!checkBasicAuth(authRequest.getReceiver().getId())) {
         return buildErrorDetailResponse(
               body.getId(),
               null,
               ErrorDetail.ErrorTypeEnum.AUTHENTICATION_ERROR,
               "ReceiverId must match basic auth username");
      }

      if (MoneyTransferTestServer.getIdCache().get(body.getId()) != null) {
         return buildErrorDetailResponse(
               body.getId(),
               body.getRequestId(),
               ErrorDetail.ErrorTypeEnum.DUPLICATE_RECORD,
               "Id already in use");
      }

      MoneyTransferTestServer.getIdCache().put(body.getId(), Status.ORDER_CONFIRMED);
      requestKey = new RequestKey(username, password, RequestKey.CONFIRM_PAYMENT_RESOURCE, body.getRequestId());
      MoneyTransferTestServer.getAuthConfirmationRecords().put(requestKey, body);

      return Response.accepted().entity(body).build();
   }
}
