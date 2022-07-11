package com.edc.edcweb.core.models.questionnaire.pojo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Question {
    Integer number;
    String mainText;
    String answersPlaceholder;
    boolean saveAnswer;
    String saveName;
    String secondaryText;
    String helperTitle;
    String helperText;
    Boolean isMulti;
    Integer maxSelections;

    List<Answer> answers;

    // Answers Yes
    @JsonIgnore
    String labelYes;
    @JsonIgnore
    String valueYes;
    @JsonIgnore
    String goToYes;
    @JsonIgnore
    String userStatusYes;
    // Answers No
    @JsonIgnore
    String labelNo;
    @JsonIgnore
    String valueNo;
    @JsonIgnore
    String goToNo;
    @JsonIgnore
    String userStatusNo;

    @JsonIgnore
    String lovPath;

    @JsonIgnore
    Boolean forceElegible;

    public Integer getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = Integer.parseInt(number);
    }

    public String getMainText() {
        return mainText;
    }

    public void setMainText(String mainText) {
        this.mainText = mainText;
    }

    public String getAnswersPlaceholder() {
        return answersPlaceholder;
    }

    public void setAnswersPlaceholder(String answersPlaceholder) {
        this.answersPlaceholder = answersPlaceholder;
    }

    public boolean isSaveAnswer() {
        return saveAnswer;
    }

    public void setSaveAnswer(boolean saveAnswer) {
        this.saveAnswer = saveAnswer;
    }

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    public String getSaveName() {
        return saveName;
    }

    public void setSaveName(String saveName) {
        this.saveName = saveName;
    }

    public String getSecondaryText() {
        return secondaryText;
    }

    public void setSecondaryText(String secondaryText) {
        this.secondaryText = secondaryText;
    }

    public String getHelperTitle() {
        return helperTitle;
    }

    public void setHelperTitle(String helperTitle) {
        this.helperTitle = helperTitle;
    }

    public String getHelperText() {
        return helperText;
    }

    public void setHelperText(String helperText) {
        this.helperText = helperText;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }

    public String getLovPath() {
        return lovPath;
    }

    public void setLovPath(String lovPath) {
        this.lovPath = lovPath;
    }

    public String getLabelYes() {
        return labelYes;
    }

    public void setLabelYes(String labelYes) {
        this.labelYes = labelYes;
    }

    public String getValueYes() {
        return valueYes;
    }

    public void setValueYes(String valueYes) {
        this.valueYes = valueYes;
    }

    public String getGoToYes() {
        return goToYes;
    }

    public void setGoToYes(String goToYes) {
        this.goToYes = goToYes;
    }

    public String getUserStatusYes() {
        return userStatusYes;
    }

    public void setUserStatusYes(String userStatusYes) {
        this.userStatusYes = userStatusYes;
    }

    public String getLabelNo() {
        return labelNo;
    }

    public void setLabelNo(String labelNo) {
        this.labelNo = labelNo;
    }

    public String getValueNo() {
        return valueNo;
    }

    public void setValueNo(String valueNo) {
        this.valueNo = valueNo;
    }

    public String getGoToNo() {
        return goToNo;
    }

    public void setGoToNo(String goToNo) {
        this.goToNo = goToNo;
    }

    public String getUserStatusNo() {
        return userStatusNo;
    }

    public void setUserStatusNo(String userStatusNo) {
        this.userStatusNo = userStatusNo;
    }

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    public void setIsMulti(Boolean isMulti) {
        this.isMulti = isMulti;
    }

    public void setForceElegible(Boolean forceElegible) {
        this.forceElegible = forceElegible;
    }

    public Boolean getForceElegible() {
        return forceElegible;
    }

    public Boolean getIsMulti() {
        return isMulti;
    }

    public Integer getMaxSelections() {
        return maxSelections;
    }

    public void setMaxSelections(Integer maxSelections) {
        this.maxSelections = maxSelections;
    }

}
