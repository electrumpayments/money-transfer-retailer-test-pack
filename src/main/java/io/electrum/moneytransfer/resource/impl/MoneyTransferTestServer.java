package io.electrum.moneytransfer.resource.impl;

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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;

import io.dropwizard.jersey.validation.DropwizardConfiguredValidator;
import io.dropwizard.jersey.validation.HibernateValidationFeature;
import io.dropwizard.jersey.validation.Validators;
import io.electrum.moneytransfer.model.MoneyTransferAdminMessage;
import io.electrum.moneytransfer.model.MoneyTransferAuthRequest;
import io.electrum.moneytransfer.model.MoneyTransferAuthResponse;
import io.electrum.moneytransfer.model.MoneyTransferConfirmation;
import io.electrum.moneytransfer.model.MoneyTransferRedeemRequest;
import io.electrum.moneytransfer.model.MoneyTransferRedeemResponse;
import io.electrum.moneytransfer.model.MoneyTransferReversal;
import io.electrum.moneytransfer.server.MoneyTransferViolationExceptionMapper;
import io.electrum.moneytransfer.server.util.RequestKey;
import io.electrum.moneytransfer.server.util.Status;

public class MoneyTransferTestServer extends ResourceConfig {

   private static ConcurrentHashMap<RequestKey, MoneyTransferAdminMessage> adminRecords = new ConcurrentHashMap<>();
   private static ConcurrentHashMap<String, String> customerIdToIdRecords = new ConcurrentHashMap<>();
   private static ConcurrentHashMap<RequestKey, MoneyTransferAuthRequest> authRequestRecords =
         new ConcurrentHashMap<>();
   private static ConcurrentHashMap<RequestKey, MoneyTransferAuthResponse> authResponseRecords =
         new ConcurrentHashMap<>();
   private static ConcurrentHashMap<RequestKey, MoneyTransferConfirmation> authConfirmationRecords =
         new ConcurrentHashMap<>();
   private static ConcurrentHashMap<RequestKey, MoneyTransferReversal> authReversalRecords = new ConcurrentHashMap<>();
   private static ConcurrentHashMap<RequestKey, MoneyTransferRedeemRequest> redeemRequestRecords =
         new ConcurrentHashMap<>();
   private static ConcurrentHashMap<RequestKey, MoneyTransferRedeemResponse> redeemResponseRecords =
         new ConcurrentHashMap<>();
   private static ConcurrentHashMap<RequestKey, MoneyTransferConfirmation> redeemConfirmationRecords =
         new ConcurrentHashMap<>();
   private static ConcurrentHashMap<RequestKey, MoneyTransferReversal> redeemReversalRecords =
         new ConcurrentHashMap<>();
   private static ConcurrentHashMap<String, Status> idCache = new ConcurrentHashMap<>();
   private static ConcurrentHashMap<String, String> orderRedeemRef = new ConcurrentHashMap<>();
   private static ConcurrentHashMap<String, Integer> authRequestPinRetries = new ConcurrentHashMap<>();

   private static final Logger log = LoggerFactory.getLogger(MoneyTransferTestServer.class.getPackage().getName());

   public MoneyTransferTestServer() {
      packages(MoneyTransferTestServer.class.getPackage().getName());

      register(MyObjectMapperProvider.class);
      register(JacksonFeature.class);

      register(
            new HibernateValidationFeature(
                  new DropwizardConfiguredValidator(Validators.newValidatorFactory().getValidator())));
      register(new MoneyTransferViolationExceptionMapper());

      log.debug("Initiating new TestServer");
   }

   public static void resetMoneyTransferTestServer() {
      adminRecords = new ConcurrentHashMap<>();
      customerIdToIdRecords = new ConcurrentHashMap<>();
      authRequestRecords = new ConcurrentHashMap<>();
      authConfirmationRecords = new ConcurrentHashMap<>();
      authReversalRecords = new ConcurrentHashMap<>();
      redeemRequestRecords = new ConcurrentHashMap<>();
      redeemConfirmationRecords = new ConcurrentHashMap<>();
      redeemReversalRecords = new ConcurrentHashMap<>();
      idCache = new ConcurrentHashMap<>();
      orderRedeemRef = new ConcurrentHashMap<>();
      authRequestPinRetries = new ConcurrentHashMap<>();
   }

   public static ConcurrentHashMap<String, String> getCustomerIdToIdRecords() {
      return customerIdToIdRecords;
   }

   public static ConcurrentHashMap<RequestKey, MoneyTransferAdminMessage> getAdminRecords() {
      return adminRecords;
   }

   public static ConcurrentHashMap<RequestKey, MoneyTransferAuthRequest> getAuthRequestRecords() {
      return authRequestRecords;
   }

   public static ConcurrentHashMap<RequestKey, MoneyTransferAuthResponse> getAuthResponseRecords() {
      return authResponseRecords;
   }

   public static ConcurrentHashMap<RequestKey, MoneyTransferConfirmation> getAuthConfirmationRecords() {
      return authConfirmationRecords;
   }

   public static ConcurrentHashMap<RequestKey, MoneyTransferReversal> getAuthReversalRecords() {
      return authReversalRecords;
   }

   public static ConcurrentHashMap<RequestKey, MoneyTransferRedeemRequest> getRedeemRequestRecords() {
      return redeemRequestRecords;
   }

   public static ConcurrentHashMap<RequestKey, MoneyTransferRedeemResponse> getRedeemResponseRecords() {
      return redeemResponseRecords;
   }

   public static ConcurrentHashMap<RequestKey, MoneyTransferConfirmation> getRedeemConfirmationRecords() {
      return redeemConfirmationRecords;
   }

   public static ConcurrentHashMap<RequestKey, MoneyTransferReversal> getRedeemReversalRecords() {
      return redeemReversalRecords;
   }

   public static ConcurrentHashMap<String, Status> getIdCache() {
      return idCache;
   }

   public static ConcurrentHashMap<String, Integer> getAuthRequestPinRetries() {
      return authRequestPinRetries;
   }

   public static ConcurrentHashMap<String, String> getOrderRedeemRef() {
      return orderRedeemRef;
   }

   @Provider
   public static class MyObjectMapperProvider implements ContextResolver<ObjectMapper> {

      private final ObjectMapper mapper;

      public MyObjectMapperProvider() {
         mapper = new ObjectMapper();
         mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
         mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
         mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
         mapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
         mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
         mapper.registerModule(new JodaModule());
         DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
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
