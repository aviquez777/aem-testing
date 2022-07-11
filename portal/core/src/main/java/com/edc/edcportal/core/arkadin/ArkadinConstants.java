package com.edc.edcportal.core.arkadin;

/**
 * Values and properties needed to prepare the APIM Requests
 *
 */

public class ArkadinConstants {

    public static final String SEPARATOR = "&";
    public static final String EQUALS = "=";
    public static final String USER_AUTH_CODE = SEPARATOR + "APIUserAuthCode" + EQUALS;
    public static final String USER_CREDENTIALS = SEPARATOR + "APIUserCredentials" + EQUALS;
    public static final String OP_CODE_LIST = SEPARATOR + "OpCodeList" + EQUALS;
    public static final String OUTPUT_FORMAT = SEPARATOR + "OutputFormat" + EQUALS;
    public static final String LOOKUP_BY_USERID = SEPARATOR + "LookupByExternalUserID" + EQUALS;
    public static final String EXTERNAL_USERID = SEPARATOR + "ExternalUserID" + EQUALS;
    public static final String SHOWKEY = SEPARATOR + "ShowKey" + EQUALS;
    public static final String SHOWPACKAGEKEY = SEPARATOR + "ShowPackageKey" + EQUALS;
    public static final String TRIGGER_REG_EVENTS = SEPARATOR + "TriggerRegistrationEvents" + EQUALS;
    public static final String RESULT_OK = "OK";

    /**
     * Api opcodes
     *
     */
    public enum OpCodes {

        ARKADIN_OPCODE_REGISTER_TO_WEBINAR("R"), ARKADIN_OPCODE_GET_WEBINAR_REG_STATUS("Z");

        private String opCode;

        OpCodes(String opCode) {
            this.opCode = opCode;
        }

        public String getOpCode() {
            return opCode;
        }
    }
}
