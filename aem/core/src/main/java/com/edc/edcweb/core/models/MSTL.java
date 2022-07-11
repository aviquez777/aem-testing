package com.edc.edcweb.core.models;

import com.day.cq.wcm.api.Page;
import com.edc.edcweb.core.helpers.LanguageUtil;
import com.edc.edcweb.core.helpers.constants.ConstantsForm;
import com.edc.edcweb.core.service.EloquaService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

@Model(adaptables = SlingHttpServletRequest.class)
public class MSTL {

    @Inject
    @Optional
    private Page currentPage;

    @Inject
    @Optional
    private EloquaService eloquaService;

    private String languageAbbreviation;
    private String pageLanguage;

    private String eloquaFormName;
    private String eloquaSiteId;
    private String eloquaFormSubmitURL;
    private String eloquaFormId;



    /**
     * Populates the Model with values from the applicable ContentPolicy (based on current path language).
     *
     */
    @PostConstruct
    public void initModel() {
        String pagePath = currentPage.getPath();

        this.languageAbbreviation = LanguageUtil.getLanguageAbbreviationFromPath(pagePath, ConstantsForm.SupportedLanguages.ENGLISH.getLanguageAbbreviation());

        // Special String format required by eloqua form
        if(this.languageAbbreviation.equalsIgnoreCase(ConstantsForm.FormProperties.FORM_EN)) {
            this.pageLanguage = ConstantsForm.FormProperties.FORM_HIDDEN_ENGLISH;
        } else if(this.languageAbbreviation.equalsIgnoreCase(ConstantsForm.FormProperties.FORM_FR)) {
            this.pageLanguage = ConstantsForm.FormProperties.FORM_HIDDEN_FRENCH;
        }

        // Configs for Eloqua form
        this.eloquaFormName = eloquaService.getMSTLFormName()==null?"":eloquaService.getMSTLFormName();
        this.eloquaSiteId = eloquaService.getSiteId()==null?"":eloquaService.getSiteId();
        this.eloquaFormSubmitURL = eloquaService.getFormSubmitURL()==null?"":eloquaService.getFormSubmitURL();
        this.eloquaFormId = eloquaService.getMSTLFormId()==null?"":eloquaService.getMSTLFormId();

    }

    public String getLanguageAbbreviation() {
        return this.languageAbbreviation;
    }

    public String getPageLanguage() {
        return this.pageLanguage;
    }

    public String getEloquaFormName() {
        return this.eloquaFormName;
    }

    public String getEloquaFormId() {
        return this.eloquaFormId;
    }

    public String getEloquaFormSubmitURL() {
        return this.eloquaFormSubmitURL;
    }

    public String getEloquaSiteId() {
        return this.eloquaSiteId;
    }


}

