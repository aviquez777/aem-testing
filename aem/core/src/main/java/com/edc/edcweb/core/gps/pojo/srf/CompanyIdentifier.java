package com.edc.edcweb.core.gps.pojo.srf;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CompanyIdentifier {
    // Canada Only
    @JsonProperty("CanGstHstNumber")
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private String canGstHstNumber;

    @JsonProperty("CanSinNumber")
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private String canSinNumber;
    // End Canada Only

    // USA Only
    @JsonProperty("UsaTin")
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private String usaTin;
    // End USA only

    // International Only
    @JsonProperty("IntBin")
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private String intBin;

    @JsonProperty("IntVat")
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private String intVat;

    @JsonProperty("IntSin")
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private String intSin;
    // End International Only

    @JsonProperty("RegistrationType")
    private String registrationType;

    @JsonIgnore
    Map<String, String> errorMsgs = new HashMap<>();

    public String getCanGstHstNumber() {
        return canGstHstNumber;
    }

    public void setCanGstHstNumber(String canGstHstNumber) {
        this.canGstHstNumber = canGstHstNumber;
    }

    public String getCanSinNumber() {
        return canSinNumber;
    }

    public void setCanSinNumber(String canSinNumber) {
        this.canSinNumber = canSinNumber;
    }

    public String getUsaTin() {
        return usaTin;
    }

    public void setUsaTin(String usaTin) {
        this.usaTin = usaTin;
    }

    public String getIntBin() {
        return intBin;
    }

    public void setIntBin(String intBin) {
        this.intBin = intBin;
    }

    public String getIntVat() {
        return intVat;
    }

    public void setIntVat(String intVat) {
        this.intVat = intVat;
    }

    public String getIntSin() {
        return intSin;
    }

    public void setIntSin(String intSin) {
        this.intSin = intSin;
    }

    public String getRegistrationType() {
        return registrationType;
    }

    public void setRegistrationType(String registrationType) {
        this.registrationType = registrationType;
    }

    public Map<String, String> getErrorMsgs() {
        return errorMsgs;
    }

    public void setErrorMsgs(Map<String, String> errorMsgs) {
        this.errorMsgs = errorMsgs;
    }
}
