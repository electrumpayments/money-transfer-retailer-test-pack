package io.electrum.moneytransfer.handler.order;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import io.electrum.moneytransfer.handler.BaseHandler;
import io.electrum.moneytransfer.model.ErrorDetail;
import io.electrum.moneytransfer.model.MoneyTransferReversal;
import io.electrum.moneytransfer.server.backend.records.RedemptionRecord;
import io.electrum.moneytransfer.server.backend.records.RedemptionReversalRecord;
import io.electrum.moneytransfer.server.backend.records.RequestRecord;

public class ReverseRedeemHandler extends BaseHandler {

   public ReverseRedeemHandler(HttpHeaders httpHeaders, UriInfo uriInfo) {
      super(httpHeaders, uriInfo);
   }

   public Response handle(MoneyTransferReversal body) {
      RedemptionRecord redemptionRecord = moneyTransferDb.getRedemptionTable().getRecord(body.getRequestId());
      if (redemptionRecord == null) {
         return buildErrorDetailResponse(
               body.getId(),
               body.getRequestId(),
               ErrorDetail.ErrorTypeEnum.UNABLE_TO_LOCATE_RECORD,
               "No redeem request found for the reversal");
      }

      if (!checkBasicAuth(redemptionRecord.getRedeemRequest().getReceiver().getId())) {
         return buildErrorDetailResponse(
               body.getId(),
               body.getRequestId(),
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

      if (redemptionRecord.getState().equals(RequestRecord.State.CONFIRMED)) {
         return buildErrorDetailResponse(
               body.getId(),
               body.getRequestId(),
               ErrorDetail.ErrorTypeEnum.ALREADY_REDEEMED,
               "Unable to reverse a confirmed redemption");
      }

      redemptionRecord.addReversalId(body.getId());
      redemptionRecord.setState(RequestRecord.State.REVERSED);
      moneyTransferDb.getRedemptionReversalTable().putRecord(new RedemptionReversalRecord(body.getId(), body));

      return Response.accepted().entity(body).build();
   }
}
