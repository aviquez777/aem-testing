package com.edc.edcportal.core.arkadin.services.configuration;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Arkadin Service Configuration", description = "Arkadin configuration for MyEDC integration")
public @interface ArkadinConfigServiceConfiguration {

    @AttributeDefinition(name = "Get API Url", description = "Arkadin Endpoint", type = AttributeType.STRING)
    String getAPIUrl() default "";
    
    @AttributeDefinition(name = "getRegistrationLASCmd value, for 'Regular calls'", description = "LASCmd", type = AttributeType.STRING)
    String getLASCmd();

    @AttributeDefinition(name = "getRegistrationLASCmd value, for 'registation calls'", description = "tRegistrationLASCmd", type = AttributeType.STRING)
    String getStudioShowRegistrationLASCmd();

    @AttributeDefinition(name = "Get API User Auth Code", description = "Get API User Auth Code", type = AttributeType.STRING)
    String getAPIUserAuthCode() default "";

    @AttributeDefinition(name = "Get User Credentials", description = "Get User Credentials", type = AttributeType.STRING)
    String getAPIUserCredentials() default "";

    @AttributeDefinition(name = "Get Show Base Url", description = "Get Show Base Url", type = AttributeType.STRING)
    String getShowBaseUrl() default "";
}
