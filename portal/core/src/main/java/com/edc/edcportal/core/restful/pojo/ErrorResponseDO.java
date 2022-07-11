package com.edc.edcportal.core.restful.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponseDO {

    private Integer error;
    @JsonProperty("ErrorMessage")
    private String errorMessage;
    private String extendedError;

    public Integer getError() {
        return error;
    }

    public void setError(Integer error) {
        this.error = error;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getExtendedError() {
        return extendedError;
    }

    public void setExtendedError(String extendedError) {
        this.extendedError = extendedError;
    }

}
