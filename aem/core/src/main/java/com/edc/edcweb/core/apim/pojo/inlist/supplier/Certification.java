
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
    "CertificationId",
    "Code",
    "ServiceTypeId",
    "CertificationName",
    "IsCustom"
})
public class Certification {

    @JsonProperty("CertificationId")
    private Integer certificationId;
    @JsonProperty("Code")
    private String code;
    @JsonProperty("ServiceTypeId")
    private Integer serviceTypeId;
    @JsonProperty("CertificationName")
    private String certificationName;
    @JsonProperty("IsCustom")
    private Boolean isCustom;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("CertificationId")
    public Integer getCertificationId() {
        return certificationId;
    }

    @JsonProperty("CertificationId")
    public void setCertificationId(Integer certificationId) {
        this.certificationId = certificationId;
    }

    @JsonProperty("Code")
    public String getCode() {
        return code;
    }

    @JsonProperty("Code")
    public void setCode(String code) {
        this.code = code;
    }

    @JsonProperty("ServiceTypeId")
    public Integer getServiceTypeId() {
        return serviceTypeId;
    }

    @JsonProperty("ServiceTypeId")
    public void setServiceTypeId(Integer serviceTypeId) {
        this.serviceTypeId = serviceTypeId;
    }

    @JsonProperty("CertificationName")
    public String getCertificationName() {
        return certificationName;
    }

    @JsonProperty("CertificationName")
    public void setCertificationName(String certificationName) {
        this.certificationName = certificationName;
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
