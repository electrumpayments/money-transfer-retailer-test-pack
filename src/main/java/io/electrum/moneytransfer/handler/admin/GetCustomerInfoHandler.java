package io.electrum.moneytransfer.handler.admin;

import java.util.UUID;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import io.electrum.moneytransfer.handler.BaseHandler;
import io.electrum.moneytransfer.model.ErrorDetail;
import io.electrum.moneytransfer.model.IdType;
import io.electrum.moneytransfer.model.MoneyTransferAdminMessage;
import io.electrum.moneytransfer.resource.impl.MoneyTransferTestServer;
import io.electrum.moneytransfer.server.util.MoneyTransferUtils;
import io.electrum.moneytransfer.server.util.RequestKey;

public class GetCustomerInfoHandler extends BaseHandler {

   public GetCustomerInfoHandler(HttpHeaders httpHeaders, UriInfo uriInfo) {
      super(httpHeaders, uriInfo);
   }

   public Response handle(
         String idNumber,
         IdType idType,
         String idCountryCode,
         String merchantId,
         String originatorInstId,
         String receiverId) {

      String validationString = getValidationStringGetCustomerInfoHandler(idNumber, receiverId);
      if (validationString.length() > 0) {
         return Response.status(400)
               .entity(
                     MoneyTransferUtils.getErrorDetail(
                           UUID.randomUUID().toString(),
                           null,
                           ErrorDetail.ErrorTypeEnum.FORMAT_ERROR,
                           validationString))
               .build();
      }

      RequestKey key = new RequestKey(username, password, RequestKey.GET_CUSTOMER_INFO_RESOURCE, idNumber);
      MoneyTransferAdminMessage moneyTransferAdminMessage = MoneyTransferTestServer.getAdminRecords().get(key);

      if (moneyTransferAdminMessage == null) {
         Response.status(400)
               .entity(
                     MoneyTransferUtils.getErrorDetail(
                           UUID.randomUUID().toString(),
                           null,
                           ErrorDetail.ErrorTypeEnum.UNABLE_TO_LOCATE_RECORD,
                           null))
               .build();
      }

      if (moneyTransferAdminMessage.getCustomerDetails().getIdNumber().equals(idNumber)
            && moneyTransferAdminMessage.getReceiver().getId().equals(receiverId)) {
         return Response.status(200).entity(moneyTransferAdminMessage).build();
      }

      return Response.status(400)
            .entity(
                  MoneyTransferUtils.getErrorDetail(
                        UUID.randomUUID().toString(),
                        null,
                        ErrorDetail.ErrorTypeEnum.CUSTOMER_CHECK_FAILED,
                        null))
            .build();
   }

   private String getValidationStringGetCustomerInfoHandler(String idNumber, String receiverId) {
      StringBuilder sb = new StringBuilder();
      if (idNumber == null) {
         sb.append("Query param, idNumber is null.\n");
      }
      if (receiverId == null) {
         sb.append("Query param, receiverId is null.\n");
      }
      return sb.toString();
   }
}
