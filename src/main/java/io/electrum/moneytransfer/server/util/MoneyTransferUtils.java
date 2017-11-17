package io.electrum.moneytransfer.server.util;

import io.electrum.moneytransfer.model.ErrorDetail;
import io.electrum.moneytransfer.server.model.DetailMessage;

import javax.ws.rs.core.Response;

import org.glassfish.jersey.internal.util.Base64;

public class MoneyTransferUtils {
   public static Response isUuidConsistent(String uuid, String id) {
      return isUuidConsistent(uuid, id, null);
   }

   public static Response isUuidConsistent(String uuid, String id, String originalId) {
      Response rsp = null;
      String pathId = uuid.toString();
      String objectId = id;
      ErrorDetail errorDetail = null;
      if (!pathId.equals(objectId)) {
         errorDetail =
               new ErrorDetail().errorType(ErrorDetail.ErrorTypeEnum.FORMAT_ERROR)
                     .errorMessage("String inconsistent")
                     .id(objectId);
         DetailMessage detailMessage = new DetailMessage();
         detailMessage.setPathId(pathId);
         detailMessage.setFreeString("The ID path parameter is not the same as the object's ID.");
         errorDetail.setDetailMessage(detailMessage);
      }
      if (errorDetail == null) {
         return null;
      } else {
         errorDetail.id(id).originalId(originalId);
         DetailMessage detailMessage = (DetailMessage) errorDetail.getDetailMessage();
         detailMessage.setReversalId(objectId);
         rsp = Response.status(400).entity(errorDetail).build();
      }
      return rsp;
   }

   public static String getAuthString(String authHeader) {
      if (authHeader == null || authHeader.isEmpty() || !authHeader.startsWith("Basic ")) {
         return null;
      }
      String credsSubstring = authHeader.substring("Basic ".length());
      String usernameAndPassword = Base64.decodeAsString(credsSubstring);
      return usernameAndPassword;
   }

   public static String getUsernameFromAuth(String authString) {
      String username = "null";
      if (authString != null && !authString.isEmpty()) {
         username = authString.substring(0, authString.indexOf(':'));
      }
      return username;
   }

   public static String getPasswordFromAuth(String authString) {
      String password = "null";
      if (authString != null && !authString.isEmpty()) {
         password = authString.substring(authString.indexOf(':') + 1);
      }
      return password;
   }
}
