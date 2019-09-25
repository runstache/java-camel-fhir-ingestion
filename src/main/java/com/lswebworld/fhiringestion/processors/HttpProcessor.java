package com.lswebworld.fhiringestion.processors;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.hl7.fhir.dstu3.model.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class HttpProcessor implements Processor {

  @Autowired
  CloseableHttpClient client;

  @Autowired
  FhirContext ctx;

  private static final Logger LOGGER = LoggerFactory.getLogger(HttpProcessor.class);

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
      
      HttpPost httpPost = new HttpPost();
      httpPost.setEntity(new StringEntity(parser.encodeResourceToString(bundle)));

      CloseableHttpResponse response = client.execute(httpPost);
      StatusLine statusLine = response.getStatusLine();
      HttpEntity entity = response.getEntity();
      response.close();
      if (statusLine.getStatusCode() == 200 || statusLine.getStatusCode() == 201) {
        exchange.getIn().setBody(getContent(entity));
      } else {
        LOGGER.error("FHIR Bundle Failed to post.");
        exchange.getIn().setBody(getContent(entity));
      }
    }
  }

  /**
   * Retrieves the Content from the Http Entity Response.
   * @param entity Http Entity.
   * @return String value
   * @throws IOException IO Exception.
   */
  private String getContent(HttpEntity entity) throws IOException {

    InputStream stream = entity.getContent();
    byte[] buffer = new byte[1024];
    int count = stream.read(buffer);
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    while (count != -1) {
      output.write(buffer, 0, count);
      count = stream.read(buffer);
    }
    stream.close();
    return new String(output.toByteArray());
  } 
}