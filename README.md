# Camel FHIR Ingestion Engine

Simple Camel Ingestion Engine for importing JSON FHIR Bundle files into a FHIR Server.

## Configuration

The following Configuration options are available via Environment Variables: (REQUIRED)

* __ACTIVE_PROFILE__: This is the Spring Profile to use.
* __LOG_LEVEL__: This is the Logging Level that should be set.
* __APP_FHIRVERSION__: This is the version of FHIR that should be used for the communication with the FHIR Server.
* __APP_SOURCEPATH__: This is the directory path for the FHIR Bundle JSON Files.
* __APP_FHIRSERVER__: This is the url for the FHIR Server to post the Bundles to.
* __APP_ENABLED__: Denotes to automatically start the FHIR Consumer Route automatically.
* __APP_IDENTIFIEREXIST__: Denotes for the system to replace the Request section of the Bundle to use the PUT method and a URL that includes the first identifier on the resource.  If set to False, the existing Request section will be utilized on the Bundle Entry.  If set to True, it will be replaced.
* __APP_TIMEOUT__: The value in milliseconds that the Camel Fhir Component should wait for a response.
* __APP_FHIRLOGGING__: Denotes as to whether the verbose logging should be enabled on the Camel Fhir Component.
