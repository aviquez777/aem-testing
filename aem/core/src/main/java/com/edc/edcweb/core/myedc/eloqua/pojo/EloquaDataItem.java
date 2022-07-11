package com.edc.edcweb.core.myedc.eloqua.pojo;

public class EloquaDataItem {

    private String eloquaValue;
    private String formFieldName;
    private String formFieldValue;
    private Boolean requiered = true;

    public String getEloquaValue() {
        return eloquaValue;
    }

    public void setEloquaValue(String eloquaValue) {
        this.eloquaValue = eloquaValue;
    }

    public String getFormFieldName() {
        return formFieldName;
    }

    public void setFormFieldName(String formFieldName) {
        this.formFieldName = formFieldName;
    }

    public String getFormFieldValue() {
        return formFieldValue;
    }

    public void setFormFieldValue(String formFieldValue) {
        this.formFieldValue = formFieldValue;
    }

    public Boolean getRequiered() {
        return requiered;
    }

    public void setRequiered(Boolean requiered) {
        this.requiered = requiered;
    }

}
