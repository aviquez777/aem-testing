package com.edc.edcweb.core.models.inlist;

import com.edc.edcweb.core.helpers.FormFieldsUtil;
import com.edc.edcweb.core.service.EDCSystemConfigurationService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.Map;

@Model(adaptables = SlingHttpServletRequest.class)
public class InListServiceProviderIntakeForm {
    @Self
    private SlingHttpServletRequest request;

    @Inject
    private EDCSystemConfigurationService edcSystemConfigurationService;

    private Map<String, String> countriesitems;
    private Map<String, String> provincesitems;
    private Map<String, String> usstatesitems;
    private Map<String, String> industryitems;

    @PostConstruct
    public void initModel() {
        this.countriesitems = FormFieldsUtil.getCountriesForAddressMap(this.request);
        this.provincesitems = FormFieldsUtil.getProvincesForAddressMap(this.request);
        this.usstatesitems = FormFieldsUtil.getUsStatesMap(this.request);
        this.industryitems = FormFieldsUtil.getIndustryListMap(this.request);
    }

    public Map<String, String> getCountriesitems() {
        return countriesitems;
    }

    public Map<String, String> getProvincesitems() {
        return provincesitems;
    }

    public Map<String, String> getUsstatesitems() {
        return usstatesitems;
    }

    public Map<String, String> getIndustryitems() {
        return industryitems;
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
