package com.edc.edcportal.core.lovapi.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GenericLov {

    @JsonProperty("Code")
    private String value;

    @JsonProperty("DescriptionEnglish")
    private String enName;

    @JsonProperty("DescriptionFrench")
    private String frName;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getEnName() {
        return enName;
    }

    public void setEnName(String enName) {
        this.enName = enName;
    }

    public String getFrName() {
        return frName;
    }

    public void setFrName(String frName) {
        this.frName = frName;
    }

}
