package com.edc.edcweb.core.models;

import com.day.cq.commons.ValueMapWrapper;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;
import com.edc.edcweb.core.helpers.constants.ConstantsForm;
import com.edc.edcweb.core.helpers.LanguageUtil;
import com.edc.edcweb.core.helpers.FormFieldsUtil;
import com.edc.edcweb.core.service.EloquaService;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

@Model(adaptables = SlingHttpServletRequest.class)
public class Matchmaking {

    private static final Logger log = LoggerFactory.getLogger(Matchmaking.class);

    @Self
    private SlingHttpServletRequest request;

    @Inject
    @Default(values = "")
    private Resource resource;

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

    private Map<String,String> industrysvditems;
    private Map<String,String> subindustrysvditems;
    private Map<String,String> exportsalesitems;
    private Map<String,String> painpointitems;
    private Map<String,String> tradestatusitems;
    private Map<String,String> industryitems;
    private Map<String,String> marketsintitems;
    private Map<String,String> marketsexportitems;
    private Map<String,String> annualsalesitems;
    private Map<String,String> employeesitems;
    private Map<String,String> countriesitems;
    private Map<String,String> provincesitems;
    private Map<String,String> usstatesitems;

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
        this.eloquaFormName = eloquaService.getMatchmakingFormName() == null ? "" : eloquaService.getMatchmakingFormName();
        this.eloquaSiteId = eloquaService.getSiteId() == null ? "" : eloquaService.getSiteId();
        this.eloquaFormSubmitURL = eloquaService.getFormSubmitURL() == null ? "" : eloquaService.getFormSubmitURL();

        // Dropdown items from EDC data
        this.painpointitems = FormFieldsUtil.getPainPointListMap(request);
        this.tradestatusitems = FormFieldsUtil.getTradeStatusListMap(this.request);
        this.industryitems = FormFieldsUtil.getIndustryListMap(this.request);
        this.annualsalesitems = FormFieldsUtil.getAnnualSalesListMap(this.request);
        this.employeesitems = FormFieldsUtil.getEmployeeListMap(this.request);
        this.countriesitems = FormFieldsUtil.getCountriesForAddressMap(this.request);
        this.provincesitems = FormFieldsUtil.getProvincesForAddressMap(this.request);
        this.usstatesitems = FormFieldsUtil.getUsStatesMap(this.request);
        this.marketsintitems = FormFieldsUtil.getCountriesForAddressMap(this.request);
        this.marketsexportitems = FormFieldsUtil.getCountriesForMarketOfInterestMap(this.request);
        this.industrysvditems = FormFieldsUtil.getIndustryServedPrimaryListMap(this.request);
        this.subindustrysvditems = FormFieldsUtil.getIndustryServedSecondaryListMap(this.request);
        this.exportsalesitems = FormFieldsUtil.getAnnualSalesListMap(this.request);

    }

    /**
     *
     * @param fieldsContainerName
     * @return list of options for drop-down
     */
    private List<ValueMap> populateItemsFromDialog(String fieldsContainerName) {
        List<ValueMap> fields = new ArrayList<>();
        Resource fieldsContainerResource = resource.getChild(fieldsContainerName);
        if (fieldsContainerResource != null) {
            Iterator<Resource> fieldsIterator = fieldsContainerResource.listChildren();
            while (fieldsIterator.hasNext()) {
                fields.add(fieldsIterator.next().getValueMap());
            }
        }

        return fields;
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

    public String getEloquaFormSubmitURL() {
        return this.eloquaFormSubmitURL;
    }

    public String getEloquaSiteId() {
        return this.eloquaSiteId;
    }

    public Map<String,String> getIndustrysvditems() {
        return industrysvditems;
    }

    public Map<String,String> getSubindustrysvditems() {
        return subindustrysvditems;
    }

    public Map<String,String> getExportsalesitems() {
        return exportsalesitems;
    }

    public Map<String,String> getAnnualsalesitems() {
        return annualsalesitems;
    }

    public Map<String,String> getEmployeesitems() {
        return employeesitems;
    }

    public Map<String, String> getCountriesitems() {
        return countriesitems;
    }

    public Map<String,String> getIndustryitems() {
        return industryitems;
    }

    public Map<String,String> getTradestatusitems() {
        return tradestatusitems;
    }

    public Map<String,String> getMarketsintitems() {
        return marketsintitems;
    }

    public Map<String,String> getMarketsexportitems() {
        return marketsexportitems;
    }

    public Map<String,String> getPainpointitems() {
        return painpointitems;
    }

    public Map<String,String> getProvincesitems() {
        return provincesitems;
    }

    public Map<String, String> getUsstatesitems() {
        return usstatesitems;
    }
}
