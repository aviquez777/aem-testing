package com.edc.edcweb.core.telp.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BeneficialOwner {
    @JsonProperty("UltimateBeneficialOwnerFirstName")
    private String uboFirstName;

    @JsonProperty("UltimateBeneficialOwnerLastName")
    private String uboLastName;

    @JsonProperty("UltimateBeneficialOwnerCountryOfResidence")
    private TelpCountry uboCountry;

    public String getUboFirstName() {
        return uboFirstName;
    }

    public void setUboFirstName(String uboFirstName) {
        this.uboFirstName = uboFirstName;
    }

    public String getUboLastName() {
        return uboLastName;
    }

    public void setUboLastName(String uboLastName) {
        this.uboLastName = uboLastName;
    }

    public TelpCountry getUboCountry() {
        return uboCountry;
    }

    public void setUboCountry(TelpCountry uboCountry) {
        this.uboCountry = uboCountry;
    }

}
