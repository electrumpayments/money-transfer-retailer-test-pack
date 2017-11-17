package io.electrum.moneytransfer.resource.impl;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;

import io.electrum.moneytransfer.model.MoneyTransferAdminMessage;
import io.electrum.moneytransfer.model.MoneyTransferAuthRequest;
import io.electrum.moneytransfer.model.MoneyTransferAuthResponse;
import io.electrum.moneytransfer.model.MoneyTransferConfirmation;
import io.electrum.moneytransfer.model.MoneyTransferLookupResponse;
import io.electrum.moneytransfer.model.MoneyTransferRedeemRequest;
import io.electrum.moneytransfer.model.MoneyTransferRedeemResponse;
import io.electrum.moneytransfer.model.MoneyTransferReversal;
import io.electrum.moneytransfer.server.util.RequestKey;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MoneyTransferTestServer extends ResourceConfig {

   private ConcurrentHashMap<RequestKey, MoneyTransferAdminMessage> adminRecords;
   private ConcurrentHashMap<RequestKey, MoneyTransferAuthRequest> authRequestRecords;
   private ConcurrentHashMap<RequestKey, MoneyTransferAuthResponse> authResponseRecords;
   private ConcurrentHashMap<RequestKey, MoneyTransferConfirmation> confirmationRecords;
   private ConcurrentHashMap<RequestKey, MoneyTransferLookupResponse> lookupResponseRecords;
   private ConcurrentHashMap<RequestKey, MoneyTransferRedeemRequest> redeemRequestRecords;
   private ConcurrentHashMap<RequestKey, MoneyTransferRedeemResponse> redeemResponseRecords;
   private ConcurrentHashMap<RequestKey, MoneyTransferReversal> reversalRecords;
   private static final Logger log = LoggerFactory.getLogger(MoneyTransferTestServer.class.getPackage().getName());

   public MoneyTransferTestServer() {
      packages(MoneyTransferTestServer.class.getPackage().getName());

      register(MyObjectMapperProvider.class);
      register(JacksonFeature.class);
      log.debug("Initing new TestServer");
      this.adminRecords = new ConcurrentHashMap<>();
      this.authRequestRecords = new ConcurrentHashMap<>();
      this.authResponseRecords = new ConcurrentHashMap<>();
      this.confirmationRecords = new ConcurrentHashMap<>();
      this.lookupResponseRecords = new ConcurrentHashMap<>();
      this.redeemRequestRecords = new ConcurrentHashMap<>();
      this.redeemResponseRecords = new ConcurrentHashMap<>();
      this.reversalRecords = new ConcurrentHashMap<>();
   }

   public ConcurrentHashMap<RequestKey, MoneyTransferAdminMessage> getAdminRecords() {
      return adminRecords;
   }

   public ConcurrentHashMap<RequestKey, MoneyTransferAuthRequest> getAuthRequestRecords() {
      return authRequestRecords;
   }

   public ConcurrentHashMap<RequestKey, MoneyTransferAuthResponse> getAuthResponseRecords() {
      return authResponseRecords;
   }

   public ConcurrentHashMap<RequestKey, MoneyTransferConfirmation> getConfirmationRecords() {
      return confirmationRecords;
   }

   public ConcurrentHashMap<RequestKey, MoneyTransferLookupResponse> getLookupResponseRecords() {
      return lookupResponseRecords;
   }

   public ConcurrentHashMap<RequestKey, MoneyTransferRedeemRequest> getRedeemRequestRecords() {
      return redeemRequestRecords;
   }

   public ConcurrentHashMap<RequestKey, MoneyTransferRedeemResponse> getRedeemResponseRecords() {
      return redeemResponseRecords;
   }

   public ConcurrentHashMap<RequestKey, MoneyTransferReversal> getReversalRecords() {
      return reversalRecords;
   }

   @Provider
   public static class MyObjectMapperProvider implements ContextResolver<ObjectMapper> {

      private final ObjectMapper mapper;
      private final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

      public MyObjectMapperProvider() {
         mapper = new ObjectMapper();
         mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
         mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
         mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
         mapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
         mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
         mapper.registerModule(new JodaModule());
         DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
         mapper.setDateFormat(DATE_FORMAT);
      }

      @Override
      public ObjectMapper getContext(Class<?> type) {
         return mapper;
      }
   }

   @SuppressWarnings("serial")
   private static class LowerCaseWitHyphenStrategy extends PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy {
      @Override
      public String translate(String input) {
         String output = super.translate(input);
         return output == null ? null : output.replace('_', '-');
      }
   }
}
