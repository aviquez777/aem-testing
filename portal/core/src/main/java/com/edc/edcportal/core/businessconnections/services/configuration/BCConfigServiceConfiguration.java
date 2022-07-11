package com.edc.edcportal.core.businessconnections.services.configuration;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Business Connections Service Configuration", description = "Business Connections Service Configuration")
public @interface BCConfigServiceConfiguration {

    @AttributeDefinition(name = "Get Token Url", description = "Endpoint to Get the token ", type = AttributeType.STRING)
    String getTokenUrl() default "";

    @AttributeDefinition(name = "Get Client Id", description = "Get Client ID", type = AttributeType.STRING)
    String getClientId() default "";

    @AttributeDefinition(name = "Get Client Secret", description = "Get Client Secret", type = AttributeType.STRING)
    String getClientSecret() default "";

    @AttributeDefinition(name = "Get Token Resource", description = "Get Token Resource", type = AttributeType.STRING)
    String getResource() default "";

    @AttributeDefinition(name = "Get BCToken proxy URL", description = "BCToken proxy URL to get requests", type = AttributeType.STRING)
    String getBCTokenProxyUrl() default "";
    
    @AttributeDefinition(name = "Get BCToken Base endpoint", description = "BCToken base endpooint to get requests", type = AttributeType.STRING)
    String getBCTokenBaseEndpoint() default "";

    @AttributeDefinition(name = "Get BCToken subscription Key", description = "Get BCToken subscription Key", type = AttributeType.STRING)
    String getOCPBCTokensubscriptionKey() default "";

    @AttributeDefinition(name = "Get Default SPA Enpoint", description = "Get Default SPA Enpoint", type = AttributeType.STRING)
    String getSPAEndpoint()  default "";
}