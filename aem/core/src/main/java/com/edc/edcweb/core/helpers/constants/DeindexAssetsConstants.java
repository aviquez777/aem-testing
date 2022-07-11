package com.edc.edcweb.core.helpers.constants;

public class DeindexAssetsConstants {
    private DeindexAssetsConstants() {
        // SonarQube
    }

    // double "\\" needed by Java to escape the "\" on the string
    public static final String FILTER_PATTERN =  "^.*\\/content\\/dam\\/edc\\/.*\\.(pdf|docx?|xlsx?)$";
    public static final String JCR_CONTENT = "jcr:content";
    public static final String SEO_EXCLUDE_METADATA_PROPERTY = "seoExclude";
    public static final String X_ROBOTS_HEADER_TAG = "X-Robots-Tag";
    public static final String NO_INDEX_NO_FOLLOW = "noindex, nofollow";

}
