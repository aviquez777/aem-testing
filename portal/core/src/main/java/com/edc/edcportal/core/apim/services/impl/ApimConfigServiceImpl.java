package com.edc.edcportal.core.apim.services.impl;

import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edc.edcportal.core.apim.services.ApimConfigService;
import com.edc.edcportal.core.apim.services.configuration.ApimConfigServiceConfiguration;
import com.edc.edcportal.core.apicommon.service.ApiCommonConfigService;

@Component(immediate = true, service = ApimConfigService.class, configurationPid = "com.edc.portal.core.apim.services.configuration.impl.ApimConfigServiceImpl")

@Designate(ocd = ApimConfigServiceConfiguration.class)

public class ApimConfigServiceImpl implements ApimConfigService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApimConfigServiceImpl.class);

    @Reference
    private ApiCommonConfigService commonConfig;

    private ApimConfigServiceConfiguration config;

    @Activate
    @Modified
    protected void activate(ApimConfigServiceConfiguration config) {
        this.config = config;
        LOGGER.debug("Activated ApimConfigService");
    }

    // Common Values
    @Override
    public String getTokenUrl() {
        String tokenUrl = config.getTokenUrl();
        if (StringUtils.isBlank(tokenUrl)) {
            tokenUrl = commonConfig.getTokenUrl();
        }
        return tokenUrl;
    }

    @Override
    public String getClientId() {
        String clientId = config.getClientId();
        if (StringUtils.isBlank(clientId)) {
            clientId = commonConfig.getClientId();
        }
        return clientId;
    }

    @Override
    public String getClientSecret() {
        String clientSecret = config.getClientSecret();
        if (StringUtils.isBlank(clientSecret)) {
            clientSecret = commonConfig.getClientSecret();
        }
        return clientSecret;
    }

    @Override
    public String getOCPAPIMsubscriptionKey() {
        String subKey = config.getOCPAPIMsubscriptionKey();
        if (StringUtils.isBlank(subKey)) {
            subKey = commonConfig.getOCPSubscriptionKey();
        }
        return subKey;
    }

    @Override
    public String getAPIMProxyUrl() {
        String proxy = config.getAPIMProxyUrl();
        if (StringUtils.isBlank(proxy)) {
            proxy = commonConfig.getProxyUrl();
        }
        return proxy;
    }

    // APIM Specific values
    @Override
    public String getResource() {
        return config.getResource();
    }

    @Override
    public String getAPIMBaseEndpoint() {
        return config.getAPIMBaseEndpoint();
    }

}