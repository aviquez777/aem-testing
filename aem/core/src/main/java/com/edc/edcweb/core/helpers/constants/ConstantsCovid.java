package com.edc.edcweb.core.helpers.constants;

public class ConstantsCovid {
    private ConstantsCovid() {
        // SonarQube
    }

    public static final String FILTER_TAGS = "filtertags";
    public static final String ORGANIZATION_TAGS = "organizationtags";
    public static final String FILTER_SEPARATOR = "::";

    public static final String SHOW_USED_TAGS_FIELD_NAME = "showUsedTagsOnly";
    public static final String TAG_NAMESPACE_FIELD_NAME = "tagNamespace";
    public static final String CARD_RESOURCE_TYPE = "edc/components/content/covidresponse/covidcard";
    public static final String QUERY = "SELECT * FROM [nt:unstructured] AS card "
            .concat("WHERE ISDESCENDANTNODE(card, '%s') AND [sling:resourceType] = '").concat(CARD_RESOURCE_TYPE)
            .concat("'");
}
