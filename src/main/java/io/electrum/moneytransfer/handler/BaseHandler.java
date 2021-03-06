package io.electrum.moneytransfer.handler;

import java.util.UUID;

import javax.lang.model.type.ErrorType;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import io.electrum.moneytransfer.server.backend.MoneyTransferBackend;
import io.electrum.moneytransfer.server.backend.db.MockMoneyTransferDb;
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
   private final boolean databasePresentBeforeRequest;
   protected final MockMoneyTransferDb moneyTransferDb;

   public BaseHandler(HttpHeaders httpHeaders, UriInfo uriInfo) {
      this.httpHeaders = httpHeaders;
      this.uriInfo = uriInfo;
      String authString = MoneyTransferUtils.getAuthString(httpHeaders.getHeaderString(HttpHeaders.AUTHORIZATION));
      username = MoneyTransferUtils.getUsernameFromAuth(authString);
      password = MoneyTransferUtils.getPasswordFromAuth(authString);
      databasePresentBeforeRequest = MoneyTransferTestServer.getBackend().doesDbForUserExist(username, password);
      moneyTransferDb = MoneyTransferTestServer.getBackend().getDbForUser(username, password);
   }

   protected Response buildErrorDetailResponse(
         String id,
         String originalId,
         ErrorDetail.ErrorTypeEnum errorType,
         Object detailedMessage) {
      return Response.status(Response.Status.BAD_REQUEST)
            .entity(MoneyTransferUtils.getErrorDetail(id, originalId, errorType, detailedMessage))
            .build();
   }

   protected Response buildErrorDetailResponse(ErrorDetail.ErrorTypeEnum errorType, Object detailedMessage) {
      return buildErrorDetailResponse(UUID.randomUUID().toString(), null, errorType, detailedMessage);
   }

   protected boolean checkBasicAuth(String clientId) {
      return clientId != null && username != null && username.equals(clientId);
   }

   protected boolean wasDatabasePresentBeforeRequest() {
      return databasePresentBeforeRequest;
   }

}
