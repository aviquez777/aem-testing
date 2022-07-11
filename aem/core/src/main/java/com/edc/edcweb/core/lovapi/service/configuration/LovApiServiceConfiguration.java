package com.edc.edcweb.core.lovapi.service.configuration;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "LOV API Service Configuration", description = "LOV API Service configuration integration")
public @interface LovApiServiceConfiguration {
    
    @AttributeDefinition(name = "Get Token Url", description = "Endpoint to Get the token ", type = AttributeType.STRING)
    String getTokenUrl() default "";

    @AttributeDefinition(name = "Get Client Id", description = "Get Client ID", type = AttributeType.STRING)
    String getClientId() default "";

    @AttributeDefinition(name = "Get Client Secret", description = "Get Client Secret", type = AttributeType.STRING)
    String getClientSecret() default "";

    @AttributeDefinition(name = "Get Token Resource", description = "Get Token Resource", type = AttributeType.STRING)
    String getResource() default "";

    @AttributeDefinition(name = "Get LOV API subscription Key", description = "Get LOV API subscription Key", type = AttributeType.STRING)
    String getOCPLovApisubscriptionKey() default "";
    
    @AttributeDefinition(name = "Get LOV API proxy URL", description = "LOV API proxy URL to get requests", type = AttributeType.STRING)
    String getLovApiProxyUrl() default "";

    @AttributeDefinition(name = "Get LOV API Base endpoint", description = "LOV API base endpoint to get requests", type = AttributeType.STRING)
    String getBaseEndpoint();

    /** Configurations below are LOV dependent **/
    @AttributeDefinition(name = "Get Generic LOV API Base endpoint", description = "Generic LOV API base endpoint to get requests", type = AttributeType.STRING)
    String getGenericEndpoint();

    @AttributeDefinition(name = "Get FI LOV API Base endpoint", description = "Financial Institution LOV API base endpoint to get requests", type = AttributeType.STRING)
    String getFiEndpoint();

    @AttributeDefinition(name = "Get the MMG Form Endpoint", description = "Get the MMG Form Endpoint", type = AttributeType.STRING)
    String getMmgFormFiEndPoint();

    @AttributeDefinition(name = "Get the BCAP Form Endpoint", description = "Get the BCAP Form Endpoint", type = AttributeType.STRING)
    String getBcapFormFiEndPoint();

    @AttributeDefinition(name = "Get the TELP Form Endpoint", description = "Get the TElP Form Endpoint", type = AttributeType.STRING)
    String getTelpFormFiEndPont();
}
