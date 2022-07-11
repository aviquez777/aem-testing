package com.edc.edcweb.core.apim.services;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "EDC APIM Configuration", description = "EDC APIM configuration for EDC integration")
public @interface APIMDAOServiceConfiguration {

    @AttributeDefinition(
            name = "APIM Access Token URL",
            description = "URL for getting APIM Access Token",
            type = AttributeType.STRING)
    String accessTokenURL() default "https://login.microsoftonline.com/d4167d58-713e-4e8f-8ba2-869b5491fb80/oauth2/token";

    @AttributeDefinition(
            name = "Client ID",
            description = "APIM Client ID")
    String clientId()  default "f6dfe945-7ef1-4365-b53b-90eff2172aaa";

    @AttributeDefinition(
            name = "Client Secret",
            description = "APIM Client Secret",
            type = AttributeType.STRING)
    String clientSecret() default "+0S2T18mR?eqFLqc_r=aZaA.gNvOiJ:e";

    @AttributeDefinition(
            name = "Grand Type",
            description = "Grand Type for APIM Authentication",
            type = AttributeType.STRING)
    String grandType() default "client_credentials";
    
    @AttributeDefinition(
            name = "Resource",
            description = "Resource Parameter Value",
            type = AttributeType.STRING)
    String resource() default "https://EDCanada.onmicrosoft.com/999f87c5-f2ea-4e52-b185-33a8a3b4eacb";

    @AttributeDefinition(
            name = "APIM Proxy URL",
            description = "APIM Proxy URL",
            type = AttributeType.STRING)
    String proxyURL() default "https://proxyqac.api.edc.ca/";
    
    @AttributeDefinition(
            name = "Ocp Apim Subscription Key",
            description = "Ocp Apim Subscription Key",
            type = AttributeType.STRING)
    String subscriptionKey() default "197de8852a0f4df586db8836932dbd01";
    
    @AttributeDefinition(
            name = "Supplier Filter Service Point",
            description = "APIM service point for supplier filter, , like 'api/inlist/v1/suppliersOfflineClient/'",
            type = AttributeType.STRING)
    String servicePointInListSupplierFilter() default "inlist/dev/api/inlist/v1/suppliersOfflineClient/";
    
    @AttributeDefinition(
            name = "Supplier Profile Service Point",
            description = "APIM service point for supplier profile, like 'api/inlist/v1/suppliers/'",
            type = AttributeType.STRING)
    String servicePointInListSupplierProfile() default "inlist/dev/api/inlist/v1/suppliers/";
}
