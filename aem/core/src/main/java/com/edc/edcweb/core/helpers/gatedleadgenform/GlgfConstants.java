package com.edc.edcweb.core.helpers.gatedleadgenform;

public class GlgfConstants {

    private GlgfConstants() {
        // SonarQube
    }

    public static final String POLICY_BASE_DATA_NODE = "/conf/edc/settings/wcm/policies/edc/components/content/gatedleadgenform/openquestions";
    public static final String QUESTIONS_BASE_DATA_NODE = "/apps/edc/settings/wcm/designs/edc-data/cglgfQuestionType";
    public static final String GENERIC_QUESTIONS_DATA_NODE = "/customerField";
    public static final String STANDARD_QUESTIONS_DATA_NODE = "/standard";

    public static final String QUESTION_TO_USE = "questionToUse";
    public static final String QUESTION_FIELD_NAME = "questionFieldName";
    public static final String QUESTION_NAME = "questionName";
    public static final String QUESTION_TYPE = "questionType";
    public static final String QUESTION_LOV = "questionLov";

    public static final String QUESTION_CONFIGURED_MULTIFIELD = "configured";

    public static final String NO_QUESTIONS_FOUND = "No questions Available, please contact support";
    public static final String MULTIPLE_DELIMITER = "::";
    public static final String ESCAPED_QUOTE = "\"";
    public static final String COMMA_ESCAPED_QUOTE = ",\"";

    public static final String CONTAINS_SELECT = "Select";
    public static final String QUESTION_TYPE_TEXT = "Text Field";
    public static final String QUESTION_TYPE_SINGLE_SELECT = "Single ".concat(CONTAINS_SELECT);
    public static final String QUESTION_TYPE_MULTI_SELECT = "Multi ".concat(CONTAINS_SELECT);
    public static final String QUESTION_TYPE_COUNTRY_MULTI_SELECT = "Country Multi ".concat(CONTAINS_SELECT);
    public static final String QUESTION_TYPE_YES_NO = "Yes / No Radio";
    public static final String QUESTION_TYPE_ADDRESS_BLOCK = "Address Block";
    public static final String QUESTION_TYPE_COUNTRY_BLOCK = "Country Prov/State City Block";
    public static final String QUESTION_TYPE_PHONE_BLOCK = "Phone Block";
    public static final String QUESTION_TYPE_NAME_BLOCK = "Name Block";

    public static final String QUESTION_MF_HELPER_TEXT = "questionHelperText";
    public static final String QUESTION_MF_LABEL = "questionLabel";
    public static final String QUESTION_MF_OVERRIDE = "questionOverride";
    public static final String QUESTION_MF_OTHER_LABEL = "questionOtherlabel";
    public static final String QUESTION_MF_OTHER_HELPER_TEXT = "questionOtherHelperText";

    public static final Object QUESTION_PHONE_EXT_FIELD_NAME = "CoMainPhoneExt";
    public static final Object QUESTION_ADD_LINE_2 = "companyAddress2";

    public static final String FORWARD_SLASH = "/";
}
