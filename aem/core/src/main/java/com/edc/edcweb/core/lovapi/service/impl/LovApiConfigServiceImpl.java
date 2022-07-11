package com.edc.edcweb.core.lovapi.service.impl;

import com.edc.edcweb.core.apicommon.service.ApiCommonConfigService;
import com.edc.edcweb.core.lovapi.service.LovApiConfigService;
import com.edc.edcweb.core.lovapi.service.configuration.LovApiServiceConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



@Component(immediate = true, service = LovApiConfigService.class, configurationPid = "com.edc.edcweb.core.apim.services.configuration.impl.LovApiConfigServiceImpl")

@Designate(ocd = LovApiServiceConfiguration.class)

public class LovApiConfigServiceImpl implements LovApiConfigService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LovApiConfigServiceImpl.class);

    private LovApiServiceConfiguration config;

    @Reference
    private ApiCommonConfigService commonConfig;

    @Activate
    @Modified
    protected void activate(LovApiServiceConfiguration config) {
        this.config = config;
        LOGGER.debug("Activated LovApiConfigService");
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
    public String getOCPLovApisubscriptionKey() {
        String subKey = config.getOCPLovApisubscriptionKey();
        if (StringUtils.isBlank(subKey)) {
            subKey = commonConfig.getOCPSubscriptionKey();
        }
        return subKey;
    }

    // LOV Specific values
    @Override
    public String getResource() {
        return config.getResource();
    }

    @Override
    public String getLovApiProxyUrl() {
        String proxy = config.getLovApiProxyUrl();
        if (StringUtils.isBlank(proxy)) {
            proxy = commonConfig.getProxyUrl();
        }
        return proxy;
    }

    @Override
    public String getBaseEndpoint() {
        return config.getBaseEndpoint();
    }

    /** Configurations below are LOV dependent **/
    @Override
    public String getGenericEndpoint() {
        return config.getGenericEndpoint();
    }

    @Override
    public String getFiEndpoint() {
        return config.getFiEndpoint();
    }

    @Override
    public String getMmgFormFiEndPoint() {
        return config.getMmgFormFiEndPoint();
    }

    @Override
    public String getBcapFormFiEndPoint() {
        return config.getBcapFormFiEndPoint();
    }

    @Override
    public String getTelpFormFiEndPont() {
        return config.getTelpFormFiEndPont();
    }
}
