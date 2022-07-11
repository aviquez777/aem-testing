package com.edc.edcweb.core.gps.service.configuration;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "GPS Service Configuration", description = "GPS Service configuration integration")
public @interface GpsServiceConfiguration {

    @AttributeDefinition(name = "Get Token Url", description = "Endpoint to Get the token ", type = AttributeType.STRING)
    String getTokenUrl() default "";

    @AttributeDefinition(name = "Get Client Id", description = "Get Client ID", type = AttributeType.STRING)
    String getClientId() default "";

    @AttributeDefinition(name = "Get Client Secret", description = "Get Client Secret", type = AttributeType.STRING)
    String getClientSecret() default "";

    @AttributeDefinition(name = "Get Token Resource", description = "Get Token Resource", type = AttributeType.STRING)
    String getResource() default "";

    @AttributeDefinition(name = "Get GPS proxy URL", description = "GPS proxy URL to get requests", type = AttributeType.STRING)
    String getGpsProxyUrl() default "";
    
    @AttributeDefinition(name = "Get GPS Base endpoint", description = "GPS base endpooint to get requests", type = AttributeType.STRING)
    String getGpsBaseEndpoint() default "";

    @AttributeDefinition(name = "Get GPS subscription Key", description = "Get GPS subscription Key", type = AttributeType.STRING)
    String getOCPGpsSubKey() default "";
}
