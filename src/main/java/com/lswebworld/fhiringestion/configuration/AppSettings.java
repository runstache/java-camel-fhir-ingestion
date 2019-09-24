package com.lswebworld.fhiringestion.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app")
public class AppSettings {

  private String fhirVersion;
  private String fhirServer;
  private String sourcePath;
  private boolean enabled;
  private boolean identifierExist;
  

  /**
   * Constructor.
   */
  public AppSettings() {
    this.fhirServer = new String();
    this.fhirVersion = new String();
    this.sourcePath = new String();
  }

  public String getFhirVersion() {
    return fhirVersion;
  }

  public void setFhirVersion(String fhirVersion) {
    this.fhirVersion = fhirVersion;
  }

  public String getFhirServer() {
    return fhirServer;
  }

  public void setFhirServer(String fhirServer) {
    this.fhirServer = fhirServer;
  }

  public String getSourcePath() {
    return sourcePath;
  }

  public void setSourcePath(String sourcePath) {
    this.sourcePath = sourcePath;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public boolean isIdentifierExist() {
    return identifierExist;
  }

  public void setIdentifierExist(boolean identifierExist) {
    this.identifierExist = identifierExist;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof AppSettings) {
      AppSettings settings = (AppSettings)obj;
      return this.enabled == settings.isEnabled()
          && this.fhirServer.equals(settings.getFhirServer())
          && this.fhirVersion.equals(settings.getFhirVersion())
          && this.sourcePath.equals(settings.getSourcePath())
          && this.identifierExist == settings.isIdentifierExist();
    }
    return false;
  }





}