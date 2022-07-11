package com.edc.edcweb.core.onlinepayments.helpers;

public class ConstantsOP {

    private ConstantsOP() {
        // Sonar Lint
    }

    public static final String DP_TOKEN_CACHE_NAME = "dp_token";

    public static final String SERVLET_BASE_URL = "/bin/op";
    public static final String SERVLET_URL_STEP_1 = SERVLET_BASE_URL + "/step1";
    public static final String SERVLET_URL_STEP_3 = SERVLET_BASE_URL + "/step3";

    public static final String MONERIS_ACTION_PRELOAD = "preload";
    public static final String MONERIS_ACTION_RECEIPT = "receipt";

    public static final String MONERIS_RECEIPT_RESULT_APPROVED_VALUE = "a";
    public static final String MONERIS_RECEIPT_RESULT_DECLINED_VALUE = "d";

    public static final String PPS_EDC_ID_FIELD_NAME = "accountEDCId";
    public static final String PPS_FIRST_NAME_FIELD_NAME = "contactFirstName";
    public static final String PPS_LAST_NAME_FIELD_NAME = "contactLastName";
    public static final String PPS_COMPANY_NAME_FIELD_NAME = "companyName";
    public static final String PPS_POLICY_NUMBER_FIELD_NAME = "policyNumber";
    public static final String PPS_AMOUNT_FIELD_NAME = "paymentAmount";
    public static final String PPS_CURRENCY_FIELD_NAME = "paymentCurrency";
    public static final String PPS_EMAIL_FIELD_NAME = "emailAddress";
    public static final String PPS_COMMENTS_FIELD_NAME = "comments";

    public static final String PPS_CUURENCY_CAN_DOL_VALUE = "CAD";
    public static final String PPS_CUURENCY_USA_DOL_VALUE = "USD";

    public static final String FORM_TICKET_FIELD_NAME = "ticket";

    public static final String API_ERROR_KEY = "apiError";
    public static final String API_ERROR_MSG = "Sorry, we were unable to initiate your form at this time. Please try again later.";
    public static final String ERROR_TEXT_PROPERTY = "defaultErrorMessage";

    public static final String CHECK_PATH_FIELD = "cp";
    public static final String RESOURCE_TYPE = "edc/components/content/onlinepayments";

    public static final String API_METHOD_VALUE = "CRD";

}
