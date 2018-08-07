package io.electrum.moneytransfer.handler.order;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import io.electrum.moneytransfer.handler.BaseHandler;
import io.electrum.moneytransfer.model.ErrorDetail;
import io.electrum.moneytransfer.model.MoneyTransferReversal;
import io.electrum.moneytransfer.server.backend.records.AuthRecord;
import io.electrum.moneytransfer.server.backend.records.AuthReversalRecord;
import io.electrum.moneytransfer.server.backend.records.RequestRecord;

public class ReverseAuthHandler extends BaseHandler {

   public ReverseAuthHandler(HttpHeaders httpHeaders, UriInfo uriInfo) {
      super(httpHeaders, uriInfo);
   }

   public Response handle(MoneyTransferReversal body) {
      AuthRecord authRecord = moneyTransferDb.getAuthTable().getRecord(body.getRequestId());
      if (authRecord == null) {
         return buildErrorDetailResponse(
               body.getId(),
               body.getRequestId(),
               ErrorDetail.ErrorTypeEnum.UNABLE_TO_LOCATE_RECORD,
               "No auth found for the reversal");
      }

      if (!checkBasicAuth(authRecord.getAuthRequest().getReceiver().getId())) {
         return buildErrorDetailResponse(
               body.getId(),
               null,
               ErrorDetail.ErrorTypeEnum.AUTHENTICATION_ERROR,
               "ReceiverId must match basic auth username");
      }

      if (moneyTransferDb.doesUuidExist(body.getId())) {
         return buildErrorDetailResponse(
               body.getId(),
               body.getRequestId(),
               ErrorDetail.ErrorTypeEnum.DUPLICATE_RECORD,
               "Id already in use");
      }

      if (authRecord.getState().equals(RequestRecord.State.CONFIRMED)) {
         return buildErrorDetailResponse(
               body.getId(),
               body.getRequestId(),
               ErrorDetail.ErrorTypeEnum.DUPLICATE_RECORD,
               "Unable to reverse a confirmed order");
      }

      moneyTransferDb.getAuthReversalTable().putRecord(new AuthReversalRecord(body.getId(), body));
      authRecord.addReversalId(body.getId());
      authRecord.setState(RequestRecord.State.REVERSED);

      return Response.accepted().entity(body).build();
   }
}
