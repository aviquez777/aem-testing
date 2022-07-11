package com.edc.edcportal.core.eloqua.pojo;

public class EloquaDataItem {

    private String eloquaValue;
    private String formFieldName;
    private String formFieldValue;
    private boolean requiered = true;

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

    public boolean getRequiered() {
        return requiered;
    }

    public void setRequiered(boolean requiered) {
        this.requiered = requiered;
    }

}
