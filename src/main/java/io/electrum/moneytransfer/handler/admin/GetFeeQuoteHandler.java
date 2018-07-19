package io.electrum.moneytransfer.handler.admin;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import io.electrum.moneytransfer.handler.BaseHandler;
import io.electrum.moneytransfer.model.ErrorDetail;
import io.electrum.moneytransfer.server.util.AdminUtils;

public class GetFeeQuoteHandler extends BaseHandler {

   public GetFeeQuoteHandler(HttpHeaders httpHeaders, UriInfo uriInfo) {
      super(httpHeaders, uriInfo);
   }

   public Response handle(
         Long amount,
         Boolean amountIncludesFee,
         String idNumber,
         String merchantId,
         String originatorInstId,
         String receiverId,
         String senderCell,
         String recipientCell) {

      String validationString = getValidationStringGetFeeQuote(amount, amountIncludesFee, originatorInstId, receiverId);
      if (validationString.length() > 0) {
         return buildErrorDetailResponse(ErrorDetail.ErrorTypeEnum.FORMAT_ERROR, validationString);
      }

      if (!checkBasicAuth(receiverId)) {
         return buildErrorDetailResponse(
               ErrorDetail.ErrorTypeEnum.AUTHENTICATION_ERROR,
               "ReceiverId must match basic auth username");
      }

      if (amount < 0) {
         return buildErrorDetailResponse(ErrorDetail.ErrorTypeEnum.INVALID_AMOUNT, null);
      }

      return Response.ok(
            AdminUtils.getMoneyTransferFeeQuote(
                  amount,
                  amountIncludesFee,
                  idNumber,
                  merchantId,
                  originatorInstId,
                  receiverId,
                  senderCell,
                  recipientCell))
            .build();
   }

   private String getValidationStringGetFeeQuote(
         Long amount,
         Boolean amountIncludesFee,
         String originatorInstId,
         String receiverId) {
      StringBuilder sb = new StringBuilder();

      if (amount == null) {
         sb.append("Query param, amount is null.\n");
      }
      if (amountIncludesFee == null) {
         sb.append("Query param, amountIncludesFee is null.\n");
      }
      if (originatorInstId == null) {
         sb.append("Query param, originatorInstId is null.\n");
      }
      if (receiverId == null) {
         sb.append("Query param, receiverId is null.\n");
      }
      return sb.toString();
   }
}
