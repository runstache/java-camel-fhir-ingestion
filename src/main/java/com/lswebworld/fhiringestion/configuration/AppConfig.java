package com.lswebworld.fhiringestion.configuration;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.api.ServerValidationModeEnum;

import com.lswebworld.fhiringestion.processors.HttpProcessor;
import com.lswebworld.fhiringestion.processors.RuleProcessor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class AppConfig {

  @Autowired
  AppSettings settings;

  @Autowired
  FhirContext ctx;


  @Bean(name = "RuleProcessor")
  public RuleProcessor ruleProcessor() {
    return new RuleProcessor();
  }

  @Bean(name = "HttpProcessor")
  public HttpProcessor httpProcessor() {
    return new HttpProcessor();
  }

  /**
   * Populates a Generic Fhir Client to the Bean Factory.
   * @return IGeneric Client
   */
  @Bean(name = "IGenericClient")
  public IGenericClient igenericClient() {
    ctx.getRestfulClientFactory().setServerValidationMode(ServerValidationModeEnum.NEVER);
    ctx.getRestfulClientFactory().setSocketTimeout(settings.getTimeout());
    return ctx.newRestfulGenericClient(settings.getFhirServer());
  }

  /**
   * Populates a Fhir Context into the Bean Factory based on the configured Fhir Version.
   * @return FHIR Context.
   */
  @Bean(name = "FhirContext")
  public FhirContext fhirContext() {
    switch (settings.getFhirVersion().toLowerCase()) {
      case "dstu2":
        return FhirContext.forDstu2();
      case "dstu3":
        return FhirContext.forDstu3();
      case "R4":
        return FhirContext.forR4();
      default:
        return FhirContext.forDstu3();
    }
  }
}