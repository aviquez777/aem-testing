
package com.edc.edcweb.core.apim.pojo.inlist.supplier;

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
    "MarketId",
    "Name",
    "Code",
    "IsCountry"
})
public class MarketsServed {

    @JsonProperty("MarketId")
    private Integer marketId;
    @JsonProperty("Name")
    private String name;
    @JsonProperty("Code")
    private String code;
    @JsonProperty("IsCountry")
    private Boolean isCountry;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("MarketId")
    public Integer getMarketId() {
        return marketId;
    }

    @JsonProperty("MarketId")
    public void setMarketId(Integer marketId) {
        this.marketId = marketId;
    }

    @JsonProperty("Name")
    public String getName() {
        return name;
    }

    @JsonProperty("Name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("Code")
    public String getCode() {
        return code;
    }

    @JsonProperty("Code")
    public void setCode(String code) {
        this.code = code;
    }

    @JsonProperty("IsCountry")
    public Boolean getIsCountry() {
        return isCountry;
    }

    @JsonProperty("IsCountry")
    public void setIsCountry(Boolean isCountry) {
        this.isCountry = isCountry;
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
