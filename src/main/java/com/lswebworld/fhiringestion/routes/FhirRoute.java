package com.lswebworld.fhiringestion.routes;

import com.lswebworld.fhiringestion.configuration.AppSettings;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;

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
        + "&move=completed")
        .routeId("fhir-ingestion")
        .marshal().fhirJson(settings.getFhirVersion())
        .to("fhir://transaction/withBundle?inBody=stringBundle" 
            + "&serverUrl=" + settings.getFhirServer()
            + "&fhirVersion=" + settings.getFhirVersion()
            + "&validationMode=NEVER"
            + "&log=true");
          

  }

}