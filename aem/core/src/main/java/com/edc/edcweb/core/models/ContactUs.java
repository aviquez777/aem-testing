package com.edc.edcweb.core.models;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;

import com.day.cq.wcm.api.Page;
import com.edc.edcweb.core.helpers.constants.ConstantsForm;
import com.edc.edcweb.core.helpers.FormFieldsUtil;
import com.edc.edcweb.core.helpers.LanguageUtil;
import com.edc.edcweb.core.service.EloquaService;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;

@Model(adaptables = SlingHttpServletRequest.class)
public class ContactUs {
	
    @Self
    private SlingHttpServletRequest request;

    @Inject @Default(values="")
    private Resource resource;

    @Inject
    @Optional
    private Page currentPage;

    @ScriptVariable
    private ValueMap properties;

    @Inject
    @Optional
    private EloquaService eloquaService;

    private String languageAbbreviation;
    private String pageLanguage;

    private String eloquaFormName;
    private String eloquaSiteId;
    private String eloquaFormSubmitURL;
    private String eloquaFormId;

    private List<InquiryTypeOptions> inquiryitems;
    private Map<String,String> exportstatusitems = new HashMap<>();

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
        this.eloquaFormName = eloquaService.getContactUsFormName()==null?"":eloquaService.getContactUsFormName();
        this.eloquaSiteId = eloquaService.getSiteId()==null?"":eloquaService.getSiteId();
        this.eloquaFormSubmitURL = eloquaService.getFormSubmitURL()==null?"":eloquaService.getFormSubmitURL();
        this.eloquaFormId = eloquaService.getContactUsFormId()==null?"":eloquaService.getContactUsFormId();

        Resource inquiryTypeResource = this.resource.getChild(ConstantsForm.FormProperties.FORM_HTML_INQUIRYITEMS);
        if ( inquiryTypeResource != null ) {
            this.inquiryitems = populateInquiryTypeList(inquiryTypeResource);
        }

        this.exportstatusitems = FormFieldsUtil.getTradeStatusListMap(this.request);
    }

    /**
     *
     * @param resource
     * @return list of options for type of inquiry
     */
    private List<InquiryTypeOptions> populateInquiryTypeList(Resource resource) {
        List<InquiryTypeOptions> listOfOptions = new ArrayList<>();
        if (resource != null) {
            Iterator<Resource> resources = resource.listChildren();
            while (resources.hasNext()) {
                InquiryTypeOptions item = resources.next().adaptTo(InquiryTypeOptions.class);
                listOfOptions.add(item);
            }
        }
        return listOfOptions;
    }

    public String getLanguageAbbreviation() {
        return this.languageAbbreviation;
    }

    public String getPageLanguage() {
        return this.pageLanguage;
    }

    public List<InquiryTypeOptions> getInquiryitems() {
        return this.inquiryitems;
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

    public Map<String, String> getExportstatusitems() {
        return this.exportstatusitems;
    }
}
