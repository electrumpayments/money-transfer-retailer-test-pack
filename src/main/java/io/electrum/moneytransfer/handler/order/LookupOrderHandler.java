package io.electrum.moneytransfer.handler.order;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import io.electrum.moneytransfer.handler.BaseHandler;
import io.electrum.moneytransfer.model.ErrorDetail;
import io.electrum.moneytransfer.model.MoneyTransferLookupResponse;
import io.electrum.moneytransfer.server.backend.records.AuthRecord;
import io.electrum.moneytransfer.server.backend.records.RedemptionRecord;
import io.electrum.vas.model.Originator;

public class LookupOrderHandler extends BaseHandler {

   public LookupOrderHandler(HttpHeaders httpHeaders, UriInfo uriInfo) {
      super(httpHeaders, uriInfo);
   }

   public Response handle(String orderRedeemRef, String merchantId, String originatorInstId, String receiverId) {

      RedemptionRecord redemptionRecord =
            moneyTransferDb.getRedemptionTable().getRedemptionRecordWithOrderRedeemRef(orderRedeemRef);
      if (redemptionRecord != null) {
         MoneyTransferLookupResponse lookupResponse = new MoneyTransferLookupResponse();
         lookupResponse.setOriginator(redemptionRecord.getRedeemRequest().getOriginator());
         lookupResponse.setReceiver(redemptionRecord.getRedeemRequest().getReceiver());
         lookupResponse.setAmount(redemptionRecord.getRedeemRequest().getAmount());

         if (redemptionRecord.getRedeemRequest().getTime().minusMonths(1).isAfter(System.currentTimeMillis())) {
            lookupResponse.setStatus(MoneyTransferLookupResponse.StatusEnum.EXPIRED);
            return Response.ok(lookupResponse).build();
         }

         switch (redemptionRecord.getState()) {
         case CONFIRMED:
            lookupResponse.setStatus(MoneyTransferLookupResponse.StatusEnum.REDEEMED);
            break;
         case REVERSED:
         case REQUESTED:
         default:
            lookupResponse.setStatus(MoneyTransferLookupResponse.StatusEnum.UNREDEEMED);
         }
         return Response.ok(lookupResponse).build();
      }

      AuthRecord authRecord = moneyTransferDb.getAuthTable().getWithOrderRedeemRef(orderRedeemRef);
      if (authRecord != null) {
         MoneyTransferLookupResponse lookupResponse = new MoneyTransferLookupResponse();
         lookupResponse.setReceiver(authRecord.getAuthRequest().getReceiver());
         lookupResponse.setOriginator(authRecord.getAuthRequest().getOriginator());
         lookupResponse.setAmount(authRecord.getAuthRequest().getAmount());

         if (authRecord.getAuthRequest().getTime().minusMonths(1).isAfter(System.currentTimeMillis())) {
            lookupResponse.setStatus(MoneyTransferLookupResponse.StatusEnum.EXPIRED);
            return Response.ok(lookupResponse).build();
         }

         switch (authRecord.getState()) {
         case REVERSED:
            lookupResponse.setStatus(MoneyTransferLookupResponse.StatusEnum.CANCELLED);
            break;
         case CONFIRMED:
         case REQUESTED:
         default:
            lookupResponse.setStatus(MoneyTransferLookupResponse.StatusEnum.UNREDEEMED);
         }

         return Response.ok(lookupResponse).build();
      }

      return buildErrorDetailResponse(
            ErrorDetail.ErrorTypeEnum.UNABLE_TO_LOCATE_RECORD,
            "Order could not be located, check using correct orderRedeemRef");
   }
}
