
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
    "ReferalResponseTimeId",
    "DisplayValue"
})
public class FilterValueInQuoteResponseTime {

    @JsonProperty("ReferalResponseTimeId")
    private Integer referalResponseTimeId;
    @JsonProperty("DisplayValue")
    private String displayValue;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("ReferalResponseTimeId")
    public Integer getReferalResponseTimeId() {
        return referalResponseTimeId;
    }

    @JsonProperty("ReferalResponseTimeId")
    public void setReferalResponseTimeId(Integer referalResponseTimeId) {
        this.referalResponseTimeId = referalResponseTimeId;
    }

    @JsonProperty("DisplayValue")
    public String getDisplayValue() {
        return displayValue;
    }

    @JsonProperty("DisplayValue")
    public void setDisplayValue(String displayValue) {
        this.displayValue = displayValue;
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
