package io.electrum.moneytransfer.server.backend.db;

import io.electrum.moneytransfer.server.backend.tables.CustomerTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.electrum.moneytransfer.resource.impl.MoneyTransferTestServer;

public class MockMoneyTransferDb {
   private static final Logger log = LoggerFactory.getLogger(MoneyTransferTestServer.class);

   private CustomerTable customerTable;

   public MockMoneyTransferDb() {
      reset();
   }

   public void reset() {
      customerTable = new CustomerTable();
   }

   public CustomerTable getCustomerTable() {
      return customerTable;
   }

   public void setCustomerTable(CustomerTable customerTable) {
      this.customerTable = customerTable;
   }
}
