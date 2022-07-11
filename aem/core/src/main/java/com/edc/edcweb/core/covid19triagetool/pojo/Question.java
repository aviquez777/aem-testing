package com.edc.edcweb.core.covid19triagetool.pojo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Question {
    int number;
    boolean multi;
    String type;
    String mainText;
    String title;
    @JsonProperty("secondaryText")
    String helpText;
    @JsonProperty("answersPlaceholder")
    String pleaseSelectText;
    List<Answer> answers;

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    public boolean isMulti() {
        return multi;
    }

    public void setMulti(boolean multi) {
        this.multi = multi;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMainText() {
        return mainText;
    }

    public void setMainText(String mainText) {
        this.mainText = mainText;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHelpText() {
        return helpText;
    }

    public void setHelpText(String helpText) {
        this.helpText = helpText;
    }

    public String getPleaseSelectText() {
        return pleaseSelectText;
    }

    public void setPleaseSelectText(String pleaseSelectText) {
        this.pleaseSelectText = pleaseSelectText;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }

}
