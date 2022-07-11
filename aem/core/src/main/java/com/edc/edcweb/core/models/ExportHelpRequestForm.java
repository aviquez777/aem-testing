package com.edc.edcweb.core.models;

import com.edc.edcweb.core.helpers.Constants;
import com.edc.edcweb.core.helpers.FormFieldsUtil;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.util.Map;

@Model(adaptables = SlingHttpServletRequest.class)
public class ExportHelpRequestForm {

    private static final Logger log = LoggerFactory.getLogger(ExportHelpRequestForm.class);

    @Self
    private SlingHttpServletRequest request;

    private String postUrl;

    private Map<String,String> tradestatusitems;
    private Map<String,String> provinceitems;
    private Map<String,String> usstatesitems;
    private Map<String,String> countriesitems;
    private Map<String,String> annualsalesitems;
    private Map<String,String> employeesitems;
    private Map<String,String> painpointitems;
    private Map<String,String> industryitems;
    private Map<String,String> marketsintitems;

    @PostConstruct
    public void initModel() {
        this.postUrl = Constants.Paths.PROGRESSIVEPROFILING_POSTSERVLET;

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
    }

    public String getPostUrl() {
        return postUrl;
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
}
