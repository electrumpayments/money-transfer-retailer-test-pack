package io.electrum.moneytransfer.server.backend.records;

import io.electrum.moneytransfer.model.PersonalDetails;

public class CustomerRecord extends MoneyTransferRecord {

   protected PersonalDetails personalDetails;
   protected String customerProfileId;

   private CustomerRecord() {
      // Required recordId
   }

   public CustomerRecord(String recordId) {
      this.recordId = recordId;
   }

   public void setPersonalDetails(PersonalDetails personalDetails) {
      this.personalDetails = personalDetails;
   }

   public PersonalDetails getPersonalDetails() {
      return personalDetails;
   }

   public void setCustomerProfileId(String customerProfileId) {
      this.customerProfileId = customerProfileId;
   }

   public String getCustomerProfileId() {
      return customerProfileId;
   }
}
