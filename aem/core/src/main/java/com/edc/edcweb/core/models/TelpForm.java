package com.edc.edcweb.core.models;

import com.edc.edcweb.core.service.EDCSystemConfigurationService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

@Model(adaptables = SlingHttpServletRequest.class)
public class TelpForm {

    @Inject
    private EDCSystemConfigurationService edcSystemConfigurationService;

    @PostConstruct
    public void initModel() {
        //default implementation for model initialization not needed as all members are injected.
    }
    
    public String getAddressCompleteCSSUrl() {
        return edcSystemConfigurationService.getAddressCompleteCSSUrl();
    }

    public String getAddressCompleteJSUrl() {
        return edcSystemConfigurationService.getAddressCompleteJSUrl();
    }

    public String getAddressCompleteKey() {
        return edcSystemConfigurationService.getAddressCompleteKey();
    }
    
    public String getAddressCompleteService() {
        return edcSystemConfigurationService.getAddressCompleteService();
    }
}
