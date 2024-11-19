package it.eng.idra.beans.zenodo;

import java.util.ArrayList;

public class ZenodoException extends Exception {

   private ArrayList<String> messages = new ArrayList<String>();

   public ZenodoException(String message) {
      this.messages.add(message);
   }

   public void addError(String error) {
      this.messages.add(error);
   }

   public ArrayList<String> getErrorMessages() {
      return this.messages;
   }

   public String toString() {
      return this.messages.toString();
   }
}
