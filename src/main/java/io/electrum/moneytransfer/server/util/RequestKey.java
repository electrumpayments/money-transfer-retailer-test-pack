package io.electrum.moneytransfer.server.util;

import java.util.Objects;

public class RequestKey {
   public static final String GET_CUSTOMER_INFO_RESOURCE = "getCustomerInfo";
   public static final String CREATE_OR_UPDATE_CUSTOMER_RESOURCE = "createOrUpdateCustomer";
   public static final String CREATE_ORDER_RESOURCE = "createOrder";
   public static final String CONFIRM_PAYMENT_RESOURCE = "confirmPayment";
   public static final String REVERSE_PAYMENT_RESOURCE = "reversePayment";
   public static final String LOOKUP_ORDER_RESOURCE = "lookupOrder";
   public static final String REDEEM_ORDER_RESOURCE = "redeemOrder";
   public static final String CONFIRM_REDEEM_RESOURCE = "confirmRedeem";
   public static final String REVERSE_REDEEM_RESOURCE = "reverseRedeem";

   private String username;
   private String password;
   private String resourceType;
   private String uuid;

   public RequestKey(String username, String password, String resourceType, String uuid) {
      this.username = username;
      this.password = password;
      this.resourceType = resourceType;
      this.uuid = uuid;
   }

   public String getUsername() {
      return username;
   }

   public void setUsername(String username) {
      this.username = username;
   }

   public String getPassword() {
      return password;
   }

   public void setPassword(String password) {
      this.password = password;
   }

   public String getResourceType() {
      return resourceType;
   }

   public void setResourceType(String resourceType) {
      this.resourceType = resourceType;
   }

   public String getUuid() {
      return uuid;
   }

   public void setUuid(String uuid) {
      this.uuid = uuid;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      }
      if (o == null || getClass() != o.getClass()) {
         return false;
      }

      RequestKey otherKey = (RequestKey) o;

      return username != null && username.equals(otherKey.username) && password != null
            && password.equals(otherKey.password) && resourceType != null && resourceType.equals(otherKey.resourceType)
            && uuid != null && uuid.equals(otherKey.uuid);
   }

   @Override
   public int hashCode() {
      return Objects.hash(username, password, resourceType, uuid);
   }

   @Override
   public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append(username);
      sb.append("|");
      sb.append(password);
      sb.append("|");
      sb.append(resourceType);
      sb.append("|");
      sb.append(uuid);
      return sb.toString();
   }
}
