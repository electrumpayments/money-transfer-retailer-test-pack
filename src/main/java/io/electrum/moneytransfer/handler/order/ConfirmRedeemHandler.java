package io.electrum.moneytransfer.handler.order;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import io.electrum.moneytransfer.handler.BaseHandler;
import io.electrum.moneytransfer.model.ErrorDetail;
import io.electrum.moneytransfer.model.MoneyTransferConfirmation;
import io.electrum.moneytransfer.server.backend.records.RedemptionConfirmationRecord;
import io.electrum.moneytransfer.server.backend.records.RedemptionRecord;
import io.electrum.moneytransfer.server.backend.records.RequestRecord;

public class ConfirmRedeemHandler extends BaseHandler {

   public ConfirmRedeemHandler(HttpHeaders httpHeaders, UriInfo uriInfo) {
      super(httpHeaders, uriInfo);
   }

   public Response handle(MoneyTransferConfirmation body) {
      if (!wasDatabasePresentBeforeRequest()) {
         buildErrorDetailResponse(
                 body.getId(),
                 body.getRequestId(),
                 ErrorDetail.ErrorTypeEnum.AUTHENTICATION_ERROR,
                 "The RedeemRequest and Confirmation should be sent with same basic auth credentials");
      }

      RedemptionRecord redemptionRecord = moneyTransferDb.getRedemptionTable().getRecord(body.getRequestId());
      if (redemptionRecord == null) {
         return buildErrorDetailResponse(
               body.getId(),
               body.getRequestId(),
               ErrorDetail.ErrorTypeEnum.UNABLE_TO_LOCATE_RECORD,
               "No redeem request found for the confirmation");
      }

      if (moneyTransferDb.doesUuidExist(body.getId())) {
         return buildErrorDetailResponse(
               body.getId(),
               body.getRequestId(),
               ErrorDetail.ErrorTypeEnum.DUPLICATE_RECORD,
               "Id already in use");
      }

      if (redemptionRecord.getState().equals(RequestRecord.State.REVERSED)) {
         return buildErrorDetailResponse(
               body.getId(),
               body.getRequestId(),
               ErrorDetail.ErrorTypeEnum.UNABLE_TO_REDEEM,
               "Redemption request has been reversed");
      }

      redemptionRecord.addConfirmationId(body.getId());
      redemptionRecord.setState(RequestRecord.State.CONFIRMED);
      moneyTransferDb.getRedemptionConfirmationTable().putRecord(new RedemptionConfirmationRecord(body.getId(), body));

      return Response.accepted().entity(body).build();
   }
}
