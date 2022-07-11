package com.edc.edcportal.core.apim.services.configuration;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "APIM Service Configuration", description = "APIM configuration for MyEDC integration")
public @interface ApimConfigServiceConfiguration {

    @AttributeDefinition(name = "Get Token Url", description = "Endpoint to Get the token ", type = AttributeType.STRING)
    String getTokenUrl() default "";

    @AttributeDefinition(name = "Get Client Id", description = "Get Client ID", type = AttributeType.STRING)
    String getClientId() default "";

    @AttributeDefinition(name = "Get Client Secret", description = "Get Client Secret", type = AttributeType.STRING)
    String getClientSecret() default "";

    @AttributeDefinition(name = "Get Token Resource", description = "Get Token Resource", type = AttributeType.STRING)
    String getResource() default "";

    @AttributeDefinition(name = "Get APIM proxy URL", description = "APIM proxy URL to get requests", type = AttributeType.STRING)
    String getAPIMProxyUrl() default "";
    
    @AttributeDefinition(name = "Get APIM Base endpoint", description = "APIM base endpooint to get requests", type = AttributeType.STRING)
    String getAPIMBaseEndpoint() default "";

    @AttributeDefinition(name = "Get APIM subscription Key", description = "Get APIM subscription Key", type = AttributeType.STRING)
    String getOCPAPIMsubscriptionKey() default "";

}
