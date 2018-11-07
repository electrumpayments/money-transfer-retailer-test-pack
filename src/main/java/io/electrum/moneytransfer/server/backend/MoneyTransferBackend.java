package io.electrum.moneytransfer.server.backend;

import java.util.concurrent.ConcurrentHashMap;

import io.electrum.moneytransfer.server.backend.db.MockMoneyTransferDb;

public class MoneyTransferBackend {
   private static MoneyTransferBackend instance;
   private static ConcurrentHashMap<String, MockMoneyTransferDb> dbs;

   private MoneyTransferBackend() {
      dbs = new ConcurrentHashMap<String, MockMoneyTransferDb>();
   }

   public static MoneyTransferBackend getInstance() {
      if (instance == null) {
         instance = new MoneyTransferBackend();
      }
      return instance;
   }

   public MockMoneyTransferDb getDbForUser(String username, String password) {
      String dbKey = username + "|" + password;
      MockMoneyTransferDb db = dbs.get(dbKey);
      if (db == null) {
         db = new MockMoneyTransferDb();
         dbs.put(dbKey, db);
      }
      return db;
   }

   public boolean doesDbForUserExist(String username, String password) {
      String dbKey = username + "|" + password;
      MockMoneyTransferDb db = dbs.get(dbKey);
      if (db == null) {
         return false;
      }
      return true;
   }
}
