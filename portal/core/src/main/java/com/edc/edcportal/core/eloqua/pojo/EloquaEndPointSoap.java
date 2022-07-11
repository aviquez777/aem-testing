package com.edc.edcportal.core.eloqua.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class EloquaEndPointSoap {

    private String standard;
    private String dataTransfer;
    private String externalAction;
    private String email;

    @JsonProperty("standard")
    public String getStandard() {
        return standard;
    }

    public void setStandard(String standard) {
        this.standard = standard;
    }

    @JsonProperty("dataTransfer")
    public String getDataTransfer() {
        return dataTransfer;
    }

    public void setDataTransfer(String dataTransfer) {
        this.dataTransfer = dataTransfer;
    }

    @JsonProperty("externalAction")
    public String getExternalAction() {
        return externalAction;
    }

    public void setExternalAction(String externalAction) {
        this.externalAction = externalAction;
    }

    @JsonProperty("email")
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

}
