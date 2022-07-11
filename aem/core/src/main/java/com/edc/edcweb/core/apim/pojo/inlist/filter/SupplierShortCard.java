
package com.edc.edcweb.core.apim.pojo.inlist.filter;

import java.util.HashMap;
import java.util.Map;
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
    "City",
    "Country",
    "NumberOfOtherLocations",
    "Services",
    "ModesOfTransportation",
    "MarketsServed",
    "Industries",
    "ReferalResponseTime",
    "ReferalResponseTimes"
})
public class SupplierShortCard {

    @JsonProperty("SupplierId")
    private Integer supplierId;
    @JsonProperty("SupplierName")
    private String supplierName;
    @JsonProperty("SupplierNameEnglish")
    private String supplierNameEnglish;
    @JsonProperty("SupplierDescription")
    private String supplierDescription;
    @JsonProperty("ServiceTypes")
    private ServiceTypesInCard serviceTypes;
    @JsonProperty("City")
    private String city;
    @JsonProperty("Country")
    private String country;
    @JsonProperty("NumberOfOtherLocations")
    private Integer numberOfOtherLocations;
    @JsonProperty("Services")
    private ServicesInCard services;
    @JsonProperty("ModesOfTransportation")
    private ModesOfTransportationInCard modesOfTransportation;
    @JsonProperty("MarketsServed")
    private MarketsServedInCard marketsServed;
    @JsonProperty("Industries")
    private IndustriesInCard industries;
    @JsonProperty("ReferalResponseTime")
    private String referalResponseTime;
    @JsonProperty("ReferalResponseTimes")
    private ReferalResponseTimes referalResponseTimes;
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
    public ServiceTypesInCard getServiceTypes() {
        return serviceTypes;
    }

    @JsonProperty("ServiceTypes")
    public void setServiceTypes(ServiceTypesInCard serviceTypes) {
        this.serviceTypes = serviceTypes;
    }

    @JsonProperty("City")
    public String getCity() {
        return city;
    }

    @JsonProperty("City")
    public void setCity(String city) {
        this.city = city;
    }

    @JsonProperty("Country")
    public String getCountry() {
        return country;
    }

    @JsonProperty("Country")
    public void setCountry(String country) {
        this.country = country;
    }

    @JsonProperty("NumberOfOtherLocations")
    public Integer getNumberOfOtherLocations() {
        return numberOfOtherLocations;
    }

    @JsonProperty("NumberOfOtherLocations")
    public void setNumberOfOtherLocations(Integer numberOfOtherLocations) {
        this.numberOfOtherLocations = numberOfOtherLocations;
    }

    @JsonProperty("Services")
    public ServicesInCard getServices() {
        return services;
    }

    @JsonProperty("Services")
    public void setServices(ServicesInCard services) {
        this.services = services;
    }

    @JsonProperty("ModesOfTransportation")
    public ModesOfTransportationInCard getModesOfTransportation() {
        return modesOfTransportation;
    }

    @JsonProperty("ModesOfTransportation")
    public void setModesOfTransportation(ModesOfTransportationInCard modesOfTransportation) {
        this.modesOfTransportation = modesOfTransportation;
    }

    @JsonProperty("MarketsServed")
    public MarketsServedInCard getMarketsServed() {
        return marketsServed;
    }

    @JsonProperty("MarketsServed")
    public void setMarketsServed(MarketsServedInCard marketsServed) {
        this.marketsServed = marketsServed;
    }

    @JsonProperty("Industries")
    public IndustriesInCard getIndustries() {
        return industries;
    }

    @JsonProperty("Industries")
    public void setIndustries(IndustriesInCard industries) {
        this.industries = industries;
    }

    @JsonProperty("ReferalResponseTime")
    public String getReferalResponseTime() {
        return referalResponseTime;
    }

    @JsonProperty("ReferalResponseTime")
    public void setReferalResponseTime(String referalResponseTime) {
        this.referalResponseTime = referalResponseTime;
    }

    @JsonProperty("ReferalResponseTimes")
    public ReferalResponseTimes getReferalResponseTimes() {
        return referalResponseTimes;
    }

    @JsonProperty("ReferalResponseTimes")
    public void setReferalResponseTimes(ReferalResponseTimes referalResponseTimes) {
        this.referalResponseTimes = referalResponseTimes;
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
