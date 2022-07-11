package com.edc.edcweb.core.gatedcontentaccess.helpers;

public class GatedContentAccessConstats {

    private GatedContentAccessConstats() {
        // SonarQube
    }

    public static final String LANDING_PAGE_PROPERTY = "landingPage";
    public static final String ASSET_GROUP_NAME_PROPERTY = "assetGroupName";
    public static final String GATED_CONTENT_PATH_PROPERTY = "gatedContentPath";
    public static final String QUESTION_MULTIFIELD_PROPERTY = "questions";
    public static final String QUESTION_TYPE_PROPERTY = "questionType";
    public static final String ELQ_SITE_ID_PROPERTY = "elqSiteId";
    public static final String FULL_FROM_PAGE_PROPERTY = "fullFromPage";
    public static final String SUBMIT_ERROR_TEXT_PROPERTY = "submitErrorText";
    public static final String CASL_CONSENT_PROPERTY = "caslConsent";
    public static final String QUESTION_OVERRIDE_PROPERTY = "questionOverride";
    public static final String QUESTION_DEPENDS_ON_PROPERTY = "dependsOn";

    public static final String CASL_CONSENT_VALUE = "yes";

    public static final String EMAIL_ADDRESS_FIELD = "emailAddress";
    public static final String OVERRIDE_PATH_FIELD = "ovpa";
    public static final String RESOURCE_TYPE_FIELD = "rtype";

    // will check for html and pdf on content/dam or content/edc
    public static final String FILTER_PATTERN = ".*(?<!\\.html)\\/content(\\/dam|)\\/edc\\/.*\\/gated\\/.*(\\.html|\\.pdf)$";
    public static final String JCR_CONTENT = "jcr:content";
    public static final String SERVLET_PATH = "bin/gatedContentServlet";
    public static final String SHARE_DETAILS_SERVLET_PATH = "bin/shareDetailsServlet";
    public static final String FORM_RESOURCE_TYPE = "edc/components/content/gatedcontentaccess/gatedcontentaccessform";
    public static final String FORM_GLGF_RESOURCE_TYPE = "edc/components/content/gatedleadgenform";
    public static final String CQ_PAGE = "cq:Page";
    public static final String DAM_ASSET = "dam:Asset";
    public static final String NOT_FOUND = "not found";
    public static final String STRING_YES = "Y";
    public static final String GET_METHOD = "get";

    // Pdf metadata landing teaser page
    public static final String PDF_LANDING_PAGE = "pdflandingpage";
    public static final String PDF_IS_CRG = "isCRG";
  

}
