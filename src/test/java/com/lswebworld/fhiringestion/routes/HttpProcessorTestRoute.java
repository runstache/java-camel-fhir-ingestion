package com.lswebworld.fhiringestion.routes;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class HttpProcessorTestRoute extends RouteBuilder {

  @Override
  public void configure() throws Exception {
    
    from("direct:httpinput")
      .routeId("http-test-route")
      .to("bean:HttpProcessor")
      .to("mock:httpoutput")
      .end();

  }

}