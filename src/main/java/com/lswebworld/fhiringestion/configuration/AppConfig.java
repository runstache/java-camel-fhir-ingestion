package com.lswebworld.fhiringestion.configuration;

import ca.uhn.fhir.context.FhirContext;
import com.lswebworld.fhiringestion.processors.RuleProcessor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



@Configuration
public class AppConfig {

  @Bean(name = "RuleProcessor")
  public RuleProcessor ruleProcessor() {
    return new RuleProcessor();
  }

  @Bean(name = "FhirContext")
  public FhirContext fhirContext() {
    return FhirContext.forDstu3();
  }
}