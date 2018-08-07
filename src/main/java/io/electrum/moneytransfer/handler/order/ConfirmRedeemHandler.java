package io.electrum.moneytransfer.handler.order;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import io.electrum.moneytransfer.handler.BaseHandler;
import io.electrum.moneytransfer.model.ErrorDetail;
import io.electrum.moneytransfer.model.MoneyTransferConfirmation;
import io.electrum.moneytransfer.model.MoneyTransferRedeemRequest;
import io.electrum.moneytransfer.resource.impl.MoneyTransferTestServer;
import io.electrum.moneytransfer.server.util.RequestKey;
import io.electrum.moneytransfer.server.util.Status;

public class ConfirmRedeemHandler extends BaseHandler {

   public ConfirmRedeemHandler(HttpHeaders httpHeaders, UriInfo uriInfo) {
      super(httpHeaders, uriInfo);
   }

   public Response handle(MoneyTransferConfirmation body) {
      RequestKey requestKey = new RequestKey(username, password, RequestKey.REDEEM_ORDER_RESOURCE, body.getRequestId());
      MoneyTransferRedeemRequest redeemRequest = MoneyTransferTestServer.getRedeemRequestRecords().get(requestKey);
      if (redeemRequest == null) {
         return buildErrorDetailResponse(
               body.getId(),
               body.getRequestId(),
               ErrorDetail.ErrorTypeEnum.UNABLE_TO_LOCATE_RECORD,
               "No redeem request found for the confirmation");
      }

      if (!checkBasicAuth(redeemRequest.getReceiver().getId())) {
         return buildErrorDetailResponse(
               body.getId(),
               body.getRequestId(),
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

      MoneyTransferTestServer.getIdCache().put(body.getId(), Status.REDEEM_CONFIRMED);
      requestKey = new RequestKey(username, password, RequestKey.CONFIRM_REDEEM_RESOURCE, body.getRequestId());
      MoneyTransferTestServer.getRedeemConfirmationRecords().put(requestKey, body);

      return Response.accepted().entity(body).build();
   }
}
