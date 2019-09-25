package com.lswebworld.fhiringestion.processors;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;

import java.lang.reflect.Method;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.dstu3.model.Bundle.BundleEntryRequestComponent;
import org.hl7.fhir.dstu3.model.Bundle.HTTPVerb;
import org.hl7.fhir.dstu3.model.Identifier;
import org.springframework.beans.factory.annotation.Autowired;



public class RuleProcessor implements Processor {


  @Autowired
  FhirContext ctx;

  @Override
  public void process(Exchange exchange) throws Exception {
    
    IParser parser = ctx.newJsonParser();
    Bundle bundle = new Bundle();
    String jsonString = exchange.getIn().getBody().toString();
    bundle = parser.parseResource(Bundle.class, 
        jsonString.replace("&nbsp;", " ").replace("&copy;", ""));
    
    for (BundleEntryComponent entry : bundle.getEntry()) {
      
      Method[] methods = entry.getResource().getClass().getMethods();
      BundleEntryRequestComponent request = new BundleEntryRequestComponent();
      Method masterMethod = null;

      for (Method method : methods) {
        if (method.getName().toLowerCase().equals("getidentifierfirstrep")) {
          masterMethod = method;          
          break;
        }
      }
      if (masterMethod != null) {
        String className = entry.getResource().getClass().getSimpleName();
        request.setMethod(HTTPVerb.PUT);
        
        try {
          Identifier id = (Identifier)masterMethod.invoke(entry.getResource());
          request.setUrl(className + "?identifier=" + id.getSystem() + "|" + id.getValue());
          entry.setRequest(request);
        } catch (Exception ex) {
          request.setUrl(className);
          entry.setRequest(request);
        } 
      } else {
        request.setMethod(HTTPVerb.POST);
        request.setUrl(entry.getResource().getClass().getSimpleName());
        entry.setRequest(request);
      }
    }
    exchange.getIn().setBody(bundle);
  }

}