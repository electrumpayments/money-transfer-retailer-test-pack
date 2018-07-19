package io.electrum.moneytransfer.handler.admin;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import io.electrum.moneytransfer.handler.BaseHandler;
import io.electrum.moneytransfer.model.ErrorDetail;
import io.electrum.moneytransfer.model.MoneyTransferAdminMessage;
import io.electrum.moneytransfer.resource.impl.MoneyTransferTestServer;
import io.electrum.moneytransfer.server.util.AdminUtils;
import io.electrum.moneytransfer.server.util.RequestKey;

public class CreateOrUpdateCustomerHandler extends BaseHandler {

   public CreateOrUpdateCustomerHandler(HttpHeaders headers, UriInfo uriInfo) {
      super(headers, uriInfo);
   }

   public Response handle(MoneyTransferAdminMessage body) {

      String validationString = AdminUtils.getValidationStringCreateOrUpdateCustomer(body);
      if (validationString.length() > 0) {
         return buildErrorDetailResponse(ErrorDetail.ErrorTypeEnum.FORMAT_ERROR, validationString);
      }

      if (!checkBasicAuth(body.getReceiver().getId())) {
         return buildErrorDetailResponse(
               ErrorDetail.ErrorTypeEnum.AUTHENTICATION_ERROR,
               "ReceiverId must match basic auth username");
      }

      // Add some error testing if a ID ends in 5 with will give an error.
      if (body.getCustomerDetails().getIdNumber().endsWith("5")) {
         return buildErrorDetailResponse(ErrorDetail.ErrorTypeEnum.CUSTOMER_CHECK_FAILED, "Invalid Id number (Sample error if id ends in 5)");
      }

      RequestKey key =
            new RequestKey(
                  username,
                  password,
                  RequestKey.CUSTOMER_RESOURCE,
                  body.getCustomerDetails().getIdNumber());
      MoneyTransferAdminMessage cachedBody = MoneyTransferTestServer.getAdminRecords().get(key);

      if (cachedBody == null) {
         MoneyTransferTestServer.getAdminRecords().put(key, body);
         return Response.created(uriInfo.getRequestUri()).entity(body).build();
      }

      MoneyTransferTestServer.getAdminRecords().put(key, body);
      return Response.ok(body).build();
   }
}
