package io.electrum.moneytransfer.handler;

import java.util.UUID;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.electrum.moneytransfer.model.ErrorDetail;
import io.electrum.moneytransfer.resource.impl.MoneyTransferTestServer;
import io.electrum.moneytransfer.server.util.MoneyTransferUtils;

public abstract class BaseHandler {

   protected static final Logger log = LoggerFactory.getLogger(MoneyTransferTestServer.class.getPackage().getName());

   protected final HttpHeaders httpHeaders;
   protected final UriInfo uriInfo;
   protected final String username;
   protected final String password;

   public BaseHandler(HttpHeaders httpHeaders, UriInfo uriInfo) {
      this.httpHeaders = httpHeaders;
      this.uriInfo = uriInfo;
      String authString = MoneyTransferUtils.getAuthString(httpHeaders.getHeaderString(HttpHeaders.AUTHORIZATION));
      username = MoneyTransferUtils.getUsernameFromAuth(authString);
      password = MoneyTransferUtils.getPasswordFromAuth(authString);
   }

   protected Response buildErrorDetailResponse(
         String id,
         String originalId,
         ErrorDetail.ErrorTypeEnum errorType,
         Object detailedMessage) {
      return Response.status(400)
            .entity(MoneyTransferUtils.getErrorDetail(id, originalId, errorType, detailedMessage))
            .build();
   }

   protected Response buildErrorDetailResponse(ErrorDetail.ErrorTypeEnum errorType, Object detailedMessage) {
      return buildErrorDetailResponse(UUID.randomUUID().toString(), null, errorType, detailedMessage);
   }

}
