package io.electrum.moneytransfer.resource.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

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
import io.electrum.moneytransfer.server.MoneyTransferViolationExceptionMapper;
import io.electrum.moneytransfer.server.backend.MoneyTransferBackend;

public class MoneyTransferTestServer extends ResourceConfig {

   private static MoneyTransferBackend backend;
   private static final Logger log = LoggerFactory.getLogger(MoneyTransferTestServer.class.getPackage().getName());

   public MoneyTransferTestServer() {
      packages(MoneyTransferTestServer.class.getPackage().getName());

      backend = MoneyTransferBackend.getInstance();

      register(MyObjectMapperProvider.class);
      register(JacksonFeature.class);

      register(
            new HibernateValidationFeature(
                  new DropwizardConfiguredValidator(Validators.newValidatorFactory().getValidator())));
      register(new MoneyTransferViolationExceptionMapper());

      log.debug("Initiating new TestServer");
   }

   public static MoneyTransferBackend getBackend() {
      return backend;
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
