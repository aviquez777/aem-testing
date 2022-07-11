package com.edc.edcweb.core.covid19triagetool.helpers;

public class TriageToolConstants {

    public static final String TRIAGE_RESOURCE_TYPE_BASE = "edc/components/content/covid19triagetool";
    public static final String TRIAGE_RESOURCE_TYPE = TRIAGE_RESOURCE_TYPE_BASE.concat("/triagetool");
    public static final String TRIAGE_QUESTIONNAIRE_RESOURCE_TYPE = TRIAGE_RESOURCE_TYPE_BASE.concat("/questionnaire");
    public static final String TRIAGE_RESULT_RESOURCE_TYPE = TRIAGE_RESOURCE_TYPE_BASE.concat("/result");
    public static final String BUTTONS = "buttons";
    public static final String SLIDER = "slider";
    public static final String DROPDOWN = "dropdown";

    public static final String SERVLET_SELECTOR = "covidtrigetool";
    public static final String SERVLET_RESOURCE_TYPE="edc/components/structure/page";

    public enum Properties {
        QUESTIONS_NULTIFIELD("questions"), QUESTION_TYPE("questionType"), QUESTION_TITLE("questionTitle"),
        QUESTION_TEXT("questionText"), QUESTION_NUMBER("questionNumber"), QUESTION_HELP_TEXT("questionHelpText"), DROPDOWN_PLACEHOLDER("answersPlaceholder"), DROPDOWN_MULTIPLE("dropDownMultiple"), BUTTTON_MULTIPLE("buttonMultiple"), SLIDER_MIN("sliderMin"),
        SLIDER_MAX("sliderMax"), SLIDER_STEP("sliderStep"), ANSWERS_NULTIFIELD("answers"), BUTTON_LABEL_VAR("answer"),
        BUTTON_VALUE_VAR("answerValue"), BUTTON_EXCLUSIVE_VAR("mutuallyExclusive"), FI_NULTIFIELD("fis"), FI_LIST_VALUE("fi"), FLOW_JSON("flowJson"), SOLUTIONS_JSON("solutionsJson"), SOLUTION_CODE("solutionCode");

        private String property;

        Properties(String property) {
            this.property = property;
        }

        public String getProperty() {
            return property;
        }
    }
}
