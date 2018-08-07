package io.electrum.moneytransfer.handler.test.server.admin;

import io.electrum.moneytransfer.handler.BaseHandler;
import io.electrum.moneytransfer.model.ErrorDetail;
import io.electrum.moneytransfer.resource.impl.MoneyTransferTestServer;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

public class ResetHandler extends BaseHandler {

   public ResetHandler(HttpHeaders httpHeaders, UriInfo uriInfo) {
      super(httpHeaders, uriInfo);
   }

   public Response handle(String receiverId) {

      if (!checkBasicAuth(receiverId)) {
         return buildErrorDetailResponse(
                 ErrorDetail.ErrorTypeEnum.AUTHENTICATION_ERROR,
                 "ReceiverId must match basic auth username");
      }

      MoneyTransferTestServer.resetMoneyTransferTestServer();

      return Response.noContent().build();
   }
}
