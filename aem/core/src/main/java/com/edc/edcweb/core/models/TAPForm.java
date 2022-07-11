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
public class TAPForm {

    private static final Logger log = LoggerFactory.getLogger(TAPForm.class);

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

    private String pageLanguage;

    private String eloquaSiteId;
    private String eloquaFormSubmitURL;
    private String eloquaFormName;
    private String eloquaFormID;

    private Map<String,String> tradestatusitems;
    private Map<String,String> provinceitems;
    private Map<String,String> usstatesitems;
    private Map<String,String> countriesitems;
    private Map<String,String> annualsalesitems;
    private Map<String,String> employeesitems;
    private Map<String,String> painpointitems;
    private Map<String,String> industryitems;
    private Map<String,String> marketsintitems;

    //TapForm
    private Map<String,String> legalFormItems;
    private Map<String,String> companyProductItems;
    private Map<String,String> companyServiceItems;
    private Map<String,String> channelSellItems;
    private Map<String,String> onlineSalesItems;
    private Map<String,String> exportingExperienceItems;
    private Map<String,String> expensesItems;
    private Map<String,String> exportSalesItems;

    @PostConstruct
    public void initModel() {
        String pagePath = currentPage.getPath();
        String languageAbbreviation = LanguageUtil.getLanguageAbbreviationFromPath(pagePath, ConstantsForm.SupportedLanguages.ENGLISH.getLanguageAbbreviation());

        // Special String format required by eloqua form
        if (languageAbbreviation.equalsIgnoreCase(ConstantsForm.FormProperties.FORM_EN)) {
            this.pageLanguage = ConstantsForm.FormProperties.FORM_HIDDEN_ENGLISH;
        } else if (languageAbbreviation.equalsIgnoreCase(ConstantsForm.FormProperties.FORM_FR)) {
            this.pageLanguage = ConstantsForm.FormProperties.FORM_HIDDEN_FRENCH;
        }

        // Configs for Eloqua form
        this.eloquaSiteId = eloquaService.getSiteId() == null ? "" : eloquaService.getSiteId();
        this.eloquaFormSubmitURL = eloquaService.getFormSubmitURL() == null ? "" : eloquaService.getFormSubmitURL();
        this.eloquaFormName = eloquaService.getTAPFormName() == null ? "" : eloquaService.getTAPFormName();
        this.eloquaFormID = eloquaService.getTAPFormId() == null ? "" : eloquaService.getTAPFormId();

        // Dropdown items from EDC data
        this.tradestatusitems = FormFieldsUtil.getTradeStatusListMap(this.request);
        this.provinceitems = FormFieldsUtil.getProvinceListMap(this.request);
        this.usstatesitems = FormFieldsUtil.getUsStatesMap(this.request);
        this.countriesitems = FormFieldsUtil.getCountriesForAddressMap(this.request);
        this.annualsalesitems = FormFieldsUtil.getAnnualSalesListMap(this.request);
        this.employeesitems = FormFieldsUtil.getEmployeeListMap(this.request);
        this.painpointitems = FormFieldsUtil.getPainPointListMap(this.request);
        this.industryitems = FormFieldsUtil.getIndustryListMap(this.request);
        this.marketsintitems = FormFieldsUtil.getCountriesForAddressMap(this.request);

        // Dropdown items from TAP form
        this.legalFormItems = FormFieldsUtil.getLegalFormMap(this.request);
        this.companyProductItems = FormFieldsUtil.getCompanyProductMap(this.request);
        this.companyServiceItems = FormFieldsUtil.getCompanyServiceMap(this.request);
        this.channelSellItems = FormFieldsUtil.getChannelSelleMap(this.request);
        this.onlineSalesItems = FormFieldsUtil.getOnlineSaleMap(this.request);
        this.exportingExperienceItems = FormFieldsUtil.getExportingExperienceMap(this.request);
        this.expensesItems = FormFieldsUtil.getExpensesMap(this.request);
        this.exportSalesItems  = FormFieldsUtil.getExportSalesMap(this.request);

    }

    /*
        Getters
     */

    public String getPageLanguage() {
        return pageLanguage;
    }

    public String getEloquaSiteId() {
        return eloquaSiteId;
    }

    public String getEloquaFormSubmitURL() {
        return eloquaFormSubmitURL;
    }

    public String getEloquaFormName() {
        return eloquaFormName;
    }

    public String getEloquaFormID() {
        return eloquaFormID;
    }

    public Map<String, String> getTradestatusitems() {
        return tradestatusitems;
    }

    public Map<String, String> getProvinceitems() {
        return provinceitems;
    }

    public Map<String, String> getUsstatesitems() {
        return usstatesitems;
    }

    public Map<String, String> getCountriesitems() {
        return countriesitems;
    }

    public Map<String, String> getAnnualsalesitems() {
        return annualsalesitems;
    }

    public Map<String, String> getEmployeesitems() {
        return employeesitems;
    }

    public Map<String, String> getPainpointitems() {
        return painpointitems;
    }

    public Map<String, String> getIndustryitems() {
        return industryitems;
    }

    public Map<String, String> getMarketsintitems() {
        return marketsintitems;
    }

    public Map<String, String> getLegalFormItems() {
        return legalFormItems;
    }

    public Map<String, String> getCompanyProductItems() {
        return companyProductItems;
    }

    public Map<String, String> getCompanyServiceItems() {
        return companyServiceItems;
    }

    public Map<String, String> getChannelSellItems() {
        return channelSellItems;
    }

    public Map<String, String> getOnlineSalesItems() {
        return onlineSalesItems;
    }

    public Map<String, String> getExportingExperienceItems() {
        return exportingExperienceItems;
    }

    public Map<String, String> getExpensesItems() {
        return expensesItems;
    }

    public Map<String, String> getExportSalesItems() {
        return exportSalesItems;
    }
}
