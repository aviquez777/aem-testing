package com.edc.edcweb.core.gps.pojo.srf;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SrfDO {

    @JsonProperty("Language")
    private String language;

    @JsonProperty("CodeOfConductConsent")
    private String codeOfConductConsent;

    @JsonProperty("ContactInfo")
    private ContactInfo contactInfo;

    @JsonProperty("SupplierInformation")
    private SupplierInformation supplierInformation;

    @JsonProperty("CompanyIdentifier")
    private CompanyIdentifier companyIdentifier;

    @JsonProperty("CompanyInformation")
    private CompanyInformation companyInformation;

    @JsonIgnore
    private Map<String, String> errorMsgs;

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCodeOfConductConsent() {
        return codeOfConductConsent;
    }

    public void setCodeOfConductConsent(String codeOfConductConsent) {
        this.codeOfConductConsent = codeOfConductConsent;
    }

    public ContactInfo getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(ContactInfo contactInfo) {
        this.contactInfo = contactInfo;
    }

    public SupplierInformation getSupplierInformation() {
        return supplierInformation;
    }

    public void setSupplierInformation(SupplierInformation supplierInformation) {
        this.supplierInformation = supplierInformation;
    }

    public CompanyIdentifier getCompanyIdentifier() {
        return companyIdentifier;
    }

    public void setCompanyIdentifier(CompanyIdentifier companyIdentifier) {
        this.companyIdentifier = companyIdentifier;
    }

    public CompanyInformation getCompanyInformation() {
        return companyInformation;
    }

    public void setCompanyInformation(CompanyInformation companyInformation) {
        this.companyInformation = companyInformation;
    }

    public Map<String, String> getErrorMsgs() {
        return errorMsgs;
    }

    public void setErrorMsgs(Map<String, String> errorMsgs) {
        this.errorMsgs = errorMsgs;
    }

}
