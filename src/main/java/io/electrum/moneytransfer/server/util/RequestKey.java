package io.electrum.moneytransfer.server.util;

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
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (!(obj instanceof RequestKey)) {
         return false;
      }
      RequestKey other = (RequestKey) obj;
      if (password == null) {
         if (other.password != null) {
            return false;
         }
      } else if (!password.equals(other.password)) {
         return false;
      }
      if (resourceType == null) {
         if (other.resourceType != null) {
            return false;
         }
      } else if (!resourceType.equals(other.resourceType)) {
         return false;
      }
      if (username == null) {
         if (other.username != null) {
            return false;
         }
      } else if (!username.equals(other.username)) {
         return false;
      }
      if (uuid == null) {
         if (other.uuid != null) {
            return false;
         }
      } else if (!uuid.equals(other.uuid)) {
         return false;
      }
      return true;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((password == null) ? 0 : password.hashCode());
      result = prime * result + ((resourceType == null) ? 0 : resourceType.hashCode());
      result = prime * result + ((username == null) ? 0 : username.hashCode());
      return prime * result + ((uuid == null) ? 0 : uuid.hashCode());
   }

   @Override
   public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append(username);
      sb.append('|');
      sb.append(password);
      sb.append('|');
      sb.append(resourceType);
      sb.append('|');
      sb.append(uuid);
      return sb.toString();
   }
}
