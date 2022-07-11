package com.edc.edcweb.core.gps.service.impl;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edc.edcweb.core.gps.service.GpsService;
import com.edc.edcweb.core.gps.service.configuration.GpsServiceConfiguration;



@Component(immediate = true, service = GpsService.class, configurationPid = "com.edc.edcweb.core.gps.services.configuration.impl.GpsConfigServiceImpl")

@Designate(ocd = GpsServiceConfiguration.class)

public class GpsServiceImpl implements GpsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GpsServiceImpl.class);

    private GpsServiceConfiguration config;

    @Activate
    @Modified
    protected void activate(GpsServiceConfiguration config) {
        this.config = config;
        LOGGER.debug("Activated GpsConfigService");
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
    public String getGpsProxyUrl() {
        return config.getGpsProxyUrl();
    }

    @Override
    public String getGpsBaseEndpoint() {
        return config.getGpsBaseEndpoint();
    }

    @Override
    public String getOCPGpsSubKey() {
        return config.getOCPGpsSubKey();
    }
}
