package com.edc.edcweb.core.telp.service.impl;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edc.edcweb.core.telp.service.TelpService;
import com.edc.edcweb.core.telp.service.configuration.TelpServiceConfiguration;

@Component(immediate = true, service = TelpService.class, configurationPid = "com.edc.edcweb.core.apim.services.configuration.impl.TelpConfigServiceImpl")

@Designate(ocd = TelpServiceConfiguration.class)

public class TelpServiceImpl implements TelpService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TelpServiceImpl.class);

    private TelpServiceConfiguration config;

    @Activate
    @Modified
    protected void activate(TelpServiceConfiguration config) {
        this.config = config;
        LOGGER.debug("Activated TelpConfigService");
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
    public String getResource() {
        return config.getResource();
    }

    @Override
    public String getTelpProxyUrl() {
        return config.getTelpProxyUrl();
    }

    @Override
    public String getTelpBaseEndpoint() {
        return config.getTelpBaseEndpoint();
    }

    @Override
    public String getOCPTelpsubscriptionKey() {
        return config.getOCPTelpsubscriptionKey();
    }
}
