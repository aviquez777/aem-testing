package com.edc.edcweb.core.myedc.eloqua.pojo;

import java.util.Map;

public class EloquaUserProfileDO {
    private String cdoRecordId;
    private Map<String, EloquaDataItem> formFieldsValues;
    private Boolean updateSucessfull;

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

    public Boolean getUpdateSucessfull() {
        return updateSucessfull;
    }

    public void setUpdateSucessfull(Boolean updateSucessfull) {
        this.updateSucessfull = updateSucessfull;
    }

}
