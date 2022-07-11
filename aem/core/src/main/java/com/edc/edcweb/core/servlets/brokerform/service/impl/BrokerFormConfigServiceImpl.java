package com.edc.edcweb.core.servlets.brokerform.service.impl;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edc.edcweb.core.servlets.brokerform.service.BrokerFormConfigService;
import com.edc.edcweb.core.servlets.brokerform.service.configuration.BrokerFormServiceConfiguration;

@Component(immediate = true, service = BrokerFormConfigService.class, configurationPid = "com.edc.edcweb.core.servlets.brokerform.service.impl.BrokerFormServiceImpl")
@Designate(ocd = BrokerFormServiceConfiguration.class)

public class BrokerFormConfigServiceImpl implements BrokerFormConfigService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BrokerFormConfigServiceImpl.class);

    private BrokerFormServiceConfiguration config;

    @Activate
    @Modified
    protected void activate(BrokerFormServiceConfiguration config) {
        this.config = config;
        LOGGER.debug("Activated BrokerFormServiceConfiguration");
    }

    @Override
    public String getEmailFrom() {
        return config.getEmailFrom();
    }

    @Override
    public String getEmailTo() {
        return config.getEmailTo();
    }

    @Override
    public String getEmailCc() {
        return config.getEmailCc();
    }

}
