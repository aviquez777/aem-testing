package com.edc.edcweb.core.gps.pojo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Collections;

import org.apache.sling.api.request.RequestParameter;

public class GpsFormFields {
    private Map<String, String[]> formFields = new HashMap<>();
    private List<RequestParameter> fileFields = Collections.emptyList();

    public Map<String, String[]> getFormFields() {
        return formFields;
    }

    public void setFormFields(Map<String, String[]> formFields) {
        this.formFields = formFields;
    }

    public List<RequestParameter> getFileFields() {
        return fileFields;
    }

    public void setFileFields(List<RequestParameter> fileFields) {
        fileFields = new ArrayList<>(fileFields);
        this.fileFields = Collections.unmodifiableList(fileFields);
    }

}
