package com.lswebworld.fhiringestion.processors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.ITransaction;
import ca.uhn.fhir.rest.gclient.ITransactionTyped;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.http.client.ClientProtocolException;

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
  IGenericClient client;

  @Autowired
  FhirContext ctx;

  private String jsonBody = "";
  private Bundle bundle;

  /**
   * Setup Method.
   * 
   * @throws IOException IO Exception.
   */
  @Before
  @SuppressWarnings("unchecked")
  public void setup() throws IOException {
    MockitoAnnotations.initMocks(this);

    ClassLoader classLoader = getClass().getClassLoader();
    File file = new File(classLoader.getResource("samplebundle.json").getFile());
    byte[] contents = Files.readAllBytes(file.toPath());
    jsonBody = new String(contents);

    IParser parser = ctx.newJsonParser();
    bundle = parser.parseResource(Bundle.class, jsonBody);

    ITransaction transaction = mock(ITransaction.class);
    transaction.withBundle(bundle);
    ITransactionTyped<Bundle> mockBundle = mock(ITransactionTyped.class);
    when(transaction.withBundle(any(Bundle.class))).thenReturn(mockBundle);
    when(mockBundle.execute()).thenReturn(bundle);
    when(client.transaction()).thenReturn(transaction);

  }


  @Test
  @DirtiesContext
  public void testSendBundleJson() throws ClientProtocolException, IOException {
    template.sendBody(jsonBody);
    Exchange msg = output.getExchanges().get(0);
    assertEquals(bundle, msg.getIn().getBody());
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
    template.sendBody(bundle);
    Exchange msg = output.getExchanges().get(0);
    assertEquals(bundle, msg.getIn().getBody());
  }


}