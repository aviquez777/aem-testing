package com.edc.edcportal.core.eloqua.pojo;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EloquaUserProfileDO {
    private String cdoRecordId;
    private Map<String, EloquaDataItem> formFieldsValues;
    private boolean updateSucessfull;
    private boolean popReady = false;
    private String errorMessage;

    public String getCdoRecordId() {
        return cdoRecordId;
    }

    public void setCdoRecordId(String cdoRecordId) {
        this.cdoRecordId = cdoRecordId;
    }

    public Map<String, EloquaDataItem> getFormFieldsValues() {
        return formFieldsValues;
    }

    public void setFormFieldsValues(Map<String, EloquaDataItem> formFieldsValues) {
        this.formFieldsValues = formFieldsValues;
    }

    public boolean getUpdateSucessfull() {
        return updateSucessfull;
    }

    public void setUpdateSucessfull(boolean updateSucessfull) {
        this.updateSucessfull = updateSucessfull;
    }

    public boolean getPopReady() {
        return popReady;
    }

    public void setPopReady(boolean popReady) {
        this.popReady = popReady;
    }

    @JsonProperty("ErrorMessage")
    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

}
