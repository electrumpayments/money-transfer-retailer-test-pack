package io.electrum.moneytransfer.handler.admin;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import io.electrum.moneytransfer.handler.BaseHandler;
import io.electrum.moneytransfer.model.ErrorDetail;
import io.electrum.moneytransfer.model.MoneyTransferAdminMessage;
import io.electrum.moneytransfer.resource.impl.MoneyTransferTestServer;
import io.electrum.moneytransfer.server.util.RandomData;
import io.electrum.moneytransfer.server.util.RequestKey;

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

      MoneyTransferTestServer.getCustomerIdToIdRecords().put(body.getCustomerProfileId(), body.getCustomerDetails().getIdNumber());

      RequestKey key =
            new RequestKey(username, password, RequestKey.CUSTOMER_RESOURCE, body.getCustomerDetails().getIdNumber());
      MoneyTransferAdminMessage cachedBody = MoneyTransferTestServer.getAdminRecords().get(key);

      if (cachedBody == null) {
         MoneyTransferTestServer.getAdminRecords().put(key, body);
         return Response.created(uriInfo.getRequestUri()).entity(body).build();
      }

      body.setCustomerProfileId(cachedBody.getCustomerProfileId());
      MoneyTransferTestServer.getAdminRecords().put(key, body);
      return Response.ok(body).build();
   }
}
