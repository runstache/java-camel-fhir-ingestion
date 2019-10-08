package com.lswebworld.fhiringestion.processors;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.client.api.IGenericClient;

import com.lswebworld.fhiringestion.configuration.AppSettings;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.dstu3.model.DiagnosticReport;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.Reference;

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

      // Tie the Diagnostic reports to the Encounter
      // if the Performed Date is within the Encounter Date.
      for (BundleEntryComponent entry : bundle.getEntry()) {
        if (entry.getResource() instanceof DiagnosticReport) {
          DiagnosticReport report = (DiagnosticReport) entry.getResource();
          if (report.getEffectivePeriod() != null && report.getEffectivePeriod().getStart() != null
              && report.getEffectivePeriod().getEnd() != null) {

            bundle.getEntry().stream()
                .filter(c -> c.getResource() instanceof Encounter).forEach(e -> {
                  Encounter enc = (Encounter) e.getResource();
                  if (enc.getPeriod() != null && enc.getPeriod().getStart() != null 
                      && enc.getPeriod().getEnd() != null) {

                    if (report.getEffectivePeriod().getStart().after(enc.getPeriod().getStart())
                        && report.getEffectivePeriod().getEnd().before(enc.getPeriod().getEnd())) {
                      report.setContext(new Reference(e.getFullUrl()));                      
                    }
                  }
                });
          }
        }
      }

      Bundle response = client.transaction().withBundle(bundle).execute();
      exchange.getIn().setBody(response);

    }

  }
}