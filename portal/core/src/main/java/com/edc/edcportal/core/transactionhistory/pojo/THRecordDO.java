package com.edc.edcportal.core.transactionhistory.pojo;

import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.collect.ImmutableList;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class THRecordDO {

    private String id;
    private String type = "CustomObjectData";
    private List<THFieldValue> fieldValues = new LinkedList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<THFieldValue> getFieldValues() {
        // Task 22143 squid:S2384
        return fieldValues != null ? ImmutableList.copyOf(fieldValues) : fieldValues;
    }

    // Task 22143 squid:S2384
    @SuppressWarnings("squid:S2384")
    public void setFieldValues(List<THFieldValue> fieldValues) {
        /// we cannot store as an inmutable copy as we need to we need to modify the
        /// content on the property later.
        this.fieldValues = fieldValues;
    }

}
