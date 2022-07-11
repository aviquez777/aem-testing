package com.edc.edcportal.core.transactionhistory.services.impl;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edc.edcportal.core.transactionhistory.services.THConfigService;
import com.edc.edcportal.core.transactionhistory.services.configuration.THConfigServiceConfiguration;

@Component(immediate = true, service = THConfigService.class, configurationPid = "com.edc.edcportal.core.transactionhistory.services.configuration.impl.THConf")
@Designate(ocd = THConfigServiceConfiguration.class)
public class THConfigServiceImpl implements THConfigService {

    private static final Logger LOGGER = LoggerFactory.getLogger(THConfigServiceImpl.class);

    private THConfigServiceConfiguration config;

    @Activate
    @Modified
    protected void activate(THConfigServiceConfiguration config) {
        this.config = config;
        LOGGER.debug("Activated THConfigServiceConfiguration");
    }

    @Override
    public String getEmailAdressFieldId() {
        return config.getEmailAdressFieldId();
    }

    @Override
    public String getExternalIDAEMPathFieldId() {
        return config.getExternalIDAEMPathFieldId();
    }

    @Override
    public String getExternalIDFieldId() {
        return config.getExternalIDFieldId();
    }

    @Override
    public String getAEMpathFieldId() {
        return config.getAEMpathFieldId();
    }

    @Override
    public String getCounterFieldId() {
        return config.getCounterFieldId();
    }

    @Override
    public String getEloquaTrafficSourceFieldId() {
        return config.getEloquaTrafficSourceFieldId();
    }

    @Override
    public String getParnersTCFieldId() {
        return config.getParnersTCFieldId();
    }

    @Override
    public String getParnersCASLFieldId() {
        return config.getParnersCASLFieldId();
    }

    @Override
    public String getTimestampFieldId() {
        return config.getTimestampFieldId();
    }

    @Override
    public String getWitFieldId() {
        return config.getWitFieldId();
    }

}