package io.electrum.moneytransfer.server.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import io.electrum.moneytransfer.model.Address;
import io.electrum.vas.model.Amounts;
import io.electrum.vas.model.BasicAdvice;
import io.electrum.vas.model.BasicReversal;
import io.electrum.vas.model.Tender;
import io.electrum.vas.model.TenderAdvice;
import io.electrum.vas.model.Transaction;
import org.glassfish.jersey.internal.util.Base64;

import io.electrum.moneytransfer.model.ErrorDetail;
import io.electrum.moneytransfer.model.PersonalDetails;
import io.electrum.vas.model.Institution;
import io.electrum.vas.model.LedgerAmount;
import io.electrum.vas.model.Merchant;
import io.electrum.vas.model.MerchantName;
import io.electrum.vas.model.Originator;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

public class MoneyTransferUtils {

   private static final HashMap<ErrorDetail.ErrorTypeEnum, ErrorMessages> ERROR_MESSAGES_MAP =
         new HashMap<ErrorDetail.ErrorTypeEnum, ErrorMessages>();

   static {
      ERROR_MESSAGES_MAP.put(
            ErrorDetail.ErrorTypeEnum.TRANSACTION_NOT_ALLOWED_FOR_MERCHANT,
            new ErrorMessages("Transaction not allowed", "E01", "Transaction not allowed for this client"));
      ERROR_MESSAGES_MAP.put(
            ErrorDetail.ErrorTypeEnum.ALREADY_REDEEMED,
            new ErrorMessages("Voucher already redeemed", "E02", "Money Transfer voucher already redeemed"));
      ERROR_MESSAGES_MAP.put(
            ErrorDetail.ErrorTypeEnum.PIN_RETRIES_EXCEEDED,
            new ErrorMessages("User PIN retries have been exceeded", "E03", "Maximum PIN retries have been exceeded"));
      ERROR_MESSAGES_MAP.put(
            ErrorDetail.ErrorTypeEnum.INCORRECT_PIN,
            new ErrorMessages("User PIN entered was incorrect", "E04", "PIN did not match PIN in our records"));
      ERROR_MESSAGES_MAP.put(
            ErrorDetail.ErrorTypeEnum.UNABLE_TO_REDEEM,
            new ErrorMessages("Voucher is unable to be redeemed", "E05", "Voucher has been blocked"));
      ERROR_MESSAGES_MAP.put(
            ErrorDetail.ErrorTypeEnum.INVALID_REDEEM_REF,
            new ErrorMessages(
                  "Invalid redemption voucher reference number",
                  "E06",
                  "Voucher number does not match available vouchers"));
      ERROR_MESSAGES_MAP.put(
            ErrorDetail.ErrorTypeEnum.DAILY_LIMIT_EXCEEDED,
            new ErrorMessages("Daily limit is exceeded, please try again in 24 hours", "E07", "Limit exceeded"));
      ERROR_MESSAGES_MAP.put(
            ErrorDetail.ErrorTypeEnum.MONTHLY_LIMIT_EXCEEDED,
            new ErrorMessages("Monthly limit is exceeded", "E08", "Limit exceeded"));
      ERROR_MESSAGES_MAP.put(
            ErrorDetail.ErrorTypeEnum.PROVIDER_SYSTEM_ERROR,
            new ErrorMessages("Upstream internal error, please try again later", "E09", "SYSTEM_ERROR"));
      ERROR_MESSAGES_MAP.put(
            ErrorDetail.ErrorTypeEnum.CUSTOMER_CHECK_FAILED,
            new ErrorMessages("Customer could not be verified", "E10", "Customer verification failure"));
      ERROR_MESSAGES_MAP.put(
            ErrorDetail.ErrorTypeEnum.DUPLICATE_RECORD,
            new ErrorMessages("Duplicate record", "E11", "Duplicate record"));
      ERROR_MESSAGES_MAP.put(
            ErrorDetail.ErrorTypeEnum.FORMAT_ERROR,
            new ErrorMessages("Message format error", "E12", "Bad message format"));
      ERROR_MESSAGES_MAP
            .put(ErrorDetail.ErrorTypeEnum.SYSTEM_ERROR, new ErrorMessages("Internal system error", null, null));
      ERROR_MESSAGES_MAP.put(
            ErrorDetail.ErrorTypeEnum.TRANSACTION_DECLINED,
            new ErrorMessages("Transaction was declined by upstream", "E14", "Transaction declined"));
      ERROR_MESSAGES_MAP.put(
            ErrorDetail.ErrorTypeEnum.INVALID_AMOUNT,
            new ErrorMessages("Invalid amount to be redeemed", "E15", "Amount to be redeemed is invalid"));
      ERROR_MESSAGES_MAP.put(
            ErrorDetail.ErrorTypeEnum.ROUTING_ERROR,
            new ErrorMessages("Routing error", "E16", "Message could not be delivered successfully"));
      ERROR_MESSAGES_MAP.put(
            ErrorDetail.ErrorTypeEnum.TRANSACTION_NOT_SUPPORTED,
            new ErrorMessages("Transaction is not supported by third party", "E17", "Transaction not supported"));
      ERROR_MESSAGES_MAP.put(
            ErrorDetail.ErrorTypeEnum.UNABLE_TO_LOCATE_RECORD,
            new ErrorMessages("Record could not be located", "E18", "Record not found"));
      ERROR_MESSAGES_MAP.put(
            ErrorDetail.ErrorTypeEnum.UPSTREAM_UNAVAILABLE,
            new ErrorMessages("Upstream entity is not available", "E19", "Unavailable"));
      ERROR_MESSAGES_MAP.put(
            ErrorDetail.ErrorTypeEnum.AUTHENTICATION_ERROR,
            new ErrorMessages("Incorrect PIN", "E20", "PIN does not match records"));
   }

   private static class ErrorMessages {
      final String errorMessage;
      final String providerErrorCode;
      final String providerErrorMessage;

      ErrorMessages(String errorMessage, String providerErrorCode, String providerErrorMessage) {
         this.errorMessage = errorMessage;
         this.providerErrorCode = providerErrorCode;
         this.providerErrorMessage = providerErrorMessage;
      }
   }

   static <T> Set<ConstraintViolation<T>> validate(T tInstance) {
      if (tInstance == null) {
         return new HashSet<ConstraintViolation<T>>();
      }
      Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
      Set<ConstraintViolation<T>> violations = validator.validate(tInstance);
      return violations;
   }

   protected static String buildFormatErrorString(Set<ConstraintViolation<?>> violations) {
      if (violations.size() == 0) {
         return "";
      }

      StringBuilder sb  = new StringBuilder();
      for (ConstraintViolation violation : violations) {
         sb.append("Field: ").append(violation.getPropertyPath()).append("  Message: ");
         sb.append(violation.getMessage()).append("\n");
      }

      return sb.toString();
   }

   protected static void validateTransaction(Transaction transaction, Set<ConstraintViolation<?>> violations) {
      violations.addAll(validate(transaction));
      if (transaction != null) {
         violations.addAll(validate(transaction.getId()));
         violations.addAll(validate(transaction.getTime()));
         validateOriginator(transaction.getOriginator(), violations);
         violations.addAll(validate(transaction.getClient()));
         violations.addAll(validate(transaction.getSettlementEntity()));
         violations.addAll(validate(transaction.getReceiver()));
         violations.addAll(validate(transaction.getThirdPartyIdentifiers()));
         violations.addAll(validate(transaction.getSlipData()));
         violations.addAll(validate(transaction.getBasketRef()));
         violations.addAll(validate(transaction.getTranType()));
         violations.addAll(validate(transaction.getSrcAccType()));
         violations.addAll(validate(transaction.getDestAccType()));
      }
   }

   protected static void validateOriginator(Originator originator, Set<ConstraintViolation<?>> violations) {
      violations.addAll(validate(originator));
      if (originator != null) {
         violations.addAll(validate(originator.getInstitution()));
         violations.addAll(validate(originator.getTerminalId()));
         Merchant merchant = originator.getMerchant();
         violations.addAll(validate(merchant));
         if (merchant != null) {
            violations.addAll(validate(merchant.getMerchantId()));
            violations.addAll(validate(merchant.getMerchantType()));
            violations.addAll(validate(merchant.getMerchantName()));
         }
      }
   }

   protected static void validateBasicReversal(BasicReversal reversal, Set<ConstraintViolation<?>> violations) {
      violations.addAll(validate(reversal));
      if (reversal != null) {
         validateBasicAdvice(reversal, violations);
         violations.addAll(validate(reversal.getReversalReason()));
      }
   }

   protected static void validateInstitution(Institution institution, Set<ConstraintViolation<?>> violations) {
      violations.addAll(validate(institution));
      if (institution != null) {
         violations.addAll(validate(institution.getId()));
         violations.addAll(validate(institution.getName()));
      }
   }

   protected static void validateBasicAdvice(BasicAdvice basicAdvice, Set<ConstraintViolation<?>> violations) {
      violations.addAll(validate(basicAdvice));
      if (basicAdvice != null) {
         violations.addAll(validate(basicAdvice.getId()));
         violations.addAll(validate(basicAdvice.getRequestId()));
         violations.addAll(validate(basicAdvice.getThirdPartyIdentifiers()));
         violations.addAll(validate(basicAdvice.getTime()));
      }
   }

   protected static void validatePersonalDetails(PersonalDetails personalDetails, Set<ConstraintViolation<?>> violations) {
      violations.addAll(validate(personalDetails));
      if (personalDetails != null) {
         violations.addAll(validate(personalDetails.getFirstName()));
         violations.addAll(validate(personalDetails.getLastName()));
         violations.addAll(validate(personalDetails.getDateOfBirth()));
         violations.addAll(validate(personalDetails.getEmail()));
         violations.addAll(validate(personalDetails.getContactNumber()));
         violations.addAll(validate(personalDetails.getAltContactHome()));
         violations.addAll(validate(personalDetails.getAltContactWork()));
         validateAddress(personalDetails.getAddress(), violations);
         violations.addAll(validate(personalDetails.getIdNumber()));
         violations.addAll(validate(personalDetails.getIdType()));
         violations.addAll(validate(personalDetails.getIdCountryCode()));
         violations.addAll(validate(personalDetails.getNationality()));
      }
   }

   protected static void validateAddress(Address address, Set<ConstraintViolation<?>> violations) {
      violations.addAll(validate(address));
      if (address != null) {
         violations.addAll(validate(address.getAddressLine1()));
         violations.addAll(validate(address.getAddressLine2()));
         violations.addAll(validate(address.getCity()));
         violations.addAll(validate(address.getProvince()));
         violations.addAll(validate(address.getPostCode()));
         violations.addAll(validate(address.getCountry()));
      }
   }

   public static String getAuthString(String authHeader) {
      if (authHeader == null || authHeader.isEmpty() || !authHeader.startsWith("Basic ")) {
         return null;
      }
      String credsSubstring = authHeader.substring("Basic ".length());
      return Base64.decodeAsString(credsSubstring);
   }

   public static String getUsernameFromAuth(String authString) {
      String username = "null";
      if (authString != null && !authString.isEmpty()) {
         username = authString.substring(0, authString.indexOf(':'));
      }
      return username;
   }

   public static String getPasswordFromAuth(String authString) {
      String password = "null";
      if (authString != null && !authString.isEmpty()) {
         password = authString.substring(authString.indexOf(':') + 1);
      }
      return password;
   }

   protected static Originator getOriginator(String originatorInstId, String merchantId) {
      Originator originator = new Originator();
      originator.setMerchant(getRandomMerchant(merchantId));
      originator.setInstitution(getRandomInstitution(originatorInstId));
      originator.setTerminalId(RandomData.random09(10));
      return originator;
   }

   protected static Institution getRandomInstitution(String instId) {
      Institution institution = new Institution();
      institution.setId(instId);
      institution.setName(RandomData.randomAZ(6));
      return institution;
   }

   private static Merchant getRandomMerchant(String merchantId) {
      Merchant merchant = new Merchant();
      merchant.setMerchantId(merchantId);
      merchant.setMerchantType("Retailer");
      merchant.setMerchantName(new MerchantName().name("electrum").city("Cape Town").region("WC").country("RSA"));
      return merchant;
   }

   protected static LedgerAmount getLedgerAmount(Long amount) {
      LedgerAmount ledgerAmount = new LedgerAmount();
      ledgerAmount.setAmount(amount == null ? new Random().nextLong() : amount);
      ledgerAmount.setCurrency("710");
      return ledgerAmount;
   }

   protected static PersonalDetails getPersonalDetails(String idNumber, String cell) {
      PersonalDetails personalDetails = new PersonalDetails();
      personalDetails.setIdNumber(idNumber);
      personalDetails.setContactNumber(cell);
      return personalDetails;
   }

   public static ErrorDetail getErrorDetail(
         String id,
         String originalId,
         ErrorDetail.ErrorTypeEnum errorType,
         Object detailedMessage) {
      ErrorDetail errorDetail = new ErrorDetail();
      errorDetail.setId(id);
      errorDetail.setOriginalId(originalId);
      errorDetail.setErrorType(errorType);

      errorDetail.setErrorMessage(ERROR_MESSAGES_MAP.get(errorType).errorMessage);
      errorDetail.setProviderErrorCode(ERROR_MESSAGES_MAP.get(errorType).providerErrorCode);
      errorDetail.setProviderErrorMessage(ERROR_MESSAGES_MAP.get(errorType).providerErrorMessage);
      errorDetail.setDetailMessage(detailedMessage);
      return errorDetail;
   }
}
