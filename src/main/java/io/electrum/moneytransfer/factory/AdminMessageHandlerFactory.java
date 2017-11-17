package io.electrum.moneytransfer.factory;

import io.electrum.moneytransfer.handler.CreateOrUpdateCustomerHandler;
import io.electrum.moneytransfer.handler.GetCustomerInfoHandler;

public class AdminMessageHandlerFactory {
   public static GetCustomerInfoHandler getGetCustomerInfoHandler() {
      return new GetCustomerInfoHandler();
   }

   public static CreateOrUpdateCustomerHandler getCreateOrUpdateCustomerHandler() {
      return new CreateOrUpdateCustomerHandler();
   }
}
