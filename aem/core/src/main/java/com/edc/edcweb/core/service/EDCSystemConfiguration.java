package com.edc.edcweb.core.service;

/**
 * Interface EDCSystemConfigurationService.
 * @author gavik.wadhwa
 *
 */

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "EDC System Configuration", description = "EDC System Configuration")
public @interface EDCSystemConfiguration {

    @AttributeDefinition(name = "Site Domain", description = "Site Domain Name", type = AttributeType.STRING, defaultValue = "wwwdev.edc.ca")
    String siteDomainName();

    @AttributeDefinition(name = "Live Chat Domain", description = "Live Chat Domain Name", type = AttributeType.STRING, defaultValue = "wwwppr.edc.ca")
    String liveChatDomainName();

    @AttributeDefinition(name = "DTM URL", description = "DTM URL", type = AttributeType.STRING, defaultValue = "")
    String dtmLibURL();

    @AttributeDefinition(name = "Google Site Verification", description = "Google Site Verification", type = AttributeType.STRING, defaultValue = "")
    String googleSiteVerification();

    @AttributeDefinition(name = "MS Validate", description = "MS Validate", type = AttributeType.STRING, defaultValue = "")
    String msValidate();

    @AttributeDefinition(name = "Enable Author", description = "If tracking scripts should load on author side", type = AttributeType.STRING, defaultValue = "false")
    String enableAuthor();

    @AttributeDefinition(name = "One Trust Script (English EU)", description = "One Trurst's Full Javascript's URI for English EU banner", type = AttributeType.STRING, defaultValue = "")
    String oneTrustScriptUrlEuEn();

    @AttributeDefinition(name = "One Trust Script (French EU)", description = "One Trurst's Full Javascript's URI for French EU banner", type = AttributeType.STRING, defaultValue = "")
    String oneTrustScriptUrlEuFr();

    @AttributeDefinition(name = "One Trust Script (English Non SEU)", description = "One Trurst's Full Javascript's URI for English Non EU banner", type = AttributeType.STRING, defaultValue = "")
    String oneTrustScriptUrlNonEuEn();

    @AttributeDefinition(name = "One Trust Script (French Non EU)", description = "One Trurst's Full Javascript's URI for French Non EU banner", type = AttributeType.STRING, defaultValue = "")
    String oneTrustScriptUrlNonEuFr();

    @AttributeDefinition(name = "One Trust Script Source", description = "One Trust script URL for script loader", type = AttributeType.STRING, defaultValue = "")
    String oneTrustScriptSrc();

    @AttributeDefinition(name = "One Trust Script SRI Hash", description = "Get this from https://www.srihash.org/ when script changes", type = AttributeType.STRING, defaultValue = "")
    String oneTrustScriptIntegrityHash();

    @AttributeDefinition(name = "One Trust Domain Source", description = "One Trust domain script", type = AttributeType.STRING, defaultValue = "")
    String oneTrustDomainSrc();

    @AttributeDefinition(name = "Webinar Window time", description = "Webinar Window time (use a negative number to start early)", type = AttributeType.INTEGER, defaultValue = "-15")
    int webinarWindowTime();

    @AttributeDefinition(name = "WIT Webinar List Of Values", description = "WIT Webinar List Of Value", type = AttributeType.STRING, defaultValue = "")
    String[] witWebinarLOV();

    @AttributeDefinition(name = "BCAP Form - Inclusion and Diversity Field Name Values", description = "BCAP Form - Inclusion and Diversity Field Name Values. Each Value pair should be separated by '::'", type = AttributeType.STRING, defaultValue = "")
    String[] bcapIncDivFieldNames();

    @AttributeDefinition(name = "BCAP Form - Inclusion and DiversityValues", description = "BCAP Form - Inclusion and Diversity List Of Values. Each Value pair should be separated by '::'", type = AttributeType.STRING, defaultValue = "")
    String[] bcapIncDivFieldLOV();

    @AttributeDefinition(name = "Logout urls", description = "List of the logout urls first English then French'", type = AttributeType.STRING, defaultValue = "")
    String[] logoutUrls();

    @AttributeDefinition(name = "Email From", description = "Email server will send email only if sent from ths address", type = AttributeType.STRING, defaultValue = "")
    String emailFrom();

    @AttributeDefinition(name = "AddressComplete Service", description = "AddressComplete Service: CandaPost or Loqate", type = AttributeType.STRING)
    String getAddressCompleteService() default "Loqate";

    @AttributeDefinition(name = "AddressComplete CSS URL", description = "AddressComplete CSS URL please include ?key=", type = AttributeType.STRING)
    String getAddressCompleteCSSUrl();

    @AttributeDefinition(name = "AddressComplete JavaScript URL", description = "AddressComplete CSS URL ", type = AttributeType.STRING)
    String getAddressCompleteJSUrl();

    @AttributeDefinition(name = "AddressComplete Key", description = " AddressComplete Key", type = AttributeType.STRING)
    String getAddressCompleteKey();
}