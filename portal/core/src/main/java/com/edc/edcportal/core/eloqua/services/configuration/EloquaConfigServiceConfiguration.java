package com.edc.edcportal.core.eloqua.services.configuration;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

/**
 * OSGi configuration model. This model is used by an OSGi service to handle its
 * configuration.
 */
@ObjectClassDefinition(name = "MyEDC Eloqua Service Configuration", description = "MyEDC Configuration for Eloqua Service")
public @interface EloquaConfigServiceConfiguration {

    @AttributeDefinition(name = "Get Base Url", description = "Endpoint to Get Base Url ", type = AttributeType.STRING)
    String getUrlToGetBaseUrl() default "https://login.eloqua.com/id";
    
    @AttributeDefinition(name = "Client Id ", description = "Client Id as the password to get the token", type = AttributeType.STRING)
    String getClientId() default "";
    
    @AttributeDefinition(name = "Client Secret", description = "Client Secret as the password to get the token", type = AttributeType.STRING)
    String getClientSecret() default "";

    @AttributeDefinition(name = "Get Token Url", description = "Endpoint to Get the token ", type = AttributeType.STRING)
    String getTokenUrl() default "https://login.eloqua.com/auth/oauth2/token";
    
    @AttributeDefinition(name = "Eloqua Site Name", description = "The company name used to log in to Eloqua", type = AttributeType.STRING)
    String getSiteName() default "";
    
    @AttributeDefinition(name = "Eloqua UserName", description = "Eloqua username", type = AttributeType.STRING)
    String getUserName() default "";

    @AttributeDefinition(name = "Eloqua Password", description = "Eloqua password", type = AttributeType.STRING)
    String getPassword() default "";

    @AttributeDefinition(name = "My Edc Profile CDO Id", description = "Id for the MyEDC Profile CustomObjectData to be used ", type = AttributeType.STRING)
    String getMyEDCProfileCDOId() default "73";
    
    @AttributeDefinition(name = "Progerssive Profile CDO Id", description = "Id for the Progressive Profile CustomObjectData to be used ", type = AttributeType.STRING)
    String getProgressiveProfileCDOId() default "";
    
    @AttributeDefinition(name = "Document History CDO Id", description = "Id for the Document History CustomObjectData to be used ", type = AttributeType.STRING)
    String getDocumentHistoryCDOId() default "75";
    
    @AttributeDefinition(name = "Fallback endpoint", description = "Fallback endpoint if cannot retrieve ", type = AttributeType.STRING)
    String getEndPointFallBack() default "https://secure.p01.eloqua.com/API/REST/{version}/";


}