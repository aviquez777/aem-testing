package com.edc.edcportal.core.ci.services.impl;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edc.edcportal.core.ci.services.CiConfigService;
import com.edc.edcportal.core.ci.services.configuration.CiConfigServiceConfiguration;

@Component(immediate = true, service = CiConfigService.class, configurationPid = "com.edc.portal.core.ci.services.configuration.impl.CiConfigServiceImpl")

@Designate(ocd = CiConfigServiceConfiguration.class)

public class CiConfigServiceImpl implements CiConfigService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CiConfigServiceImpl.class);

    private CiConfigServiceConfiguration config;

    @Activate
    @Modified
    protected void activate(CiConfigServiceConfiguration config) {
        this.config = config;
        LOGGER.debug("Activated CiConfigServiceConfiguration");
    }

    @Override
    public String getLandingSearchPageNode() {
        return config.getLandingSearchPageNode();
    }

    @Override
    public String getSearchResultsPageNode() {
        return config.getSearchResultsPageNode();
    }

    @Override
    public String getCompanyProfilePageNode() {
        return config.getCompanyProfilePageNode();
    }

    @Override
    public String getMapKeys() {
        return config.getMapKeys();
    }

    @Override
    public String getMapBaseUrl() {
        return config.getMapBaseUrl();
    }

    @Override
    public int getPageCount() {
        return config.getPageCount();
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
    public String getCIProxyUrl() {
        return config.getCIProxyUrl();
    }

    @Override
    public String getCIBaseEndpoint() {
        return config.getCIBaseEndpoint();
    }

    @Override
    public String getOCPCIsubscriptionKey() {
        return config.getOCPCIsubscriptionKey();
    }

}