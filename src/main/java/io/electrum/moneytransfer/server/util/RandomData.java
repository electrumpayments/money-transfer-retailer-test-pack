package io.electrum.moneytransfer.server.util;

import java.security.SecureRandom;

public class RandomData {
   private static SecureRandom secRandom = new SecureRandom();

   public static String random09(int length) {
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < length; i++) {
         sb.append(secRandom.nextInt(10));
      }

      return sb.toString();
   }

   public static String randomaz(int length) {
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < length; i++) {
         sb.append((int) (secRandom.nextDouble() * 27 + 'a'));
      }

      return sb.toString();
   }

   public static String randomAZ(int length) {
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < length; i++) {
         sb.append((int) (secRandom.nextDouble() * 27 + 'A'));
      }

      return sb.toString();
   }

   public static String random09azAZ(int length) {
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < length; i++) {
         switch ((int) secRandom.nextDouble() * 3) {
         case 0:
            sb.append(random09(1));
            break;
         case 1:
            sb.append(randomaz(1));
            break;
         case 2:
            sb.append(randomAZ(1));
            break;
         }
      }

      return sb.toString();
   }

   public static String random09AZ(int length) {
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < length; i++) {
         switch ((int) secRandom.nextDouble() * 2) {
         case 0:
            sb.append(random09(1));
            break;
         case 1:
            sb.append(randomAZ(1));
            break;
         }
      }

      return sb.toString();
   }
}
