package com.edc.edcweb.core.telp.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MmEntryCountryOfOperation {

    @JsonProperty("Country")
    private TelpCountry country;

    public TelpCountry getCountry() {
        return country;
    }

    public void setCountry(TelpCountry country) {
        this.country = country;
    }

}
