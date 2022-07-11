package com.edc.edcportal.core.arkadin.services.impl;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edc.edcportal.core.arkadin.services.ArkadinConfigService;
import com.edc.edcportal.core.arkadin.services.configuration.ArkadinConfigServiceConfiguration;

@Component(immediate = true, service = ArkadinConfigService.class, configurationPid = "com.edc.portal.core.arkadin.services.configuration.impl.ArkadinConfigServiceImpl")

@Designate(ocd = ArkadinConfigServiceConfiguration.class)

public class ArkadinConfigServiceImpl implements ArkadinConfigService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ArkadinConfigServiceImpl.class);

    private ArkadinConfigServiceConfiguration config;

    @Activate
    @Modified
    protected void activate(ArkadinConfigServiceConfiguration config) {
        this.config = config;
        LOGGER.debug("Activated ArkadinConfigService");
    }

    @Override
    public String getAPIUrl() {
        return config.getAPIUrl();
    }

    @Override
    public String getLASCmd() {
        return config.getLASCmd();
    }

    @Override
    public String getStudioShowRegistrationLASCmd() {
        return config.getStudioShowRegistrationLASCmd();
    }

    @Override
    public String getAPIUserAuthCode() {
        return config.getAPIUserAuthCode();
    }

    @Override
    public String getAPIUserCredentials() {
        return config.getAPIUserCredentials();
    }

    @Override
    public String getShowBaseUrl() {
        return config.getShowBaseUrl();
    }
}