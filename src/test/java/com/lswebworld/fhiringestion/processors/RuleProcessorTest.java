package com.lswebworld.fhiringestion.processors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.dstu3.model.Bundle.HTTPVerb;
import org.hl7.fhir.dstu3.model.Provenance;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;



@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
public class RuleProcessorTest {

  @EndpointInject(uri = "direct:fhir")
  ProducerTemplate template;

  @EndpointInject(uri = "mock:fhiroutput")
  MockEndpoint output;


  /**
   * Setup Method.
   * 
   * @throws IOException IO Exception
   */
  @Before
  public void setup() throws IOException {

  }

  @Test
  @DirtiesContext
  public void testAddBundleRules() throws IOException {

    String jsonBody = "";

    ClassLoader classLoader = getClass().getClassLoader();
    File file = new File(classLoader.getResource("samplebundle.json").getFile());
    byte[] contents = Files.readAllBytes(file.toPath());
    jsonBody = new String(contents);

    template.sendBody(jsonBody);
    Exchange msg = output.getExchanges().get(0);
    assertNotNull(msg.getIn().getBody());
    Bundle bundle = msg.getIn().getBody(Bundle.class);
    for (BundleEntryComponent entry : bundle.getEntry()) {
      assertNotNull(entry.getRequest().getUrl());
      assertEquals(HTTPVerb.PUT, entry.getRequest().getMethod());
      assertTrue(entry.getRequest().getUrl().contains("identifier="));
    }
  }

  @Test
  @DirtiesContext
  public void testAddBundleRuleNoIdentifier() throws IOException {
    String jsonBody = "";
    ClassLoader classLoader = getClass().getClassLoader();
    File file = new File(classLoader.getResource("samplebundle-noidentifier.json").getFile());
    byte[] contents = Files.readAllBytes(file.toPath());
    jsonBody = new String(contents);

    template.sendBody(jsonBody);
    Exchange msg = output.getExchanges().get(0);
    assertNotNull(msg.getIn().getBody());
    Bundle bundle = msg.getIn().getBody(Bundle.class);
    
    for (BundleEntryComponent entry : bundle.getEntry()) {
      if (entry.getResource() instanceof Provenance) {
        assertNotNull(entry.getRequest().getUrl());
        assertEquals(HTTPVerb.POST, entry.getRequest().getMethod());
        assertEquals("Provenance", entry.getRequest().getUrl());
      }
    }
  }
}