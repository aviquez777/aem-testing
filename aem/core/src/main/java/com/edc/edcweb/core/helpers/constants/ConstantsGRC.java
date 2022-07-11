package com.edc.edcweb.core.helpers.constants;

import com.edc.edcweb.core.helpers.Constants;

public class ConstantsGRC extends Constants {

    /**
     * Empty Constructor
     */

    private ConstantsGRC() {
    }

    public class Paths
    {
        private Paths() { }

		public static final String RESULT_PAGE_SELECTOR = "/company.verification";
        public static final String RESULT_PAGE_SELECTOR_ALIAS = "/entreprise.verification";

    }

    public  class GRCProperties {
        // GRC page selector
        public static final String GRC_VERIFICATION = "verification";
        // US Country Code
        public static final String GRC_COUNTRY_US = "USA";
        //Component properties
        public static final String DISPLAY_TYPE = "displaytype";
        public static final String STATES_TIPS = "statesTips";
        public static final String DISPLAY_TYPE_INCONTENT = "incontent";
        public static final String REGISTRIES = "registries";
        public static final String REGISTRY_TAG = "countryTag";
        public static final String REGISTRY_DESCRIPTION = "busRegDesc";
        public static final String REGISTRY_ICON = "busRegIcon";
        public static final String REGISTRY_NAME = "busRegName";
        public static final String REGISTRY_TIPS = "busRegTips";
        public static final String REGISTRY_TIPS_TITLE = "busRegTipsTitle";
        public static final String REGISTRY_URL = "busRegUrl";
        public static final String REGISTRY_URL_TEXT = "busRegUrlText";
        public static final String STATE = "state";

        public static final String CIMREQUESTFORM_SERVLET ="/bin/cimRequestForm";
        public static final String CIMREQUESTFORM_RESOURCE_TYPE ="edc/components/content/grc/cimrequestform";
        public static final String CIMREQUESTFORM_EMAIL_TEMPLATE = "/apps/edc/settings/notification/email/edc/grc/cim-template.txt";

        public static final String CIMREQUESTFORM_IDENTIFIER = "ISV";
        public static final String CIMREQUESTFORM_DELIMITER = "-";


        public static final String CIMREQUESTFORM_TEMPLATE_DEFAULTSUBJECT = "KB InSight Company Validation Request";
        public static final String CIMREQUESTFORM_TEMPLATE_TITLE = "emailTitle";
        public static final String CIMREQUESTFORM_TEMPLATE_DESCRIPTION = "emailDescription";
        public static final String CIMREQUESTFORM_TEMPLATE_FIRSTNAMELABEL = "emailFirstNameLabel";
        public static final String CIMREQUESTFORM_TEMPLATE_FIRSTNAMETEXT = "firstNameText";
        public static final String CIMREQUESTFORM_TEMPLATE_FULLNAMELABEL = "emailFullNameLabel";
        public static final String CIMREQUESTFORM_TEMPLATE_FULLNAMETEXT = "fullNameText";
        public static final String CIMREQUESTFORM_TEMPLATE_EMAILLABEL = "emailEmailLabel";
        public static final String CIMREQUESTFORM_TEMPLATE_EMAILTEXT = "emailText";
        public static final String CIMREQUESTFORM_TEMPLATE_REQUESTDATELABEL = "emailRequestDateLabel";
        public static final String CIMREQUESTFORM_TEMPLATE_REQUESTDATETEXT = "requestDateText";
        public static final String CIMREQUESTFORM_TEMPLATE_REFERENCENUMBERLABEL = "emailReferenceNumberLabel";
        public static final String CIMREQUESTFORM_TEMPLATE_REFERENCENUMBERTEXT = "referenceNumberText";
        public static final String CIMREQUESTFORM_TEMPLATE_LANGUAGELABEL = "emailLanguageLabel";
        public static final String CIMREQUESTFORM_TEMPLATE_LANGUAGETEXT = "languageText";
        public static final String CIMREQUESTFORM_TEMPLATE_COMPANYINFOTITLE = "companyInfoTitle";
        public static final String CIMREQUESTFORM_TEMPLATE_COMPANYNAMELABEL = "companyNameLabel";
        public static final String CIMREQUESTFORM_TEMPLATE_COMPANYNAMETEXT = "companyNameText";
        public static final String CIMREQUESTFORM_TEMPLATE_COMPANYWEBSITELABEL = "websiteLabel";
        public static final String CIMREQUESTFORM_TEMPLATE_COMPANYWEBSITETEXT = "websiteText";
        public static final String CIMREQUESTFORM_TEMPLATE_HEADQUARTERSTITLE = "headquartersTitle";
        public static final String CIMREQUESTFORM_TEMPLATE_COUNTRYLABEL = "countryLabel";
        public static final String CIMREQUESTFORM_TEMPLATE_COUNTRYTEXT = "countryText";
        public static final String CIMREQUESTFORM_TEMPLATE_ADDRESSLABEL = "addressLabel";
        public static final String CIMREQUESTFORM_TEMPLATE_ADDRESSTEXT = "addressText";
        public static final String CIMREQUESTFORM_TEMPLATE_CITYLABEL = "cityLabel";
        public static final String CIMREQUESTFORM_TEMPLATE_CITYTEXT = "cityText";
        public static final String CIMREQUESTFORM_TEMPLATE_PROVINCELABEL = "provinceLabel";
        public static final String CIMREQUESTFORM_TEMPLATE_PROVINCETEXT = "provinceText";
        public static final String CIMREQUESTFORM_TEMPLATE_POSTALCODELABEL = "postalCodeLabel";
        public static final String CIMREQUESTFORM_TEMPLATE_POSTALCODETEXT = "postalCodeText";
        public static final String CIMREQUESTFORM_TEMPLATE_CONTACTINFOTITLE = "contactTitle";
        public static final String CIMREQUESTFORM_TEMPLATE_CONTACTNAMELABEL = "contactNameLabel";
        public static final String CIMREQUESTFORM_TEMPLATE_CONTACTNAMETEXT = "contactNameText";
        public static final String CIMREQUESTFORM_TEMPLATE_CONTACTEMAILLABEL = "contactEmailLabel";
        public static final String CIMREQUESTFORM_TEMPLATE_CONTACTEMAILTEXT = "contactEmailText";
        public static final String CIMREQUESTFORM_TEMPLATE_CONTACTPHONELABEL = "contactPhoneLabel";
        public static final String CIMREQUESTFORM_TEMPLATE_CONTACTPHONETEXT = "contactPhoneText";
        public static final String CIMREQUESTFORM_TEMPLATE_COMPANYLABEL = "emailCompanyNameLabel";
        public static final String CIMREQUESTFORM_TEMPLATE_COMPANYTEXT = "companyNameText";
        public static final String CIMREQUESTFORM_TEMPLATE_INDUSTRYLABEL = "emailIndustryLabel";
        public static final String CIMREQUESTFORM_TEMPLATE_INDUSTRYTEXT = "industryText";
        public static final String CIMREQUESTFORM_TEMPLATE_MARKETSLABEL = "emailMarketsLabel";
        public static final String CIMREQUESTFORM_TEMPLATE_MARKETSTEXT = "marketsText";
        public static final String CIMREQUESTFORM_TEMPLATE_PAINPOINTLABEL = "emailPainPointLabel";
        public static final String CIMREQUESTFORM_TEMPLATE_PAINPOINTTEXT = "painPointText";
        public static final String CIMREQUESTFORM_TEMPLATE_EHHQUESTIONLABEL = "emailQuestionLabel";
        public static final String CIMREQUESTFORM_TEMPLATE_EHHQUESTIONTEXT = "questionText";



    }

}
