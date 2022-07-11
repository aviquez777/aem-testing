package com.edc.edcweb.core.helpers.constants;

import com.edc.edcweb.core.helpers.Constants;

public class ConstantsDatalayer extends Constants {

    private ConstantsDatalayer() { }

    public class DLProperties extends Constants.Properties {

        public static final String DATALAYER_ENV_DEV = "dev";
        public static final String DATALAYER_ENV_PROD = "prod";
        public static final String DATALAYER_ENV_QA = "qa";
        public static final String DATALAYER_ENV_STAGE = "stage";
        public static final String DATALAYER_ENV_TEST = "test";
        public static final String DATALAYER_REGION = "region";
        public static final String DATALAYER_INDUSTRY = "industry";
        public static final String DATALAYER_FTAS= "ftas";
        public static final String DATALAYER_CONTRIBUTORS = "contributors";
        public static final String DATALAYER_CATEGORY = "category";
        public static final String DATALAYER_SUBCATEGORY = "subCategory";
        public static final String DATALAYER_ACCESSTYPE = "accessType";
        public static final String DATALAYER_FORMATYPE = "formatType";
        public static final String DATALAYER_SOLUTION = "solution";
        public static final String DATALAYER_EXPORTSTATUS = "exportStatus";
        public static final String DATALAYER_PERSONA = "persona";
        public static final String DATALAYER_BUYERSTAGE = "buyerStage";
        public static final String DATALAYER_OWNERID = "ownerId";
        public static final String DATALAYER_EVENTTYPE = "eventType";
        public static final String DATALAYER_TAG_REGION = DATALAYER_REGION + "/";
        public static final String DATALAYER_TAG_INDUSTRY = DATALAYER_INDUSTRY + "/";
        public static final String DATALAYER_TAG_FTAS = DATALAYER_FTAS + "/";
        public static final String DATALAYER_TAG_CONTRIBUTORS = DATALAYER_CONTRIBUTORS + "/";
        public static final String DATALAYER_TAG_CATEGORY = DATALAYER_CATEGORY + "/";
        public static final String DATALAYER_TAG_ACCESSTYPE = "access-type/";
        public static final String DATALAYER_TAG_FORMATYPE = "format-type/";
        public static final String DATALAYER_TAG_SOLUTION = DATALAYER_SOLUTION + "/";
        public static final String DATALAYER_TAG_SOLUTIONS = "solutions/";
        public static final String DATALAYER_TAG_EXPORTSTATUS = "export-status/";
        public static final String DATALAYER_TAG_PERSONA = DATALAYER_PERSONA + "/";
        public static final String DATALAYER_TAG_BUYERSTAGE = "buyer-stage/";
        public static final String DATALAYER_TAG_OWNERID = "owner-id/";
        public static final String DATALAYER_TAG_EVENTTYPE = "event-type/";
        public static final String DATALAYER_TAG_SEPARATOR = ";";
        public static final String DATALAYER_TYPE_ARTICLE = "edc/components/structure/article/page";
        public static final String DATALAYER_COMPONENTPATH = Paths.JCR_CONTENT + "/root";
        public static final String DATALAYER_ARTICLENODENAME = "articlecontainer";
        public static final String DATALAYER_PRYMARYTAG = "primarytag";
    }
}
