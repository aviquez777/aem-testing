
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
    "Id",
    "StreetAddress",
    "City",
    "Province",
    "Country",
    "PostalCode",
    "IsHeadQuarters"
})
public class MainAddress {

    @JsonProperty("Id")
    private Integer id;
    @JsonProperty("StreetAddress")
    private String streetAddress;
    @JsonProperty("City")
    private String city;
    @JsonProperty("Province")
    private String province;
    @JsonProperty("Country")
    private String country;
    @JsonProperty("PostalCode")
    private String postalCode;
    @JsonProperty("IsHeadQuarters")
    private Boolean isHeadQuarters;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("Id")
    public Integer getId() {
        return id;
    }

    @JsonProperty("Id")
    public void setId(Integer id) {
        this.id = id;
    }

    @JsonProperty("StreetAddress")
    public String getStreetAddress() {
        return streetAddress;
    }

    @JsonProperty("StreetAddress")
    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    @JsonProperty("City")
    public String getCity() {
        return city;
    }

    @JsonProperty("City")
    public void setCity(String city) {
        this.city = city;
    }

    @JsonProperty("Province")
    public String getProvince() {
        return province;
    }

    @JsonProperty("Province")
    public void setProvince(String province) {
        this.province = province;
    }

    @JsonProperty("Country")
    public String getCountry() {
        return country;
    }

    @JsonProperty("Country")
    public void setCountry(String country) {
        this.country = country;
    }

    @JsonProperty("PostalCode")
    public String getPostalCode() {
        return postalCode;
    }

    @JsonProperty("PostalCode")
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    @JsonProperty("IsHeadQuarters")
    public Boolean getIsHeadQuarters() {
        return isHeadQuarters;
    }

    @JsonProperty("IsHeadQuarters")
    public void setIsHeadQuarters(Boolean isHeadQuarters) {
        this.isHeadQuarters = isHeadQuarters;
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
