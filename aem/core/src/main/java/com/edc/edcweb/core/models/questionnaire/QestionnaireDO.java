package com.edc.edcweb.core.models.questionnaire;

import java.util.List;

import com.edc.edcweb.core.models.questionnaire.pojo.Question;

public class QestionnaireDO {

    List<Question> questions;

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

}
