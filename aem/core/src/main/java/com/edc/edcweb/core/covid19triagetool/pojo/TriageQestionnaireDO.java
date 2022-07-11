package com.edc.edcweb.core.covid19triagetool.pojo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TriageQestionnaireDO {
    private List<Question> questions;
    private List<String> finantialInstitutionsList;

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public List<String> getFinantialInstitutionsList() {
        return finantialInstitutionsList;
    }

    public void setFinantialInstitutionsList(List<String> finantialInstitutionsList) {
        this.finantialInstitutionsList = finantialInstitutionsList;
    }

}
