package io.electrum.moneytransfer.server.util;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.electrum.vas.JsonUtil;
import io.electrum.vas.model.ThirdPartyIdentifier;

public class OrderUtils {
   private static final Logger LOGGER = LoggerFactory.getLogger(OrderUtils.class.getPackage().getName());
   public static ConcurrentHashMap<String, String> orderRedeemRef = new ConcurrentHashMap<>();

   public static <T, S> T copyClass(S source, Class<S> sourceClass, Class<T> destinationClass) {
      try {
         return JsonUtil.deserialize(JsonUtil.serialize(source, sourceClass), destinationClass);
      } catch (IOException e) {
         LOGGER.error(
               "Message not able to transformed from " + sourceClass.getName() + " to " + destinationClass.getName());
         LOGGER.error(e.getMessage());
         return null;
      }
   }

   public static ThirdPartyIdentifier getRandomThirdPartyIdentifier(String id) {
      ThirdPartyIdentifier thirdPartyIdentifier = new ThirdPartyIdentifier();
      thirdPartyIdentifier.setInstitutionId(id);
      thirdPartyIdentifier.setTransactionIdentifier(RandomData.random09AZ(20));
      return thirdPartyIdentifier;
   }

}
