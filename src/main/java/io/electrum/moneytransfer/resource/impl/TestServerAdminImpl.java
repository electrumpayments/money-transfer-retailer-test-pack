package io.electrum.moneytransfer.resource.impl;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import io.electrum.moneytransfer.factory.TestServerAdminHandlerFactory;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.electrum.moneytransfer.api.AdminResource;
import io.electrum.moneytransfer.model.ErrorDetail;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;

@Path("/moneytransfer/v2/testServerAdmin")
@Consumes({ "application/json" })
@Produces({ "application/json" })
@Api(description = "the Money Transfer API")
public class TestServerAdminImpl {

   private static TestServerAdminImpl instance = null;
   private static final Logger LOGGER = LoggerFactory.getLogger(MoneyTransferTestServer.class.getPackage().getName());

   protected TestServerAdminImpl getResourceImplementation() {
      if (instance == null) {
         instance = new TestServerAdminImpl();
      }
      return instance;
   }

   @POST
   @Path("/reset")
   @Produces({ "application/json" })
   @ApiOperation(value = "Reset Money Transfer Test Server.", notes = "The Test Server Admin Reset endpoint allows a user of the Test Server "
         + "to reset the test data in the Test Server's database. This means that "
         + "all data will be reset to initial settings and ALL "
         + "message data will be lost. This operation affects all data used by the "
         + "user identified by the HTTP Basic Auth username and password "
         + "combination. <em>This cannot be reversed.</em>", authorizations = {
               @Authorization(value = "httpBasic") }, tags = { "Test Server Admin", })
   @ApiResponses(value = { @ApiResponse(code = 204, message = "No Content", response = Void.class),
         @ApiResponse(code = 400, message = "Bad Request", response = ErrorDetail.class),
         @ApiResponse(code = 500, message = "Internal Server Error", response = ErrorDetail.class) })
   public void reset(
         @ApiParam(value = "Identifies the service provider to whom this request must be directed.", required = true) @QueryParam("receiverId") @NotNull String receiverId,
         @Context SecurityContext securityContext,
         @Suspended AsyncResponse asyncResponse,
         @Context HttpHeaders httpHeaders,
         @Context UriInfo uriInfo,
         @Context HttpServletRequest httpServletRequest) {
      LOGGER.info(String.format("%s %s", httpServletRequest.getMethod(), uriInfo.getPath()));
      LOGGER.debug(String.format("%s %s\n%s", httpServletRequest.getMethod(), uriInfo.getPath(), receiverId));
      Response rsp = TestServerAdminHandlerFactory.getResetHandler(httpHeaders, uriInfo).handle(receiverId);
      LOGGER.debug(String.format("Response code returned:\n%s", rsp.getStatus()));
      asyncResponse.resume(rsp);
   }
}
