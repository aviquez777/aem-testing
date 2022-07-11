package com.edc.edcweb.core.onlinepayments.services.impl;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edc.edcweb.core.onlinepayments.services.MonerisConfigurationService;
import com.edc.edcweb.core.onlinepayments.services.configuration.MonerisServiceConfiguration;

@Component(immediate = true, service = MonerisConfigurationService.class, configurationPid = "com.edc.edcweb.core.onlinepayments.services.impl.MonerisServiceConfigurationImpl")
@Designate(ocd = MonerisServiceConfiguration.class)
public class MonerisConfigurationServiceImpl implements MonerisConfigurationService {

    private static final Logger log = LoggerFactory.getLogger(MonerisConfigurationServiceImpl.class);

    private MonerisServiceConfiguration config;

    @Activate
    @Modified
    protected void activate(MonerisServiceConfiguration config) {
        this.config = config;
        log.debug("Activated OPConfigurationServiceImpl");
    }

    @Override
    public String getMonerisUrl() {
        return config.getMonerisUrl();
    }

    @Override
    public String getCADStoreId() {
        return config.getCADStoreId();
    }

    @Override
    public String getCADApiToken() {
        return config.getCADApiToken();
    }

    @Override
    public String getCADCheckoutId() {
        return config.getCADCheckoutId();
    }

    @Override
    public String getUSDStoreId() {
        return config.getUSDStoreId();
    }

    @Override
    public String getUSDApiToken() {
        return config.getUSDApiToken();
    }
    
    @Override
    public String getUSDCheckoutId() {
        return config.getUSDCheckoutId();
    }
    
    @Override
    public String getEnvironment() {
        return config.getEnvironment();
    }

    @Override
    public String getJavaScriptUrl() {
        return config.getJavaScriptUrl();
    }

}
