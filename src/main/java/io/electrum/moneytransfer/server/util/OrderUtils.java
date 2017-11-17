package io.electrum.moneytransfer.server.util;

import io.electrum.moneytransfer.model.ErrorDetail;
import io.electrum.moneytransfer.model.ErrorDetail.ErrorTypeEnum;
import io.electrum.moneytransfer.model.MoneyTransferAuthRequest;
import io.electrum.moneytransfer.model.MoneyTransferAuthResponse;
import io.electrum.moneytransfer.model.MoneyTransferReversal;
import io.electrum.moneytransfer.server.MoneyTransferTestServerRunner;
import io.electrum.moneytransfer.server.model.DetailMessage;
import io.electrum.moneytransfer.server.model.FormatError;
import io.electrum.vas.model.BasicReversal;
import io.electrum.vas.model.Merchant;
import io.electrum.vas.model.Originator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.ws.rs.core.Response;

public class OrderUtils {
   public static Response canCreateOrder(String id, String username, String password) {
      ErrorDetail errorDetail = new ErrorDetail().id(id);
      ConcurrentHashMap<RequestKey, MoneyTransferAuthRequest> authRequestRecords =
            MoneyTransferTestServerRunner.getTestServer().getAuthRequestRecords();
      RequestKey requestKey = new RequestKey(username, password, RequestKey.CREATE_ORDER_RESOURCE, id);
      MoneyTransferAuthRequest originalRequest = authRequestRecords.get(requestKey);
      if (originalRequest != null) {
         errorDetail.errorType(ErrorTypeEnum.DUPLICATE_RECORD).errorMessage("Duplicate UUID.");
         ConcurrentHashMap<RequestKey, MoneyTransferAuthResponse> authResponseRecords =
               MoneyTransferTestServerRunner.getTestServer().getAuthResponseRecords();
         MoneyTransferAuthResponse rsp = authResponseRecords.get(requestKey);
         errorDetail.setDetailMessage("Create Order request already processed.\n Response:\n" + String.valueOf(rsp));
         return Response.status(400).entity(errorDetail).build();
      }

      ConcurrentHashMap<RequestKey, MoneyTransferReversal> reversalRecords =
            MoneyTransferTestServerRunner.getTestServer().getReversalRecords();
      RequestKey reversalKey = new RequestKey(username, password, RequestKey.REVERSE_PAYMENT_RESOURCE, id);
      BasicReversal reversal = reversalRecords.get(reversalKey);
      if (reversal != null) {
         errorDetail.errorType(ErrorTypeEnum.ALREADY_REDEEMED).errorMessage("Payment reversed.");
         ConcurrentHashMap<RequestKey, MoneyTransferAuthResponse> responseRecords =
               MoneyTransferTestServerRunner.getTestServer().getAuthResponseRecords();
         MoneyTransferAuthResponse rsp = responseRecords.get(requestKey);
         errorDetail.setDetailMessage("Payment Reversal already processed.\n Response:\n" + String.valueOf(rsp));
         return Response.status(400).entity(errorDetail).build();
      }
      return null;
   }

   public static Response validateCreateOrderRequest(MoneyTransferAuthRequest authRequest) {
      Set<ConstraintViolation<?>> violations = new HashSet<ConstraintViolation<?>>();
      validateCreateOrderRequest(authRequest, violations);
      ErrorDetail errorDetail = buildFormatErrorRsp(violations);
      if (errorDetail == null) {
         return null;
      }
      errorDetail.id(authRequest.getId());
      return Response.status(400).entity(errorDetail).build();
   }

   private static <T> Set<ConstraintViolation<T>> validate(T tInstance) {
      if (tInstance == null) {
         return new HashSet<ConstraintViolation<T>>();
      }
      Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
      Set<ConstraintViolation<T>> violations = validator.validate(tInstance);
      return violations;
   }

   public static void validateCreateOrderRequest(
         MoneyTransferAuthRequest authRequest,
         Set<ConstraintViolation<?>> violations) {
      violations.addAll(validate(authRequest));
      if (authRequest != null) {
         violations.addAll(validate(authRequest.getRecipientDetails()));
         violations.addAll(validate(authRequest.getSenderDetails()));
         violations.addAll(validate(authRequest.getAmount()));
         violations.addAll(validate(authRequest.getClient()));
         violations.addAll(validate(authRequest.getId()));
         Originator originator = authRequest.getOriginator();
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
         violations.addAll(validate(authRequest.getReceiver()));
         violations.addAll(validate(authRequest.getSettlementEntity()));
         violations.addAll(validate(authRequest.getThirdPartyIdentifiers()));
         violations.addAll(validate(authRequest.getTime()));
      }
   }

   private static ErrorDetail buildFormatErrorRsp(Set<ConstraintViolation<?>> violations) {
      if (violations.size() == 0) {
         return null;
      }
      List<FormatError> formatErrors = new ArrayList<FormatError>(violations.size());
      int i = 0;
      for (ConstraintViolation<?> violation : violations) {
         System.out.println(i);
         formatErrors.add(
               new FormatError().msg(violation.getMessage()).field(violation.getPropertyPath().toString()).value(
                     violation.getInvalidValue() == null ? "null" : violation.getInvalidValue().toString()));
         i++;
      }
      ErrorDetail errorDetail =
            new ErrorDetail().errorType(ErrorTypeEnum.FORMAT_ERROR).errorMessage("Bad formatting").detailMessage(
                  new DetailMessage().formatErrors(formatErrors));
      return errorDetail;
   }
}
