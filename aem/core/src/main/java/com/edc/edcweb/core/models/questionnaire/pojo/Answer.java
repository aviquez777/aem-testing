package com.edc.edcweb.core.models.questionnaire.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Answer implements Comparable<Answer> {
    String label;
    String value;
    String goTo;
    String userStatus;
    boolean specialDeclaration = false;

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

    public String getGoTo() {
        return goTo;
    }

    public void setGoTo(String goTo) {
        this.goTo = goTo;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    // override compareTo method for sorting
    public int compareTo(Answer compareAnswer) {

        String compareLabel = ((Answer) compareAnswer).getLabel();

        // order is ascending
        return this.label.compareTo(compareLabel);

    }

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    public boolean isSpecialDeclaration() {
        return specialDeclaration;
    }

    public void setSpecialDeclaration(boolean specialDeclaration) {
        this.specialDeclaration = specialDeclaration;
    }
}
