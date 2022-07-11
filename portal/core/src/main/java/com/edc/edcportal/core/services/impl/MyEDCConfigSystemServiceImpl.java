package com.edc.edcportal.core.services.impl;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edc.edcportal.core.services.MyEDCConfigSystemService;
import com.edc.edcportal.core.services.configuration.MyEDCConfigSystemConfiguration;

@Component(immediate = true, service = MyEDCConfigSystemService.class, configurationPid = "com.edc.portal.core.services.configuration.impl.MyEDCConfigSystemConfig")

@Designate(ocd = MyEDCConfigSystemConfiguration.class)
public class MyEDCConfigSystemServiceImpl implements MyEDCConfigSystemService {

    private static final Logger logger = LoggerFactory.getLogger(MyEDCConfigSystemServiceImpl.class);

    private MyEDCConfigSystemConfiguration config;

    @Activate
    @Modified
    protected void activate(MyEDCConfigSystemConfiguration config) {
        this.config = config;
        logger.debug("Activated MyEDCConfigSystemService");
    }

    @Override
    public String getLoginUrl() {
        return config.getLoginUrl();
    }

    @Override
    public String getDtmLibURL() {
        return config.getDtmLibURL();
    }

    @Override
    public String getOneTrustScriptUrlEuEn() {
        return config.oneTrustScriptUrlEuEn();
    }

    @Override
    public String getOneTrustScriptUrlEuFr() {
        return config.oneTrustScriptUrlEuFr();
    }

    @Override
    public String getOneTrustScriptUrlNonEuEn() {
        return config.oneTrustScriptUrlNonEuEn();
    }

    @Override
    public String getOneTrustScriptUrlNonEuFr() {
        return config.oneTrustScriptUrlNonEuFr();
    }

    @Override
    public String getOneTrustScriptSrc() {
        return config.oneTrustScriptSrc();
    }

    @Override
    public String getOneTrustScriptIntegrityHash() {
        return config.oneTrustScriptIntegrityHash();
    }

    @Override
    public String getOneTrustDomainSrc() {
        return config.oneTrustDomainSrc();
    }

    @Override
    public int getWebinarsWindowTime() {
        return config.webinarWindowTime();
    }

    @Override
    public String getAddressCompleteService() {
        return config.getAddressCompleteService();
    }

    @Override
    public String getAddressCompleteCSSUrl() {
        return config.getAddressCompleteCSSUrl();
    }

    @Override
    public String getAddressCompleteJSUrl() {
        return config.getAddressCompleteJSUrl();
    }

    @Override
    public String getAddressCompleteKey() {
        return config.getAddressCompleteKey();
    }
}
