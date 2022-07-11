package com.edc.edcportal.core.ci.services.configuration;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "CI Service Configuration", description = "CI configuration for MyEDC integration")
public @interface CiConfigServiceConfiguration {

    @AttributeDefinition(name = "Get Default Search Page Node", description = "Get Default Search Page Node no Language nor html extension", type = AttributeType.STRING)
    String getLandingSearchPageNode() default "";

    @AttributeDefinition(name = "Get Search Results Page Node", description = "Get Search Results Page Node no Language nor html Extension", type = AttributeType.STRING)
    String getSearchResultsPageNode() default "";

    @AttributeDefinition(name = "Get Search Results Page Node", description = "Get Company Profile Page Node no Language nor html Extension", type = AttributeType.STRING)
    String getCompanyProfilePageNode() default "";

    @AttributeDefinition(name = "Get get the JSON Keys to build the Google Maps Link", description = "Gget the JSON Keys to build the Google Maps Link", type = AttributeType.STRING)
    String getMapKeys() default "";

    @AttributeDefinition(name = "Google Maps Link to create the query", description = "Google Maps Link to create the query", type = AttributeType.STRING)
    String getMapBaseUrl() default "";

    @AttributeDefinition(name = "Get Page Count", description = "Show hoy many results to show, before showing load more ", type = AttributeType.INTEGER)
    int getPageCount();

    @AttributeDefinition(name = "Get Token Url", description = "Endpoint to Get the token ", type = AttributeType.STRING)
    String getTokenUrl() default "";

    @AttributeDefinition(name = "Get Client Id", description = "Get Client ID", type = AttributeType.STRING)
    String getClientId() default "";

    @AttributeDefinition(name = "Get Client Secret", description = "Get Client Secret", type = AttributeType.STRING)
    String getClientSecret() default "";

    @AttributeDefinition(name = "Get Token Resource", description = "Get Token Resource", type = AttributeType.STRING)
    String getResource() default "";

    @AttributeDefinition(name = "Get CI proxy URL", description = "CI proxy URL to get requests", type = AttributeType.STRING)
    String getCIProxyUrl() default "";

    @AttributeDefinition(name = "Get CI Base endpoint", description = "CI base endpooint to get requests", type = AttributeType.STRING)
    String getCIBaseEndpoint() default "";

    @AttributeDefinition(name = "Get CI subscription Key", description = "Get CI subscription Key", type = AttributeType.STRING)
    String getOCPCIsubscriptionKey() default "";
}
