package io.electrum.moneytransfer.server.util;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import io.electrum.moneytransfer.model.FeeQuote;
import io.electrum.moneytransfer.model.MoneyTransferAdminMessage;
import io.electrum.moneytransfer.model.MoneyTransferFeeQuote;
import io.electrum.vas.model.LedgerAmount;

import javax.validation.ConstraintViolation;

public class AdminUtils {

   public static String getValidationStringCreateOrUpdateCustomer(MoneyTransferAdminMessage moneyTransferAdminMessage) {
      Set<ConstraintViolation<?>> violations = new HashSet<ConstraintViolation<?>>();

      if (moneyTransferAdminMessage != null) {
         MoneyTransferUtils.validateOriginator(moneyTransferAdminMessage.getOriginator(), violations);
         MoneyTransferUtils.validatePersonalDetails(moneyTransferAdminMessage.getCustomerDetails(), violations);
      }
      return MoneyTransferUtils.buildFormatErrorString(violations);
   }

   public static MoneyTransferFeeQuote getMoneyTransferFeeQuote(
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
