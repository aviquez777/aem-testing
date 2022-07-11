package com.edc.edcweb.core.models;

import com.day.cq.wcm.api.Page;
import com.edc.edcweb.core.helpers.Constants;
import com.edc.edcweb.core.helpers.FormFieldsUtil;
import com.edc.edcweb.core.helpers.LanguageUtil;
import com.edc.edcweb.core.helpers.constants.ConstantsForm;
import com.edc.edcweb.core.service.EloquaService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.Map;

@Model(adaptables = SlingHttpServletRequest.class)
public class APSGForm {

    private static final Logger log = LoggerFactory.getLogger(APSGForm.class);

    @Self
    private SlingHttpServletRequest request;

    @ScriptVariable
    private ValueMap properties;

    @Inject
    @Optional
    private Page currentPage;

    @Inject
    @Optional
    private EloquaService eloquaService;

    private String languageAbbreviation;
    private String pageLanguage;
    private String eloquaFormName;
    private String eloquaFormID;

    @PostConstruct
    public void initModel() {
        String pagePath = currentPage.getPath();
        this.languageAbbreviation = LanguageUtil.getLanguageAbbreviationFromPath(pagePath, ConstantsForm.SupportedLanguages.ENGLISH.getLanguageAbbreviation());

        // Special String format required by eloqua form
        if (this.languageAbbreviation.equalsIgnoreCase(ConstantsForm.FormProperties.FORM_EN)) {
            this.pageLanguage = ConstantsForm.FormProperties.FORM_HIDDEN_ENGLISH;
        } else if (this.languageAbbreviation.equalsIgnoreCase(ConstantsForm.FormProperties.FORM_FR)) {
            this.pageLanguage = ConstantsForm.FormProperties.FORM_HIDDEN_FRENCH;
        }

        // Configs for Eloqua form
        
        this.eloquaFormName = eloquaService.getAPSGFormName() == null ? "" : eloquaService.getAPSGFormName();
        this.eloquaFormID = eloquaService.getAPSGFormId() == null ? "" : eloquaService.getAPSGFormId();
    }

    /*
        Getters
     */

    public String getLanguageAbbreviation() {
        return languageAbbreviation;
    }

    public String getPageLanguage() {
        return pageLanguage;
    }

    public String getEloquaFormName() {
        return eloquaFormName;
    }

    public String getEloquaFormID() {
        return eloquaFormID;
    }

}
