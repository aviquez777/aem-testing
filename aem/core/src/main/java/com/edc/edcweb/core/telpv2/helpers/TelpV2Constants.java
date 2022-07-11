package com.edc.edcweb.core.telpv2.helpers;

public class TelpV2Constants {

    public static final String TELPV2_FORM_RESOURCE_TYPE = "edc/components/content/telpformv2";
    public static final String TELPV2_FORM_TYPE = "TLP";
    public static final String FORM_VALUE_YES = "yes";
    public static final String FORM_VALUE_NO = "no";
    public static final String CDIA_FORM_VALUE_YES = FORM_VALUE_YES;
    public static final String VALUE_YES = "Y";
    public static final String VALUE_NO = "N";
    public static final String TELP_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    public enum FormFields {
        FINANCIAL_INSTITUTION("bank"), BANK_ACCOUNT_MANAGER_FIRST_NAME("managerFirstName"),
        BANK_ACCOUNT_MANAGER_LAST_NAME("managerLastName"), BANK_ACCOUNT_MANAGER_EMAIL("managerEmailAddress"),
        PRIMARY_CONTACT_FIRST_NAME("contactFirstName"), PRIMARY_CONTACT_LAST_NAME("contactLastName"),
        PRIMARY_CONTACT_EMAIL("contactEmailAddress"), PRIMARY_CONTACT_PHONE("phoneNumber"), CASL_CONSENT("caslConsent"),
        LEGAL_NAME("legalName"), COMPANY_TRADE_NAME("tradeName"), BUSINESS_REGISTRATION_NUMBER("businessNumber"),
        HEADQUARTER_STREET_ADDRESS("companyAddress1"), HEADQUARTER_STREET_ADDRESS_LINE_2("companyAddress2"),
        HEADQUARTER_CITY("companyCity"), HEADQUARTER_PROVINCE("companyProvince"),
        HEADQUARTER_POSTAL_CODE("companyPostal"), LATEST_ANNUAL_SALES("lastAnnualSales"),
        FINANCIAL_YEAR_END_MONTH("financialYearEndMonth"), EXPORTER_TYPE("q-user-status"),
        PRIMARY_COUNTRY_OF_EXPORT("countries"), AUTHORIZED_SIGNING_AUTHORITY_NAME("authorityName"),
        AUTHORIZED_SIGNING_AUTHORITY_TITLE("authorityTitle"), ELIGIBILITY_STATUS("q-result"), CDIA("cdia"),
        WOMEN_OWNED_OR_LED_CODE("womenOwnedOrLedCode"), INDIGENOUS_OWNED_OR_LED_CODE("indigenousOwnedOrLedCode"),
        SOLD_INTERNATIONALLY("q-soldinternationally"), COUNTRY_OF_OPERATION("q-countryofoperation"),
        COUNTRY_SANCTION("q-countrysanction"), ENTRY_SECTOR("q-entrysector"), FORM_VERSION("formVersion"),
        EMPLOYEES("employees"), ITM_VALUE("ITMValue"); 

        private String fieldname;

        FormFields(String fieldname) {
            this.fieldname = fieldname;
        }

        public String getFieldName() {
            return fieldname;
        }
    }

}
