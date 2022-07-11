package com.edc.edcportal.core.services.configuration;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

/**
 * OSGi configuration model. This model is used by an OSGi service to handle its
 * configuration.
 */
@ObjectClassDefinition(name = "MyEDC System Service Configuration", description = "MyEDC System Configuration Service")
public @interface MyEDCConfigSystemConfiguration {

    @AttributeDefinition(name = "Login Url", description = "Full or relative Login URL for redirect when session has expired ", type = AttributeType.STRING)
    String getLoginUrl() default "/myedc/auth/Login";

    @AttributeDefinition(name = "DTM URL", description = "DTM URL", type = AttributeType.STRING)
    String getDtmLibURL() default "";

    @AttributeDefinition(name = "One Trust Script (English EU)", description = "One Trurst's Full Javascript's URI for English EU banner", type = AttributeType.STRING)
    String oneTrustScriptUrlEuEn();

    @AttributeDefinition(name = "One Trust Script (French EU)", description = "One Trurst's Full Javascript's URI for French EU banner", type = AttributeType.STRING)
    String oneTrustScriptUrlEuFr();

    @AttributeDefinition(name = "One Trust Script (English Non SEU)", description = "One Trurst's Full Javascript's URI for English Non EU banner", type = AttributeType.STRING)
    String oneTrustScriptUrlNonEuEn();

    @AttributeDefinition(name = "One Trust Script (French Non EU)", description = "One Trurst's Full Javascript's URI for French Non EU banner", type = AttributeType.STRING)
    String oneTrustScriptUrlNonEuFr();

    @AttributeDefinition(name = "One Trust Script Source", description = "One Trust script URL for script loader", type = AttributeType.STRING)
    String oneTrustScriptSrc();

    @AttributeDefinition(name = "One Trust Domain Source", description = "One Trust domain script", type = AttributeType.STRING)
    String oneTrustDomainSrc();

    @AttributeDefinition(name = "Webinr Window time", description = "Webinr Window time (use a negative number to start early)", type = AttributeType.INTEGER)
    int webinarWindowTime() default -15;

    @AttributeDefinition(name = "AddressComplete Service", description = "AddressComplete Service: CandaPost or Loqate", type = AttributeType.STRING)
    String getAddressCompleteService() default "Loqate";

    @AttributeDefinition(name = "AddressComplete CSS URL", description = "AddressComplete CSS URL please include ?key=", type = AttributeType.STRING)
    String getAddressCompleteCSSUrl();

    @AttributeDefinition(name = "AddressComplete JavaScript URL", description = "AddressComplete CSS URL ", type = AttributeType.STRING)
    String getAddressCompleteJSUrl();

    @AttributeDefinition(name = "AddressComplete Key", description = " AddressComplete Key", type = AttributeType.STRING)
    String getAddressCompleteKey();

    @AttributeDefinition(name = "One Trust Script SRI Hash", description = "Get this from https://www.srihash.org/ when script changes", type = AttributeType.STRING, defaultValue = "")
    String oneTrustScriptIntegrityHash();
}