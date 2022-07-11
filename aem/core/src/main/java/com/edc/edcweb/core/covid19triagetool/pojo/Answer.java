package com.edc.edcweb.core.covid19triagetool.pojo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Answer {
    private String label;
    private String value;
    private boolean mutuallyExclusive;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    public boolean isMutuallyExclusive() {
        return mutuallyExclusive;
    }

    public void setMutuallyExclusive(boolean mutuallyExclusive) {
        this.mutuallyExclusive = mutuallyExclusive;
    }

}
