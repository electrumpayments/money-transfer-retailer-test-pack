package io.electrum.moneytransfer.server.util;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.ws.rs.core.Response;

import io.electrum.moneytransfer.model.ErrorDetail;
import io.electrum.moneytransfer.model.ErrorDetail.ErrorTypeEnum;
import io.electrum.moneytransfer.model.MoneyTransferAuthRequest;
import io.electrum.moneytransfer.model.MoneyTransferAuthResponse;
import io.electrum.moneytransfer.model.MoneyTransferLookupResponse;
import io.electrum.moneytransfer.resource.impl.MoneyTransferTestServer;
import io.electrum.vas.model.BasicReversal;
import io.electrum.vas.model.Institution;
import io.electrum.vas.model.SlipData;
import io.electrum.vas.model.ThirdPartyIdentifier;

public class OrderUtils {
   private static SecureRandom secRandom = new SecureRandom();
   private static ConcurrentHashMap<String, String> orderRedeemRef = new ConcurrentHashMap<>();



   public static MoneyTransferAuthResponse authRspFromReq(MoneyTransferAuthRequest req) {
      MoneyTransferAuthResponse rsp = new MoneyTransferAuthResponse();
      rsp.setAmount(req.getAmount());
      rsp.setOrderId(req.getId());
      String redeemRef = RandomData.random09(15);
      orderRedeemRef.put(redeemRef, req.getId());
      rsp.setOrderRedeemRef(redeemRef);
      rsp.setSenderDetails(req.getSenderDetails());
      rsp.setOriginator(req.getOriginator());
      rsp.setTime(req.getTime());
      Institution receiver = req.getReceiver();
      if (receiver == null) {
         receiver = new Institution();
         receiver.setId("44444444");
         receiver.setName("TransactionsReceivers");
      }
      List<ThirdPartyIdentifier> thirdPartyIds = req.getThirdPartyIdentifiers();
      if (thirdPartyIds == null) {
         thirdPartyIds = new ArrayList<ThirdPartyIdentifier>();
      }
      rsp.setReceiver(req.getReceiver());
      rsp.setId(req.getId());

      SlipData slipData = new SlipData();
      rsp.setSlipData(slipData);
      Institution settlementEntity = req.getSettlementEntity();
      if (settlementEntity == null) {
         settlementEntity = new Institution();
         settlementEntity.setId("33333333");
         settlementEntity.setName("TransactionsRUs");
      }
      thirdPartyIds.add(
            new ThirdPartyIdentifier().institutionId(settlementEntity.getId())
                  .transactionIdentifier(RandomData.random09AZ((int) ((secRandom.nextDouble() * 20) + 1))));
      thirdPartyIds.add(
            new ThirdPartyIdentifier().institutionId(receiver.getId())
                  .transactionIdentifier(RandomData.random09AZ((int) ((secRandom.nextDouble() * 20) + 1))));
      rsp.setSettlementEntity(settlementEntity);
      rsp.setThirdPartyIdentifiers(thirdPartyIds);
      return rsp;
   }

   public static boolean doesOrderExist(String id, String username, String password) {
      RequestKey requestKey = new RequestKey(username, password, RequestKey.CREATE_ORDER_RESOURCE, id);
      return MoneyTransferTestServer.getAuthRequestRecords().get(requestKey) != null;
   }

   public static Status getOrderStatus(String id, String requestId, String username, String password) {
      if (requestId == null) {
         RequestKey requestKey = new RequestKey(username, password, RequestKey.CREATE_ORDER_RESOURCE, id);
         if (MoneyTransferTestServer.getAuthRequestRecords().get(requestKey) != null) {
            return Status.ORDER_CREATED;
         }
      } else {
         RequestKey requestKey = new RequestKey(username, password, RequestKey.CREATE_ORDER_RESOURCE, requestId);
         if (MoneyTransferTestServer.getAuthRequestRecords().get(requestKey) != null) {
            requestKey = new RequestKey(username, password, RequestKey.CONFIRM_PAYMENT_RESOURCE, id);
            if (MoneyTransferTestServer.getConfirmationRecords().get(requestKey) != null) {
               return Status.ORDER_CONFIRMED;
            }

            requestKey = new RequestKey(username, password, RequestKey.REVERSE_PAYMENT_RESOURCE, id);
            if (MoneyTransferTestServer.getReversalRecords().get(requestKey) != null) {
               return Status.ORDER_REVERSED;
            }
         }
      }
      return Status.UNABLE_TO_LOCATE_RECORD;
   }

   //TODO
   public static Status getRedemptionStatus(String id, String requestId, String username, String password) {
      if (requestId == null) {
         RequestKey requestKey = new RequestKey(username, password, RequestKey.REDEEM_ORDER_RESOURCE, id);
         if (MoneyTransferTestServer.getRedeemRequestRecords().get(requestKey) != null) {
            return Status.ORDER_CREATED;
         }
      } else {
         RequestKey requestKey = new RequestKey(username, password, RequestKey.CREATE_ORDER_RESOURCE, requestId);
         if (MoneyTransferTestServer.getAuthRequestRecords().get(requestKey) != null) {
            requestKey = new RequestKey(username, password, RequestKey.CONFIRM_PAYMENT_RESOURCE, id);
            if (MoneyTransferTestServer.getConfirmationRecords().get(requestKey) != null) {
               return Status.ORDER_CONFIRMED;
            }

            requestKey = new RequestKey(username, password, RequestKey.REVERSE_PAYMENT_RESOURCE, id);
            if (MoneyTransferTestServer.getReversalRecords().get(requestKey) != null) {
               return Status.ORDER_REVERSED;
            }
         }
      }
      return Status.UNABLE_TO_LOCATE_RECORD;
   }

   public static Response canCreateOrder(String id, String username, String password) {
      ErrorDetail errorDetail = new ErrorDetail().id(id);
      RequestKey requestKey = new RequestKey(username, password, RequestKey.CREATE_ORDER_RESOURCE, id);
      MoneyTransferAuthRequest originalRequest = MoneyTransferTestServer.getAuthRequestRecords().get(requestKey);
      if (originalRequest == null) {
         RequestKey reversalKey = new RequestKey(username, password, RequestKey.REVERSE_PAYMENT_RESOURCE, id);
         BasicReversal reversal = MoneyTransferTestServer.getReversalRecords().get(reversalKey);
         if (reversal == null) {
            return null;
         } else {
            errorDetail.errorType(ErrorTypeEnum.ALREADY_REDEEMED).errorMessage("Payment reversed.").setDetailMessage(
                  "Payment Reversal already processed.\n Response:\n"
                        + String.valueOf(MoneyTransferTestServer.getReversalResponseRecords().get(reversalKey)));
         }
      } else {
         errorDetail.errorType(ErrorTypeEnum.DUPLICATE_RECORD).errorMessage("Duplicate UUID.").setDetailMessage(
               "Create Order request already processed.\n Response:\n"
                     + String.valueOf(MoneyTransferTestServer.getAuthResponseRecords().get(requestKey)));
      }

      return Response.status(400).entity(errorDetail).build();
   }

}
