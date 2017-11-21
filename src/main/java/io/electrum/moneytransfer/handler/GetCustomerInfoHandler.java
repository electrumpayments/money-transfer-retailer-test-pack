package io.electrum.moneytransfer.handler;

import io.electrum.moneytransfer.resource.impl.MoneyTransferTestServer;
import io.electrum.moneytransfer.server.util.MoneyTransferUtils;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetCustomerInfoHandler {
   private static final Logger log = LoggerFactory.getLogger(MoneyTransferTestServer.class.getPackage().getName());

   public Response handle(
         String idNumber,
         String merchantId,
         String originatorInstId,
         String receiverId,
         HttpHeaders httpHeaders,
         UriInfo uriInfo) {
      try {
         String authString = MoneyTransferUtils.getAuthString(httpHeaders.getHeaderString(HttpHeaders.AUTHORIZATION));
         String username = MoneyTransferUtils.getUsernameFromAuth(authString);
         String password = MoneyTransferUtils.getPasswordFromAuth(authString);

         return null;
      } catch (Exception e) {
         log.debug("error processing GetCustomerInfo", e);
         return Response.serverError().entity(e.getMessage()).build();
      }
   }
}
