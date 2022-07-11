package com.edc.edcportal.core.businessconnections.services.impl;

import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edc.edcportal.core.apicommon.service.ApiCommonConfigService;
import com.edc.edcportal.core.businessconnections.services.BCConfigService;
import com.edc.edcportal.core.businessconnections.services.configuration.BCConfigServiceConfiguration;

@Component(immediate = true, service = BCConfigService.class, configurationPid = "com.edc.edcportal.core.businessconnections.services.impl.BCConfigServiceImpl")
@Designate(ocd = BCConfigServiceConfiguration.class)
public class BCConfigServiceImpl implements BCConfigService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BCConfigServiceImpl.class);

    private BCConfigServiceConfiguration config;

    @Reference
    private ApiCommonConfigService commonConfig;

    @Activate
    @Modified
    protected void activate(BCConfigServiceConfiguration config) {
        this.config = config;
        LOGGER.debug("Activated BCConfigServiceConfiguration");
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
    public String getOCPBCTokensubscriptionKey() {
        String subKey = config.getOCPBCTokensubscriptionKey();
        if (StringUtils.isBlank(subKey)) {
            subKey = commonConfig.getOCPSubscriptionKey();
        }
        return subKey;
    }

    @Override
    public String getBCTokenProxyUrl() {
        String proxy = config.getBCTokenProxyUrl();
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
    public String getBCTokenBaseEndpoint() {
        return config.getBCTokenBaseEndpoint();
    }

    @Override
    public String getSPAEndpoint() {
        return config.getSPAEndpoint();
    }
}