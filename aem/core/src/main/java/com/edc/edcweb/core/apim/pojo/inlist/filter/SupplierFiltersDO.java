
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
    "ServiceTypes",
    "MarketsServed",
    "ModesOfTransportation",
    "Services",
    "Industries",
    "QuoteResponseTimes",
    "IsAllFilterSections"
})
public class SupplierFiltersDO {

    @JsonProperty("ServiceTypes")
    private ServiceTypes serviceTypes;
    @JsonProperty("MarketsServed")
    private MarketsServed marketsServed;
    @JsonProperty("ModesOfTransportation")
    private ModesOfTransportation modesOfTransportation;
    @JsonProperty("Services")
    private Services services;
    @JsonProperty("Industries")
    private Industries industries;
    @JsonProperty("QuoteResponseTimes")
    private QuoteResponseTimes quoteResponseTimes;
    @JsonProperty("IsAllFilterSections")
    private Boolean isAllFilterSections;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("ServiceTypes")
    public ServiceTypes getServiceTypes() {
        return serviceTypes;
    }

    @JsonProperty("ServiceTypes")
    public void setServiceTypes(ServiceTypes serviceTypes) {
        this.serviceTypes = serviceTypes;
    }

    @JsonProperty("MarketsServed")
    public MarketsServed getMarketsServed() {
        return marketsServed;
    }

    @JsonProperty("MarketsServed")
    public void setMarketsServed(MarketsServed marketsServed) {
        this.marketsServed = marketsServed;
    }

    @JsonProperty("ModesOfTransportation")
    public ModesOfTransportation getModesOfTransportation() {
        return modesOfTransportation;
    }

    @JsonProperty("ModesOfTransportation")
    public void setModesOfTransportation(ModesOfTransportation modesOfTransportation) {
        this.modesOfTransportation = modesOfTransportation;
    }

    @JsonProperty("Services")
    public Services getServices() {
        return services;
    }

    @JsonProperty("Services")
    public void setServices(Services services) {
        this.services = services;
    }

    @JsonProperty("Industries")
    public Industries getIndustries() {
        return industries;
    }

    @JsonProperty("Industries")
    public void setIndustries(Industries industries) {
        this.industries = industries;
    }

    @JsonProperty("QuoteResponseTimes")
    public QuoteResponseTimes getQuoteResponseTimes() {
        return quoteResponseTimes;
    }

    @JsonProperty("QuoteResponseTimes")
    public void setQuoteResponseTimes(QuoteResponseTimes quoteResponseTimes) {
        this.quoteResponseTimes = quoteResponseTimes;
    }

    @JsonProperty("IsAllFilterSections")
    public Boolean getIsAllFilterSections() {
        return isAllFilterSections;
    }

    @JsonProperty("IsAllFilterSections")
    public void setIsAllFilterSections(Boolean isAllFilterSections) {
        this.isAllFilterSections = isAllFilterSections;
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
