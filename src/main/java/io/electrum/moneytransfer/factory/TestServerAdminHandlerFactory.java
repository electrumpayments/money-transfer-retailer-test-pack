package io.electrum.moneytransfer.factory;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriInfo;

import io.electrum.moneytransfer.handler.test.server.admin.ResetHandler;

public class TestServerAdminHandlerFactory {

   public static ResetHandler getResetHandler(HttpHeaders headers, UriInfo uriInfo) {
      return new ResetHandler(headers, uriInfo);
   }
}
