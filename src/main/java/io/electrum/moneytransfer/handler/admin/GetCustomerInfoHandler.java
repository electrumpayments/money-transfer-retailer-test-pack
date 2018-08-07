package io.electrum.moneytransfer.handler.admin;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import io.electrum.moneytransfer.handler.BaseHandler;
import io.electrum.moneytransfer.model.ErrorDetail;
import io.electrum.moneytransfer.model.IdType;
import io.electrum.moneytransfer.server.backend.records.AdminRecord;

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

      AdminRecord adminRecord = moneyTransferDb.getAdminTable().getRecord(idNumber);

      if (adminRecord == null) {
         return buildErrorDetailResponse(ErrorDetail.ErrorTypeEnum.UNABLE_TO_LOCATE_RECORD, null);
      }

      if (idNumber.equals(adminRecord.getAdminMessage().getCustomerDetails().getIdNumber())
            && receiverId.equals(adminRecord.getAdminMessage().getReceiver().getId())) {

         if (idType != null && !idType.equals(adminRecord.getAdminMessage().getCustomerDetails().getIdType())) {
            return buildErrorDetailResponse(ErrorDetail.ErrorTypeEnum.UNABLE_TO_LOCATE_RECORD, null);
         }
         if (idCountryCode != null
               && !idCountryCode.equals(adminRecord.getAdminMessage().getCustomerDetails().getIdCountryCode())) {
            return buildErrorDetailResponse(ErrorDetail.ErrorTypeEnum.UNABLE_TO_LOCATE_RECORD, null);
         }
         if (merchantId != null
               && !merchantId.equals(adminRecord.getAdminMessage().getOriginator().getMerchant().getMerchantId())) {
            return buildErrorDetailResponse(ErrorDetail.ErrorTypeEnum.UNABLE_TO_LOCATE_RECORD, null);
         }
         if (originatorInstId != null
               && !originatorInstId.equals(adminRecord.getAdminMessage().getOriginator().getInstitution().getId())) {
            return buildErrorDetailResponse(ErrorDetail.ErrorTypeEnum.UNABLE_TO_LOCATE_RECORD, null);
         }
         return Response.ok(adminRecord.getAdminMessage()).build();
      }

      return buildErrorDetailResponse(ErrorDetail.ErrorTypeEnum.UNABLE_TO_LOCATE_RECORD, null);
   }
}
