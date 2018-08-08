package io.electrum.moneytransfer.handler.test.server.admin;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import io.electrum.moneytransfer.handler.BaseHandler;
import io.electrum.moneytransfer.model.ErrorDetail;

public class ResetHandler extends BaseHandler {

   public ResetHandler(HttpHeaders httpHeaders, UriInfo uriInfo) {
      super(httpHeaders, uriInfo);
   }

   public Response handle(String clientId) {

      if (!checkBasicAuth(clientId)) {
         return buildErrorDetailResponse(
               ErrorDetail.ErrorTypeEnum.AUTHENTICATION_ERROR,
               "ClientId must match basic auth username");
      }

      moneyTransferDb.reset();

      return Response.noContent().build();
   }
}
