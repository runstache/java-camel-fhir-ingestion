package com.lswebworld.fhiringestion.routes;

import com.lswebworld.fhiringestion.configuration.AppSettings;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FhirRoute extends RouteBuilder {

  @Autowired
  AppSettings settings;

  @Override
  public void configure() throws Exception {
    
    onException(Exception.class)
      .maximumRedeliveries(5)
      .maximumRedeliveryDelay(10000L)      
      .end();
    
    from("file://" + settings.getSourcePath()
        + "?delete=false"
        + "&preMove=staging"
        + "&move=completed"
        + "&antInclude=*.json")
        .routeId("fhir-ingestion")
        .autoStartup(settings.isEnabled())
        .convertBodyTo(String.class)
        .setHeader("AddRules", simple(Boolean.toString(settings.isIdentifierExist())))
        .choice()
          .when(header("AddRules").isEqualToIgnoreCase("true"))
            .to("bean:RuleProcessor")
        .end()          
        .marshal().fhirJson(settings.getFhirVersion())
        .to("fhir://transaction/withBundle?inBody=stringBundle" 
            + "&serverUrl=" + settings.getFhirServer()
            + "&fhirVersion=" + settings.getFhirVersion()
            + "&validationMode=NEVER"
            + "&log=true")
        .end();
          

  }

}