package io.electrum.moneytransfer.handler.admin;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import io.electrum.moneytransfer.handler.BaseHandler;
import io.electrum.moneytransfer.model.ErrorDetail;
import io.electrum.moneytransfer.model.MoneyTransferAdminMessage;
import io.electrum.moneytransfer.server.backend.records.AdminRecord;
import io.electrum.moneytransfer.server.util.RandomData;

public class CreateOrUpdateCustomerHandler extends BaseHandler {

   public CreateOrUpdateCustomerHandler(HttpHeaders headers, UriInfo uriInfo) {
      super(headers, uriInfo);
   }

   public Response handle(MoneyTransferAdminMessage body) {
      if (!wasDatabasePresentBeforeRequest() && body.getCustomerProfileId() != null) {
         buildErrorDetailResponse(
                 ErrorDetail.ErrorTypeEnum.AUTHENTICATION_ERROR,
                 "The no customerProfileId exists with current basic auth credentials");
      }

      AdminRecord adminRecord = moneyTransferDb.getAdminTable().getRecord(body.getCustomerDetails().getIdNumber());
      if (adminRecord == null) {
         body.setCustomerProfileId(RandomData.random09AZ(10));
         moneyTransferDb.getAdminTable().putRecord(new AdminRecord(body.getCustomerDetails().getIdNumber(), body));
         return Response.created(uriInfo.getRequestUri()).entity(body).build();
      }

      if (body.getCustomerProfileId() == null
            || !body.getCustomerProfileId().equals(adminRecord.getAdminMessage().getCustomerProfileId())) {
         return buildErrorDetailResponse(
               ErrorDetail.ErrorTypeEnum.CUSTOMER_CHECK_FAILED,
               "When updating a customer the customerProfileId must be the same as the original customerProfileId");
      }
      moneyTransferDb.getAdminTable().putRecord(new AdminRecord(body.getCustomerDetails().getIdNumber(), body));
      return Response.ok(body).build();
   }
}
