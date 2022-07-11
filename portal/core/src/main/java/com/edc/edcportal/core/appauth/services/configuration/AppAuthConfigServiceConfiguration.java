package com.edc.edcportal.core.appauth.services.configuration;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "AppAuth Service Configuration", description = "AppAuth configuration for MyEDC integration")
public @interface AppAuthConfigServiceConfiguration {

    @AttributeDefinition(name = "Get Token Url", description = "Endpoint to Get the token ", type = AttributeType.STRING)
    String getTokenUrl() default "";

    @AttributeDefinition(name = "Get Client Id", description = "Get Client ID", type = AttributeType.STRING)
    String getClientId() default "";

    @AttributeDefinition(name = "Get Client Secret", description = "Get Client Secret", type = AttributeType.STRING)
    String getClientSecret() default "";

    @AttributeDefinition(name = "Get Token Resource", description = "Get Token Resource", type = AttributeType.STRING)
    String getResource() default "";

    @AttributeDefinition(name = "Get AppAuth proxy URL", description = "AppAuth proxy URL to get requests", type = AttributeType.STRING)
    String getAppAuthProxyUrl() default "";
    
    @AttributeDefinition(name = "Get AppAuth Base endpoint", description = "AppAuth base endpooint to get requests", type = AttributeType.STRING)
    String getAppAuthBaseEndpoint() default "";

    @AttributeDefinition(name = "Get AppAuth subscription Key", description = "Get AppAuth subscription Key", type = AttributeType.STRING)
    String getOCPAppAuthsubscriptionKey() default "";

}
