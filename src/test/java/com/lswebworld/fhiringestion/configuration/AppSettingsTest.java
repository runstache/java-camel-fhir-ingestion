package com.lswebworld.fhiringestion.configuration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
public class AppSettingsTest {

  private AppSettings settings;

  /**
   * Setup Method.
   */
  @Before
  public void setup() {
    settings = new AppSettings();
    settings.setEnabled(true);
    settings.setFhirServer("http://localhost/baseDstu3");
    settings.setFhirVersion("DSTU3");
    settings.setSourcePath("c:/docker/data/fhir");
    settings.setIdentifierExist(true);
  }

  @Test
  public void testIsEnabled() {
    assertTrue(settings.isEnabled());
  }

  @Test
  public void testGetFhirServer() {
    assertEquals("http://localhost/baseDstu3", settings.getFhirServer());
  }

  @Test
  public void testGetFhirVersion() {
    assertEquals("DSTU3", settings.getFhirVersion());
  }

  @Test
  public void testGetSourcePath() {
    assertEquals("c:/docker/data/fhir", settings.getSourcePath());
  }

  @Test
  public void testIsIdentifierExist() {
    assertTrue(settings.isIdentifierExist());
  }

  @Test
  public void testEquals() {
    AppSettings settings2 = new AppSettings();
    settings2.setEnabled(true);
    settings2.setFhirServer("http://localhost/baseDstu3");
    settings2.setFhirVersion("DSTU3");
    settings2.setSourcePath("c:/docker/data/fhir");
    settings2.setIdentifierExist(true);
    assertTrue(settings.equals(settings2));
  }

  @Test
  public void testNotEqualsEnabled() {
    AppSettings settings2 = new AppSettings();
    settings2.setEnabled(false);
    settings2.setFhirServer("http://localhost/baseDstu3");
    settings2.setFhirVersion("DSTU3");
    settings2.setSourcePath("c:/docker/data/fhir");
    settings2.setIdentifierExist(true);
    assertFalse(settings.equals(settings2));
  }

  @Test
  public void testNotEqualsFhirServer() {
    AppSettings settings2 = new AppSettings();
    settings2.setEnabled(true);
    settings2.setFhirServer("http://localhost/baseDstu2");
    settings2.setFhirVersion("DSTU3");
    settings2.setSourcePath("c:/docker/data/fhir");
    settings2.setIdentifierExist(true);
    assertFalse(settings.equals(settings2));
  }

  @Test
  public void testNotEqualsFhirVersion() {
    AppSettings settings2 = new AppSettings();
    settings2.setEnabled(true);
    settings2.setFhirServer("http://localhost/baseDstu3");
    settings2.setFhirVersion("DSTU2");
    settings2.setSourcePath("c:/docker/data/fhir");
    settings2.setIdentifierExist(true);
    assertFalse(settings.equals(settings2));
  }

  @Test
  public void testNotEqualsSourcePath() {
    AppSettings settings2 = new AppSettings();
    settings2.setEnabled(true);
    settings2.setFhirServer("http://localhost/baseDstu3");
    settings2.setFhirVersion("DSTU3");
    settings2.setSourcePath("c:/docker/data/ccd");
    settings2.setIdentifierExist(true);
    assertFalse(settings.equals(settings2));
  }

  @Test
  public void testNotEqualsIdentifierExist() {
    AppSettings settings2 = new AppSettings();
    settings2.setEnabled(true);
    settings2.setFhirServer("http://localhost/baseDstu3");
    settings2.setFhirVersion("DSTU3");
    settings2.setSourcePath("c:/docker/data/fhir");
    settings2.setIdentifierExist(false);
    assertFalse(settings.equals(settings2));
  } 

  @Test
  public void testNotEqualsWrongObject() {
    assertFalse(settings.equals(new Object()));
  }

}