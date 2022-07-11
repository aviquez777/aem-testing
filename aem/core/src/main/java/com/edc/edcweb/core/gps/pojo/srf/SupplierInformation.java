package com.edc.edcweb.core.gps.pojo.srf;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SupplierInformation {

    @JsonProperty("SupplierType")
    private String supplierType;

    @JsonProperty("ReferredByFirstName")
    private String referredByFirstName;

    @JsonProperty("ReferredByLastName")
    private String referredByLastName;

    @JsonProperty("CorporateStatus")
    private String corporateStatus;

    @JsonProperty("OtherCorportateStatus")
    private String otherCorportateStatus;

    @JsonProperty("BusinessServices")
    private String[] businessServices;

    @JsonProperty("OtherBusinessServices")
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private String[] otherBusinessServices;

    @JsonIgnore
    Map<String, String> errorMsgs = new HashMap<>();

    public String getSupplierType() {
        return supplierType;
    }

    public void setSupplierType(String supplierType) {
        this.supplierType = supplierType;
    }

    public String getReferredByFirstName() {
        return referredByFirstName;
    }

    public void setReferredByFirstName(String referredByFirstName) {
        this.referredByFirstName = referredByFirstName;
    }

    public String getReferredByLastName() {
        return referredByLastName;
    }

    public void setReferredByLastName(String referredByLastName) {
        this.referredByLastName = referredByLastName;
    }

    public String getCorporateStatus() {
        return corporateStatus;
    }

    public void setCorporateStatus(String corporateStatus) {
        this.corporateStatus = corporateStatus;
    }

    public String getOtherCorportateStatus() {
        return otherCorportateStatus;
    }

    public void setOtherCorportateStatus(String otherCorportateStatus) {
        this.otherCorportateStatus = otherCorportateStatus;
    }

    public String[] getBusinessServices() {
        return businessServices;
    }

    public void setBusinessServices(String[] businessServices) {
        this.businessServices = businessServices;
    }

    public String[] getOtherBusinessServices() {
        return otherBusinessServices;
    }

    public void setOtherBusinessServices(String[] otherBusinessServices) {
        this.otherBusinessServices = otherBusinessServices;
    }

    public Map<String, String> getErrorMsgs() {
        return errorMsgs;
    }

    public void setErrorMsgs(Map<String, String> errorMsgs) {
        this.errorMsgs = errorMsgs;
    }

}
