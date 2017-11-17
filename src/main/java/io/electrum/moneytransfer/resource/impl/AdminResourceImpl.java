package io.electrum.moneytransfer.resource.impl;

import io.electrum.moneytransfer.api.AdminResource;
import io.electrum.moneytransfer.api.IAdminResource;
import io.electrum.moneytransfer.factory.AdminMessageHandlerFactory;
import io.electrum.moneytransfer.model.MoneyTransferAdminMessage;
import io.swagger.annotations.Api;
import io.swagger.annotations.Authorization;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/moneytransfer/v0/admin")
@Api(description = "the Money Transfer Retailer Admin API", authorizations = { @Authorization("httpBasic") })
public class AdminResourceImpl extends AdminResource implements IAdminResource {
   static AdminResourceImpl instance = null;
   private static final Logger log = LoggerFactory.getLogger(MoneyTransferTestServer.class.getPackage().getName());

   @Override
   protected IAdminResource getResourceImplementation() {
      if (instance == null) {
         return new AdminResourceImpl();
      }
      return instance;
   }

   @Override
   public void getCustomerInfoImpl(
         String idNumber,
         String merchantId,
         String originatorInstId,
         String receiverId,
         SecurityContext securityContext,
         Request request,
         HttpHeaders httpHeaders,
         AsyncResponse asyncResponse,
         UriInfo uriInfo,
         HttpServletRequest httpServletRequest)
         throws NotFoundException {
      log.info(String.format("%s %s", httpServletRequest.getMethod(), uriInfo.getPath()));
      log.debug(
            String.format(
                  "%s %s\nID Number: %s\nMerchant ID: %s\nOriginator Institute ID: %s\nReceiver ID: %",
                  httpServletRequest.getMethod(),
                  uriInfo.getPath(),
                  idNumber,
                  merchantId,
                  originatorInstId,
                  receiverId));
      Response rsp =
            AdminMessageHandlerFactory.getGetCustomerInfoHandler()
                  .handle(idNumber, merchantId, originatorInstId, receiverId, httpHeaders);
      log.debug(String.format("Entity returned:\n%s", rsp.getEntity()));

      asyncResponse.resume(rsp);
   }

   @Override
   public void createOrUpdateCustomerImpl(
         MoneyTransferAdminMessage body,
         SecurityContext securityContext,
         Request request,
         HttpHeaders httpHeaders,
         AsyncResponse asyncResponse,
         UriInfo uriInfo,
         HttpServletRequest httpServletRequest) {
      log.info(String.format("%s %s", httpServletRequest.getMethod(), uriInfo.getPath()));
      log.debug(String.format("%s %s\n%s", httpServletRequest.getMethod(), uriInfo.getPath(), body));
      Response rsp = AdminMessageHandlerFactory.getCreateOrUpdateCustomerHandler().handle(body, httpHeaders);
      log.debug(String.format("Entity returned:\n%s", rsp.getEntity()));

      asyncResponse.resume(rsp);
   }
}