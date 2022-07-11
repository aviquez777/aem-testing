package com.edc.edcweb.core.models;

import com.edc.edcweb.core.helpers.FormFieldsUtil;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.Self;
import com.edc.edcweb.core.service.EloquaService;
import com.edc.edcweb.core.service.EDCSystemConfigurationService;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import java.util.Map;

@Model(adaptables = SlingHttpServletRequest.class)
public class EhForm {

    @Self
    private SlingHttpServletRequest request;

    @Inject
    @Optional
    private EloquaService eloquaService;

    @Inject
    private EDCSystemConfigurationService edcSystemConfigurationService;

    private String eloquaFormName;
    private String eloquaFormId;
    private String eloquaSiteId;
    private String eloquaFormSubmitURL;
    private Map<String,String> tradestatusitems;
    private Map<String,String> provinceitems;
    private Map<String,String> usstatesitems;
    private Map<String,String> annualsalesitems;
    private Map<String,String> employeesitems;
    private Map<String,String> painpointitems;
    private Map<String,String> industryitems;
    private Map<String,String> marketsintitems;
    private String lookupIdVisitor;
    private String lookupIdPrimary;

    @PostConstruct
    public void initModel() {
        this.eloquaFormName = eloquaService.getEhFormName()==null?"":eloquaService.getEhFormName();
        this.eloquaFormId = eloquaService.getEhFormId()==null?"":eloquaService.getEhFormId();
        this.eloquaSiteId = eloquaService.getSiteId()==null?"":eloquaService.getSiteId();
        this.eloquaFormSubmitURL = eloquaService.getFormSubmitURL()==null?"":eloquaService.getFormSubmitURL();
        this.lookupIdVisitor = eloquaService.getEhLookupIdVisitor()==null?"":eloquaService.getEhLookupIdVisitor();
        this.lookupIdPrimary = eloquaService.getEhLookupIdPrimary()==null?"":eloquaService.getEhLookupIdPrimary();

        // Dropdown items from EDC data
        this.tradestatusitems = FormFieldsUtil.getTradeStatusListMap(this.request);
        this.provinceitems = FormFieldsUtil.getProvinceListMap(this.request);
        this.usstatesitems = FormFieldsUtil.getUsStatesMap(this.request);
        this.annualsalesitems = FormFieldsUtil.getAnnualSalesListMap(this.request);
        this.employeesitems = FormFieldsUtil.getEmployeeListMap(this.request);
        this.painpointitems = FormFieldsUtil.getPainPointListMap(this.request);
        this.industryitems = FormFieldsUtil.getIndustryListMap(this.request);
        this.marketsintitems = FormFieldsUtil.getCountriesListMap(this.request);
    }

    public String getEloquaFormName(){
       return  this.eloquaFormName;
    }

    public String getEloquaFormId(){
       return  this.eloquaFormId;
    }

    public String getEloquaSiteId() {
        return this.eloquaSiteId;
    }

    public String getEloquaFormSubmitURL(){
        return this.eloquaFormSubmitURL;
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

    public String getLookupIdVisitor() {
        return lookupIdVisitor;
    }

    public String getLookupIdPrimary() {
        return lookupIdPrimary;
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
