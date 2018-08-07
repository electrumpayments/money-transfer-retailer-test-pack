package io.electrum.moneytransfer.server.backend.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.electrum.moneytransfer.resource.impl.MoneyTransferTestServer;
import io.electrum.moneytransfer.server.backend.records.AdminRecord;
import io.electrum.moneytransfer.server.backend.tables.AdminTable;
import io.electrum.moneytransfer.server.backend.tables.AuthConfirmationTable;
import io.electrum.moneytransfer.server.backend.tables.AuthReversalTable;
import io.electrum.moneytransfer.server.backend.tables.AuthTable;
import io.electrum.moneytransfer.server.backend.tables.RedemptionConfirmationTable;
import io.electrum.moneytransfer.server.backend.tables.RedemptionReversalTable;
import io.electrum.moneytransfer.server.backend.tables.RedemptionTable;

public class MockMoneyTransferDb {
   private static final Logger LOGGER = LoggerFactory.getLogger(MoneyTransferTestServer.class);

   private AdminTable adminTable;
   private AuthTable authTable;
   private AuthConfirmationTable authConfirmationTable;
   private AuthReversalTable authReversalTable;
   private RedemptionTable redemptionTable;
   private RedemptionConfirmationTable redemptionConfirmationTable;
   private RedemptionReversalTable redemptionReversalTable;

   public MockMoneyTransferDb() {
      reset();
   }

   public void reset() {
      adminTable = new AdminTable();
      authTable = new AuthTable();
      authConfirmationTable = new AuthConfirmationTable();
      authReversalTable = new AuthReversalTable();
      redemptionTable = new RedemptionTable();
      redemptionConfirmationTable = new RedemptionConfirmationTable();
      redemptionReversalTable = new RedemptionReversalTable();
   }

   public AdminRecord getAdminRecord(String id) {
      if (id == null) {
         return null;
      }
      return getAdminTable().getRecord(id);
   }

   public boolean doesUuidExist(String uuid) {
      return authTable.getRecord(uuid) != null || authConfirmationTable.getRecord(uuid) != null
            || authReversalTable.getRecord(uuid) != null || redemptionTable.getRecord(uuid) != null
            || redemptionConfirmationTable.getRecord(uuid) != null || redemptionReversalTable.getRecord(uuid) != null;
   }

   public AdminTable getAdminTable() {
      return adminTable;
   }

   public AuthTable getAuthTable() {
      return authTable;
   }

   public AuthConfirmationTable getAuthConfirmationTable() {
      return authConfirmationTable;
   }

   public AuthReversalTable getAuthReversalTable() {
      return authReversalTable;
   }

   public RedemptionTable getRedemptionTable() {
      return redemptionTable;
   }

   public RedemptionConfirmationTable getRedemptionConfirmationTable() {
      return redemptionConfirmationTable;
   }

   public RedemptionReversalTable getRedemptionReversalTable() {
      return redemptionReversalTable;
   }
}
