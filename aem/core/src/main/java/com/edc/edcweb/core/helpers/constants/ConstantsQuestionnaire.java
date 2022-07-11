package com.edc.edcweb.core.helpers.constants;

public class ConstantsQuestionnaire {

    private ConstantsQuestionnaire() {
        // Sonar Lint
    }

    public static final String QUESTIONNAIRE_RESOURCE_TYPE = "edc/components/content/questionnaire";
    public static final String YES = "Yes";
    public static final String NO = "No";

    // Answer Types
    public static final String YES_NO = "yesNo";
    public static final String DROP_DOWN = "dropdown";
    public static final String EXPORT_COUNTRY = "exportCountry";
    public static final String OPERATION_COUNTRIES = "mmEntryCountryOfOperation";
    public static final String ENTRY_SECTOR = "mmEntrySector";

    public static final String QUESTIONNAIRE_JSON = "questionnairejson";
    public static final String APPROVAL_VALUE = "approvalvalue";
    public static final String REJECTION_VALUE = "rejectionvalue";
    public static final String QUESTION_TEXT = "questiontext";
    public static final String REVIEW_ANSWERS = "reviewyouranswers";
    public static final String END_QUESTIONNAIRE = "endofquestionnaire";
    public static final String APPROVED_TITLE = "approvedtitle";
    public static final String REJECTED_TITLE = "rejectedtitle";
    public static final String APPROVED_MESSAGE = "approvedmsg";
    public static final String REJECTED_MESSAGE = "rejectedmsg";
    public static final String RESET_BTN_TEXT = "resetbtntext";
    public static final String DROPDOWN_LABEL = "dropdownlabel";
    public static final String DD_REQUIRED_ERROR_MSG = "dropdownrequirederrormsg";
    public static final String DD_SELECT_ONE_MSG = "dropdownselectonemsg";
    public static final String LOADING_TEXT = "loadingtext";
    public static final String NEXT_BTN_TEXT = "nextbtntext";
    public static final String SUBMIT_BTN_TEXT = "submitbtntext";
    public static final String MODAL_ARIA_OPEN = "modalariaopen";
    public static final String MODAL_ARIA_CLOSE = "modalariaclose";
    public static final String QUESTIONNAIRE_TYPE = "questionnaireType";
    public static final String QUESTIONNAIRE_TYPE_DYNAMIC = "dynamic";
// Multiple drop down
    public static final String MULTIPLE_SELECT_CONFIRM = "multiconfirmtext";
    public static final String MULTIPLE_SELECT_CLEAR_ALL = "multiclearalltext";
    public static final String MULTIPLE_SELECT_SINGULAR = "multisingulartext";
    public static final String MULTIPLE_SELECT_PLURAL = "multipluraltext";
    public static final String REJECTED_CTA_TEXT = "rejectedctatext";
    public static final String REJECTED_CTA_URL = "rejectedctaurl";

    public enum Properties {
        QUESTIONS("questions"), QUESTION_NUMBER("questionNumber"), MAIN_TEXT("mainText"), IS_MULTIPLE("isMultiple"),
        ANSWER_PLACEEHOLDER("answersPlaceholder"), SAVE_NAME("saveName"), SECONDARY_TEXT("secondaryText"),
        HELPER_TITLE("helperTitle"), HELPER_TEXT("helperText"), ANSWER_TYPE("answerType"), ANSWER_LABEL("label"),
        ANSWER_VALUE("value"), ANSWER_GO_TO("goTo"), ANSWER_USER_STATUS("userStatus"),
        SPECIAL_DECLARATION("specialDeclaration"), LOV_GO_TO("Goto"), LOV_PLACEHOLDER("Placeholder"), 
        FORCE_ELGIBLE("countryForceElegible"), MAX_SELECTIONS("maxSelections");

        private String property;

        Properties(String property) {
            this.property = property;
        }

        public String getProperty() {
            return property;
        }
    }

}
