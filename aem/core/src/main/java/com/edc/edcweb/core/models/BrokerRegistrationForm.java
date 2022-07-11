package com.edc.edcweb.core.models;

import com.day.cq.wcm.api.Page;
import com.edc.edcweb.core.helpers.LanguageUtil;
import com.edc.edcweb.core.helpers.constants.ConstantsForm;
import com.edc.edcweb.core.service.EloquaService;
import com.edc.edcweb.core.service.EDCSystemConfigurationService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.Self;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

@Model(adaptables = SlingHttpServletRequest.class)
public class BrokerRegistrationForm {

    @Self
    private SlingHttpServletRequest request;

    @Inject
    private EDCSystemConfigurationService edcSystemConfigurationService;

    @Inject
    @Optional
    private Page currentPage;

    @Inject
    @Optional
    private EloquaService eloquaService;

    private String pageLanguage;

    private String eloquaSiteId;
    private String eloquaFormSubmitURL;
    private String eloquaFormName;
    private String eloquaFormId;

    /**
     * Populates the Model with values from the applicable ContentPolicy (based on current path language).
     *
     */
    @PostConstruct
    public void initModel() {
        String pagePath = currentPage.getPath();
        String languageAbbreviation = LanguageUtil.getLanguageAbbreviationFromPath(pagePath, ConstantsForm.SupportedLanguages.ENGLISH.getLanguageAbbreviation());

        // Special String format required by eloqua form
        if(languageAbbreviation.equalsIgnoreCase(ConstantsForm.FormProperties.FORM_EN)) {
            this.pageLanguage = ConstantsForm.FormProperties.FORM_HIDDEN_ENGLISH;
        } else if(languageAbbreviation.equalsIgnoreCase(ConstantsForm.FormProperties.FORM_FR)) {
            this.pageLanguage = ConstantsForm.FormProperties.FORM_HIDDEN_FRENCH;
        }

        // Configs for Eloqua form
        this.eloquaSiteId = eloquaService.getSiteId() == null ? "" : eloquaService.getSiteId();
        this.eloquaFormSubmitURL = eloquaService.getFormSubmitURL() == null ? "" : eloquaService.getFormSubmitURL();
        this.eloquaFormName = eloquaService.getBrokerRegistrationFormName() == null ? "" : eloquaService.getBrokerRegistrationFormName();
        this.eloquaFormId = eloquaService.getBrokerRegistrationFormId() == null ? "" : eloquaService.getBrokerRegistrationFormId();

    }

    public String getPageLanguage() {
        return this.pageLanguage;
    }

    public String getEloquaSiteId() {
        return this.eloquaSiteId;
    }

    public String getEloquaFormSubmitURL() {
        return this.eloquaFormSubmitURL;
    }

    public String getEloquaFormName() {
        return this.eloquaFormName;
    }

    public String getEloquaFormId() {
        return this.eloquaFormId;
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

