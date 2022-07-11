package com.edc.edcweb.core.telp.service.configuration;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Telp Service Configuration", description = "Telp Service configuration integration")
public @interface TelpServiceConfiguration {

    @AttributeDefinition(name = "Get Token Url", description = "Endpoint to Get the token ", type = AttributeType.STRING)
    String getTokenUrl() default "";

    @AttributeDefinition(name = "Get Client Id", description = "Get Client ID", type = AttributeType.STRING)
    String getClientId() default "";

    @AttributeDefinition(name = "Get Client Secret", description = "Get Client Secret", type = AttributeType.STRING)
    String getClientSecret() default "";

    @AttributeDefinition(name = "Get Token Resource", description = "Get Token Resource", type = AttributeType.STRING)
    String getResource() default "";

    @AttributeDefinition(name = "Get Telp proxy URL", description = "Telp proxy URL to get requests", type = AttributeType.STRING)
    String getTelpProxyUrl() default "";
    
    @AttributeDefinition(name = "Get Telp Base endpoint", description = "Telp base endpooint to get requests", type = AttributeType.STRING)
    String getTelpBaseEndpoint() default "";

    @AttributeDefinition(name = "Get Telp subscription Key", description = "Get Telp subscription Key", type = AttributeType.STRING)
    String getOCPTelpsubscriptionKey() default "";
}
