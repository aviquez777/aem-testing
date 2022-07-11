package com.edc.edcweb.core.telp.helpers;

public class TelpConstants {

    public static final String NAME = "Name";
    public static final String TELP_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final String FORM_VALUE_YES = "yes";
    public static final String FORM_VALUE_NO = "no";
    public static final String FORM_VALUE_YES_FR = "oui";
    public static final String FORM_VALUE_NO_FR = "non";
    public static final String CDIA_FORM_VALUE_YES = FORM_VALUE_YES;
    public static final String VALUE_YES = "Y";
    public static final String VALUE_NO = "N";
    public static final String GRANT_TYPE = "client_credentials";
    public static final String JSON_ERROR_MESSAGE_KEY = "ErrorMessage";
    public static final String FORM_TYPE_PROPERTY = "formType";
    public static final String PRE_SCREEN_PROPERTY = "preScreen";
    public static final boolean DEFAULT_PRE_SCREEN = false;

    public static final String DEFAULT_FORM_TYPE = "TLP";
    public static final String COVID_FORM_TYPE = "COVID";
    public static final String MMG_FORM_TYPE = "MMG";
    // BCAP = COVID for GPS API
    public static final String COVIDR_E_FORM_TYPE = "COVIDR-E";
    public static final String COVIDR_D_FORM_TYPE = "COVIDR-D";
    public static final String MMG_E_FORM_TYPE = "MMG-E";
    public static final String MMG_D_FORM_TYPE = "MMG-D";
    public static final String BCAP_EXT_FORM_TYPE = "TRG";

    public static final String DOMESTIC_TYPE = "Domestic";
    public static final String NOT_ANSWERED = "NA";
    public static final String MULTIPLE_DELIMITER = "::";

    public static final String FORM_VALUE_NONE = "NONE";
    public static final String FORM_VALUE_BCAP = "COVID";
    public static final String FORM_VALUE_MMG = "MMG";
    public static final String FORM_VALUE_BCAP_EXT = "BCAP-EXT";
    
    public static final String FORM_RESOURCE_TYPE = "edc/components/content/telpform";


    private TelpConstants() {
        // Sonar LInt
    }

    /**
     * Header Key that must be present on all requests
     *
     * 
     */
    public enum HeaderParams {
        OCP_APIM_SUB_KEY("Ocp-apim-subscription-key");

        private String headerValue;

        HeaderParams(String headerValue) {
            this.headerValue = headerValue;
        }

        public String getHeaderValue() {
            return headerValue;
        }
    }

    public enum FormFields {
        // First Part
        FINANCIAL_INSTITUTION("bank"), BANK_ACCOUNT_MANAGER_FIRST_NAME("managerFirstName"),
        BANK_ACCOUNT_MANAGER_LAST_NAME("managerLastName"), BANK_ACCOUNT_MANAGER_EMAIL("managerEmailAddress"),
        PRIMARY_CONTACT_FIRST_NAME("contactFirstName"), PRIMARY_CONTACT_LAST_NAME("contactLastName"),
        PRIMARY_CONTACT_EMAIL("contactEmailAddress"), PRIMARY_CONTACT_PHONE("phoneNumber"), CASL_CONSENT("caslConsent"),
        LEGAL_NAME("legalName"), COMPANY_TRADE_NAME("tradeName"), BUSINESS_REGISTRATION_NUMBER("businessNumber"),
        // Not for submitting
        ULTIMATE_BENEFICIAL_OWNERS("ultimateBeneficialOwners"), UBO_FIRST_NAME("uboFirstName"),
        UBO_LAST_NAME("uboLastName"), UBO_RESIDENCE_COUNTRY("uboCountry"),
        // Second Part
        HEADQUARTER_STREET_ADDRESS("companyAddress1"), HEADQUARTER_STREET_ADDRESS_LINE_2("companyAddress2"),
        HEADQUARTER_CITY("companyCity"), HEADQUARTER_PROVINCE("companyProvince"),
        HEADQUARTER_POSTAL_CODE("companyPostal"), HEADQUARTER_COUNTRY("companyCountry"),
        CHIEF_EXECUTIVE_OFFICER_FIRST_NAME("ceoFirstName"), CHIEF_EXECUTIVE_OFFICER_LAST_NAME("ceoLastName"),
        CHIEF_EXECUTIVE_OFFICER_COUNTRY("ceoCountry"), LATEST_ANNUAL_SALES("lastAnnualSales"),
        FINANCIAL_YEAR_END_MONTH("financialYearEndMonth"), EXPORTER_TYPE("q-user-status"),
        PRIMARY_COUNTRY_OF_EXPORT("countries"), AUTHORIZED_SIGNING_AUTHORITY_NAME("authorityName"),
        AUTHORIZED_SIGNING_AUTHORITY_TITLE("authorityTitle"), ELIGIBILITY_STATUS("q-result"), CDIA("cdia"),
        WOMEN_OWNED_OR_LED_CODE("womenOwnedOrLedCode"), INDIGENOUS_OWNED_OR_LED_CODE("indigenousOwnedOrLedCode"),
        SOLD_INTERNATIONALLY("q-soldinternationally"), COUNTRY_OF_OPERATION("q-countryofoperation"),
        COUNTRY_SANCTION("q-countrysanction"), ENTRY_SECTOR("q-entrysector"), FORM_VERSION("formVersion"),
        MM_EMPLOYEES("employees"),ITM_VALUE("ITMValue"); 

        private String fieldname;

        FormFields(String fieldname) {
            this.fieldname = fieldname;
        }

        public String getFieldName() {
            return fieldname;
        }
    }

    public enum UBOKeys {
        UBO_FIRST_NAME_KEY("UltimateBeneficialOwner%sFirstName"),
        UBO_LAST_NAME_KEY("UltimateBeneficialOwner%sLastName"),
        UBO_COUNTRY_KEY("UltimateBeneficialOwner%sCountryOfResidence");

        private String keyName;

        UBOKeys(String keyName) {
            this.keyName = keyName;
        }

        public String getKeyName() {
            return keyName;
        }
    }

    public enum PostFormFields {
        FIELD_TYPE("Type"), FIELD_DATA("Data");

        private String name;

        PostFormFields(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

    }
}
