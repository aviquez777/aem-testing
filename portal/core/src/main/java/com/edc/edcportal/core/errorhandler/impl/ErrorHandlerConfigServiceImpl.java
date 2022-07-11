package com.edc.edcportal.core.errorhandler.impl;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edc.edcportal.core.errorhandler.services.ErrorHandlerConfigService;
import com.edc.edcportal.core.errorhandler.configuration.ErrorHandlerConfigServiceConfiguration;

@Component(immediate = true, service = ErrorHandlerConfigService.class, configurationPid = "com.edc.edcportal.core.servlets.ErrorHandler")
@Designate(ocd = ErrorHandlerConfigServiceConfiguration.class)
public class ErrorHandlerConfigServiceImpl implements ErrorHandlerConfigService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ErrorHandlerConfigServiceImpl.class);

    private ErrorHandlerConfigServiceConfiguration config;

    @Activate
    @Modified
    protected void activate(ErrorHandlerConfigServiceConfiguration config) {
        this.config = config;
        LOGGER.debug("Activated ErrorHandlerConfiguration Service");
    }

    @Override
    public String getB2CChangePassEntityId() {
        return config.getB2CChangePassEntityId();
    }

    @Override
    public String getB2CChangePassStatusCode() {
        return config.getB2CChangePassStatusCode();
    }

    @Override
    public String getB2CChangePassStatusMessage() {
        return config.getB2CChangePassStatusMessage();
    }

    @Override
    public String getB2CChangePassRedirectURL() {
        return config.getB2CChangePassRedirectURL();
    }

    @Override
    public String getB2CEditProfileEntityId() {
        return config.getB2CEditProfileEntityId();
    }

    @Override
    public String getB2CEditProfileStatusCode() {
        return config.getB2CEditProfileStatusCode();
    }

    @Override
    public String getB2CEditProfileStatusMessage() {
        return config.getB2CEditProfileStatusMessage();
    }

    @Override
    public String getB2CEditProfileRedirectURL() {
        return config.getB2CEditProfileRedirectURL();
    }

    @Override
    public String getB2CForgetPassEntityId() {
        return config.getB2CForgetPassEntityId();
    }

    @Override
    public String getB2CForgetPassStatusCode() {
        return config.getB2CForgetPassStatusCode();
    }

    @Override
    public String getB2CForgetPassStatusMessage() {
        return config.getB2CForgetPassStatusMessage();
    }

    @Override
    public String getB2CForgetPassRedirectURL() {
        return config.getB2CForgetPassRedirectURL();
    }

    @Override
    public String getB2CSignUpEntityId() {
        return config.getB2CSignUpEntityId();
    }

    @Override
    public String getB2CSignUpStatusCode() {
        return config.getB2CSignUpStatusCode();
    }

    @Override
    public String getB2CSignUpStatusMessage() {
        return config.getB2CSignUpStatusMessage();
    }

    @Override
    public String getB2CSignUpRedirectURL() {
        return config.getB2CSignUpRedirectURL();
    }

    @Override
    public String getB2CResetPassEntityId() {
        return config.getB2CResetPassEntityId();
    }

    @Override
    public String getB2CResetPassStatusCode() {
        return config.getB2CResetPassStatusCode();
    }

    @Override
    public String getB2CResetPassStatusMessage() {
        return config.getB2CResetPassStatusMessage();
    }

    @Override
    public String getB2CResetPassRedirectURL() {
        return config.getB2CResetPassRedirectURL();
    }

    @Override
    public String getB2CSignUpDirectEntityId() {
        return config.getB2CSignUpDirectEntityId();
    }

    @Override
    public String getB2CSignUpDirectStatusCode() {
        return config.getB2CSignUpDirectStatusCode();
    }

    @Override
    public String getB2CSignUpDirectStatusMessage() {
        return config.getB2CSignUpDirectStatusMessage();
    }

    @Override
    public String getB2CSignUpDirectRedirectURL() {
        return config.getB2CSignUpDirectRedirectURL();
    }

    @Override
    public String getB2CFRChangePassEntityId() {
        return config.getB2CFRChangePassEntityId();
    }

    @Override
    public String getB2CFRChangePassStatusCode() {
        return config.getB2CFRChangePassStatusCode();
    }

    @Override
    public String getB2CFRChangePassStatusMessage() {
        return config.getB2CFRChangePassStatusMessage();
    }

    @Override
    public String getB2CFRChangePassRedirectURL() {
        return config.getB2CFRChangePassRedirectURL();
    }

    @Override
    public String getB2CFREditProfileEntityId() {
        return config.getB2CFREditProfileEntityId();
    }

    @Override
    public String getB2CFREditProfileStatusCode() {
        return config.getB2CFREditProfileStatusCode();
    }

    @Override
    public String getB2CFREditProfileStatusMessage() {
        return config.getB2CFREditProfileStatusMessage();
    }

    @Override
    public String getB2CFREditProfileRedirectURL() {
        return config.getB2CFREditProfileRedirectURL();
    }

    @Override
    public String getB2CFRForgetPassEntityId() {
        return config.getB2CFRForgetPassEntityId();
    }

    @Override
    public String getB2CFRForgetPassStatusCode() {
        return config.getB2CFRForgetPassStatusCode();
    }

    // BUG 341393
    @Override
    public String getB2CFRForgetPassStatusMessage() {
        return config.getB2CFRForgetPassStatusMessage();
    }

    @Override
    public String getB2CFRForgetPassRedirectURL() {
        return config.getB2CFRForgetPassRedirectURL();
    }

    @Override
    public String getB2CFRSignUpEntityId() {
        return config.getB2CFRSignUpEntityId();
    }

    @Override
    public String getB2CFRSignUpStatusCode() {
        return config.getB2CFRSignUpStatusCode();
    }

    @Override
    public String getB2CFRSignUpStatusMessage() {
        return config.getB2CFRSignUpStatusMessage();
    }

    @Override
    public String getB2CFRSignUpRedirectURL() {
        return config.getB2CFRSignUpRedirectURL();
    }

    @Override
    public String getB2CFRResetPassEntityId() {
        return config.getB2CFRResetPassEntityId();
    }

    @Override
    public String getB2CFRResetPassStatusCode() {
        return config.getB2CFRResetPassStatusCode();
    }

    @Override
    public String getB2CFRResetPassStatusMessage() {
        return config.getB2CFRResetPassStatusMessage();
    }

    @Override
    public String getB2CFRResetPassRedirectURL() {
        return config.getB2CFRResetPassRedirectURL();
    }

    @Override
    public String getB2CFRSignUpDirectEntityId() {
        return config.getB2CFRSignUpDirectEntityId();
    }

    @Override
    public String getB2CFRSignUpDirectStatusCode() {
        return config.getB2CFRSignUpDirectStatusCode();
    }

    @Override
    public String getB2CFRSignUpDirectStatusMessage() {
        return config.getB2CFRSignUpDirectStatusMessage();
    }

    @Override
    public String getB2CFRSignUpDirectRedirectURL() {
        return config.getB2CFRSignUpDirectRedirectURL();
    }

}
