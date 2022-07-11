package com.edc.edcportal.core.helpers;

public class Constants {
    public Constants() {
        // Add a private constructor to hide the implicit public one.
    }

    public static final String HTML_EXTENSION = ".html";
    public static final String FORWARD_SLASH = "/";
    public static final String FIELD_TYPE_INPUT = "input";
    public static final String FIELD_TYPE_SELECT = "select";
    public static final String FIELD_TYPE_MULTI_SELECT = "multiSelect";
    public static final String FIELD_TYPE_HIDDEN = "hidden";
    public static final String FIELD_TYPE_AUTOCOMPLETE = "autocompletefield";

    public static final String USA_COUNTRY_CODE = "USA";
    public static final String CAN_COUNTRY_CODE = "CAN";
    public static final String COUNTRIES = "countries";
    public static final String STATES = "states";
    public static final String PROVINCE = "province";

    public static final String UTF_8 = "UTF-8";

    public static final String TAGS_EDC_NAMESPACE = "edc:";
    public static final String TAGS_EDC_ACCESSTYPE = TAGS_EDC_NAMESPACE + "access-type";
    public static final String TAGS_EDC_PREMIUM = TAGS_EDC_NAMESPACE + "access-type/premium";
    public static final String UNDERSCORE = "_";
    public static final String COLON = ":";
    public static final String PIPE = "|";
    public static final String COMMA = ",";

    public static final String CANADIAN_PROFILE_TYPE = "canadian-company";
    // Task 314271
    public static final String FI_PROFILE_TYPE = "fi";
    //US 321052
    public static final String FI_PROFILE_TYPE_INSTITUTION_FIELD = "companyName";
    public static final String FI_PROFILE_TYPE_INSTITUTION_FIELD_LOV = "PopFinancialInstitution";
    // end US 321052

    public static final String ACTION_NEW_PROFILE = "newProfile";
    public static final String ACTION_UPDATE_PROFILE = "updateProfile";
    public static final String ACTION_RENEW_PROFILE = "renewProfile";;

    public static final String STRING_TRUE = "true";
    public static final String STRING_FALSE = "false";
    public static final String PHONE_DATA_MASK = "phoneDataMask";

    public static final String LOV_EN_FIELD_NAME = "enName";
    public static final String LOV_FR_FIELD_NAME = "frName";
    public static final String NORTH_AMERICA_REGION = "northAmerica";

    public static final String ALLOWED_REDIRECT_REGEX = "(^https:\\/\\/(.*\\.|)edc\\.ca\\/|^\\/(content(\\/dam|)\\/edc\\/|)(en|fr)\\/)";

    // 30 seconds as sometimes the API takes a while to answer
    public static final int CONNECT_TIMEOUT = 30000;

    public static final String EXTRA_INFO_FIELD_TYPE = "fieldtype";
    public static final String EXTRA_INFO_TITLE = "title";
    public static final String EXTRA_INFO_SUBTITLE = "subtitle";
    public static final String EXTRA_INFO_TOOLTIP = "tooltip";
    public static final String EXTRA_INFO_LABEL = "label";
    public static final String EXTRA_INFO_PLACEHOLDER = "placeholder";
    public static final String EXTRA_INFO_LOV_API = "lovApiName";
    public static final String EXTRA_INFO_FORM_GROUP_CLASS = "formGroupClass";
    public static final String EXTRA_INFO_CCS_CLASS = "cssClass";
    public static final String EXTRA_INFO_START_FORM_ROW = "startFormRow";
    public static final String EXTRA_INFO_END_FORM_ROW = "endFormRow";
    public static final String EXTRA_INFO_END_VALIDATION_RULE = "validationRule";

    public static final String OTHER_VALUE = "other";


    // -------------------------------------------------------------------------
    public class DataPaths {
        private DataPaths() {
            // Sonar Lint
        }

        public static final String MYEDCDATA = "/apps/edcportal/settings/wcm/designs/myEDC-data/";
        public static final String MYEDCDATA_PROFILE_TYPE = MYEDCDATA + Properties.PROFILE_TYPE_FIELD;
        public static final String MYEDCDATA_OTHER_PROFILE_TYPE = MYEDCDATA + "profileTypeOther";
        public static final String MYEDCDATA_FORM_FIELD_DEFINITIONS = MYEDCDATA + "formFieldsDefinition";
        public static final String MYEDCDATA_APIM_FIELD_DEFINITIONS = MYEDCDATA + "apimFieldsDefinition";
        public static final String MYEDCDATA_RENEWAL_UTM_DEFINITIONS = MYEDCDATA + "renewalUtmFieldsDefinition";
    }

    // -------------------------------------------------------------------------
    /**
     * Inner-enum that represents the supported languages (currently, English and
     * French). The default language if English for cases where the language could
     * not be determined.
     *
     */
    public enum SupportedLanguages {
        ENGLISH("English", "EN", "MMMM dd, yyyy"), FRENCH("Français", "FR", "dd MMMM yyyy");

        /**
         * Populates the values of this <code>SupportedLanguages</code> object.
         *
         * @param description  Textual description of this language (e.g., "English" for
         *                     English, "French" for French).
         * @param abbreviation Abbreviation of the language (e.g., "EN" for English,
         *                     "FR" for French).
         */
        SupportedLanguages(String description, String abbreviation, String dateFormat) {
            this.language = description;
            this.languageAbbreviation = abbreviation;
            this.dateFormat = dateFormat;
        }

        private final String language;
        private final String languageAbbreviation;
        private final String dateFormat;

        /**
         * Get the textual description of this language.
         *
         * @return String Textual description of this language (e.g., "English" for
         *         English, "French" for French).
         *
         */
        public String getLanguageText() {
            return this.language;
        }

        /**
         * Get the abbreviation of this language.
         *
         * @return String Abbreviation of the language (e.g., "EN" for English, "FR" for
         *         French).
         *
         */
        public String getLanguageAbbreviation() {
            return this.languageAbbreviation;
        }

        /**
         * Get the date format of this language.
         *
         * @return String Date format of the language (e.g., "MMMM dd, yyyy" for
         *         English, "dd MMMM yyyy" for French).
         *
         */
        public String getDateFormat() {
            return this.dateFormat;
        }

        /**
         * Given the abbreviation of a language, return its
         * <code>SupportedLanguages</code> object.
         *
         * @return SupportedLanguages <code>SupportedLanguages</code> object for the
         *         given Abbreviation. If the abbreviation does not match a language,
         *         English is returned.
         *
         */
        public static SupportedLanguages getLanguageFromAbbreviation(String abbreviation) {
            SupportedLanguages supportedLanguage = ENGLISH;
            // -----------------------------------------------------------------
            // Find our Language object using the language abbreviation
            // -----------------------------------------------------------------
            for (SupportedLanguages lang : SupportedLanguages.values()) {
                if (abbreviation.equalsIgnoreCase(lang.getLanguageAbbreviation())) {
                    supportedLanguage = lang;
                    break;
                }
            }

            return supportedLanguage;
        }
    }

    // -------------------------------------------------------------------------
    public class Paths {
        private Paths() {
            // Sonar Lint
        }

        public static final String CONTENT = "/content";
        public static final String EDCWEB = "/edc";
        public static final String EDCPORTAL = "/edcportal";
        public static final String MYEDC = "/myedc";
        public static final String MYACCOUNT = "/my-account";
        public static final String MYACCOUNT_FR = "/mon-compte";
        public static final String CONFIG = "/conf/";
        public static final String CONTENT_EDCPORTAL = CONTENT + EDCPORTAL;
        public static final String CONTENT_MYEDC = CONTENT + MYEDC;
        public static final String CONTENT_EDC = CONTENT + EDCWEB;
        public static final String JCR_CONTENT = "/jcr:content";

        public static final String FRONT_CONTROLLER_SERVLET_URL = "/bin/myedc/frontController";
        public static final String EVENT_TEMPLATE_URL = "edc/components/structure/event/page";
        public static final String USER_STATE_URL = "/bin/myedc/userstate";
        public static final String USER_STATE_JS_URL = USER_STATE_URL + "js";

        public static final String SELECT_PROFILE = "/profile-type";
        public static final String REGISTER = "/register";
        public static final String MYEDC_HOME_PAGE = "/home";
        public static final String MYEDC_HOME_PAGE_FR = "/accueil";
        public static final String PROFILE = "/profile";
        public static final String PROFILE_FR = "/profil";
        public static final String EDIT_PROFILE = "/profile-edit";
        public static final String EDIT_PROFILE_FR = "/modifiez-profil";
        public static final String RENEWAL = "/profile-renewal";
        public static final String RENEWAL_FR = "/profil-renouvellement";

        public static final String BASE_EN = "/en";
        public static final String BASE_FR = "/fr";
        public static final String BASE_BIN = "/bin";

        public static final String SIGN_UP_EN = BASE_EN + MYACCOUNT + "/sign-up-b2c";
        public static final String SIGN_UP_FR = BASE_FR + MYACCOUNT_FR + "/inscrire-b2c.html";
        public static final String LOGIN_EN = BASE_EN + MYACCOUNT + "/login-b2c.html";
        public static final String LOGIN_FR = BASE_FR + MYACCOUNT_FR + "/connexion-b2c.html";
        public static final String ACCOUNT_BASIC_EDIT_EN = BASE_EN + MYACCOUNT + "/account-edit-b2c.html";
        public static final String ACCOUNT_BASIC_EDIT_FR = BASE_FR + MYACCOUNT_FR + "/modifier-compte-b2c.html";
        // Task 21435 squid:S2068
        public static final String CHANGE_P_EN = BASE_EN + MYACCOUNT + "/change-password-b2c.html";
        public static final String CHANGE_P_FR = BASE_FR + MYACCOUNT_FR + "/changer-mot-passe-b2c.html";
        public static final String RESET_P_EN = BASE_EN + MYACCOUNT + "/reset-password-b2c.html";
        public static final String RESET_P_FR = BASE_FR + MYACCOUNT_FR + "/reinitialiser-mot-passe-b2c.html";

        public static final String LOGOUT_EN = BASE_EN + MYACCOUNT + "/logout-b2c.html";
        public static final String LOGOUT_FR = BASE_FR + MYACCOUNT_FR + "/deconnexion-b2c.html";
        public static final String ERROR_PATH_EN = BASE_EN + "/errors";
        public static final String ERROR_PATH_FR = BASE_FR + "/errors";
        public static final String MYACCOUNT_SYSTEM_ERROR_PATH_EN = LOGOUT_EN + "?return=" + ERROR_PATH_EN + "/myaccount-system-error.html";
        public static final String MYACCOUNT_SYSTEM_ERROR_PATH_FR = LOGOUT_FR + "?return=" + ERROR_PATH_FR + "/myaccount-system-error.html";

        public static final String OAUTH_REDIRECT = "/oauthredir";
        public static final String OAUTH_REDIRECT_URL_EN = BASE_BIN + BASE_EN + OAUTH_REDIRECT;
        public static final String OAUTH_REDIRECT_URL_FR = BASE_BIN + BASE_FR + OAUTH_REDIRECT;

        public static final String ERROR_HANDLER_SERVLET_URL = "/bin/redirectErrors";
    }

    // -------------------------------------------------------------------------
    public class Properties { //NOSONAR

        public static final String CANONICAL = "canonical";
        public static final String JCR_TITLE = "jcr:title";
        public static final String JCR_DESCRIPTION = "jcr:description";
        public static final String NAV_TITLE = "navTitle";
        public static final String PAGE_TITLE = "pageTitle";
        public static final String LAST_MODIFIED = "cq:lastModified";
        public static final String LAST_REPLICATED = "cq:lastReplicated";

        public static final String LOGO_IMAGE = "logoImage";
        public static final String LOGO_IMAGE_2X = "logoImage2x";
        public static final String LOGO_ALT_TEXT = "logoAltText";
        public static final String LOGO_URL = "logoUrl";
        public static final String LOGO_TARGET = "logoTarget";

        public static final String CANADA_LOGO = "canadaLogo";
        public static final String CANADA_LOGO_2X = "canadaLogo2x";
        public static final String CANADA_ALT_TEXT = "canadaAltText";
        public static final String CANADA_LOGO_URL = "canadaLogoUrl";
        public static final String CANADA_LOGO_TARGET = "canadaLogoTarget";

        public static final String LINK_TEXT = "linkText";
        public static final String LINK_URL = "linkUrl";
        public static final String LINK_TARGET = "linkTarget";

        public static final String MENU_SKIP = "menuSkip";
        public static final String EDC_ADDRESS = "edcAddress";
        public static final String COMPANY_NAME = "companyName";
        public static final String FOOTER_LINKS = "footerLinks";
        public static final String ERROR_MESSAGE = "errorMessage";

        public static final String SESSION_MY_RECORD_VAR = "currentValues";

        public static final String HEADER_FIRST_NAME = "firstName";
        public static final String HEADER_LAST_NAME = "lastName";
        public static final String HEADER_EMAIL_ID = "emailId";
        public static final String HEADER_MOBILE_NUMBER = "mobileNumber";
        public static final String HEADER_EXTERNAL_ID = "externalId";
        public static final String HEADER_CREATED_DATE_TIME = "createdDateTime";
        // https://wiki.shibboleth.net/confluence/display/SHIB2/NativeSPAttributeAccess
        public static final String HEADER_SHIB_SESSION_ID = "Shib-Session-ID";

        public static final String PROFILE_TYPE_FIELD = "profileType";
        public static final String TERMS_CONDITIONS_FIELD = "termsConditions";
        public static final String COUNTRY_FIELD = "companyCountry";
        public static final String PRODUCT_TYPE = "productType";
        public static final String PRODUCT_DESC = "productDesc";
        public static final String PRODUCT_TYPE_DEFAULT_VALUE = "Subscription";
        public static final String PRODUCT_DESC_DEFAULT_VALUE = "Account Creation";
        public static final String ADDRESS_2_FIELD = "companyAddress2";
        public static final String GUID_FIELD = "guid";
        public static final String DATA_SHARE_CONSENT_FIELD = "dataShareConsent";
        public static final String MARKETS_INTEREST_FIELD = "marketsInt";
        public static final String PAIN_POINTS_INTEREST_FIELD = "painPoint";

        public static final String CREATED = "created";
        public static final String JCR_COLON = "jcr:";
        public static final String JCR_COLON_CREATED = JCR_COLON + CREATED;

        public static final String FIRST = "first";
        public static final String FIRST_PUBLISHED = FIRST + "published";

        public static final String ELOQUA_EMAIL_CHANGED_FLAG_VALUE = "Yes";
        public static final String ELOQUA_DELIMITER = "::";

        public static final String QUERY_PARAM_ACTION_TYPE = "actionType";
        public static final String QUERY_PARAM_ACTION_TYPE_DELIMITER = ":";
        public static final String QUERY_PARAM_PRODUCT_INTENT = "productIntent";
        public static final String QUERY_PARAM_REDIRECT_URL = "redirectUrl";
        public static final String QUERY_PARAM_STATE = "state";

        public static final String QUERY_PARAM_ACTION_TYPE_PP_DELIMITER = "_!_";
        public static final String SUB_QUERY_PARAM_ACTION_TYPE_DELIMITER = "!_!";
        public static final String SUB_QUERY_PARAM_ACTION_TYPE_AMPERSAND = "&";
        public static final String SUB_QUERY_PARAM_ACTION_TYPE_QUESTION_MARK_DELIMITER = "%3F";
        public static final String SUB_QUERY_PARAM_ACTION_TYPE_QUESTION_MARK = "?";
        public static final String SUB_QUERY_PARAM_ACTION_TYPE_EQUAL_SIGN = "=";
        public static final String SUB_QUERY_PARAM_ACTION_TYPE_SPACE = " ";
        public static final String SUB_QUERY_PARAM_ACTION_TYPE_PLUS_SGIN = "+";

        public static final String QUERY_PARAM_ACTION_TYPE_BASIC = "basic";
        public static final String QUERY_PARAM_ACTION_TYPE_PASS = "password";

        public static final String QUERY_PARAM_UTM = "utm";
        public static final String QUERY_PARAM_UTM_SOURCE = QUERY_PARAM_UTM + "_source";
        public static final String QUERY_PARAM_UTM_MEDIUM = QUERY_PARAM_UTM + "_medium";
        public static final String QUERY_PARAM_UTM_CAMPAIGN = QUERY_PARAM_UTM + "_campaign";
        public static final String QUERY_PARAM_UTM_CONTENT = QUERY_PARAM_UTM + "_content";
        public static final String QUERY_PARAM_UTM_TERM = QUERY_PARAM_UTM + "_term";

        public static final String ARTICLE = "article";
        public static final String DATE = "date";
        public static final String PUBLISH = "publish";
        public static final String PUBLISH_DATE = PUBLISH + DATE;
        public static final String NO_DATE = "nodate";
        public static final String JCR_CREATED = "jcr:created";
        public static final String LAST = "last";

        public static final String REFERER = "referer";
        public static final String APPLICATION = "application";
        public static final String JSON = "json";
        public static final String APPLICATION_SLASH_JSON = APPLICATION + "/" + JSON;

        public static final String IMAGE = "image";
        public static final String ALIGNMENT = "alignment";
        public static final String IMAGE_ALIGNMENT = IMAGE + ALIGNMENT;
        public static final String ALT_TEXT = "alttext";
        public static final String ARTICLE_IMAGE_ALT_TEXT = IMAGE + ALT_TEXT;
        public static final String FILE_REFERENCE = "fileReference";
        public static final String TABLET_FILE_REFERENCE = "tabletFileReference";
        public static final String CENTER = "center";

        public static final String SHIBBOLETH_COOKIE_NAME = "_shibsession_login_token";
        public static final String SHIBBOLETH_SESSION_VAR = "shibession_id";
        public static final String PERSONA_SESSION_VAR = "persona";
        public static final String TRAFFIC_SOURCE_COOKIE_NAME = "trafficsrc";

        public static final String  PROFILE_STATUS_SESSION_VAR = "ProfileUpdateStatus";
        public static final String  PROFILE_HAS_ERRORS = "profileHasErrors";
   
        public static final String FORM_FIELD_QS_TEXT = "qstext";


    }

    /**
     * Inner-enum that represents the possible article content types. This enum has
     * a "default" type for cases where the content type could not be determined.
     * Order the enum constants as in UI #16801 **Important** This order will be
     * used as tag priority
     */
    public enum ArticleContentType {
        SUCCESSSTORY("edc:format-type/success-story", "success-story"),
        WEEKLYCOMMENTARY("edc:format-type/weekly-commentary", "weekly-commentary"),
        WEBINAR("edc:event-type/webinar", "webinar"), TOOL("edc:format-type/tool", "tool"),
        VIDEO("edc:format-type/video", "video"), EBOOK("edc:format-type/ebook", "ebook"),
        GUIDE("edc:format-type/guide", "guide"), BLOG("edc:format-type/blog", "blog"),
        REPORT("edc:format-type/report", "report"), ARTICLE("edc:format-type/article", Constants.Properties.ARTICLE);

        /**
         * Populates the values of this <code>ArticleContentType</code> object.
         *
         * @param tagId     Tag id for this content type.
         * @param className Class name associated with this content type.
         */
        ArticleContentType(String tagId, String className) {
            this.tagId = tagId;
            this.classNm = className;
        }

        private final String tagId;
        private final String classNm;

        /**
         * Get the class name of this article content type.
         *
         * @return String CSS class name for this article content type.
         */
        public String getClassName() {
            return this.classNm;
        }

        /**
         * Get the tag id of this article content type.
         *
         * @return String Tag id for this article content type.
         */
        public String getTagId() {
            return this.tagId;
        }

        /**
         * Given the article content type tag id, return its
         * <code>ArticleContentType</code> object.
         *
         * @return ArticleContentType <code>ArticleContentType</code> object for the
         *         given tag id. If the tag id does not match a content type, "default"
         *         is returned.
         */
        public static ArticleContentType getArticleContentTypeFromTagId(String tagId) {
            ArticleContentType articleContentType = ARTICLE;
            // -----------------------------------------------------------------
            // Find our ArticleContentType object with the given tagId
            // -----------------------------------------------------------------
            for (ArticleContentType contentType : ArticleContentType.values()) {
                if (tagId.equalsIgnoreCase(contentType.getTagId())) {
                    articleContentType = contentType;
                    break;
                }
            }
            return articleContentType;
        }
    }
}