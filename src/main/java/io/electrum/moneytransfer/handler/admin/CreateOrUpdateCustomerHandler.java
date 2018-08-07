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

      if (!checkBasicAuth(body.getReceiver().getId())) {
         return buildErrorDetailResponse(
               ErrorDetail.ErrorTypeEnum.AUTHENTICATION_ERROR,
               "ReceiverId must match basic auth username");
      }

      // Add some error testing if a ID ends in 5 with will give an error.
      if (body.getCustomerDetails().getIdNumber().endsWith("5")) {
         return buildErrorDetailResponse(
               ErrorDetail.ErrorTypeEnum.CUSTOMER_CHECK_FAILED,
               "Invalid Id number (Sample error if id ends in 5)");
      }

      if (body.getCustomerProfileId() == null) {
         body.setCustomerProfileId(RandomData.random09AZ(10));
      }

      AdminRecord adminRecord = moneyTransferDb.getAdminTable().getRecord(body.getCustomerDetails().getIdNumber());

      if (adminRecord == null) {
         moneyTransferDb.getAdminTable().putRecord(new AdminRecord(body.getCustomerDetails().getIdNumber(), body));
         return Response.created(uriInfo.getRequestUri()).entity(body).build();
      }

      body.setCustomerProfileId(adminRecord.getAdminMessage().getCustomerProfileId());
      moneyTransferDb.getAdminTable().putRecord(new AdminRecord(body.getCustomerDetails().getIdNumber(), body));
      return Response.ok(body).build();
   }
}
