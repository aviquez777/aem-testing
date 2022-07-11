
package com.edc.edcweb.core.apim.pojo.inlist.supplier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Collections;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "SupplierId",
    "SupplierName",
    "SupplierNameEnglish",
    "SupplierDescription",
    "ServiceTypes",
    "Services",
    "ModesOfTransportation",
    "IndustriesServed",
    "MarketsServed",
    "Certifications",
    "Languages",
    "ReferalResponseTime",
    "Email",
    "Website",
    "PhoneNumber",
    "PreferredContactChannel",
    "RequestQuoteURL",
    "BusinessType",
    "CompanySize",
    "YearsInTheBusiness",
    "MainAddress",
    "OtherAddresses"
})
public class SupplierProfileDO {

    @JsonProperty("SupplierId")
    private Integer supplierId;
    @JsonProperty("SupplierName")
    private String supplierName;
    @JsonProperty("SupplierNameEnglish")
    private String supplierNameEnglish;
    @JsonProperty("SupplierDescription")
    private String supplierDescription;
    @JsonProperty("ServiceTypes")
    private List<ServiceType> serviceTypes = Collections.emptyList();
    @JsonProperty("Services")
    private List<Service> services = Collections.emptyList();
    @JsonProperty("ModesOfTransportation")
    private List<ModesOfTransportation> modesOfTransportation = Collections.emptyList();
    @JsonProperty("IndustriesServed")
    private List<IndustriesServed> industriesServed = Collections.emptyList();
    @JsonProperty("MarketsServed")
    private List<MarketsServed> marketsServed = Collections.emptyList();
    @JsonProperty("Certifications")
    private List<Certification> certifications = Collections.emptyList();
    @JsonProperty("Languages")
    private List<Language> languages = Collections.emptyList();
    @JsonProperty("ReferalResponseTime")
    private String referalResponseTime;
    @JsonProperty("Email")
    private String email;
    @JsonProperty("Website")
    private String website;
    @JsonProperty("PhoneNumber")
    private String phoneNumber;
    @JsonProperty("PreferredContactChannel")
    private String preferredContactChannel;
    @JsonProperty("RequestQuoteURL")
    private String requestQuoteURL;
    @JsonProperty("BusinessType")
    private String businessType;
    @JsonProperty("CompanySize")
    private String companySize;
    @JsonProperty("YearsInTheBusiness")
    private String yearsInTheBusiness;
    @JsonProperty("MainAddress")
    private MainAddress mainAddress;
    @JsonProperty("OtherAddresses")
    private List<String> otherAddresses = Collections.emptyList();
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("SupplierId")
    public Integer getSupplierId() {
        return supplierId;
    }

    @JsonProperty("SupplierId")
    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    @JsonProperty("SupplierName")
    public String getSupplierName() {
        return supplierName;
    }

    @JsonProperty("SupplierName")
    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    @JsonProperty("SupplierNameEnglish")
    public String getSupplierNameEnglish() {
        return supplierNameEnglish;
    }

    @JsonProperty("SupplierNameEnglish")
    public void setSupplierNameEnglish(String supplierNameEnglish) {
        this.supplierNameEnglish = supplierNameEnglish;
    }

    @JsonProperty("SupplierDescription")
    public String getSupplierDescription() {
        return supplierDescription;
    }

    @JsonProperty("SupplierDescription")
    public void setSupplierDescription(String supplierDescription) {
        this.supplierDescription = supplierDescription;
    }

    @JsonProperty("ServiceTypes")
    public List<ServiceType> getServiceTypes() {
        return serviceTypes;
    }

    @JsonProperty("ServiceTypes")
    public void setServiceTypes(List<ServiceType> serviceTypes) {
        serviceTypes = new ArrayList<>(serviceTypes);
        this.serviceTypes = Collections.unmodifiableList(serviceTypes);
    }

    @JsonProperty("Services")
    public List<Service> getServices() {
        return services;
    }

    @JsonProperty("Services")
    public void setServices(List<Service> services) {
        services = new ArrayList<>(services);
        this.services = Collections.unmodifiableList(services);
    }

    @JsonProperty("ModesOfTransportation")
    public List<ModesOfTransportation> getModesOfTransportation() {
        return modesOfTransportation;
    }

    @JsonProperty("ModesOfTransportation")
    public void setModesOfTransportation(List<ModesOfTransportation> modesOfTransportation) {
        modesOfTransportation = new ArrayList<>(modesOfTransportation);
        this.modesOfTransportation = Collections.unmodifiableList(modesOfTransportation);
    }

    @JsonProperty("IndustriesServed")
    public List<IndustriesServed> getIndustriesServed() {
        return industriesServed;
    }

    @JsonProperty("IndustriesServed")
    public void setIndustriesServed(List<IndustriesServed> industriesServed) {
        industriesServed = new ArrayList<>(industriesServed);
        this.industriesServed = Collections.unmodifiableList(industriesServed);
    }

    @JsonProperty("MarketsServed")
    public List<MarketsServed> getMarketsServed() {
        return marketsServed;
    }

    @JsonProperty("MarketsServed")
    public void setMarketsServed(List<MarketsServed> marketsServed) {
        marketsServed = new ArrayList<>(marketsServed);
        this.marketsServed = Collections.unmodifiableList(marketsServed);
    }

    @JsonProperty("Certifications")
    public List<Certification> getCertifications() {
        return certifications;
    }

    @JsonProperty("Certifications")
    public void setCertifications(List<Certification> certifications) {
        certifications = new ArrayList<>(certifications);
        this.certifications = Collections.unmodifiableList(certifications);
    }

    @JsonProperty("Languages")
    public List<Language> getLanguages() {
        return languages;
    }

    @JsonProperty("Languages")
    public void setLanguages(List<Language> languages) {
        languages = new ArrayList<>(languages);
        this.languages = Collections.unmodifiableList(languages);
    }

    @JsonProperty("ReferalResponseTime")
    public String getReferalResponseTime() {
        return referalResponseTime;
    }

    @JsonProperty("ReferalResponseTime")
    public void setReferalResponseTime(String referalResponseTime) {
        this.referalResponseTime = referalResponseTime;
    }

    @JsonProperty("Email")
    public String getEmail() {
        return email;
    }

    @JsonProperty("Email")
    public void setEmail(String email) {
        this.email = email;
    }

    @JsonProperty("Website")
    public String getWebsite() {
        return website;
    }

    @JsonProperty("Website")
    public void setWebsite(String website) {
        this.website = website;
    }

    @JsonProperty("PhoneNumber")
    public String getPhoneNumber() {
        return phoneNumber;
    }

    @JsonProperty("PhoneNumber")
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @JsonProperty("PreferredContactChannel")
    public String getPreferredContactChannel() {
        return preferredContactChannel;
    }

    @JsonProperty("PreferredContactChannel")
    public void setPreferredContactChannel(String preferredContactChannel) {
        this.preferredContactChannel = preferredContactChannel;
    }

    @JsonProperty("RequestQuoteURL")
    public String getRequestQuoteURL() {
        return requestQuoteURL;
    }

    @JsonProperty("RequestQuoteURL")
    public void setRequestQuoteURL(String requestQuoteURL) {
        this.requestQuoteURL = requestQuoteURL;
    }

    @JsonProperty("BusinessType")
    public String getBusinessType() {
        return businessType;
    }

    @JsonProperty("BusinessType")
    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    @JsonProperty("CompanySize")
    public String getCompanySize() {
        return companySize;
    }

    @JsonProperty("CompanySize")
    public void setCompanySize(String companySize) {
        this.companySize = companySize;
    }

    @JsonProperty("YearsInTheBusiness")
    public String getYearsInTheBusiness() {
        return yearsInTheBusiness;
    }

    @JsonProperty("YearsInTheBusiness")
    public void setYearsInTheBusiness(String yearsInTheBusiness) {
        this.yearsInTheBusiness = yearsInTheBusiness;
    }

    @JsonProperty("MainAddress")
    public MainAddress getMainAddress() {
        return mainAddress;
    }

    @JsonProperty("MainAddress")
    public void setMainAddress(MainAddress mainAddress) {
        this.mainAddress = mainAddress;
    }

    @JsonProperty("OtherAddresses")
    public List<String> getOtherAddresses() {
        return otherAddresses;
    }

    @JsonProperty("OtherAddresses")
    public void setOtherAddresses(List<String> otherAddresses) {
        otherAddresses = new ArrayList<>(otherAddresses);
        this.otherAddresses = Collections.unmodifiableList(otherAddresses);
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
