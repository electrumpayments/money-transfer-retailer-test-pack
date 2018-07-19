package io.electrum.moneytransfer.handler.admin;

import java.util.UUID;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import io.electrum.moneytransfer.handler.BaseHandler;
import io.electrum.moneytransfer.model.ErrorDetail;
import io.electrum.moneytransfer.model.MoneyTransferAdminMessage;
import io.electrum.moneytransfer.server.util.AdminUtils;
import io.electrum.moneytransfer.server.util.MoneyTransferUtils;

public class CreateOrUpdateCustomerHandler extends BaseHandler {

   public CreateOrUpdateCustomerHandler(HttpHeaders headers, UriInfo uriInfo) {
      super(headers, uriInfo);
   }

   public Response handle(MoneyTransferAdminMessage body) {

      String validationString = AdminUtils.getValidationStringCreateOrUpdateCustomer(body);
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


      return null;
   }
}
