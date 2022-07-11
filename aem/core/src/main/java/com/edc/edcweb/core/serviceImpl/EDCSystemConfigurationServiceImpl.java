package com.edc.edcweb.core.serviceImpl;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.Designate;

import com.edc.edcweb.core.service.EDCSystemConfiguration;
import com.edc.edcweb.core.service.EDCSystemConfigurationService;

@Component(immediate = true, service = EDCSystemConfigurationService.class, configurationPid = "com.edc.edcweb.core.serviceImpl.EDCSystemConfigurationServiceImpl")

@Designate(ocd = EDCSystemConfiguration.class)
public class EDCSystemConfigurationServiceImpl implements EDCSystemConfigurationService {

    private EDCSystemConfiguration edcSystemConfiguration;

    private String siteDomainName;

    private String liveChatDomainName;

    private String dtmLibURL;

    private String googleSiteVerification;

    private String msValidate;

    private String enableAuthor;

    private String oneTrustScriptEnEu;

    private String oneTrustScriptFrEu;

    private String oneTrustScriptEnNonEu;

    private String oneTrustScriptFrNonEu;

    private String oneTrustScriptSrc;

    private String oneTrustScriptIntegrityHash;

    private String oneTrustDomainSrc;

    private int webinarWindowTime;

    private String[] witWebinarLOV;

    private String[] bcapIncDivFieldNames;

    private String[] bcapIncDivFieldLOV;

    private String[] logoutUrls;

    private String emailFrom;
    
    private String autoCompleteService;

    private String autoCompleteCSSUrl;

    private String autoCompleteJSUrl;

    private String autoCompleteKey;

    @Override
    public String getSiteDomainName() {
        return this.siteDomainName;
    }

    @Override
    public String getLiveChatDomainName() {
        return this.liveChatDomainName;
    }

    @Override
    public String getDtmLibURL() {
        return this.dtmLibURL;
    }

    @Override
    public String getGoogleSiteVerification() {
        return this.googleSiteVerification;
    }

    @Override
    public String getMsValidate() {
        return this.msValidate;
    }

    @Override
    public String getEnableAuthor() {
        return this.enableAuthor;
    }

    @Override
    public String getOneTrustScriptUrlEuEn() {
        return this.oneTrustScriptEnEu;
    }

    @Override
    public String getOneTrustScriptUrlEuFr() {
        return this.oneTrustScriptFrEu;
    }

    @Override
    public String getOneTrustScriptUrlNonEuEn() {
        return this.oneTrustScriptEnNonEu;
    }

    @Override
    public String getOneTrustScriptUrlNonEuFr() {
        return this.oneTrustScriptFrNonEu;
    }

    @Override
    public String getOneTrustScriptSrc() {
        return this.oneTrustScriptSrc;
    }

    @Override
    public String getOneTrustScriptIntegrityHash() {
        return this.oneTrustScriptIntegrityHash;
    }

    @Override
    public String getOneTrustDomainSrc() {
        return this.oneTrustDomainSrc;
    }

    @Override
    public int getWebinarsWindowTime() {
        return this.webinarWindowTime;
    }

    @Override
    public String[] getWitWebinarLOV() {
        return this.witWebinarLOV;
    }

    @Override
    public String[] getBcapIncDivFieldNames() {
        return bcapIncDivFieldNames;
    }

    @Override
    public String[] getBcapIncDivFieldLOV() {
        return bcapIncDivFieldLOV;
    }

    @Override
    public String[] getLogoutUrls() {
        return logoutUrls;
    }

    @Override
    public String getEmailFrom() {
        return emailFrom;
    }

    @Override
    public String getAddressCompleteService() {
        return autoCompleteService;
    }

    @Override
    public String getAddressCompleteCSSUrl() {
        return autoCompleteCSSUrl;
    }

    @Override
    public String getAddressCompleteJSUrl() {
        return autoCompleteJSUrl;
    }

    @Override
    public String getAddressCompleteKey() {
        return autoCompleteKey;
    }

    @Activate
    @Modified
    public void activate(EDCSystemConfiguration edcSystemConfiguration) {
        this.edcSystemConfiguration = edcSystemConfiguration;
        this.siteDomainName = this.edcSystemConfiguration.siteDomainName();
        this.liveChatDomainName = this.edcSystemConfiguration.liveChatDomainName();
        this.dtmLibURL = this.edcSystemConfiguration.dtmLibURL();
        this.googleSiteVerification = this.edcSystemConfiguration.googleSiteVerification();
        this.msValidate = this.edcSystemConfiguration.msValidate();
        this.enableAuthor = this.edcSystemConfiguration.enableAuthor();
        this.oneTrustScriptEnEu = this.edcSystemConfiguration.oneTrustScriptUrlEuEn();
        this.oneTrustScriptFrEu = this.edcSystemConfiguration.oneTrustScriptUrlEuFr();
        this.oneTrustScriptEnNonEu = this.edcSystemConfiguration.oneTrustScriptUrlNonEuEn();
        this.oneTrustScriptFrNonEu = this.edcSystemConfiguration.oneTrustScriptUrlNonEuFr();
        this.oneTrustDomainSrc = this.edcSystemConfiguration.oneTrustDomainSrc();
        this.oneTrustScriptSrc = this.edcSystemConfiguration.oneTrustScriptSrc();
        this.oneTrustScriptIntegrityHash =  this.edcSystemConfiguration.oneTrustScriptIntegrityHash();
        this.webinarWindowTime = this.edcSystemConfiguration.webinarWindowTime();
        this.witWebinarLOV = this.edcSystemConfiguration.witWebinarLOV();
        this.bcapIncDivFieldNames = this.edcSystemConfiguration.bcapIncDivFieldNames();
        this.bcapIncDivFieldLOV = this.edcSystemConfiguration.bcapIncDivFieldLOV();
        this.logoutUrls = this.edcSystemConfiguration.logoutUrls();
        this.emailFrom = this.edcSystemConfiguration.emailFrom();        
        this.autoCompleteService = this.edcSystemConfiguration.getAddressCompleteService();
        this.autoCompleteCSSUrl = this.edcSystemConfiguration.getAddressCompleteCSSUrl();
        this.autoCompleteJSUrl = this.edcSystemConfiguration.getAddressCompleteJSUrl();   
        this.autoCompleteKey = this.edcSystemConfiguration.getAddressCompleteKey();
    }

}