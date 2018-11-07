package io.electrum.moneytransfer.server.backend.tables;

import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

import io.electrum.moneytransfer.server.backend.records.MoneyTransferRecord;

public abstract class MoneyTransferTable<T extends MoneyTransferRecord> {

   private ConcurrentHashMap<String, T> records;

   public MoneyTransferTable() {
      records = new ConcurrentHashMap<String, T>();
   }

   public T getRecord(String recordId) {
      if (records == null) {
         throw new IllegalStateException("Table not initialised yet!");
      }
      if (recordId == null) {
         return null;
      }
      return records.get(recordId);
   }

   public void putRecord(T record) {
      if (record.getRecordId() == null) {
         throw new IllegalStateException("Cannot store record without a recordId (used as primary key)");
      }
      records.put(record.getRecordId(), record);
   }

   public Enumeration<T> getRecords() {
      return records.elements();
   }
}
