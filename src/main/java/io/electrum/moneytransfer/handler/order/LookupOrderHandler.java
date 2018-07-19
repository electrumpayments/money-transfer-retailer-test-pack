package io.electrum.moneytransfer.handler.order;

import io.electrum.moneytransfer.handler.BaseHandler;
import io.electrum.moneytransfer.model.ErrorDetail;
import io.electrum.moneytransfer.resource.impl.MoneyTransferTestServer;
import io.electrum.moneytransfer.server.util.MoneyTransferUtils;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class LookupOrderHandler extends BaseHandler {

   public LookupOrderHandler(HttpHeaders httpHeaders, UriInfo uriInfo) {
      super(httpHeaders, uriInfo);
   }

   public Response handle(
         String orderRedeemRef,
         String merchantId,
         String originatorInstId,
         String receiverId) {

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
