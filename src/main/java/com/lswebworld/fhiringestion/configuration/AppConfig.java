package com.lswebworld.fhiringestion.configuration;

import ca.uhn.fhir.context.FhirContext;

import com.lswebworld.fhiringestion.processors.HttpProcessor;
import com.lswebworld.fhiringestion.processors.RuleProcessor;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



@Configuration
public class AppConfig {

  @Autowired
  AppSettings settings;


  @Bean(name = "RuleProcessor")
  public RuleProcessor ruleProcessor() {
    return new RuleProcessor();
  }

  /**
   * Creates a Closeable HTTP Client in the Bean Registry.
   * @return
   */
  @Bean(name = "CloseableHttpClient")
  public CloseableHttpClient closeableHttpClient() {
    PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
    connectionManager.setDefaultMaxPerRoute(100);
    connectionManager.setMaxTotal(100);

    RequestConfig requestConfig = RequestConfig.custom()
        .setConnectTimeout(30000)
        .setSocketTimeout(settings.getTimeout())
        .build();
    
    CloseableHttpClient client = HttpClients.custom()
        .setDefaultRequestConfig(requestConfig)
        .setConnectionManager(connectionManager)
        .build();

    return client;
  }

  @Bean(name = "HttpProcessor")
  public HttpProcessor httpProcessor() {
    return new HttpProcessor();
  }

  /**
   * Populates a Fhir Context into the Bean Factory based on the configured Fhir Version.
   * @return FHIR Context.
   */
  @Bean(name = "FhirContext")
  public FhirContext fhirContext() {
    switch (settings.getFhirVersion().toLowerCase()) {
      case "dstu2":
        return FhirContext.forDstu2();
      case "dstu3":
        return FhirContext.forDstu3();
      case "R4":
        return FhirContext.forR4();
      default:
        return FhirContext.forDstu3();
    }
  }
}