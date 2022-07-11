package com.edc.edcportal.core.models;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;

@Model(adaptables = Resource.class)
public class ApimFieldDefinition {
    @Inject
    @Named("eloquaId")
    @Optional
    private String eloquaId;

    @Inject
    @Named("apimKey")
    @Optional
    private String apimKey;

    @Inject
    @Named("fieldName")
    @Optional
    private String fieldName;

    @Inject
    @Named("valueFrom")
    @Optional
    private String valueFrom;

    @Inject
    @Named("useFunction")
    @Optional
    private String useFunction;

    @Inject
    @Named("hardValue")
    @Optional
    private String hardValue;

    public String getEloquaId() {
        return eloquaId;
    }

    public void setEloquaId(String eloquaId) {
        this.eloquaId = eloquaId;
    }

    public String getApimKey() {
        return apimKey;
    }

    public void setApimKey(String apimKey) {
        this.apimKey = apimKey;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getValueFrom() {
        return valueFrom;
    }

    public void setValueFrom(String valueFrom) {
        this.valueFrom = valueFrom;
    }

    public String getUseFunction() {
        return useFunction;
    }

    public void setUseFunction(String useFunction) {
        this.useFunction = useFunction;
    }

    public String getHardValue() {
        return hardValue;
    }

    public void setHardValue(String hardValue) {
        this.hardValue = hardValue;
    }

}
