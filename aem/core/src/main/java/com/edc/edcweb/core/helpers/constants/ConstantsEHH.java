package com.edc.edcweb.core.helpers.constants;

import com.edc.edcweb.core.helpers.Constants;

public class ConstantsEHH extends Constants {

    public static final String SUB_CATEGORY_DELIMITER = ";";

    /**
     * Empty Constructor
     */

    private ConstantsEHH() {
    }

    public class Paths
    {
        private Paths() { }

        // EHH question nodes location
        public static final String EDCDATA_EHH_QUESTIONS = "/edc/settings/wcm/designs/edc-data/kbp/ehh";
        public static final String EHH_URL = "/premium/tool/export-help-hub/";
        public static final String EXPORT_HELP_HUB_SEARCH = Constants.Paths.PREMIUM + Constants.Paths.EXPORT_HELP_HUB + "/search";
        public static final String EXPORT_HELP_HUB_SEARCH_ALIAS = Constants.Paths.PREMIUM + Constants.Paths.EXPORT_HELP_HUB_ALIAS + "/rechercher";
        public static final String EDCDATA_EU_COUNTRIES = "edc/settings/wcm/designs/edc-data/euCountries";

    }

    public  class EHHProperties {
        // EHH page selector
        public static final String EHH_EHH = "ehh";
        public static final String ENGLISH_ABBR = "en";
        public static final String EHH_PATH_FILTERSEARCHSERVLET = "/bin/ehhFilterSearchServlet";
        public static final String EHH_PATH_QNAMAKERSERVLET = "/bin/qnaMakerServlet";

        //EHH Team Form
        public static final String EHH_PATH_TEAM_FORM_SERVLET ="/bin/ehhTeamRequestForm";
        public static final String EHH_PATH_TEAM_FORM_EMAIL_TEMPLATE = "edc/settings/notification/email/edc/ehh/ehh-template.txt";
        public static final String EHH_REQUESTFORM_RESOURCE_TYPE ="edc/components/content/ehh/ehhteamform";
        public static final String EHH_REQUESTFORM_IDENTIFIER = "ISV";
        public static final String EHH_REQUESTFORM_DELIMITER = "-";
        public static final String EHH_REQUESTFORM_TEMPLATE_DEFAULTSUBJECT = "Export Help Question Submitted";
        public static final String EHH_REQUESTFORM_TEMPLATE_TITLE = "emailTitle";
        public static final String EHH_REQUESTFORM_TEMPLATE_DESCRIPTION = "emailDescription";
        public static final String EHH_REQUESTFORM_TEMPLATE_EMAILLABEL = "emailEmailLabel";
        public static final String EHH_REQUESTFORM_TEMPLATE_EMAILTEXT = "emailText";
        public static final String EHH_REQUESTFORM_TEMPLATE_REQUESTDATELABEL = "emailRequestDateLabel";
        public static final String EHH_REQUESTFORM_TEMPLATE_REQUESTDATETEXT = "requestDateText";
        public static final String EHH_REQUESTFORM_TEMPLATE_REFERENCENUMBERLABEL = "emailReferenceNumberLabel";
        public static final String EHH_REQUESTFORM_TEMPLATE_REFERENCENUMBERTEXT = "referenceNumberText";
        public static final String EHH_REQUESTFORM_TEMPLATE_LANGUAGELABEL = "emailLanguageLabel";
        public static final String EHH_REQUESTFORM_TEMPLATE_LANGUAGETEXT = "languageText";
        public static final String EHH_REQUESTFORM_TEMPLATE_FIRSTNAMELABEL = "firstNameLabel";
        public static final String EHH_REQUESTFORM_TEMPLATE_FIRSTNAMETEXT = "firstNameText";
        public static final String EHH_REQUESTFORM_TEMPLATE_LASTNAMELABEL = "lastNameLabel";
        public static final String EHH_REQUESTFORM_TEMPLATE_LASTNAMETEXT = "lastNameText";
        public static final String EHH_REQUESTFORM_TEMPLATE_JOBTITLELABEL = "JobTitleLabel";
        public static final String EHH_REQUESTFORM_TEMPLATE_JOBTITLETEXT = "JobTitleText";
        public static final String EHH_REQUESTFORM_TEMPLATE_TRADESTATUSLABEL = "TradeStatusLabel";
        public static final String EHH_REQUESTFORM_TEMPLATE_TRADESTATUSTEXT = "TradeStatusText";
        public static final String EHH_REQUESTFORM_TEMPLATE_COMPANYNAMELABEL = "CompanyNameLabel";
        public static final String EHH_REQUESTFORM_TEMPLATE_COMPANYNAMETEXT = "CompanyNameText";
        public static final String EHH_REQUESTFORM_TEMPLATE_COMPANYPHONELABEL = "CompanyPhoneLabel";
        public static final String EHH_REQUESTFORM_TEMPLATE_COMPANYPHONETEXT = "CompanyPhoneText";
        public static final String EHH_REQUESTFORM_TEMPLATE_ADDRESSLABEL = "AddressLabel";
        public static final String EHH_REQUESTFORM_TEMPLATE_ADDRESSTEXT = "AddressText";
        public static final String EHH_REQUESTFORM_TEMPLATE_CITYLABEL = "CityLabel";
        public static final String EHH_REQUESTFORM_TEMPLATE_CITYTEXT = "CityText";

        public static final String EHH_REQUESTFORM_TEMPLATE_PROVINCELABEL = "ProvinceLabel";
        public static final String EHH_REQUESTFORM_TEMPLATE_PROVINCETEXT = "ProvinceText";

        public static final String EHH_REQUESTFORM_TEMPLATE_POSTALCODELABEL = "PostalCodeLabel";
        public static final String EHH_REQUESTFORM_TEMPLATE_POSTALCODETEXT = "PostalCodeText";

        public static final String EHH_REQUESTFORM_TEMPLATE_COUNTRYLABEL = "CountryLabel";
        public static final String EHH_REQUESTFORM_TEMPLATE_COUNTRYTEXT = "CountryText";

        public static final String EHH_REQUESTFORM_TEMPLATE_ANNUALSALESLABEL = "AnnualSalesLabel";
        public static final String EHH_REQUESTFORM_TEMPLATE_ANNUALSALESTEXT = "AnnualSalesText";

        public static final String EHH_REQUESTFORM_TEMPLATE_EMPLOYEESLABEL = "EmployeesLabel";
        public static final String EHH_REQUESTFORM_TEMPLATE_EMPLOYEESTEXT = "EmployeesText";

        public static final String EHH_REQUESTFORM_TEMPLATE_PAINPOINTLABEL = "PainPointLabel";
        public static final String EHH_REQUESTFORM_TEMPLATE_PAINPOINTTEXT = "PainPointText";

        public static final String EHH_REQUESTFORM_TEMPLATE_INDUSTRYLABEL = "IndustryLabel";
        public static final String EHH_REQUESTFORM_TEMPLATE_INDUSTRYTEXT = "IndustryText";

        public static final String EHH_REQUESTFORM_TEMPLATE_MARKETSLABEL = "MarketsLabel";
        public static final String EHH_REQUESTFORM_TEMPLATE_MARKETSTEXT = "MarketsText";

        public static final String EHH_REQUESTFORM_TEMPLATE_INTERNALUSELABEL = "InternalUseLabel";
        public static final String EHH_REQUESTFORM_TEMPLATE_EHHQUESTIONREQUESTTITLELABEL = "EHHQuestionRequestTitle";

        public static final String EHH_REQUESTFORM_TEMPLATE_URLFORMLABEL = "urlFormLabel";
        public static final String EHH_REQUESTFORM_TEMPLATE_URLFORMTEXT = "urlFormText";

        public static final String EHH_REQUESTFORM_TEMPLATE_QUESTIONTITLELABEL = "questionTitle";
        public static final String EHH_REQUESTFORM_TEMPLATE_USERPROFILETITLELABEL = "userProfileTitle";


        public static final String EHH_REQUESTFORM_TEMPLATE_HOWCANHELPYOULABEL = "howCanWeHelpYouLabel";
        public static final String EHH_REQUESTFORM_TEMPLATE_HOWCANHELPYOUTEXT = "howCanWeHelpYouText";

        public static final String EHH_REQUESTFORM_TEMPLATE_EDCCOOKIESLABEL = "EDCCookieUseLabel";
        public static final String EHH_REQUESTFORM_TEMPLATE_EDCCOOKIESTEXT = "EDCCookieUseLabel";

        // EHH query param
        public static final String EHH_QUERY = "query";
        public static final String EHH_REFERER = "referer";
        public static final String EHH_SEARCHQUERY_FILTER = "search=*&%24select=questions%2Cmetadata_language%2Cmetadata_english_question%2Cmetadata_topic%2Cmetadata_subtopic%2Cmetadata_country&%24filter=search.in(metadata_language%2C%20'${languageCode}')";

        // EHH Json attributes
        // QnA response
        public static final String EHH_ATTR_QUESTION = "question";
        public static final String EHH_ATTR_QUESTIONS = EHH_ATTR_QUESTION + "s";
        public static final String EHH_ATTR_DATALAYERQUESTION = "dataLayerQuestion";
        public static final String EHH_ATTR_COUNTRY = "country";
        public static final String EHH_ATTR_TOPIC = "topic";
        public static final String EHH_ATTR_ANSWER = "answer";
        public static final String EHH_ATTR_DESCRIPTION = "description";
        public static final String EHH_ATTR_ANSWERS = EHH_ATTR_ANSWER +"s";
        public static final String EHH_ATTR_ID = "id";
        public static final String EHH_ATTR_SCORE = "score";
        public static final String EHH_ATTR_SCORE_HIGH = "scoreHigh";
        public static final String EHH_ATTR_CONFIDENCE_LEVEL = "confidenceLevel";
        public static final String EHH_ATTR_AUTHOR = "author";
        public static final String EHH_ATTR_IMAGEURL = "imageUrl";
        public static final String EHH_ATTR_IMAGEALT = "imageAlt";
        public static final String EHH_ATTR_NAME = "name";
        public static final String EHH_ATTR_POSITION = "position";
        public static final String EHH_ATTR_BIOURL = "bioURL";
        public static final String EHH_ATTR_LINKEDINURL = "linkedInUrl";
        public static final String EHH_ATTR_COMPANYNAME = "companyName";
        public static final String EHH_ATTR_ENGLISHQUESTION = "english_question";
        public static final String EHH_ATTR_METACOUNTRY = "country";
        public static final String EHH_ATTR_METATOPIC = "topic";
        public static final String EHH_ATTR_EHHID = "ehh_id";
        public static final String EHH_ATTR_RESULTSNUMBER = "resultsnumber";
        public static final String EHH_ATTR_SOURCE= "source";
        public static final String EHH_ATTR_METADATA= "metadata";
        public static final String EHH_ATTR_VALUE="value";
        public static final String EHH_ATTR_METADATA_COUNTRY_EU = "european union";
        public static final String EHH_ATTR_METADATA_TOPIC = "metadata_topic";
        public static final String EHH_ATTR_METADATA_SUBTOPIC = "metadata_subtopic";
        public static final String EHH_ATTR_METADATA_COUNTRY = "metadata_country";
        public static final String EHH_ATTR_METADATA_MARKET = "metadata_market";

        // EHH Markets
        public static final String EHH_MARKETS = "united states,european union,mexico,other";
        public static final String EHH_MARKET_OTHER = "other";
        public static final String EHH_CATEGORY_DESC_SUFFIX = "us,eu,mex";
        public static final String EHH_CATEGORY_DESC_BASE = "categorydescription_";
        public static final String EHH_CATEGORIES = "categories";
    }

    public class tagIds {
        public static final String AUTHOR_TAG_ID = TAGS_EDC_NAMESPACE +"contributors";
        public static final String EHH_TAG_ID = TAGS_EDC_NAMESPACE +"knowledge-base/categories";
    }
}
