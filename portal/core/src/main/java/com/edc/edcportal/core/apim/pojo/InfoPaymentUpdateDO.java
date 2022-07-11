package com.edc.edcportal.core.apim.pojo;

import com.edc.edcportal.core.apim.APIMConstants;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * InfoPaymentUpdateDO POJO to use with Jackson databind
 *
 */
public class InfoPaymentUpdateDO {
    @JsonProperty("Version")
    private String version;

    @JsonProperty("StatusCode")
    private int statusCode;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("ErrorMessage")
    private String errorMessage;

    @JsonProperty("Result")
    private InfoPaymentDO result = new InfoPaymentDO();

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public InfoPaymentDO getResult() {
        return result;
    }

    public void setResult(InfoPaymentDO result) {
        this.result = result;
    }

}
