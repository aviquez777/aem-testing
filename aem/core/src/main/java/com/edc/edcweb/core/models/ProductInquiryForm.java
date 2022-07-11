package com.edc.edcweb.core.models;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;
import com.edc.edcweb.core.helpers.Constants;
import com.edc.edcweb.core.helpers.FormFieldsUtil;
import com.edc.edcweb.core.helpers.constants.ConstantsForm;
import com.edc.edcweb.core.helpers.LanguageUtil;
import com.edc.edcweb.core.service.EloquaService;
import com.edc.edcweb.core.service.EDCSystemConfigurationService;

import java.util.Map;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

@Model(adaptables = SlingHttpServletRequest.class)
public class ProductInquiryForm {
    private static final Logger log = LoggerFactory.getLogger(ProductInquiryForm.class);

    @Self
    private SlingHttpServletRequest request;

    @Inject
    @Optional
    private Page currentPage;

    @Inject
    @Optional
    private EloquaService eloquaService;

    @Inject
    private EDCSystemConfigurationService edcSystemConfigurationService;

    private String pageLanguage;

    private String eloquaSiteId;
    private String eloquaFormSubmitURL;
    private String eloquaFormName;
    private String eloquaFormId;
    private String languageAbbreviation; 
    private String langVar;

    private Map<String, String> annualsalesitems = new HashMap<>();
    private Map<String, String> tradestatusitems = new HashMap<>();
    private Map<String, String> countryitems = new HashMap<>();
    private Map<String, String> provincesitems = new HashMap<>();
    private Map<String, String> usstatesitems = new HashMap<>();
    private Map<String, String> industryitems = new HashMap<>();
    private Map<String, String> firelationshipitems = new HashMap<>();
    private Map<String, String> pofutureitems = new HashMap<>();
    private Map<String, String> yearitems = new HashMap<>();
    private String marketsintpath;
    private Map<String, String> employeesitems = new HashMap<>();

    /**
     * Populates the Model with values from the applicable ContentPolicy (based on
     * current path language).
     *
     */
    @PostConstruct
    public void initModel() {
        String pagePath = currentPage.getPath();
        languageAbbreviation = LanguageUtil.getLanguageAbbreviationFromPath(pagePath,
                ConstantsForm.SupportedLanguages.ENGLISH.getLanguageAbbreviation());

        // Special String format required by eloqua form
        if (languageAbbreviation.equalsIgnoreCase(ConstantsForm.FormProperties.FORM_EN)) {
            this.pageLanguage = ConstantsForm.FormProperties.FORM_HIDDEN_ENGLISH;
            this.langVar = "EnName";
        } else if (languageAbbreviation.equalsIgnoreCase(ConstantsForm.FormProperties.FORM_FR)) {
            this.pageLanguage = ConstantsForm.FormProperties.FORM_HIDDEN_FRENCH;
            this.langVar = "FrName";
        }

        // Configs for Eloqua form
        this.eloquaSiteId = eloquaService.getSiteId() == null ? "" : eloquaService.getSiteId();
        this.eloquaFormSubmitURL = eloquaService.getFormSubmitURL() == null ? "" : eloquaService.getFormSubmitURL();
        this.eloquaFormName = eloquaService.getProductInquiryFormName() == null ? ""
                : eloquaService.getProductInquiryFormName();

        // Dropdown items from EDC data
        this.tradestatusitems = FormFieldsUtil.getTradeStatusPIListMap(this.request);
        this.annualsalesitems = FormFieldsUtil.getAnnualSalesListMap(this.request);
        this.countryitems = FormFieldsUtil.getCountriesForAddressMap(this.request);
        this.provincesitems = FormFieldsUtil.getProvincesForAddressMap(this.request);
        this.usstatesitems = FormFieldsUtil.getUsStatesMap(this.request);
        this.industryitems = FormFieldsUtil.getIndustryListMap(this.request);
        this.firelationshipitems = FormFieldsUtil.getFiRelationshipMap(this.request);
        this.pofutureitems = FormFieldsUtil.getPoFutureMap(this.request);
        this.yearitems = FormFieldsUtil.getYearMap(this.request);
        this.marketsintpath = Constants.Paths.EDCDATA_MARKETSINT_PI;
        this.employeesitems = FormFieldsUtil.getEmployeeListMap(this.request);
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

    public Map<String, String> getAnnualsalesitems() {
        return this.annualsalesitems;
    }

    public Map<String, String> getTradestatusitems() {
        return this.tradestatusitems;
    }

    public Map<String, String> getCountryitems() {
        return this.countryitems;
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

    public Map<String, String> getFirelationshipitems() {
        return firelationshipitems;
    }

    public Map<String, String> getPofutureitems() {
        return pofutureitems;
    }

    public Map<String, String> getYearitems() {
        return yearitems;
    }

    public String getMarketsintpath() {
        return marketsintpath;
    }

    public Map<String, String> getEmployeesitems() {
        return employeesitems;
    }
    // US 172230
    public List<FormFieldOptioPI> getPainpointitems() {
        List<FormFieldOptioPI> painpointitems = new LinkedList<>();
        Resource fieldResource = request.getResourceResolver().getResource(Constants.Paths.EDCDATA_PAINPOINTS_PI);
        if (fieldResource != null) {
            Iterator<Resource> itr = fieldResource.listChildren();
            while (itr.hasNext()) {
                Resource item = itr.next();
                FormFieldOptioPI fieldOption = item.adaptTo(FormFieldOptioPI.class);
                if (fieldOption != null) {
                    painpointitems.add(fieldOption);
                }
            }
        }
        return painpointitems;
    }
    
    public String getLangVar () {
        return langVar;
    }
    // END US 172230
    
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