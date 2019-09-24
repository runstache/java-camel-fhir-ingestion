package com.lswebworld.fhiringestion.configuration;

import ca.uhn.fhir.context.FhirContext;
import com.lswebworld.fhiringestion.processors.RuleProcessor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



@Configuration
public class AppConfig {

  @Autowired
  AppSettings settings;


  @Bean(name = "RuleProcessor")
  public RuleProcessor ruleProcessor() {
    return new RuleProcessor();
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