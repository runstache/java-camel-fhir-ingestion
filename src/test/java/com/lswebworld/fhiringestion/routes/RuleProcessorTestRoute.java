package com.lswebworld.fhiringestion.routes;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class RuleProcessorTestRoute extends RouteBuilder {

  @Override
  public void configure() throws Exception {
    from("direct:fhir")
      .routeId("rule-test-route")
      .to("bean:RuleProcessor")
      .to("mock:fhiroutput")
      .end();

  }

}