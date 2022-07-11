package com.edc.edcweb.core.gps.helpers;

public class SrfConstants {
    
    
    private SrfConstants () {
        // SonarQube
    }

    public static final String SRF_FORM_TYPE = "SupplierRegistration";
    public static final String SRF_FORM_RESOURCE_TYPE = "edc/components/content/supplierregistrationform";
    public static final String SRF_FILE_TYPE_API_QUERY = "AllowedFileExtensions/findcode/SupplierRegistration";

    public static final long MAX_UPLOAD_SIZE = 10485760; //bytes
    public static final long MAX_UPLOAD_FILE_QTY = 6;
    public static final String OTHER_DELIMITER = "\\|"; // must escape the "|" symbol

    public enum SupplierType {
        USA,
        CAN,
        INT;
    }

    public enum FormFields {
        CASL_CONSENT("caslConsent"),
        LANGUAGE("language"),
        CODE_OF_CONDUCT_CONSENT("codeOfConductConsent"),
        // ContactInfo
        FIRST_NAME("firstName"),
        LAST_NAME("lastName"),
        EMAIL_ADDRESS("emailAddress"),
        MAIN_PHONE("mainPhone"),
        PHONE_EXT("ext"),
        FAX("fax"),
        // SupplierInformation
        SUPPLIER_TYPE("supplierInformation"),
        REFERRED_BY_FIRST_NAME("referredByFirstName"),
        REFERRED_BY_LAST_NAME("referredByLastName"),
        CORPORATE_STATUS("corporateStatus"),
        OTHER_CORPORTATE_STATUS("otherCorporateStatus"),
        BUSINESS_SERVICES("businessServices"),
        OTHER_BUSINESS_SERVICES("otherBusinessServices"),
        // CompanyIdentifier
        CAN_ID_TYPE("canadian-identifier-type"), //not to be submitted on JSON
        CAN_GST_HST_NUMBER("canGstHstNumber"),
        CAN_SIN_NUMBER("canSinNumber"),
        USA_TIN("usaTin"),
        INT_ID_TYPE("international-identifier-type"), //not to be submitted on JSON
        INT_BIN("internationalBin"),
        INT_VAT("internationalVat"),
        INT_SIN("internationalNin"),
        REGISTRATION_TYPE("supplierType"),
        SAMPLE_INVOINCE_FILE_1("sampleInvoinceFile1"),
        SAMPLE_INVOINCE_FILE_2("sampleInvoinceFile2"),
        SAMPLE_INVOINCE_FILE_3("sampleInvoinceFile3"),
        // CompanyInformation
        LEGAL_NAME("legalName"),
        KNOWN_AS("knownAs"),
        PARENT_COMPANY("parentCompany"),
        WEBSITE("website"),
        COMPANY_COUNTRY("companyCountry"), 
        COMPANY_ADDRESS_1("companyAddress1"), 
        COMPANY_ADDRESS_2("companyAddress2"), 
        COMPANY_CITY("companyCity"), 
        COMPANY_PROVINCE("companyProvince"),
        COMPANY_PROVINCE_ALT("companyProvince-alt"), 
        COMPANY_PROVINCE_INPUT("companyProvinceInput"), 
        COMPANY_POSTAL("companyPostal"), 
        B_CORP_CERTIFICATION("bCorpCertification"),
        CERTIFIED_DIVERSE_SUPPLIER("cerifiedDiverseSupplier"),
        CERTIFIED_DIVERSE_SUPPLIER_YES("cerifiedDiverseSupplierYes"),
        OTHER_DIVERSITY_ORGANIZATIONS("otherDiversityOrganizations"),
        DIVERSE_SUPPLIER("diverseSupplier"),
        REMIT_NAME_OF_ENTITY("nameOfEntity"),
        REMIT_TRANSIT_NUMBER("transit5"),
        REMIT_INSTITUTION_NUMBER("bank3"),
        REMIT_ACCOUNT_NUMBER("account"),
        REMIT_EFT_EMAILADDRESS("emailAddressForEFT"),
        REMIT_BANK_DETAILS_FILE_1("bankDetails1"),
        REMIT_BANK_DETAILS_FILE_2("bankDetails2"),
        REMIT_BANK_DETAILS_FILE_3("bankDetails3"),
        REMIT_BENEFICIARY_NAME("beneficiaryName"),
        REMIT_BENEFICIARY_ACCOUNT("beneficiaryAccount"),
        REMIT_BENEFICIARY_IBAN("iban"),
        REMIT_BENEFICIARY_CURRENCY("currency"),
        REMIT_BENEFICIARY_BANK_NAME("beneficiaryBankName"),
        REMIT_BENEFICIARY_BANK_ROUTING_METHOD("beneficiaryBankRoutingMethod"),
        REMIT_BENEFICIARY_INTERMEDIARY_BANK_NAME("intermediaryBankName"),
        REMIT_BENEFICIARY_INTERMEDIARY_BANK_ROUTING_METHOD("intermediaryBankRoutingMethod"),
        REMIT_SAME_ADDRESS("sameAddress"),
        REMIT_COMPANY_COUNTRY("remitCompanyCountry"), 
        REMIT_COMPANY_ADDRESS_1("remitCompanyAddress1"), 
        REMIT_COMPANY_ADDRESS_2("remitCompanyAddress2"), 
        REMIT_COMPANY_CITY("remitCompanyCity"), 
        REMIT_COMPANY_PROVINCE("remitCompanyProvince"), 
        REMIT_COMPANY_PROVINCE_ALT("remitCompanyProvince-alt"), 
        REMIT_COMPANY_PROVINCE_INPUT("remitCompanyProvinceInput"), 
        REMIT_COMPANY_POSTAL("remitCompanyPostal"), 
        REMIT_EMAIL_ADDRESS("remitEmailAddress"),
        COMMENTS("comments");

        private String fieldname;

        FormFields(String fieldname) {
            this.fieldname = fieldname;
        }

        public String getFieldName() {
            return fieldname;
        }
    }
}
