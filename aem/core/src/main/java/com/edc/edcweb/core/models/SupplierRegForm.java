package com.edc.edcweb.core.models;

import com.edc.edcweb.core.helpers.FormFieldsUtil;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;

import javax.annotation.PostConstruct;

import java.util.Map;

@Model(adaptables = SlingHttpServletRequest.class)
public class SupplierRegForm {

    @Self
    private SlingHttpServletRequest request;

    private Map<String,String> provinceitems;
    private Map<String,String> usstatesitems;
    private Map<String,String> countriesitems;

    @PostConstruct
    public void initModel() {

        // Dropdown items from EDC data
        this.provinceitems = FormFieldsUtil.getProvinceListMap(this.request);
        this.usstatesitems = FormFieldsUtil.getUsStatesMap(this.request);
        this.countriesitems = FormFieldsUtil.getCountriesForAddressMap(this.request);
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
}
