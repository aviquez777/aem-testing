package com.edc.edcweb.core.lovapi.pojo;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FinancialInstitutionsDO {

    @JsonProperty("Version")
    private String version;
    @JsonProperty("StatusCode")
    private String statusCode;
    @JsonProperty("ErrorMessage")
    private String errorMessage;
    @JsonProperty("Result")
    private List<FinancialInstitution> result = Collections.emptyList();

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public List<FinancialInstitution> getResult() {
        return result;
    }

    public void setResult(List<FinancialInstitution> result) {
        this.result = result;
        result = new ArrayList<>(result);
        this.result = Collections.unmodifiableList(result);
    }
}
