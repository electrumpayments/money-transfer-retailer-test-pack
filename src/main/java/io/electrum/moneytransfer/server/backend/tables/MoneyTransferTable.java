package io.electrum.moneytransfer.server.backend.tables;

import io.electrum.moneytransfer.server.backend.records.MoneyTransferRecord;

import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

public abstract class MoneyTransferTable<T extends MoneyTransferRecord> {
   private ConcurrentHashMap<String, T> records;

   public MoneyTransferTable() {
      records = new ConcurrentHashMap<>();
   }

   public T getRecord(String recordId){
      if(records == null){
         throw new IllegalStateException("Table not initialised yet!");
      }
      if(recordId == null)
      {
         return null;
      }
      return records.get(recordId);
   }

   public void putRecord(T record)
   {
      if(record.getRecordId() == null)
      {
         throw new IllegalStateException("Cannot store record without a recordId (used as primary key)");
      }
      records.put(record.getRecordId(), record);
   }

   public Enumeration<T> getRecords()
   {
      return records.elements();
   }

}
