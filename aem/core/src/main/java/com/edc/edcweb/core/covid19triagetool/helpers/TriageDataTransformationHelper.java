package com.edc.edcweb.core.covid19triagetool.helpers;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;

import com.day.cq.wcm.api.Page;
import com.edc.edcweb.core.covid19triagetool.pojo.Answer;
import com.edc.edcweb.core.covid19triagetool.pojo.Question;
import com.edc.edcweb.core.covid19triagetool.pojo.TriageQestionnaireDO;
import com.edc.edcweb.core.helpers.Request;
import com.edc.edcweb.core.helpers.ResourceResolverHelper;

public class TriageDataTransformationHelper {

    private TriageDataTransformationHelper() {
        // Sonar Lint
    }

    public static TriageQestionnaireDO populateQuestionnaire(SlingHttpServletRequest request) {
        TriageQestionnaireDO triageQestionnaireDO = new TriageQestionnaireDO();
        Resource trieageQuestRoot = getComponent(request, TriageToolConstants.TRIAGE_QUESTIONNAIRE_RESOURCE_TYPE);
        populateQuestions(triageQestionnaireDO, trieageQuestRoot);
        return triageQestionnaireDO;
    }

    public static String populateFlowJson(SlingHttpServletRequest request) {
        return populateJson(request, TriageToolConstants.TRIAGE_QUESTIONNAIRE_RESOURCE_TYPE, TriageToolConstants.Properties.FLOW_JSON.getProperty());
    }

    public static String populateSolutionJson(SlingHttpServletRequest request) {
        return populateJson(request, TriageToolConstants.TRIAGE_RESULT_RESOURCE_TYPE, TriageToolConstants.Properties.SOLUTIONS_JSON.getProperty());
    }

    public static String populateJson(SlingHttpServletRequest request, String component, String fieldValue) {
        String empty = "";
        Resource triageComponent = getComponent(request, component);
        String json = triageComponent.getValueMap().get(fieldValue, String.class);
        return StringUtils.isNotBlank(json) ? json : empty;
    }

    public static Resource getComponent(SlingHttpServletRequest request, String component) {
        Resource pageRes = Request.getCurrentPageResource(request, null);
        // Use servlet path to resolve the page if campaign
        if (pageRes == null) {
            String servletPath = request.getPathInfo();
            int lastIdx = servletPath.lastIndexOf("/");
            servletPath = servletPath.substring(0, lastIdx);
            pageRes = Request.getCurrentPageResource(request, servletPath);
        }
        Page currentPage = pageRes.adaptTo(Page.class);
        // Return the component resource
        return ResourceResolverHelper.getResourceByTypeAndParent(currentPage, component, TriageToolConstants.TRIAGE_RESOURCE_TYPE);
    }

    private static void populateQuestions(TriageQestionnaireDO triageQestionnaireDO, Resource trieageQuestRoot) {
        List<Question> questionList = new LinkedList<>();
        Resource trieageQuestRes = trieageQuestRoot
                .getChild(TriageToolConstants.Properties.QUESTIONS_NULTIFIELD.getProperty());
        if (trieageQuestRes != null) {
            Iterator<Resource> questionsItRes = trieageQuestRes.listChildren();
            while (questionsItRes.hasNext()) {
                Resource questionRes = questionsItRes.next();
                List<Answer> answerList = new LinkedList<>();
                // Questions
                Question question = new Question();
                int questionNumber = questionRes.getValueMap()
                        .get(TriageToolConstants.Properties.QUESTION_NUMBER.getProperty(), Integer.class);
                question.setNumber(questionNumber);
                String questionType = questionRes.getValueMap()
                        .get(TriageToolConstants.Properties.QUESTION_TYPE.getProperty(), String.class);
                question.setType(questionType);
                question.setMainText(questionRes.getValueMap()
                        .get(TriageToolConstants.Properties.QUESTION_TEXT.getProperty(), String.class));
                question.setTitle(questionRes.getValueMap()
                        .get(TriageToolConstants.Properties.QUESTION_TITLE.getProperty(), String.class));
                question.setHelpText(questionRes.getValueMap()
                        .get(TriageToolConstants.Properties.QUESTION_HELP_TEXT.getProperty(), String.class));
                question.setPleaseSelectText(questionRes.getValueMap()
                        .get(TriageToolConstants.Properties.DROPDOWN_PLACEHOLDER.getProperty(), String.class));
                boolean ismultiple = false;
                ismultiple = Boolean.parseBoolean(questionRes.getValueMap()
                        .get(TriageToolConstants.Properties.BUTTTON_MULTIPLE.getProperty(), String.class));
                if (ismultiple) {
                    question.setMulti(ismultiple);
                }
                addAnswers(questionRes, answerList);
                // Add data to question object
                question.setAnswers(answerList);
                // Add to questions list
                questionList.add(question);
            }
        }
        // add the question list to main data object
        triageQestionnaireDO.setQuestions(questionList);
        triageQestionnaireDO.setFinantialInstitutionsList(populateFi(trieageQuestRoot));
    }

    private static void addAnswers(Resource questionRes, List<Answer> answerList) {
        Iterator<Resource> answersItRoot = questionRes.listChildren();
        // Default property names
        String labelVar = TriageToolConstants.Properties.BUTTON_LABEL_VAR.getProperty();
        String valueVar = TriageToolConstants.Properties.BUTTON_VALUE_VAR.getProperty();
        String mutuallyExclusiveVar = TriageToolConstants.Properties.BUTTON_EXCLUSIVE_VAR.getProperty();
        while (answersItRoot.hasNext()) {
            Iterator<Resource> answerItRes = answersItRoot.next().listChildren();
            while (answerItRes.hasNext()) {
                Answer answer = new Answer();
                Resource answerRes = answerItRes.next();
                String ansValue = answerRes.getValueMap().get(valueVar, String.class);
                answer.setValue(ansValue);
                answer.setLabel(answerRes.getValueMap().get(labelVar, String.class));
                boolean mutuallyExcl = Boolean
                        .parseBoolean(answerRes.getValueMap().get(mutuallyExclusiveVar, String.class));
                if (mutuallyExcl) {
                    answer.setMutuallyExclusive(mutuallyExcl);
                }
                answerList.add(answer);
            }
        }
    }

    private static List<String> populateFi(Resource trieageQuestRoot) {
        List<String> fiList = new LinkedList<>();
        Resource trieageFiList = trieageQuestRoot.getChild(TriageToolConstants.Properties.FI_NULTIFIELD.getProperty());
        if (trieageFiList != null) {
            Iterator<Resource> fiItRes = trieageFiList.listChildren();
            while (fiItRes.hasNext()) {
                Resource fiRes = fiItRes.next();
                String fiValue = fiRes.getValueMap().get(TriageToolConstants.Properties.FI_LIST_VALUE.getProperty(),
                        String.class);
                fiList.add(fiValue);
            }
        }
        return fiList;
    }
}
