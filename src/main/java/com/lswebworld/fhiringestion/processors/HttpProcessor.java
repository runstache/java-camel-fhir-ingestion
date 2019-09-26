package com.lswebworld.fhiringestion.processors;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.client.api.IGenericClient;

import com.lswebworld.fhiringestion.configuration.AppSettings;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import org.hl7.fhir.dstu3.model.Bundle;
import org.springframework.beans.factory.annotation.Autowired;

public class HttpProcessor implements Processor {

  @Autowired
  IGenericClient client;

  @Autowired
  AppSettings settings;

  @Autowired
  FhirContext ctx;



  @Override
  public void process(Exchange exchange) throws Exception {

    if (exchange.getIn().getBody() != null) {
      IParser parser = ctx.newJsonParser();
      Bundle bundle;

      if (exchange.getIn().getBody() instanceof Bundle) {
        bundle = exchange.getIn().getBody(Bundle.class);
      } else {
        bundle = parser.parseResource(Bundle.class, exchange.getIn().getBody().toString());
      }
            
      Bundle response = client.transaction().withBundle(bundle).execute();
      exchange.getIn().setBody(response);
     
    }
    
  }
}