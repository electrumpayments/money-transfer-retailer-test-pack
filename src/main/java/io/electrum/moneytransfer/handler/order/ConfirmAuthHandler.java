package io.electrum.moneytransfer.handler.order;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import io.electrum.moneytransfer.handler.BaseHandler;
import io.electrum.moneytransfer.model.ErrorDetail;
import io.electrum.moneytransfer.model.MoneyTransferConfirmation;
import io.electrum.moneytransfer.server.backend.records.AuthConfirmationRecord;
import io.electrum.moneytransfer.server.backend.records.AuthRecord;
import io.electrum.moneytransfer.server.backend.records.RequestRecord;

public class ConfirmAuthHandler extends BaseHandler {

   public ConfirmAuthHandler(HttpHeaders httpHeaders, UriInfo uriInfo) {
      super(httpHeaders, uriInfo);
   }

   public Response handle(MoneyTransferConfirmation body) {
      if (!wasDatabasePresentBeforeRequest()) {
         buildErrorDetailResponse(
               body.getId(),
               body.getRequestId(),
               ErrorDetail.ErrorTypeEnum.AUTHENTICATION_ERROR,
               "The AuthRequest and Confirmation should be sent with same basic auth credentials");
      }

      AuthRecord authRecord = moneyTransferDb.getAuthTable().getRecord(body.getRequestId());
      if (authRecord == null) {
         return buildErrorDetailResponse(
               body.getId(),
               body.getRequestId(),
               ErrorDetail.ErrorTypeEnum.UNABLE_TO_LOCATE_RECORD,
               "No auth found for the confirmation");
      }

      if (moneyTransferDb.doesUuidExist(body.getId())) {
         return buildErrorDetailResponse(
               body.getId(),
               body.getRequestId(),
               ErrorDetail.ErrorTypeEnum.DUPLICATE_RECORD,
               "Id already in use");
      }

      if (authRecord.getState().equals(RequestRecord.State.REVERSED)) {
         return buildErrorDetailResponse(
               body.getId(),
               body.getRequestId(),
               ErrorDetail.ErrorTypeEnum.DUPLICATE_RECORD,
               "Create order request has already been reversed and cannot be confirmed");
      }

      moneyTransferDb.getAuthConfirmationTable().putRecord(new AuthConfirmationRecord(body.getId(), body));
      authRecord.addConfirmationId(body.getId());
      authRecord.setState(RequestRecord.State.CONFIRMED);

      return Response.accepted().entity(body).build();
   }
}
