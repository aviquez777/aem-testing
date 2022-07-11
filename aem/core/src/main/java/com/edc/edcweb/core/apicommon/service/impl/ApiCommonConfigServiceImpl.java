package com.edc.edcweb.core.apicommon.service.impl;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edc.edcweb.core.apicommon.service.ApiCommonConfigService;
import com.edc.edcweb.core.apicommon.service.configuration.ApiCommonServiceConfiguration;

/**
 * 
 * Centralized API Centralized Configuration Service Note If any value is
 * encrypted, AEM will decrypt it automatically
 *
 */
@Component(immediate = true, service = ApiCommonConfigService.class, configurationPid = "com.edc.edcweb.core.apicommon.service.impl.ApiCommonConfigServiceImpl")
@Designate(ocd = ApiCommonServiceConfiguration.class)
public class ApiCommonConfigServiceImpl implements ApiCommonConfigService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiCommonConfigServiceImpl.class);

    private ApiCommonServiceConfiguration config;

    @Activate
    @Modified
    protected void activate(ApiCommonServiceConfiguration config) {
        this.config = config;
        LOGGER.debug("Activated ApiCommonServiceConfiguration");
    }

    @Override
    public String getTokenUrl() {
        return config.getTokenUrl();
    }

    @Override
    public String getClientId() {
        return config.getClientId();
    }

    @Override
    public String getClientSecret() {
        return config.getClientSecret();
    }

    @Override
    public String getProxyUrl() {
        return config.getProxyUrl();
    }

    @Override
    public String getOCPSubscriptionKey() {
        return config.getOCPSubscriptionKey();
    }
}
