
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
    "ServiceId",
    "ServiceTypeId",
    "Name",
    "Code",
    "IsCustom"
})
public class FilterValueInModesOfTransportation {

    @JsonProperty("ServiceId")
    private Integer serviceId;
    @JsonProperty("ServiceTypeId")
    private Integer serviceTypeId;
    @JsonProperty("Name")
    private String name;
    @JsonProperty("Code")
    private String code;
    @JsonProperty("IsCustom")
    private Boolean isCustom;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("ServiceId")
    public Integer getServiceId() {
        return serviceId;
    }

    @JsonProperty("ServiceId")
    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }

    @JsonProperty("ServiceTypeId")
    public Integer getServiceTypeId() {
        return serviceTypeId;
    }

    @JsonProperty("ServiceTypeId")
    public void setServiceTypeId(Integer serviceTypeId) {
        this.serviceTypeId = serviceTypeId;
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

    @JsonProperty("IsCustom")
    public Boolean getIsCustom() {
        return isCustom;
    }

    @JsonProperty("IsCustom")
    public void setIsCustom(Boolean isCustom) {
        this.isCustom = isCustom;
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
