package io.electrum.moneytransfer.handler.admin;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import io.electrum.moneytransfer.handler.BaseHandler;
import io.electrum.moneytransfer.model.ErrorDetail;
import io.electrum.moneytransfer.model.IdType;
import io.electrum.moneytransfer.model.MoneyTransferAdminMessage;
import io.electrum.moneytransfer.resource.impl.MoneyTransferTestServer;
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

      if (!checkBasicAuth(receiverId)) {
         return buildErrorDetailResponse(
               ErrorDetail.ErrorTypeEnum.AUTHENTICATION_ERROR,
               "ReceiverId must match basic auth username");
      }

      RequestKey key = new RequestKey(username, password, RequestKey.CUSTOMER_RESOURCE, idNumber);
      MoneyTransferAdminMessage moneyTransferAdminMessage = MoneyTransferTestServer.getAdminRecords().get(key);

      if (moneyTransferAdminMessage == null) {
         return buildErrorDetailResponse(ErrorDetail.ErrorTypeEnum.UNABLE_TO_LOCATE_RECORD, null);
      }

      if (idNumber.equals(moneyTransferAdminMessage.getCustomerDetails().getIdNumber())
            && receiverId.equals(moneyTransferAdminMessage.getReceiver().getId())) {

         if (idType != null && !idType.equals(moneyTransferAdminMessage.getCustomerDetails().getIdType())) {
            return buildErrorDetailResponse(ErrorDetail.ErrorTypeEnum.UNABLE_TO_LOCATE_RECORD, null);
         }
         if (idCountryCode != null
               && !idCountryCode.equals(moneyTransferAdminMessage.getCustomerDetails().getIdCountryCode())) {
            return buildErrorDetailResponse(ErrorDetail.ErrorTypeEnum.UNABLE_TO_LOCATE_RECORD, null);
         }
         if (merchantId != null
               && !merchantId.equals(moneyTransferAdminMessage.getOriginator().getMerchant().getMerchantId())) {
            return buildErrorDetailResponse(ErrorDetail.ErrorTypeEnum.UNABLE_TO_LOCATE_RECORD, null);
         }
         if (originatorInstId != null
               && !originatorInstId.equals(moneyTransferAdminMessage.getOriginator().getInstitution().getId())) {
            return buildErrorDetailResponse(ErrorDetail.ErrorTypeEnum.UNABLE_TO_LOCATE_RECORD, null);
         }
         return Response.ok(moneyTransferAdminMessage).build();
      }

      return buildErrorDetailResponse(ErrorDetail.ErrorTypeEnum.UNABLE_TO_LOCATE_RECORD, null);
   }
}
