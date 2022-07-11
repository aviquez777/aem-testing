package com.edc.edcweb.core.apicommon.service.configuration;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "API Common Service Configuration", description = "API Common Service Configuration")
public @interface ApiCommonServiceConfiguration {

    @AttributeDefinition(name = "Get Token Url", description = "Endpoint to Get the token ", type = AttributeType.STRING)
    String getTokenUrl() default "";

    @AttributeDefinition(name = "Get Client Id", description = "Get Client ID", type = AttributeType.STRING)
    String getClientId() default "";

    @AttributeDefinition(name = "Get Client Secret", description = "Get Client Secret", type = AttributeType.STRING)
    String getClientSecret() default "";

    @AttributeDefinition(name = "Get ProxyUrl", description = "Get ProxyUrl", type = AttributeType.STRING)
    String getProxyUrl() default "";

    @AttributeDefinition(name = "Get OCPSubscriptionKey", description = "Get OCPSubscriptionKey", type = AttributeType.STRING)
    String getOCPSubscriptionKey();
}