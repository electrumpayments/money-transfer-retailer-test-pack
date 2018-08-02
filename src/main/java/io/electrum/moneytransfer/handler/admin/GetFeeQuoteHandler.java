package io.electrum.moneytransfer.handler.admin;

import java.util.Random;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import io.electrum.moneytransfer.handler.BaseHandler;
import io.electrum.moneytransfer.model.ErrorDetail;
import io.electrum.moneytransfer.model.FeeQuote;
import io.electrum.moneytransfer.model.MoneyTransferFeeQuote;
import io.electrum.moneytransfer.server.util.MoneyTransferUtils;
import io.electrum.vas.model.LedgerAmount;

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

      if (!checkBasicAuth(receiverId)) {
         return buildErrorDetailResponse(
               ErrorDetail.ErrorTypeEnum.AUTHENTICATION_ERROR,
               "ReceiverId must match basic auth username");
      }

      if (amount < 0) {
         return buildErrorDetailResponse(ErrorDetail.ErrorTypeEnum.INVALID_AMOUNT, null);
      }

      return Response
            .ok(
                  getMoneyTransferFeeQuote(
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

   private static MoneyTransferFeeQuote getMoneyTransferFeeQuote(
         Long amount,
         Boolean amountIncludesFee,
         String idNumber,
         String merchantId,
         String originatorInstId,
         String receiverId,
         String senderCell,
         String recipientCell) {

      MoneyTransferFeeQuote moneyTransferFeeQuote = new MoneyTransferFeeQuote();

      LedgerAmount totalAmount = MoneyTransferUtils.getLedgerAmount(amount);
      if (amountIncludesFee != null && amountIncludesFee) {
         moneyTransferFeeQuote.setFeeQuote(
               new FeeQuote().totalAmount(totalAmount)
                     .feeAmount(MoneyTransferUtils.getLedgerAmount((long) (totalAmount.getAmount() * 0.1)))
                     .transferAmount(MoneyTransferUtils.getLedgerAmount((long) (totalAmount.getAmount() * 0.9))));
      } else {
         moneyTransferFeeQuote.setFeeQuote(
               new FeeQuote().totalAmount(totalAmount)
                     .feeAmount(MoneyTransferUtils.getLedgerAmount((long) (new Random().nextInt(10))))
                     .transferAmount(MoneyTransferUtils.getLedgerAmount(totalAmount.getAmount())));
      }

      moneyTransferFeeQuote.setOriginator(MoneyTransferUtils.getOriginator(originatorInstId, merchantId));
      moneyTransferFeeQuote.setReceiver(MoneyTransferUtils.getRandomInstitution(receiverId));
      moneyTransferFeeQuote.setRecipientDetails(MoneyTransferUtils.getPersonalDetails(null, recipientCell));
      moneyTransferFeeQuote.setSenderDetails(MoneyTransferUtils.getPersonalDetails(idNumber, senderCell));

      return moneyTransferFeeQuote;
   }
}
