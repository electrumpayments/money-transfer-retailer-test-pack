package io.electrum.moneytransfer.server.backend.records;

import java.util.ArrayList;
import java.util.List;

public abstract class RequestRecord extends MoneyTransferRecord {

   public enum State {
      REQUESTED, CONFIRMED, REVERSED;
   }

   protected State state;
   protected List<String> confirmationIds;
   protected List<String> reversalIds;

   protected RequestRecord(String recordId) {
      super(recordId);
      this.state = State.REQUESTED;
      this.confirmationIds = new ArrayList<String>();
      this.reversalIds = new ArrayList<String>();
   }

   public State getState() {
      return state;
   }

   public void setState(State state) {
      this.state = state;
   }

   public List<String> getConfirmationIds() {
      return confirmationIds;
   }

   public void setConfirmationIds(List<String> confirmationIds) {
      this.confirmationIds = confirmationIds;
   }

   public List<String> getReversalIds() {
      return reversalIds;
   }

   public void setReversalIds(List<String> reversalIds) {
      this.reversalIds = reversalIds;
   }

   public void addConfirmationId(String confirmationId) {
      confirmationIds.add(confirmationId);
   }

   public void addReversalId(String reversalId) {
      reversalIds.add(reversalId);
   }

   public String getNthLastId(List<String> list, int index) {
      if (list.size() == 0) {
         return null;
      }
      return list.get(list.size() - index);
   }

   public String getLastConfirmationId() {
      return getNthLastId(confirmationIds, 1);
   }

   public String getLastReversalId() {
      return getNthLastId(reversalIds, 1);
   }
}
