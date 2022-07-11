package com.edc.edcportal.core.appauth.services.impl;

import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edc.edcportal.core.apicommon.service.ApiCommonConfigService;
import com.edc.edcportal.core.appauth.services.AppAuthConfigService;
import com.edc.edcportal.core.appauth.services.configuration.AppAuthConfigServiceConfiguration;

@Component(immediate = true, service = AppAuthConfigService.class, configurationPid = "com.edc.portal.core.appauth.services.configuration.impl.AppAuthConfigServiceImpl")

@Designate(ocd = AppAuthConfigServiceConfiguration.class)

public class AppAuthConfigServiceImpl implements AppAuthConfigService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppAuthConfigServiceImpl.class);

    private AppAuthConfigServiceConfiguration config;

    @Reference
    private ApiCommonConfigService commonConfig;

    @Activate
    @Modified
    protected void activate(AppAuthConfigServiceConfiguration config) {
        this.config = config;
        LOGGER.debug("Activated AppAuthConfigService");
    }

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
    public String getOCPAppAuthsubscriptionKey() {
        String subKey = config.getOCPAppAuthsubscriptionKey();
        if (StringUtils.isBlank(subKey)) {
            subKey = commonConfig.getOCPSubscriptionKey();
        }
        return subKey;
    }

    @Override
    public String getAppAuthProxyUrl() {
        String proxy = config.getAppAuthProxyUrl();
        if (StringUtils.isBlank(proxy)) {
            proxy = commonConfig.getProxyUrl();
        }  
        return proxy;
    }

    @Override
    public String getResource() {
        return config.getResource();
    }


    @Override
    public String getAppAuthBaseEndpoint() {
        return config.getAppAuthBaseEndpoint();
    }

}