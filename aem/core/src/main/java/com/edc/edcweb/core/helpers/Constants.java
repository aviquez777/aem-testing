package com.edc.edcweb.core.helpers;

public class Constants
{
    public Constants() {
        // Sonar lint
    }

    public enum ArrayValues
    {

        PAGE_TYPES_TO_INCLUDE_IN_TAG_BASED_PAGE_SEARCH (new String[] {"edc/components/structure/article/page","edc/components/structure/successstory/page","edc/components/structure/marketentryadvisors/page", "edc/components/structure/event/page", "edc/components/structure/ehh/page"}), //NOSONAR

        //Must not be part of 'Access type','Format type', 'Contributors', 'Buyer stage', 'Persona','Export status','Solutions', 'Owner ID'
        TAG_CLOUD_TAGS_TO_IGNORE(new String[] {"access-type", "format-type","contributors", "buyer-stage", "persona", "export-status","solution","owner-id"}), //NOSONAR

        //Must not be part of 'Access Type', 'Persona', 'Export Status', 'Buyer Stage', 'Owner ID'
        META_KEYWORDS_TAGS_TO_IGNORE(new String[] {"access-type", "persona","export-status", "buyer-stage", "owner-id"}),

        //Must be sent to eloqua: 'Buyer stage', 'Persona', 'Category', 'Export Status', 'Region', 'Format Type', 'Solution', 'Owner ID'
        META_ELOQUA_TAGS_TO_INCLUDE(new String[] {"buyer-stage", "persona", "category", "export-status", "region", "format-type", "solution","owner-id", "ftas", "event-type"}),

        //Following premium page use case need to have special teaser URL
        PREMIUM_PATH_WITH_SPECIAL_TEASER_URL(new String[] {"/ebook/", Constants.Paths.EXPORT_HELP_HUB+"/", Constants.Paths.EXPORT_HELP_HUB_ALIAS+"/", Constants.Paths.INLIST+"/", Constants.Paths.INLIST_ALIAS+"/", Constants.Paths.GLOBAL_RISK_CHECK+"/", Constants.Paths.GLOBAL_RISK_CHECK_ALIAS+"/"}),

        //Accepted profiles for GRC features,
        ACCEPTED_PROFILES(new String[] {"canadian-company", "edc"}),

        //Set the required paths and properties to hide pages from search
        EXCLUDED_PATHS(new String[] {"errors", "premium", "gated", "pro"}), //NOSONAR
        EXCLUDE_PROPS(new String[] {"substitutepath", "hideInNav", "excludepage"});

        ArrayValues(String[] iValues){
            this.myValues = iValues.clone() ;
        }

        private  String[] myValues;
        public String[] toArray(){
            return  myValues.clone() ;
        };
    }

    public static final String PHONE_RENDITION = "cq5dam.thumbnail.719.719.png";
    public static final String TABLET_RENDITION = "cq5dam.thumbnail.959.959.png";
    public static final String DESKTOP_RENDITION = "cq5dam.thumbnail.1800.1800.png";
    public static final String DEFAULT_RENDITION = "cq5dam.web.1280.1280.jpeg";


    public static final String HTML_EXTENTION = ".html";
    public static final String VALUE = "value";
    public static final String TEXT = "text";
    public static final String UNDERSCORE = "_";
    public static final String PLUS_SIGN = "+";
    public static final String MINUS_SIGN = "-";
    public static final String PIPE = "|";
    public static final String EQUAL_SIGN = "=";
    public static final String QUESTION_MARK = "?";
    public static final String AMPERSAND_SIGN = "&";
    public static final String PERIOD = ".";
    public static final String COMMA = ",";

    // FOR LIVE CHAT TEST ONLY
    public static final String WHOS_ON_DOMAIN_TEST = "wwwppr.edc.ca";

    // FOR PRODUCTION
    public static final String WHOS_ON_DOMAIN_PROD = "www.edc.ca";


    public static final int    CONTENT_REC_MAX_ITEMS = 5;
    public static final int    CONTENT_REC_MAX_ITEMS_PREMIUM = 1;
    public static final int    CARD_GRID_DEFAULT_ARTICLES_PER_PAGE = 10;
    public static final int    CARD_GRID_COUNTRY_DEFAULT_ARTICLES_PER_PAGE = 6;
    public static final int    TRADEINSIGHTS_DEFAULT_ORDER = 5;
    public static final String   TAG_CLOUD_NAMESPACE_DELIMITER = ":";
    public static final String   UTF_8 = "UTF-8";
    public static final String   TAGS_EDC_NAMESPACE = "edc:";

    public static final String   TAGS_EDC_ACCESSTYPE = TAGS_EDC_NAMESPACE + "access-type";
    public static final String   TAGS_EDC_PREMIUM = TAGS_EDC_NAMESPACE + "access-type/premium";
    public static final String   TAGS_EDC_NONPREMIUM = TAGS_EDC_NAMESPACE + "access-type/non-premium";
    public static final String   TAGS_EDC_EVENT_TYPE = TAGS_EDC_NAMESPACE + "event-type/webinar";

    public static final String   TAGS_KNOWLEDGE_CATEGORY_COVID = TAGS_EDC_NAMESPACE + "knowledge-base/categories/covid-19";
    public static final String   TAGS_KNOWLEDGE_MARKET_US = TAGS_EDC_NAMESPACE + "knowledge-base/markets/united-states";
    public static final String   TAGS_KNOWLEDGE_MARKET_EU = TAGS_EDC_NAMESPACE + "knowledge-base/markets/european-union";
    public static final String   TAGS_KNOWLEDGE_MARKET_MEX = TAGS_EDC_NAMESPACE + "knowledge-base/markets/mexico";
    public static final String   TAGS_KNOWLEDGE_MARKET_OTHER = TAGS_EDC_NAMESPACE + "knowledge-base/markets/other";
    public static final String   TAGS_KNOWLEDGE_MARKET = TAGS_EDC_NAMESPACE + "knowledge-base/markets/";

    public static final String   LIST_FROM = "listFrom";
    public static final String   LIST_FROM_TAGS = "tags";
    public static final String   TAGS_MATCH_ALL = "all";
    public static final String   TAGS_MATCH_ANY = "any";
    public static final String   YES = "yes";
    public static final String   NO = "no";

    public static final int    SERIES_MAX_ITEMS = 10;
    public static final int    SERIE_SERIES_DEFAULT_NBPART = 0;

    public static final String ISO_CODE = "isoCode";
    public static final String ISO_CODE2 = ISO_CODE + "2";
    public static final String ISO_CODE3 = ISO_CODE + "3";
    
    public static final String PDF_EXT= ".pdf";
    public static final String RUN_MODE_AUTHOR = "author";
    public static final String RUN_MODE_PUBLISH = "publish";
    public static final String PLEASE_SELECT = "Please Select";

    
    // MULTI-SELECT
    public static final String USA_COUNTRY_CODE = "USA";
    public static final String CAN_COUNTRY_CODE = "CAN";
    public static final String LOV_EN_FIELD_NAME = "enName";
    public static final String LOV_FR_FIELD_NAME = "frName";
    public static final String NORTH_AMERICA_REGION = "northAmerica";

    // Task 245680 CQRules:ConnectionTimeoutMechanism
    // 30 seconds as sometimes the API takes a while to answer
    public static final int CONNECT_TIMEOUT = 30000;

    public static final String WCMMODE = "wcmmode";
    public static final String WCMMODE_EQUALS_DISABLED = WCMMODE + EQUAL_SIGN + "disabled";

    public static final String DATA_ANCHOR_FOR_SCROLL_PARAM = "dataAnchorForScroll";

    public class Locales
    {
        public static final String ENGLISH_PATH = "en_pa";
        public static final String FRENCH_PATH  = "fr_pa";
    }

    /**


     */
    public class tagIds {
        public static final String REGION_TAG_ID = "edc:region";
    }


    //-------------------------------------------------------------------------
    public class Paths
    {
        private Paths() { }

        public static final String CONTENT = "/content";
        public static final String EDC = "/edc";
        public static final String CONTENT_EDC = CONTENT + EDC;
        public static final String ERRORS = "/errors/";
        public static final String CONFIG = "/conf/";
        public static final String PREMIUM = "/premium";
        public static final String JCR_CONTENT = "/jcr:content";
        public static final String EDC_NAMESPACE_TAGS = "/content/cq:tags/edc";
        public static final String CATEGORY_TAGS = "/content/cq:tags/edc/category";
        public static final String REGION_TAGS = "/content/cq:tags/edc/region";
        public static final String REGION_CANADA_TAGS = "/content/cq:tags/edc/region/north-america/canada";
        public static final String CONTENT_TYPE_TAGS = "/content/cq:tags/edc/format-type";
        public static final String EXPORT_STATUS_TAGS = "/content/cq:tags/edc/export-status";
        public static final String PERSONA_TAGS = "/content/cq:tags/edc/persona";
        public static final String OWNER_TAGS = "/content/cq:tags/edc/owner-id";
        public static final String INDUSTRY_TAGS = "/content/cq:tags/edc/industry";
        public static final String FTA_TAGS = "/content/cq:tags/edc/ftas";
        public static final String ACCESS_TYPE_PREMIUM = "/content/cq:tags/edc/access-type/premium";
        public static final String ACCESS_TYPE_NONPREMIUM = "/content/cq:tags/edc/access-type/non-premium";
        public static final String CONTENT_TYPE_WEEKLYCOMMENTARY = "/content/cq:tags/edc/format-type/weekly-commentary";
        public static final String CONTENT_TYPE_WEBINAR = "/content/cq:tags/edc/format-type/webinar";
        public static final String EVENT_TYPE_TAGS = "/content/cq:tags/edc/event-type";
        public static final String EVENT_TYPE_TAGS_WEBINAR = EVENT_TYPE_TAGS + "/webinar";
        public static final String COUTRYINFO_BASE = "/content/edc/";
        public static final String COUTRYINFO_INFO = "/data/countryinfo/";
        public static final String COUTRYINFO_ID = "/data/countryid/";
        public static final String EVENT_PAGE_TYPE = "edc/components/structure/event/page";
        public static final String TOOL = "/tool";
        public static final String TOOL_ALIAS = "/outil";
        public static final String GUIDE = "/guide";
        public static final String EXPORT_HELP_HUB = TOOL+"/export-help-hub";
        public static final String EXPORT_HELP_HUB_ALIAS = TOOL_ALIAS+"/centre-aide-export";
        public static final String INLIST = TOOL+"/inlist";
        public static final String INLIST_ALIAS = TOOL_ALIAS+"/enliste";
        public static final String GLOBAL_RISK_CHECK = TOOL+"/company-insight";
        public static final String GLOBAL_RISK_CHECK_ALIAS = TOOL_ALIAS+"/verif-entreprise";

        public static final String COUNTRYINFO_COUNTRYROOTPAGE_EN ="/country-info";
        public static final String COUNTRYINFO_COUNTRY_DETAILPAGES_EN = COUNTRYINFO_COUNTRYROOTPAGE_EN + "/country";

        public static final String COUTRYINFO_FEATUREDCOUNTRIES_COMPONENTPAGE_EN = Constants.Paths.CONTENT_EDC + "/en" + COUNTRYINFO_COUNTRYROOTPAGE_EN;
        public static final String COUTRYINFO_FEATUREDCOUNTRIES_COMPONENTPAGE_FR = Constants.Paths.CONTENT_EDC + "/fr" + COUNTRYINFO_COUNTRYROOTPAGE_EN;
        public static final String COUTRYINFO_TEMPLATE_POLICYANDRATING ="/conf/edc/settings/wcm/templates/country-detail-page";
        public static final String COUNTRYINFO_PATH_COUNTRYFEATUREDLIST = Paths.JCR_CONTENT + "/root/responsivegrid/countryfeaturedlist";
        public static final String COUNTRYINFO_TEMPLATE_POSRATINGNAME ="countryposrating";
        public static final String COUNTRYINFO_TEMPLATE_PATH_COMPONENT="edc/components/content/" + COUNTRYINFO_TEMPLATE_POSRATINGNAME;

        public static final String BLACK_LOGO = "/content/dam/edc/logo_EDC_Black.png";
        public static final String PROGRESSIVEPROFILING_POSTSERVLET="/bin/progressiveProfilingFormServlet";
        public static final String PROGRESSIVEPROFILING_PREMIUMROOT ="/premium";

        public static final String EDCDATA_BASE = "/apps/edc/settings/wcm/designs/edc-data/";
        public static final String EDCDATA_ANNUALSALES="/apps/edc/settings/wcm/designs/edc-data/annualSales";
        public static final String EDCDATA_COUNTRIES="/apps/edc/settings/wcm/designs/edc-data/countries";
        public static final String EDCDATA_EMPLOYEES="/apps/edc/settings/wcm/designs/edc-data/empolyees";
        public static final String EDCDATA_PAINPOINTS="/apps/edc/settings/wcm/designs/edc-data/painPoints";
        public static final String EDCDATA_INDUSTRY="/apps/edc/settings/wcm/designs/edc-data/industry";
        public static final String EDCDATA_PROVINCE="/apps/edc/settings/wcm/designs/edc-data/province";
        public static final String EDCDATA_US_STATES="/apps/edc/settings/wcm/designs/edc-data/usStates";
        public static final String EDCDATA_TRADESTATUS="/apps/edc/settings/wcm/designs/edc-data/tradeStatus";
        public static final String EDCDATA_FORMERRORS="/apps/edc/settings/wcm/designs/edc-data/formErrors";
        public static final String EDCDATA_INDUSTRYSERVED_PRIMARY="/apps/edc/settings/wcm/designs/edc-data/industryServedPrimary";
        public static final String EDCDATA_INDUSTRYSERVED_SECONDARY="/apps/edc/settings/wcm/designs/edc-data/industryServedSecondary";
        
        // Product inquiry Form
        public static final String EDCDATA_FIRELATIONSHIP="/apps/edc/settings/wcm/designs/edc-data/fiRelationship";
        public static final String EDCDATA_POFUTURE="/apps/edc/settings/wcm/designs/edc-data/poFuture";
        public static final String EDCDATA_YEAR="/apps/edc/settings/wcm/designs/edc-data/year";
        public static final String EDCDATA_TRADESTATUS_PI="/apps/edc/settings/wcm/designs/edc-data/tradeStatusPI";
        public static final String EDCDATA_PAINPOINTS_PI="/apps/edc/settings/wcm/designs/edc-data/painPointsPI";
        public static final String EDCDATA_MARKETSINT_PI="/apps/edc/settings/wcm/designs/edc-data/regionsMarketsInt";        

        //TAP Form
        public static final String EDCDATA_LEGALFORM="/apps/edc/settings/wcm/designs/edc-data/legalForm";
        public static final String EDCDATA_COMPANYPRODUCT="/apps/edc/settings/wcm/designs/edc-data/companyProduct";
        public static final String EDCDATA_COMPANYSERVICE="/apps/edc/settings/wcm/designs/edc-data/companyService";
        public static final String EDCDATA_CHANNELSELL="/apps/edc/settings/wcm/designs/edc-data/channelsSell";
        public static final String EDCDATA_ONLINESALES="/apps/edc/settings/wcm/designs/edc-data/onlineSales";
        public static final String EDCDATA_EXPORTINGEXPERIENCE="/apps/edc/settings/wcm/designs/edc-data/exportingExperience";
        public static final String EDCDATA_EXPENSES="/apps/edc/settings/wcm/designs/edc-data/expenses";
        public static final String EDCDATA_EXPORTSALES="/apps/edc/settings/wcm/designs/edc-data/exportSales";

        public static final String PROGRESSIVEPROFILING_SESSIONINFOSERVLET="/bin/sessionInfoServlet";
        public static final String EBOOK_PAGE_TYPE = "edc/components/structure/ebook/page";
        public static final String MEA_TEASER_PAGE_TYPE = "edc/components/structure/marketentryadvisors/page";
        public static final String MEA_PREMIUM_PAGE_TYPE = "edc/components/structure/marketentryadvisors/premium-page";
        public static final String CARD_GRID_RESOURCE_TYPE = "edc/components/content/article/cardgrid";
        public static final String TRADEINSIGHTS_SEARCH_RESOURCE_TYPE = "edc/components/content/article/tradeinsightsarticlesearch";
        public static final String INSIGHTS_SEARCH_RESOURCE_TYPE = "edc/components/content/article/insightsarticlesearch";
        public static final String APSGFORM_APSGFORMSERVLET="/bin/apsgFormServlet";
        public static final String BROKERFORM_BROKERFORMSERVLET="/bin/brokerForm";
        public static final String PREMIUMCONTENTTRANSACTIONSERVLET="/bin/contentTransaction";
        public static final String EHH_FILTER_RESOURCE_TYPE = "edc/components/content/ehh/ehhfilter";
        public static final String EHH_SEARCH_RESULT_RESOURCE_TYPE = "edc/components/content/ehh/ehhsearchresult";
        public static final String AMCHART_MAP_RESOURCE_TYPE = "/apps/edc/components/content/amchartmap";
        public static final String AMCHART_TIMELINE_RESOURCE_TYPE = "/apps/edc/components/content/amcharttimeline";

        public static final String AUTHOR_BIO_EN = CONTENT_EDC + "/en/bio/";
        public static final String AUTHOR_BIO_FR = CONTENT_EDC + "/fr/bio/";

        public static final String USER_STATUS_SERVICE_URL = "/bin/myedc/userstate";

        public static final String MSTLFORM_MSTLFORM_SERVLET = "/bin/mstlFormServlet";

        public static final String TELPFORM_TELPFORM_SERVLET = "/bin/telpFormServlet";
        public static final String TELPFORM_BCAP_SERVLET = "/bin/bcapFormServlet";
        public static final String TELPFORM_MMG_SERVLET = "/bin/mmgFormServlet";

        public static final String TRIAGE_RESULT_SERVLET_URL = "/bin/triageresult"; 

        public static final String GPS_FORMS_SERVLET_URL = "/bin/gps"; 
        
        public static final String COVID_19_TRIAGE_TOOOL_JCR_CONTENT = "_jcr_content.";
        public static final String COVID_19_TRIAGE_TOOOL_JSON_DIALOG_PROPERTY = "questionnairejson";


        //Sign In
        public static final String FORWARD_SLASH = "/";
        public static final String MYEDC = "/myedc";
        public static final String MYACCOUNT = "/my-account";
        public static final String CONTENT_MYEDC = CONTENT + MYEDC;
        public static final String MYEDC_LOGIN_EN = "/login-b2c";
        public static final String MYEDC_SIGNUP_EN = "/sign-up-b2c";
        public static final String MYEDC_LOGIN_FR = "/connexion-b2c";
        public static final String MYEDC_SIGNUP_FR = "/inscrire-b2c";
        public static final String MYEDC_HOME_PAGE = "/home";
        public static final String MYEDC_HOME_PAGE_FR = "/accueil";

        public static final String ELOQUA_MYEDC_CDO_VERSION = "2.0";
        public static final String ELOQUA_MYEDC_CDO_UNIQUE_CODE_INTERNAL_NAME = "ACC_ExternalID___Doc_ID1";

        public static final String CAMPAIGN = "/campaign";
        public static final String EVENTS =  "/events";
        public static final String EVENT_TEMPLATE_URL = "edc/components/structure/event/page";
        public static final String BC_EVENT_TEMPLATE_URL = "edc/components/structure/bc-event/page";

        public static final String YESNOQUESTION_FORM_TYPE = "edc/components/content/yesnoquestion";
        public static final String YESNOQUESTION_FORM_SERVLET = "/bin/yesnoquestionformservlet";
        public static final String YESNOQUESTION_EMAIL_TEMPLATE = "/apps/edc/settings/notification/email/edc/yesnoquestion.txt";

        public static final String MYEDC_GATING_LANDING_PAGE = "landingPage";

        // Reference Accordion
        public static final String REF_ACCORDION_RESOURCE_TYPE = "edc/components/content/referenceaccordion";
        public static final String ACCORDION_RESOURCE_TYPE = "edc/components/content/accordian";
        public static final String SECTIONTITLE_RESOURCE_TYPE = "edc/components/content/sectiontitle";
        public static final String TEXT_RESOURCE_TYPE = "edc/components/content/text";
        public static final String IMAGEBODYTEXT_RESOURCE_TYPE = "edc/components/content/imageinbodytext";
        public static final String SIMPLECTA_RESOURCE_TYPE = "edc/components/content/article/simplecta";
        public static final String RESPONSIVEGRID_RESOURCE_TYPE = "wcm/foundation/components/responsivegrid";
    }
    //-------------------------------------------------------------------------
    public class Properties
    {
        public static final String ITEMS = "items";
        public static final String BUTTON_LINK = "buttonlink";
        public static final String FIRST = "first";
        public static final String SECOND = "second";
        public static final String FIRST_CTA_URL = FIRST + "ctaurl";
        public static final String FIRST_CTA_URL_1 = FIRST_CTA_URL + "1";
        public static final String FIRST_CTA_URL_2 = FIRST_CTA_URL + "2";
        public static final String SECOND_CTA_URL = SECOND + "ctaurl";
        public static final String SECOND_CTA_URL_1 = SECOND_CTA_URL + "1";
        public static final String SECOND_CTA_URL_2 = SECOND_CTA_URL + "2";
        public static final String BULLET_ITEMS = "bulletitems";
        public static final String BULLET = "bullet";
        public static final String CARD_ITEMS = "carditems";
        public static final String THREE_COLUMNS = "threecolumns";
        public static final String FIRST_BULLET_ITEMS = FIRST + BULLET_ITEMS;
        public static final String SECOND_BULLET_ITEMS = SECOND + BULLET_ITEMS;
        public static final String SLIDES = "slides";
        public static final String LEFT = "left";
        public static final String TOPTITLE = "topTitle";
        public static final String TITLE = "title";
        public static final String SUBTITLE = "subtitle";
        public static final String TITLE_IN_TITLE_CASE = "Title";
        public static final String LEFT_TITLE = LEFT + TITLE;
        public static final String LEFT_ID = "accordion_title_1";
        public static final String CENTER_ID = "accordion_title_2";
        public static final String RIGHT_ID = "accordion_title_3";
        public static final String CONTACT_ID = "accordion_title_4";
        public static final String LEFT_ARIA_CONTROLS = "accordion_content_1";
        public static final String CENTER_ARIA_CONTROLS = "accordion_content_2";
        public static final String RIGHT_ARIA_CONTROLS = "accordion_content_3";
        public static final String CONTACT_ARIA_CONTROLS = "accordion_content_4";
        public static final String CLASS_HEADER_TITLE = "header-title";
        public static final String CENTER = "center";
        public static final String CENTER_TITLE = CENTER + TITLE;
        public static final String RIGHT = "right";
        public static final String RIGHT_TITLE = RIGHT + TITLE;
        public static final String CONTACT = "contact";
        public static final String CONTACT_TITLE = CONTACT + TITLE;
        public static final String SOCIAL = "social";
        public static final String SOCIAL_TITLE = SOCIAL + TITLE;
        public static final String PHONE = "phone";
        public static final String NUMBER = "number";
        public static final String PHONE_NUMBER = PHONE + NUMBER;
        public static final String LABEL = "label";
        public static final String ARIA = "aria";
        public static final String ARIA_LABEL = ARIA + LABEL;
        public static final String PHONE_LABEL = PHONE + LABEL;
        public static final String EDC = "edc";
        public static final String LOGO = "logo";
        public static final String BLACK = "black";
        public static final String TWO_X = "2x";
        public static final String EDC_LOGO = EDC + LOGO;
        public static final String EDC_LOGO_2X = EDC + LOGO + TWO_X;
        public static final String EDC_LOGO_BLACK = EDC + LOGO + BLACK;
        public static final String EDC_LOGO_2X_BLACK = EDC + LOGO + TWO_X + BLACK;
        public static final String URL = "url";
        public static final String EDC_LOGO_URL = EDC + LOGO + URL;
        public static final String TARGET = "target";
        public static final String EDC_LOGO_TARGET = EDC + LOGO + TARGET;
        public static final String ALT_TEXT = "alttext";
        public static final String EDC_ALT_TEXT = EDC + ALT_TEXT;
        public static final String CANADA = "canada";
        public static final String CANADA_LOGO = CANADA + LOGO;
        public static final String CANADA_LOGO_2X = CANADA + LOGO + TWO_X;
        public static final String CANADA_LOGO_BLACK = CANADA + LOGO + BLACK;
        public static final String CANADA_LOGO_2X_BLACK = CANADA + LOGO + TWO_X + BLACK;
        public static final String CANADA_LOGO_URL = CANADA + LOGO + URL;
        public static final String CANADA_LOGO_TARGET = CANADA + LOGO + TARGET;
        public static final String CANADA_ALT_TEXT = CANADA + ALT_TEXT;
        public static final String LINKS = "links";
        public static final String LINK = "link";
        public static final String LINK_URL = LINK + URL;
        public static final String LEFT_LINKS = LEFT + LINKS;
        public static final String CENTER_LINKS = CENTER + LINKS;
        public static final String RIGHT_LINKS = RIGHT + LINKS;
        public static final String CONTACT_LINKS = CONTACT + LINKS;
        public static final String SOCIAL_LINKS = SOCIAL + LINKS;
        public static final String EXTERNAL_URL = "externalUrl";
        public static final String BOTTOM = "bottom";
        public static final String BOTTOM_LINKS = BOTTOM + LINKS;
        public static final String STARTING = "starting";
        public static final String PAGE = "page";
        public static final String PAGE_TITLE = PAGE + TITLE_IN_TITLE_CASE;
        public static final String STARTING_PAGE = STARTING + PAGE;
        public static final String LEVELS = "levels";
        public static final String MAX = "max";
        public static final String MAX_LEVELS = MAX + LEVELS;
        public static final String LOGO_URL = LOGO + URL;
        public static final String IMAGE = "image";
        public static final String LOGO_IMAGE = LOGO + IMAGE;
        public static final String LOGO_ALT_TEXT = LOGO + ALT_TEXT;
        public static final String LOGO_TARGET = LOGO + TARGET;
        public static final String LOGIN = "login";
        public static final String LOGOUT = "logout";
        public static final String LOGGEDIN = "loggedin";
        public static final String LOGGEDOUT = "loggedout";
        public static final String TEXT = "text";
        public static final String LOGIN_TEXT = LOGIN + TEXT;
        public static final String LOGGEDIN_TEXT = LOGGEDIN + TEXT;
        public static final String LOGGEDOUT_TEXT = LOGGEDOUT + TEXT;
        public static final String LOGIN_URL = LOGIN + URL;
        public static final String LOGOUT_URL = LOGOUT + URL;
        public static final String LOGIN_TARGET = LOGIN + TARGET;
        public static final String MENU = "menu";
        public static final String MENU_TARGET = MENU + TARGET;
        public static final String MENU_SKIP = "skip";
        public static final String NO_BOTTOM_MARGIN = "noBottomMargin";
        public static final String EXPAND_ARIA_LABEL = "expandLabel";
        public static final String LOGOUT_BUTTON_EN = "Log out";
        public static final String LOGOUT_BUTTON_FR = "Se déconnecter";
        public static final String LOGOUT_BUTTON_EN_NEW = "Sign out of MyEDC";
        public static final String LOGOUT_BUTTON_FR_NEW = "Fermer la session dans MonEDC";
        public static final String MOBILE_SEARCH_LABEL = "msearchlabel";
        public static final String FIRST_PUBLISHED = FIRST + "published";
        public static final String CANONICAL = "canonical";
        public static final String TAGS = "tags";
        public static final String CQ_TAGS = "cq:" + TAGS;
        public static final String NT_UNSTRUCTURED = "nt:unstructured";
        public static final String PRIMARY_AUTHOR_PATH = "primaryauthorpath";
        public static final String AUTHOR_BIO_PATH = "authorbiopath";
        public static final String AUTHORS_DETAILS_RESOURCE_TYPE = "edc/components/content/article/articleauthorsdetails";
        public static final String AUTHORS_DETAILS_AUTHORS_CHILD = "authors";
        public static final String PRIMARY_SHOW_BIOGRAPHY = "primaryshowbio";
        public static final String PRIMARY_SHORT_BIOGRAPHY = "primaryshortbio";
        public static final String SECONDARY_AUTHOR_PATH = "secondaryauthorpath";
        public static final String SECONDARY_SHOW_BIOGRAPHY = "secondaryshowbio";
        public static final String SECONDARY_SHORT_BIOGRAPHY = "secondaryshortbio";
        public static final String AUTHOR_PATH_SUFFIX = Paths.JCR_CONTENT + "/root/articlecontainer/authorprofile";
        public static final String LINK_URL_1 = LINK + URL + "1";
        public static final String LINK_URL_2 = LINK + URL + "2";
        public static final String PRODUCT_PAGE = "productpage";
        public static final String RECOMMENDATION_INFO_PATH_SUFFIX = Paths.JCR_CONTENT + "/root/responsivegrid/recommendationinfo";
        public static final String LINK_TEXT = LINK + TEXT;
        public static final String LINK_TARGET = LINK + TARGET;
        public static final String ARTICLE = "article";
        public static final String ARTICLES = ARTICLE + "s";
        public static final String DESCRIPTION = "description";
        public static final String SYNOPSIS = "synopsis";
        public static final String ARTICLE_SYNOPSIS = ARTICLE + SYNOPSIS;
        public static final String TEASER = "teaser";
        public static final String SOURCE = "source";
        public static final String TEASER_IMAGE_SOURCE = TEASER + IMAGE;
        public static final String ARTICLE_IMAGE_ALT_TEXT = IMAGE + ALT_TEXT;
        public static final String ARTICLE_PAGE = ARTICLE + PAGE;
        public static final String PATH = "path";
        public static final String EXPERIENCE_FRAGMENT = "experiencefragment";
        public static final String JCR_CREATED = "jcr:created";
        public static final String DATE = "date";
        public static final String PUBLISH = "publish";
        public static final String PUBLISH_DATE = PUBLISH + DATE;
        public static final String LAST = "last";
        public static final String NO_DATE = "nodate";
        public static final String SORT = "sort";
        public static final String SORT_DATE = SORT + DATE;
        public static final String DISPLAY_DATE = "display" + DATE;
        public static final String PAGE_NAME = PAGE + "name";
        public static final String ARTICLES_PATH = ARTICLES + PATH;
        public static final String ID = "id";
        public static final String MAX_ITEMS = MAX + ITEMS;
        public static final String CONTENT = "content";
        public static final String TYPE = "type";
        public static final String CLASS = "class";
        public static final String NAME = "name";
        public static final String CONTENT_TYPE_CLASS_NAME = CONTENT + TYPE + CLASS + NAME;
        public static final String CONTENT_TYPE_TITLE_TEXT = CONTENT + TYPE + TITLE + TEXT;
        public static final String IS = "is";
        public static final String PREMIUM = "premium";
        public static final String IS_PREMIUM = IS + PREMIUM;
        public static final String APPLICATION = "application";
        public static final String JSON = "json";
        public static final String APPLICATION_SLASH_JSON = APPLICATION + "/" + JSON;
        public static final String TEXT_SLASH_XML= "text/xml";
        public static final String TEXT_SLASH_HTML= "text/html";
        public static final String TAG = "tag";
        public static final String TAG_ID = TAG + ID;
        public static final String IDS = "ids";
        public static final String TAG_IDS = TAG + IDS;
        public static final String TAG_TRENDING_IDS = "trending" + TAG + IDS;
        public static final String MATCH_TITLE_CASE = "Match";
        public static final String TAGS_MATCH = TAGS + MATCH_TITLE_CASE;
        public static final String MASTER = "master";
        public static final String LIST = "list";
        public static final String MASTER_TAG_LIST = MASTER + TAG + LIST;
        public static final String ALIGNMENT = "alignment";
        public static final String IMAGE_ALIGNMENT = IMAGE + ALIGNMENT;
        public static final String FILE_REFERENCE = "fileReference";
        public static final String TABLET_FILE_REFERENCE = "tabletFileReference";
        public static final String INCLUDE = "include";
        public static final String INCLUDE_TAGS = INCLUDE + TAGS;
        public static final String INCLUDE_TAG_MASTER_LIST = INCLUDE + TAG + MASTER + LIST;
        public static final String CURRENT = "current";
        public static final String INCLUDE_CURRENT_PAGE = INCLUDE + CURRENT + PAGE;
        public static final String SIZE = "size";
        public static final String PAGE_SIZE = PAGE + SIZE;
        public static final String PER = "per";
        public static final String ARTICLES_PER_PAGE = ARTICLES + PER + PAGE;
        public static final String ARTICLES_COUNTRY_PER_PAGE = "countryarticleperpage";
        public static final String CREATED = "created";
        public static final String JCR_COLON = "jcr:";
        public static final String JCR_COLON_CREATED = JCR_COLON + CREATED;
        public static final String JCR_TITLE = JCR_COLON + TITLE;
        public static final String JCR_DESCRIPTION = JCR_COLON + DESCRIPTION;
        public static final String NAV = "nav";
        public static final String MODIFIED_IN_TITLE_CASE = "Modified";
        public static final String REPLICATE_IN_TITLE_CASE = "Replicated";
        public static final String NAV_TITLE = NAV + TITLE_IN_TITLE_CASE;
        public static final String CQ_COLON = "cq:";
        public static final String LAST_MODIFIED = CQ_COLON + LAST + MODIFIED_IN_TITLE_CASE;
        public static final String LAST_REPLICATED = CQ_COLON + LAST + REPLICATE_IN_TITLE_CASE;
        public static final String SLING_COLON = "sling:";
        public static final String RESOURCE_TYPE = "resourceType";
        public static final String SLING_COLON_RESOURCE_TYPE = SLING_COLON + RESOURCE_TYPE;
        public static final String CODE = "code";
        public static final String COUNTRY = "country";
        public static final String ENGLISH_ABBR = "en";
        public static final String PROFILE_TYPE = "profileType";

        public static final String ASSET_TIER = "assetTier";
        public static final String DOC_ID_ELOQUA = "eloquaDocId";
        public static final String DOC_ID = "docid";

        public static final String MESSAGE = "message";
        public static final String SCHEDULE = "schedule";
        public static final String SUBMIT = "submit";
        public static final String EMPLOYEE = "employee";
        public static final String FILEREFERENCE = "fileReference";
        public static final String CENTRALLYMANAGED = "centrallymanaged";
        public static final String CTATEXT1 = "ctatext1";
        public static final String SUBMITTEXT = SUBMIT + TEXT;
        public static final String EMPLOYEENAME = EMPLOYEE + NAME;
        public static final String EMPLOYEETITLE = EMPLOYEE + TITLE;

        public static final String SERIE_NEXTARTICLE = "nextarticle";
        public static final String SERIE_ISFIRST = "isfirst";
        public static final String SERIE_CTATEXT= "ctatext";
        public static final String SERIE_PARTLABEL = "partlabel";
        public static final String SERIE_OFLABEL = "oflabel";
        public static final String SERIE_SERIESLABEL = "serieslabel";
        public static final String SERIE_SERIESNBPART = "nbpartseries";
        public static final String SERIES_BANNER_VERSION="seriesbannerversion";
        public static final String SERIES_BANNER_ENHANCED="enhanced";
        public static final String SERIES_BANNER_ENHANCED_MAIN_TITLE="sbmaintitle";
        public static final String SERIES_BANNER_ENGLISH_TITLE="Continue series";
        public static final String SERIES_BANNER_FRENCH_TITLE="Continuer la série";
        

        public static final String NON = "non";
        public static final String NON_DASH_PREMIUM = NON + "-" + PREMIUM;
        public static final String NOT = "not";
        public static final String OR = "or";
        public static final String PREMIUM_OR_NOT = PREMIUM + OR + NOT;
        public static final String HEADLINE = "headline";

        public static final String PAGEPOLICY_TILINK = "menutilink";
        public static final String PAGEPOLICY_TILABEL = "menutilabel";
        public static final String PAGEPOLICY_EWLINK = "menuewlink";
        public static final String PAGEPOLICY_EWLABEL = "menuewlabel";

        public static final String TISUB_CTATEXT = "ctatext";
        public static final String TISUB_SUCCESSMESSAGE = "successmessage";
        public static final String TISUB_LOGOIMAGE= "logoimage";

        public static final String TISUB_COOKIEDISCLAIMER= "cookiedisclaimer";
        public static final String TISUB_COOKIELABEL= "cookielabel";
        public static final String TISUB_COOKIEPATH= "cookiepath";
        public static final String TISUB_TITLE= "title";
        public static final String TISUB_EMAILEMPTYLABEL= "emailemptylabel";

        public static final String NEWSLETTER_LOGOHIGRES = "logoHighRes";
        public static final String NEWSLETTER_LOGOALT = "logoAlt";
        public static final String NEWSLETTER_PLACEHOLDER= "placeholder";
        public static final String NEWSLETTER_DISCLAIMER= "disclaimer";
        public static final String NEWSLETTER_CTATEXT= "ctaText";
        public static final String NEWSLETTER_INVALIDEMAIL= "invalidEmail";
        public static final String NEWSLETTER_REQUIREDFIELD= "requiredField";
        public static final String NEWSLETTER_SUCCESSTITLE= "successTitle";
        public static final String NEWSLETTER_SUCCESSTEXT= "successText";
        public static final String NEWSLETTER_INQUIRYID= "inquiryID";
        public static final String NEWSLETTER_DOCID= "docID";
        public static final String NEWSLETTER_DISPLAYTYPE= "displaytype";
        public static final String NEWSLETTER_CUSTOMINQUIRYID= "customInquiryId";
        public static final String NEWSLETTER_CUSTOMDOCID= "customDocId";
        public static final String NEWSLETTER_EVENTNAME= "eventName";

        public static final String WCTITLE = "wctitle";
        public static final String WCSUBTITLE = "wcsubtitle";
        public static final String WCLOGO = "wcwclogo";
        public static final String NEWSLETTER_WCLOGOHIGRES = "wclogoHighRes";
        public static final String NEWSLETTER_WCLOGOALT = "wclogoAlt";
        public static final String NEWSLETTER_WCPLACEHOLDER= "wcplaceholder";
        public static final String NEWSLETTER_WCDISCLAIMER= "wcdisclaimer";
        public static final String NEWSLETTER_WCCTATEXT= "wcctaText";
        public static final String NEWSLETTER_WCSUCCESSTITLE= "wcsuccessTitle";
        public static final String NEWSLETTER_WCSUCCESSTEXT= "wcsuccessText";
        public static final String NEWSLETTER_WCINQUIRYID= "wcinquiryID";
        public static final String NEWSLETTER_WCDOCID= "wcdocID";
        public static final String NEWSLETTER_WCISLARGEBUTTON= "wcisLargeButton";

        public static final String LINKS_SECCION_TITLE= "linksSectionTitle";
        public static final String INTRO_TEXT= "introText";

        public static final String COUTRYINFO_TAG= "tags";
        public static final String COUTRYINFO_POSITION= "position";
        public static final String COUNTRYINFO_TEMPLATE_POLICY = "cq:policy";

        public static final String COUTRYINFO_POS_OPEN= "open";
        public static final String COUTRYINFO_POS_CLOSED= "closed";
        public static final String COUTRYINFO_POS_RESTRICTED= "restricted";
        public static final String COUTRYINFO_POS_HIGHLYRESTRICTED= "highlyrestricted";
        public static final String COUTRYINFO_POS_LIMITED= "limitedinformation";
        public static final String COUTRYINFO_POS_UNDERREVIEW= "underreview";

        public static final String COUTRYINFO_RATING_HIGHRISK = "highrisk";
        public static final String COUTRYINFO_RATING_LOWMEDRISK = "lowmediumrisk";
        public static final String COUTRYINFO_RATING_MEDIUMRISK = "mediumrisk";
        public static final String COUTRYINFO_RATING_MEDHIGHRISK = "mediumhighrisk";
        public static final String COUTRYINFO_RATING_LOWRISK = "lowrisk";
        public static final String COUTRYINFO_RATING_NOINFORISK = "noinforisk";
        public static final String COUTRYINFO_RATING_UNDERREVIEW= "underreviewrisk";

        public static final String COUTRYINFO_TAGPATH = "tagpath";
        public static final String COUTRYINFO_POSITION_TITLE = "positionTitle";
        public static final String COUTRYINFO_POSITION_SUBTITLE = "positionSubtitle";
        public static final String COUTRYINFO_POSITION_URL = "positionLinkurl";
        public static final String COUTRYINFO_POSITION_URLTEXT = "positionLinktext";
        public static final String COUTRYINFO_POSITION_HELPTITLE = "positionHelpTitle";
        public static final String COUTRYINFO_POSITION_HELPTEXT = "positionHelpText";
        public static final String COUTRYINFO_POSITION_EXITTEXT = "positionExitText";
        public static final String COUTRYINFO_POSITION_IMAGE = "positionFileReference";
        public static final String COUTRYINFO_POSITION_TABLET = "positionTablet";
        public static final String COUTRYINFO_POSITION_ALT = "positionAlt";
        public static final String COUTRYINFO_POSITION_TEXT = "positionText";
        public static final String COUTRYINFO_POSITION_HEADING = "positionHeading";
        public static final String COUTRYINFO_ENGLISH = "en";

        public static final String COUTRYINFO_RATING = "rating";
        public static final String COUTRYINFO_RATING_TITLE = "cccTitle";
        public static final String COUTRYINFO_RATING_SUBTITLE = "cccSubtitle";
        public static final String COUTRYINFO_RATING_DESCRIPTION = "cccDescription";
        public static final String COUTRYINFO_RATING_URLTEXT = "cccLinktext";
        public static final String COUTRYINFO_RATING_HELPTITLE = "cccHelpTitle";
        public static final String COUTRYINFO_RATING_HELPTEXT = "cccHelpText";
        public static final String COUTRYINFO_RATING_EXITTEXT = "cccExitText";
        public static final String COUTRYINFO_RATING_HEADING = "cccHeading";
        public static final String COUTRYINFO_RATING_IMAGE = "cccFileReference";
        public static final String COUTRYINFO_RATING_TABLET = "cccTablet";
        public static final String COUTRYINFO_RATING_ALT = "cccAlt";
        public static final String COUTRYINFO_RATING_DESCRIPTION_UNDERREVIEW = "cccDescriptionunderreviewrisk";

        public static final String ADDRESS = "address";
        public static final String EDC_ADDRESS = EDC + ADDRESS;
        public static final String COMPANY = "company";
        public static final String COMPANY_NAME = COMPANY + NAME;

         public static final String STEPBYSTEPGUIDE_TITLE= "title";
        public static final String STEPBYSTEPGUIDE_SUBTITLE1= "subtitle1";
        public static final String STEPBYSTEPGUIDE_SUBTITLE2= "subtitle2";
        public static final String STEPBYSTEPGUIDE_SUBTITLE3= "subtitle3";
        public static final String STEPBYSTEPGUIDE_SUBTITLE4= "subtitle4";
        public static final String STEPBYSTEPGUIDE_TEXT1= "text1";
        public static final String STEPBYSTEPGUIDE_TEXT2= "text2";
        public static final String STEPBYSTEPGUIDE_TEXT3= "text3";
        public static final String STEPBYSTEPGUIDE_TEXT4= "text4";
        public static final String STEPBYSTEPGUIDE_DISCLAIMER= "disclaimer";
        public static final String STEPBYSTEPGUIDE_LINKURL= "linkUrl";
        public static final String STEPBYSTEPGUIDE_LINKTARGET= "linkTarget";
        public static final String STEPBYSTEPGUIDE_LINKTEXT= "linkText";
        public static final String STEPBYSTEPGUIDE_PATH_COUNTRYPOSTRATING = Paths.JCR_CONTENT + "/root/countryposrating";
        public static final String STEPBYSTEPGUIDE_EDCPOSITION_CLOSED = "closed";
        public static final String COUNTRYCTA = "countrycta";
        public static final String COUNTRYCTAURL = "countryctaurl";
        public static final String LINKTARGET = "linktarget";

        public static final String HEROBANNER_HEADING = "heading";
        public static final String HEROBANNER_TEXT = "text";
        public static final String HEROBANNER_TAGLINE = "tagline";
        public static final String HEROBANNER_ALIGNMENT = "alignment";
        public static final String HEROBANNER_LINKTARGET = "linktarget";
        public static final String HEROBANNER_LINKTEXT = "linktext";
        public static final String HEROBANNER_LINKURL = "linkurl";
        public static final String HEROBANNER_FILEREFERENCE = "fileReference";
        public static final String HEROBANNER_TABLETFILEREFERENCE = "tabletFileReference";
        public static final String HEROBANNER_PHONEFILEREFERENCE = "phoneFileReference";
        public static final String HEROBANNER_ALT = "alt";
        public static final String HEROBANNER_LAZYLOAD = "lazyload";
        public static final String HEROBANNER_COUNTRYINFOLABEL = "countryInfoLabel";
        public static final String HEROBANNER_COUNTRYINFOLINK = "countryInfoLink";
        public static final String HEROBANNER_BACKGROUNDICON = "backgroundIcon";

        public static final String MULTIPLETABS_TITLE = "title";
        public static final String MULTIPLETABS_BUSINESSENVIRONMENT = "businessenvironment";
        public static final String COUNTRYFEATUREDLIST_COUNTRYTAG = "countrytag";

        public static final String COUNTRYSEARCH_FILTERTITLE ="filtertitle";
        public static final String COUNTRYSEARCH_REGIONTITLE ="regiontitle";
        public static final String COUNTRYSEARCH_POSITIONTITLE ="positiontitle";
        public static final String COUNTRYSEARCH_RATINGTITLE ="ratingtitle";
        public static final String COUNTRYSEARCH_FTATITLE ="ftatitle";
        public static final String COUNTRYSEARCH_POSITIONCOL ="positioncolname";
        public static final String COUNTRYSEARCH_RATINGCOL ="ratingcolname";
        public static final String COUNTRYSEARCH_POSHELPLINKTEXT ="posHelpLinkText";
        public static final String COUNTRYSEARCH_CCCHELPLINKTEXT ="cccHelpLinkText";
        public static final String COUNTRYSEARCH_FTALINKTEXT ="ftaHelpLinkText";
        public static final String COUNTRYSEARCH_EXITTEXT ="exitText";
        public static final String COUNTRYSEARCH_FTAHELPTEXT ="ftaHelpText";
        public static final String COUNTRYSEARCH_FTAHELPTITLE ="ftaHelpTitle";

        public static final String COUNTRYFEATURED_TITLE ="title";
        public static final String COUNTRYFEATURED_TEXT ="textopen";
        public static final String COUNTRYFEATURED_CLOSE ="closetext";
        public static final String COUNTRYFEATURED_ACTIVATE ="activatetext";
        public static final String COUNTRYFEATURED_COLCOUNTRY ="countrycolname";
        public static final String COUNTRYFEATURED_COLPOSITION ="positioncolname";
        public static final String COUNTRYFEATURED_COLRATING ="ratingcolname";
        public static final String COUNTRYSEARCH_MSG_NODATAFOUND ="nodatamsg";
        public static final String COUNTRYSEARCH_BTN_TAPTOCLOSE ="tapclosemsg";
        public static final String COUNTRYFEATURED_TAG = "countrytag";
        public static final String COUNTRYSEARCH_BTN_CLEARALL  = "clearallmsg";
        public static final String COUNTRYSEARCH_BTN_FILTER  = "filtermsg";

        public static final String BREADCRUMB_LINKTEXT= "linkText";
        public static final String BREADCRUMB_LINKURL= "linkUrl";
        public static final String BREADCRUMB_SINGLELINK= "singleLink";

        public static final String PROGRESSIVEPROFILING_PREMIUMURL = "premiumURL";
        public static final String PROGRESSIVEPROFILING_TEASERURL = "teaserUrl";
        public static final String PROGRESSIVEPROFILING_MODE = "mode";
        public static final String PROGRESSIVEPROFILING_MODE_MEAUNLOCKED = "meaUnlocked";
        public static final String PROGRESSIVEPROFILING_MODE_MEA = "mode_mea";
        public static final String PROGRESSIVEPROFILING_MODE_MEA_MEAUNLOCKED_DOCID = "MEA-UNLOCKED";
        public static final String PROGRESSIVEPROFILING_MODE_MEA_DOCID = "MEA-";
        public static final String PROGRESSIVEPROFILING_MODE_NORMAL = "mode_normal";
        public static final String PROGRESSIVEPROFILING_MODE_EBOOK_REGULAR = "mode_ebook_regular";
        public static final String PROGRESSIVEPROFILING_MODE_EBOOK_PERSONA = "mode_ebook_persona";
        public static final String PROGRESSIVEPROFILING_MODE_EXPORT_HELP_REQUEST = "mode_export_help_request";
        public static final String PROGRESSIVEPROFILING_MODE_KNOWLEDGE_COSTUMER = "mode_knowledge";
        public static final String PROGRESSIVEPROFILING_MODE_CRG = "mode_crg";
        public static final String PROGRESSIVEPROFILING_SUBMITBUTTON = "submitButton";
        public static final String PROGRESSIVEPROFILING_CANCELBUTTON = "cancelButton";
        public static final String PROGRESSIVEPROFILING_SUBMITBUTTONEN = "submitBtnEnglishText";
        public static final String PROGRESSIVEPROFILING_CANCELBUTTONEN = "cancelBtnEnglishText";
        public static final String PROGRESSIVEPROFILING_NEXTBUTTON = "nextButton";
        public static final String PROGRESSIVEPROFILING_BACKBUTTON = "backButton";
        public static final String PROGRESSIVEPROFILING_DONEBUTTON = "doneButton";
        public static final String PROGRESSIVEPROFILING_INVALIDEMAIL = "invalidEmail";
        public static final String PROGRESSIVEPROFILING_INVALIDPHONE = "invalidPhone";
        public static final String PROGRESSIVEPROFILING_INVALIDFIELD = "invalidField";
        public static final String PROGRESSIVEPROFILING_REQUIREDFIELD = "requiredField";
        public static final String PROGRESSIVEPROFILING_SUCCESSTITLE = "successTitle";
        public static final String PROGRESSIVEPROFILING_SUCCESSMSG = "successMsg";
        public static final String PROGRESSIVEPROFILING_HEADINGP2 = "headingP2";
        public static final String PROGRESSIVEPROFILING_SUBTITLEP2 = "subtitleP2";
        public static final String PROGRESSIVEPROFILING_HEADINGP3 = "headingP3";
        public static final String PROGRESSIVEPROFILING_SUBTITLEP3 = "subtitleP3";
        public static final String PROGRESSIVEPROFILING_HEADINGP4 = "headingP4";
        public static final String PROGRESSIVEPROFILING_SUBTITLEP4 = "subtitleP4";
        public static final String PROGRESSIVEPROFILING_HEADINGP5 = "headingP5";
        public static final String PROGRESSIVEPROFILING_SUBTITLEP5 = "subtitleP5";
        public static final String PROGRESSIVEPROFILING_DISCLAIMER = "disclaimer";
        public static final String PROGRESSIVEPROFILING_PROVINCE_OUTOFCANADA = "selectOusideCanada";

        public static final String PROGRESSIVEPROFILING_ELOQUANAME_EMAIL = "emailAddress";
        public static final String PROGRESSIVEPROFILING_ELOQUANAME_FNAME = "firstName";
        public static final String PROGRESSIVEPROFILING_ELOQUANAME_LNAME = "lastName";
        public static final String PROGRESSIVEPROFILING_ELOQUANAME_TITLE = "title";
        public static final String PROGRESSIVEPROFILING_ELOQUANAME_TRADESTATUS = "tradeStatus";
        public static final String PROGRESSIVEPROFILING_ELOQUANAME_TRADESTATUS_SELECTIONS = "items_tradeStatusSelection";
        public static final String PROGRESSIVEPROFILING_ELOQUANAME_COMPANYNAME = "companyName";
        public static final String PROGRESSIVEPROFILING_ELOQUANAME_MAINPHONE = "mainPhone";
        public static final String PROGRESSIVEPROFILING_ELOQUANAME_COMPANYADDR1 = "companyAddress1";
        public static final String PROGRESSIVEPROFILING_ELOQUANAME_COMPANYADDR2 = "companyAddress2";
        public static final String PROGRESSIVEPROFILING_ELOQUANAME_COMPANYCITY = "companyCity";
        public static final String PROGRESSIVEPROFILING_ELOQUANAME_COMPANYPROVINCE = "companyProvince";
        public static final String PROGRESSIVEPROFILING_ELOQUANAME_COMPANYPROVINCE_OUTOFCANADA = "selectOusideCanada";
        public static final String PROGRESSIVEPROFILING_ELOQUANAME_COMPANYPOSTAL = "companyPostal";
        public static final String PROGRESSIVEPROFILING_ELOQUANAME_COMPANYCOUNTRY = "companyCountry";
        public static final String PROGRESSIVEPROFILING_ELOQUANAME_ANNUALSALES = "annualSales";
        public static final String PROGRESSIVEPROFILING_ELOQUANAME_ANNUALSALES_SELECTIONS = "items_annualSalesSelection";
        public static final String PROGRESSIVEPROFILING_ELOQUANAME_EMPOLYEES = "employees";
        public static final String PROGRESSIVEPROFILING_ELOQUANAME_EMPOLYEES_SELECTIONS = "items_employeesSelection";
        public static final String PROGRESSIVEPROFILING_ELOQUANAME_PAINPOINT = "painPoint";
        public static final String PROGRESSIVEPROFILING_ELOQUANAME_INDUSTRY = "industry";
        public static final String PROGRESSIVEPROFILING_ELOQUANAME_MARKETSINS = "marketsInt";

        public static final String PROGRESSIVEPROFILING_ELOQUAVALUE_NOPROVINCE = "no-province";

        public static final String PROGRESSIVEPROFILING_FIELD_LABEL = "emailFieldLabel";
        public static final String PROGRESSIVEPROFILING_FIELD_HELP = "emailFieldPlaceholder";

        public static final String PROGRESSIVEPROFILING_HEADING = "heading";
        public static final String PROGRESSIVEPROFILING_SUBTITLE = "subtitle";

        public static final String PROGRESSIVEPROFILING_UPSELL_TITLE = "upsellTitle";
        public static final String PROGRESSIVEPROFILING_UPSELL_DESCRIPTION = "upsellDesc";
        public static final String PROGRESSIVEPROFILING_UPSELL_CTA_TEXT = "upsellCtaText";
        public static final String PROGRESSIVEPROFILING_UPSELL_OPT_LINK_TEXT = "upsellOptLinkText";
        public static final String PROGRESSIVEPROFILING_UPSELL_OPT_LINK_URL = "upsellOptLinkUrl";

        public static final String L5 = "L5";
        public static final String PROGRESSIVEPROFILING_UPSELL_TITLE_L5 = PROGRESSIVEPROFILING_UPSELL_TITLE + L5;
        public static final String PROGRESSIVEPROFILING_UPSELL_DESCRIPTION_L5 = PROGRESSIVEPROFILING_UPSELL_DESCRIPTION + L5;
        public static final String PROGRESSIVEPROFILING_UPSELL_CTA_TEXT_L5 =  PROGRESSIVEPROFILING_UPSELL_CTA_TEXT + L5;
        public static final String PROGRESSIVEPROFILING_UPSELL_OPT_LINK_TEXT_L5 = PROGRESSIVEPROFILING_UPSELL_OPT_LINK_TEXT + L5;
        public static final String PROGRESSIVEPROFILING_UPSELL_OPT_LINK_URL_L5 = PROGRESSIVEPROFILING_UPSELL_OPT_LINK_URL + L5;
        public static final String PROGRESSIVEPROFILING_SUBMIT_BUTTON_L5 = PROGRESSIVEPROFILING_SUBMITBUTTON + L5;


        public static final String PROGRESSIVEPROFILING_HEADING_LOGGED_IN = "headingLoggedIn";
        public static final String PROGRESSIVEPROFILING_SUBTITLE_LOGGED_IN = "subtitleLoggedIn";
        public static final String PROGRESSIVEPROFILING_SUBMIT_BUTTON_LOGGED_IN = "submitButtonLoggedIn";

        public static final String TRADEINSIGHTS_CATEGORYORDER = "categoryOrder";
        public static final String TRADEINSIGHTS_INDUSTRYORDER = "industryOrder";
        public static final String TRADEINSIGHTS_REGIONORDER = "regionOrder";
        public static final String TRADEINSIGHTS_FORMATORDER = "formatOrder";
        public static final String TRADEINSIGHTS_TRENDINGORDER = "trendingOrder";

        public static final String BANNER_TYPE = "bannertype";
        public static final String BLUE_BLOCK = "blueBlock";
        public static final String VIDEOID = "videoid";
        public static final String VIDEO_WEBM = "videowebm";
        public static final String VIDEO_MP4 = "videomp4";
        public static final String VIDEO_OGG = "videoogg";
        public static final String VIDEO_CLOSE = "videoclose";
        public static final String TAGLINE = "tagline";
        public static final String TEXT_ALIGNMENT = TEXT + ALIGNMENT;
        public static final String VIDEOCTATEXT = "videoctatext";
        public static final String ANCHOR_TARGET = "anchortarget";
        public static final String LINK_DATA_TARGET = "ctadatatarget";
        public static final String BACKGROUND_ICON = "backgroundIcon";

        public static final String KEYLINE = "keyline";
        public static final String RELATEDPAGE1 = "relatedPage1";
        public static final String RELATEDPAGE2 = "relatedPage2";
        public static final String RELATEDPAGE3 = "relatedPage3";
        public static final String VIEWMORETEXT = "viewMoreText";
        public static final String VIEWMORELINK = "viewMoreLink";
        public static final String VIEWMORETARGET = "viewMoreTarget";

        public static final String REGIONALOFFICES = "regionalOffices";
        public static final String ABROAD  = "abroad";
        public static final String INTERNATIONAL = "international";
        public static final String CITIES = "cities";

        public static final String PREMIUM_PATH = "premiumpath";
        public static final String NON_PREMIUM_PATH = "nonpremiumpath";
        public static final String PREMIUM_TITLE = "premiumtitle";
        public static final String NON_PREMIUM_TITLE = "nonpremiumtitle";

        //Sign In
        public static final String HEADER_EXTERNAL_ID = "externalId";
        public static final String PRODUCT_TYPE = "productType";
        public static final String PRODUCT_DESC = "productDesc";
        public static final String REDIRECT_URL = "redirectUrl";
        public static final String SUBSCRIPTION = "Subscription";
        public static final String ACCOUNT_CREATION = "Account Creation";
        public static final String KNOWLEDGE_PRODUCT = "Knowledge Product";
        public static final String PREMIUM_CONTENT = "Premium Content";
        public static final String PROGRESSIVEPROFILING_TYPE = "ppType";
        public static final String SIGNINPORTAL_TYPE = "signinType";
        public static final String PARAM_DELIMITER = "_!_";
        public static final String SUB_QUERY_PARAM_ACTION_TYPE_DELIMITER = "!_!";
        public static final String SUB_QUERY_PARAM_ACTION_TYPE_AMPERSAND = "&";

        public static final String REFERER = "referer";
        public static final String QUERY_PARAM_PRODUCT_INTENT = "productIntent";

        public static final String SHIBBOLETH_COOKIE_NAME = "_shibsession_login_token";
        public static final String SHIBBOLETH_SESSION_VAR = "shibession_id";

        public static final String EMAIL_NOTAPPLICABLE_DEFAULT = "n/a";

        public static final String YESNOQUESTION_EMAIL_FEEDBACKQUESTION = "feedbackQuestion";
        public static final String YESNOQUESTION_EMAIL_USERINFO = "userInfo";
        public static final String YESNOQUESTION_EMAIL_FEEDBACKONE_QUESTIONANSWER = "feedbackOneQuestion";
        public static final String YESNOQUESTION_EMAIL_FEEDBACKONE_OPTIONS = "feedbackOneOptions";
        public static final String YESNOQUESTION_EMAIL_FEEDBACKTWO_QUESTIONANSWER = "feedbackTwoQuestion";
        public static final String YESNOQUESTION_EMAIL_FEEDBACKTWO_OPTIONS = "feedbackTwoOptions";
        public static final String SEARCHQUERY = "searchQuery";
        public static final String USERINPUT = "userInput";
        public static final String OPTION = "option";
        public static final String ANSWER = "answer";
        public static final String ANSWERS = "answers";
        public static final String ANSWERS_YES = ANSWERS + "Yes";
        public static final String ANSWERS_NO = ANSWERS + "No";
        public static final String QUESTION = "question";
        public static final String QUESTION_YES = QUESTION + "Yes";
        public static final String QUESTION_NO = QUESTION + "No";
        public static final String USA_COUNTRY_CODE = "USA";
        public static final String CAN_COUNTRY_CODE = "CAN";
        public static final String COUNTRIES = "countries";
        public static final String TRAFFIC_SOURCE_COOKIE_NAME = "trafficsrc";
        public static final String LOGOUT_RETURN = "logoutReturn";
        public static final String LEFT_TITLE_IN = "lefttitlein";
        public static final String PROGRESSIVEPROFILING_NO_REDIRECT_BACK = "noRedirectBack";
        public static final String SHOW_SEARCH = "showSearch";
        // No spaces in between the words
        public static final String INVALID_URL_QS = "refPremiumURL,query";

        public static final String PAGE_LEVEL_FEEDBACK = "idPageLevelFeedback";

        // Sticky nav version
        public static final String NAV_VERSION = "navVersion";
        public static final String DEFAULT_NAV_VERSION ="normal";
        // required for MyEDC Gating
        public static final String BC_ACTION_ID = "aid";
        // Just in case there's a need to change it later
        public static final String BC_AID_QS_PARAM = BC_ACTION_ID;
        public static final String SHOW_ALL_CARDS = "showAllCards";

        public static final String SELF_ID_LOV = "selfidlov";
        public static final String SELF_ID_LOV_VALUE = "selfidvalue";
        public static final String PREFER_NOT_TO_DISCLOSE = "prefer not to disclose";

        // Reference Accordion
        public static final String REF_ACCORDION_REF_URL = "referencedurl";
        public static final String PCI_TEMPLATE_TITLE = "Left-hand Navigation Page PCI";
        public static final String PCI_TEMPLATE_MAIN_CONTAINER = "centercolumn";
        public static final String PCI_PAGE_TITLE = "title";
        // Campaign product Carousel.
        public static final String AUTOMATIC_POPULATION = "automatic";

        // Accesibility
        public static final String SHOW_SEARCH_ACCESSIBILITY = "showSearchAccessibility";
        public static final String LANGUAGE_TOGGLE_ACCESSIBILITY = "languageToggleAccessibility";
        public static final String SECONDARY_NAVIGATION_BAR_ACCESSIBILITY = "secondaryNavigationBarAccessibility";
        public static final String CLOSE_MENU_ACCESSIBILITY = "closeMenuAccessibility";
        
    }

    public class Myedc {
        //myedc
        public static final String MYEDC = "/myedc";
        public static final String CONTENT_MYEDC = Paths.CONTENT + MYEDC;
        public static final String MYACCOUNT_EN = "/en/my-account";
        public static final String MYACCOUNT_LABEL_EN = "MyEDC Account";
        public static final String MYACCOUNT_FR = "/fr/mon-compte";
        public static final String MYACCOUNT_LABEL_FR = "MonEDC Compte";

        public static final String MYDASHBOARD_LABEL_EN = "My dashboard";
        public static final String MYDASHBOARD_LINK_EN = MYACCOUNT_EN + "/home.html";
        public static final String MYDASHBOARD_LABEL_FR = "Tableau De Bord De Compte";
        public static final String MYDASHBOARD_LINK_FR = MYACCOUNT_FR + "/accueil.html";

        public static final String MANAGEPROFILE_LABEL_EN = "Manage my profile";
        public static final String MANAGEPROFILE_EN = MYACCOUNT_EN + "/profile.html";

        public static final String MANAGEPROFILE_LABEL_FR = "Gérer mon profil";
        public static final String MANAGEPROFILE_FR = MYACCOUNT_FR + "/profil.html";
    }


    /**
     * Inner-enum that represents the supported languages (currently, English and French). The default language if English for cases
     * where the language could not be determined.
     *
     */
    public enum SupportedLanguages
    {
        ENGLISH("English", "EN", "MMMM dd, yyyy"), FRENCH("Français", "FR", "dd MMMM yyyy");

        /**
         * Populates the values of this <code>SupportedLanguages</code> object.
         *
         * @param  description  Textual description of this language (e.g., "English" for English, "French" for French).
         * @param  abbreviation  Abbreviation of the language (e.g., "EN" for English, "FR" for French).
         */
        SupportedLanguages(String description, String abbreviation, String dateFormat)
        {
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
         * @return  String  Textual description of this language (e.g., "English" for English, "French" for French).
         */
        public String getLanguageText()
        {
            return this.language;
        }
        /**
         * Get the abbreviation of this language.
         *
         * @return  String  Abbreviation of the language (e.g., "EN" for English, "FR" for French).
         */
        public String getLanguageAbbreviation()
        {
            return this.languageAbbreviation;
        }
        /**
         * Get the date format of this language.
         *
         * @return  String  Date format of the language (e.g., "MMMM dd, yyyy" for English, "dd MMMM yyyy" for French).
         */
        public String getDateFormat()
        {
            return this.dateFormat;
        }
        /**
         * Given the abbreviation of a language, return its <code>SupportedLanguages</code> object.
         *
         * @return  SupportedLanguages  <code>SupportedLanguages</code> object for the given Abbreviation. If the abbreviation does not match a language, English is returned.
         */
        public static SupportedLanguages getLanguageFromAbbreviation(String abbreviation)
        {
            SupportedLanguages supportedLanguage=ENGLISH;
            //-----------------------------------------------------------------
            // Find our Language object using the language abbreviation
            //-----------------------------------------------------------------
            for(SupportedLanguages lang : SupportedLanguages.values())
            {
                if(abbreviation.equalsIgnoreCase(lang.getLanguageAbbreviation()))
                {
                    supportedLanguage = lang;
                    break;
                }
            }
            return supportedLanguage;
        }
    }
    /**
     * Inner-enum that represents the possible article content types. This enum has a "default" type for cases
     * where the content type could not be determined.
     * Order the enum constants as in UI #16801
     * **Important** This order will be used as tag priority
     */
    public enum ArticleContentType
    {
        SUCCESSSTORY("edc:format-type/success-story", "success-story"),
        WEEKLYCOMMENTARY("edc:format-type/weekly-commentary", "weekly-commentary"),
        WEBINAR("edc:event-type/webinar", "webinar"),
        TOOL("edc:format-type/tool", "tool"),
        VIDEO("edc:format-type/video",   "video"),
        EBOOK("edc:format-type/ebook",   "ebook"),
        GUIDE("edc:format-type/guide", "guide"),
        BLOG("edc:format-type/blog",    "blog"),
        REPORT("edc:format-type/report",  "report"),
        ARTICLE("edc:format-type/article", Constants.Properties.ARTICLE);


    /**
     * Populates the values of this <code>ArticleContentType</code> object.
     *
     * @param  tagId  Tag id for this content type.
     * @param  className  Class name associated with this content type.
     */
        ArticleContentType(String tagId, String className)
        {
            this.tagId = tagId;
            this.classNm = className;
        }
        private final String tagId;
        private final String classNm;
        /**
         * Get the class name of this article content type.
         *
         * @return  String  CSS class name for this article content type.
         */
        public String getClassName()
        {
            return this.classNm;
        }
        /**
         * Get the tag id of this article content type.
         *
         * @return  String  Tag id for this article content type.
         */
        public String getTagId()
        {
            return this.tagId;
        }
        /**
         * Given the article content type tag id, return its <code>ArticleContentType</code> object.
         *
         * @return  ArticleContentType  <code>ArticleContentType</code> object for the given tag id. If the tag id does not match a content type, "default" is returned.
         */
        public static ArticleContentType getArticleContentTypeFromTagId(String tagId)
        {
            ArticleContentType articleContentType=ARTICLE;
            //-----------------------------------------------------------------
            // Find our ArticleContentType object with the given tagId
            //-----------------------------------------------------------------
            for(ArticleContentType contentType : ArticleContentType.values())
            {
                if(tagId.equalsIgnoreCase(contentType.getTagId()))
                {
                    articleContentType = contentType;
                    break;
                }
            }
            return articleContentType;
        }
    }

    //Set the required paths to use for bread crumb logic
    protected static final String[] BREADCRUMB_PATHS = {
        "solutions",
        "country-info",
        "market-entry-advisors",
        "success-stories",
        "events",
        "financial-institutions"
    };


    /**
     * getBreadcrumbPaths
     *
     * @return String Array with path names
     */
    public static String[] getBreadcrumbPaths() {
        return BREADCRUMB_PATHS;
    }
}