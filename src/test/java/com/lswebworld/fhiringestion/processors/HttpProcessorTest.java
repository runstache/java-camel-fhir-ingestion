package com.lswebworld.fhiringestion.processors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.hl7.fhir.dstu3.model.Bundle;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
public class HttpProcessorTest {

  @EndpointInject(uri = "direct:httpinput")
  ProducerTemplate template;

  @EndpointInject(uri = "mock:httpoutput")
  MockEndpoint output;

  @MockBean
  CloseableHttpClient client;

  @Autowired
  FhirContext ctx;

  private String jsonBody = "";

  private static final String ERROR_MESSAGE = "{\"message\": \"im an error\"}";
  private static final String SUCCESS_MESSAGE = "{\"message\": \"I made it\"}";
  private Bundle bundle;

  /**
   * Setup Method.
   * 
   * @throws IOException IO Exception.
   */
  @Before
  public void setup() throws IOException {
    MockitoAnnotations.initMocks(this);

    ClassLoader classLoader = getClass().getClassLoader();
    File file = new File(classLoader.getResource("samplebundle.json").getFile());
    byte[] contents = Files.readAllBytes(file.toPath());
    jsonBody = new String(contents);

    IParser parser = ctx.newJsonParser();
    bundle = parser.parseResource(Bundle.class, jsonBody);
  }

  @Test
  @DirtiesContext
  public void testSendBundle201() throws ClientProtocolException, IOException {
    CloseableHttpResponse response = mock(CloseableHttpResponse.class);
    StatusLine statusLine = mock(StatusLine.class);
    when(statusLine.getStatusCode()).thenReturn(201);
    when(response.getStatusLine()).thenReturn(statusLine);
    when(response.getEntity()).thenReturn(new StringEntity(SUCCESS_MESSAGE));
    when(client.execute((HttpPost)any(HttpPost.class))).thenReturn(response);

    template.sendBody(jsonBody);
    Exchange msg = output.getExchanges().get(0);
    assertEquals(SUCCESS_MESSAGE, msg.getIn().getBody());
  }

  @Test
  @DirtiesContext
  public void testSendBundle200() throws ClientProtocolException, IOException {
    CloseableHttpResponse response = mock(CloseableHttpResponse.class);
    StatusLine statusLine = mock(StatusLine.class);
    when(statusLine.getStatusCode()).thenReturn(200);
    when(response.getStatusLine()).thenReturn(statusLine);
    when(response.getEntity()).thenReturn(new StringEntity(SUCCESS_MESSAGE));
    when(client.execute((HttpPost)any(HttpPost.class))).thenReturn(response);

    template.sendBody(jsonBody);
    Exchange msg = output.getExchanges().get(0);
    assertEquals(SUCCESS_MESSAGE, msg.getIn().getBody());
  }

  @Test
  @DirtiesContext
  public void testSendBundle400() throws ClientProtocolException, IOException {
    CloseableHttpResponse response = mock(CloseableHttpResponse.class);
    StatusLine statusLine = mock(StatusLine.class);
    when(statusLine.getStatusCode()).thenReturn(400);
    when(response.getStatusLine()).thenReturn(statusLine);
    when(response.getEntity()).thenReturn(new StringEntity(ERROR_MESSAGE));
    when(client.execute((HttpPost)any(HttpPost.class))).thenReturn(response);

    template.sendBody(jsonBody);
    Exchange msg = output.getExchanges().get(0);
    assertEquals(ERROR_MESSAGE, msg.getIn().getBody());
  }

  @Test
  @DirtiesContext
  public void testSendNull() {
    template.sendBody(null);
    Exchange msg = output.getExchanges().get(0);
    assertNull(msg.getIn().getBody());
  }

  @Test
  @DirtiesContext
  public void testSendBundle() throws ClientProtocolException, IOException {
    CloseableHttpResponse response = mock(CloseableHttpResponse.class);
    StatusLine statusLine = mock(StatusLine.class);
    when(statusLine.getStatusCode()).thenReturn(200);
    when(response.getStatusLine()).thenReturn(statusLine);
    when(response.getEntity()).thenReturn(new StringEntity(SUCCESS_MESSAGE));
    when(client.execute((HttpPost)any())).thenReturn(response);
    

    template.sendBody(bundle);
    Exchange msg = output.getExchanges().get(0);
    assertEquals(SUCCESS_MESSAGE, msg.getIn().getBody());
  }


}