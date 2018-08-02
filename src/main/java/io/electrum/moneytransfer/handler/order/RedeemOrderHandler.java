package io.electrum.moneytransfer.handler.order;

import java.util.UUID;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import io.electrum.moneytransfer.handler.BaseHandler;
import io.electrum.moneytransfer.model.ErrorDetail;
import io.electrum.moneytransfer.model.MoneyTransferRedeemRequest;
import io.electrum.moneytransfer.resource.impl.MoneyTransferTestServer;
import io.electrum.moneytransfer.server.util.MoneyTransferUtils;

public class RedeemOrderHandler extends BaseHandler {

   public RedeemOrderHandler(HttpHeaders httpHeaders, UriInfo uriInfo) {
      super(httpHeaders, uriInfo);
   }

   public Response handle(MoneyTransferRedeemRequest body) {

      if (!checkBasicAuth(body.getReceiver().getId())) {
         return buildErrorDetailResponse(
               body.getId(),
               null,
               ErrorDetail.ErrorTypeEnum.AUTHENTICATION_ERROR,
               "ReceiverId must match basic auth username");
      }

      if (MoneyTransferTestServer.getIdCache().get(body.getId()) != null) {
         return buildErrorDetailResponse(
               body.getId(),
               null,
               ErrorDetail.ErrorTypeEnum.DUPLICATE_RECORD,
               "Id already in use");
      }

      return Response.status(501)
            .entity(
                  MoneyTransferUtils.getErrorDetail(
                        UUID.randomUUID().toString(),
                        null,
                        ErrorDetail.ErrorTypeEnum.SYSTEM_ERROR,
                        "Not implemented yet"))
            .build();
   }
}
