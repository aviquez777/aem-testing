package com.edc.edcweb.core.onlinepayments.services.configuration;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "DigitalPayments API Configuration", description = "DigitalPayments API Configuration")
public @interface DigitalPaymentsServiceConfig {

    @AttributeDefinition(name = "Get Token Url", description = "Endpoint to Get the token ", type = AttributeType.STRING)
    String getTokenUrl() default "";

    @AttributeDefinition(name = "Get Client Id", description = "Get Client ID", type = AttributeType.STRING)
    String getClientId() default "";

    @AttributeDefinition(name = "Get Client Secret", description = "Get Client Secret", type = AttributeType.STRING)
    String getClientSecret() default "";

    @AttributeDefinition(name = "Get subscription Key", description = "Get subscription Key", type = AttributeType.STRING)
    String getOCPSubscriptionKey() default "";

    @AttributeDefinition(name = "Get Token Resource", description = "Get Token Resource", type = AttributeType.STRING)
    String getDPResource() default "";

    @AttributeDefinition(name = "Get proxy URL", description = "Get proxy URL to get requests", type = AttributeType.STRING)
    String getDPProxyUrl() default "";

    @AttributeDefinition(name = "Get Base endpoint", description = "Get base endpooint to get requests", type = AttributeType.STRING)
    String getDPBaseEndpoint() default "";
}
