package com.edc.edcweb.core.gps.helpers;

public class GpsConstants {

    private GpsConstants() {
        // Sonar LInt
    }

    public static final String NAME = "Name";
    public static final String GPS_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final String VALUE_YES = "Yes";
    public static final String VALUE_NO = "No";
    public static final String GRANT_TYPE = "client_credentials";
    public static final String JSON_ERROR_MESSAGE_KEY = "ErrorMessage";


    public enum PostFormFields {
        FIELD_TYPE("Type"), FIELD_DATA("Data"), FIELD_ATTACHMENTS("Attachments");

        private String name;

        PostFormFields(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        protected static final String[] SRF_FORM_FIELDS = new String[] { "referredByFirstName", "referredByLastName",
                "corporateStatus", "Non-Profit", "otherBusinessServices", "usaTin", "legalName", "knownAs",
                "parentCompany", "website", "companyCountry", "companyAddress1", "companyAddress2", "companyCity",
                "companyProvince", "companyProvinceAlt", "companyProvinceInput", "companyPostal",
                "otherDiversityOrganizations", "nameOfEntity", "transit5", "bank3", "account", "emailAddressForEFT",
                "beneficiaryName ", "internationalIdentifierType", "beneficiaryAccountRadio", "beneficiaryAccount",
                "currency", "beneficiaryBankName", "beneficiaryBankRoutingMethod", "intermediaryBankName",
                "intermediaryBankRoutingMethod", "remitCompanyCountry", "remitCompanyAddress1", "remitCompanyAddress2",
                "remitCompanyCity", "remitCompanyProvince", "remitCompanyProvinceAlt", "remitCompanyProvinceInput",
                "remitCompanyPostal", "remitEmailAddress", "comments" };
    }
}
