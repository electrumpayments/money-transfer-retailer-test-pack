package io.electrum.moneytransfer.handler.order;

import java.util.UUID;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import io.electrum.moneytransfer.handler.BaseHandler;
import io.electrum.moneytransfer.model.ErrorDetail;
import io.electrum.moneytransfer.model.MoneyTransferConfirmation;
import io.electrum.moneytransfer.server.util.MoneyTransferUtils;

public class ConfirmPaymentHandler extends BaseHandler {

   public ConfirmPaymentHandler(HttpHeaders httpHeaders, UriInfo uriInfo) {
      super(httpHeaders, uriInfo);
   }

   public Response handle(MoneyTransferConfirmation body) {
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
