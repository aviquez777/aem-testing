package com.edc.edcweb.core.onlinepayments.services.impl;

import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edc.edcweb.core.apicommon.service.ApiCommonConfigService;
import com.edc.edcweb.core.onlinepayments.services.DigitalPaymentsConfigService;
import com.edc.edcweb.core.onlinepayments.services.configuration.DigitalPaymentsServiceConfig;

@Component(immediate = true, service = DigitalPaymentsConfigService.class, configurationPid = "com.edc.edcweb.core.onlinepayments.services.impl.DPConfigServiceImpl")

@Designate(ocd = DigitalPaymentsServiceConfig.class)

public class DigitalPaymentsConfigServiceImpl implements DigitalPaymentsConfigService {

    private static final Logger log = LoggerFactory.getLogger(DigitalPaymentsConfigServiceImpl.class);

    @Reference
    private ApiCommonConfigService commonConfig;

    private DigitalPaymentsServiceConfig config;

    @Activate
    @Modified
    protected void activate(DigitalPaymentsServiceConfig config) {
        this.config = config;
        log.debug("Activated DigitalPaymentsServiceConfiguration");
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
    public String getOCPSubscriptionKey() {
        String subKey = config.getOCPSubscriptionKey();
        if (StringUtils.isBlank(subKey)) {
            subKey = commonConfig.getOCPSubscriptionKey();
        }
        return subKey;
    }

    // Digital Payments Specific values
    @Override
    public String getDPResource() {
        return config.getDPResource();
    }

    @Override
    public String getDPProxyUrl() {
        return config.getDPProxyUrl();
    }

    @Override
    public String getDPBaseEndpoint() {
        return config.getDPBaseEndpoint();
    }

}
