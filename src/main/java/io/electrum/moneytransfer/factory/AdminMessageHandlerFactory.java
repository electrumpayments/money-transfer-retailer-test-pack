package io.electrum.moneytransfer.factory;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriInfo;

import io.electrum.moneytransfer.handler.admin.CreateOrUpdateCustomerHandler;
import io.electrum.moneytransfer.handler.admin.GetCustomerInfoHandler;
import io.electrum.moneytransfer.handler.admin.GetFeeQuoteHandler;

public class AdminMessageHandlerFactory {
   public static GetCustomerInfoHandler getGetCustomerInfoHandler(HttpHeaders headers, UriInfo uriInfo) {
      return new GetCustomerInfoHandler(headers, uriInfo);
   }

   public static CreateOrUpdateCustomerHandler getCreateOrUpdateCustomerHandler(HttpHeaders headers, UriInfo uriInfo) {
      return new CreateOrUpdateCustomerHandler(headers, uriInfo);
   }

   public static GetFeeQuoteHandler getGetFeeQuoteHandler(HttpHeaders headers, UriInfo uriInfo) {
      return new GetFeeQuoteHandler(headers, uriInfo);
   }
}
