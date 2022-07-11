package com.edc.edcweb.core.service;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "EDC Knowledge Service Configuration", description = "Knowledge service configuration for EDC integration")
public @interface KnowledgeServiceConfiguration {

	@AttributeDefinition(
            name = "Knowledge base Host URL",
            description = "Knowledge base host",
            type = AttributeType.STRING)
    String kbHost() default "https://ehhmvp3rnd.azurewebsites.net/qnamaker";

    @AttributeDefinition(
            name = "Knowledge base Service Route",
            description = "Knowledge base Service Route",
            type = AttributeType.STRING)
    String kbServiceRoute() default "/knowledgebases/cd8e3823-8f5b-4a7a-b82e-f7cd9d5f764d/generateAnswer";
    
    @AttributeDefinition(
            name = "Knowledge base Endpoint key",
            description = "Knowledge base Endpoint key",
            type = AttributeType.STRING)
    String kbEndPointKey() default "96139152-740f-4d59-ac86-522889d53fe5";
    
    
    //EHH MVP3: Server for getting all questions
    @AttributeDefinition(
            name = "EHH Static server's URL with service endpoint key",
            description = "EHH Static server  URL with service endpoint key",
            type = AttributeType.STRING)
    String ehhStaticSvrBaseURLWithKey() default "https://ehhmvp3rnd-asrbst2miae6xoe.search.windows.net/indexes/cd8e3823-8f5b-4a7a-b82e-f7cd9d5f764d/docs?";
    
    @AttributeDefinition(
            name = "'api-version' parameter used in EHH Static Server API",
            description = "'api-version' parameter used in EHH Static Server API",
            type = AttributeType.STRING)
    String ehhStaticSvrParamVersion() default "api-version=2019-05-06";
    
    @AttributeDefinition(
            name = "'api-key' parameter used in EHH Static Server API",
            description = "'api-key' parameter used in EHH Static Server API",
            type = AttributeType.STRING)
    String ehhStaticSvrParamAPIKey() default "api-key=4EB88F30806D0AF29351AD7C98D80DFE";
    
    @AttributeDefinition(
            name = "'top' parameter used in EHH Static Server API",
            description = "'top' parameter used in EHH Static Server API",
            type = AttributeType.STRING)
    String ehhStaticSvrParamTop() default "%24top=1000";
}
