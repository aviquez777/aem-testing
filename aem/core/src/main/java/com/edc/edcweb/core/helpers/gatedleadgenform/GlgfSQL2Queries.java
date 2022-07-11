package com.edc.edcweb.core.helpers.gatedleadgenform;

public class GlgfSQL2Queries {

    private GlgfSQL2Queries() {
        // SonarQube
    }

    public static String getGenericUnusedQuestionsQueryStatement(String used) {
        StringBuilder query = new StringBuilder();
        query.append("SELECT * FROM [nt:base] AS r ");
        query.append("WHERE ISDESCENDANTNODE([").append(GlgfConstants.QUESTIONS_BASE_DATA_NODE)
                .append(GlgfConstants.GENERIC_QUESTIONS_DATA_NODE).append("]) ");
        query.append("AND NOT r.[jcr:path] IN(").append(used).append(")");
        query.append("ORDER BY r.[jcr:path]");
        return query.toString();
    }

    public static String getConfiguredQuestionsQueryStatement(String used) {
        StringBuilder query = new StringBuilder();
        query.append("SELECT * FROM [nt:base] AS r ");
        query.append("WHERE (ISDESCENDANTNODE([").append(GlgfConstants.POLICY_BASE_DATA_NODE).append("/")
                .append(GlgfConstants.QUESTION_CONFIGURED_MULTIFIELD).append("]) ");
        query.append("AND r.[jcr:path] IN(").append(used).append(")) ");
        query.append("OR ISDESCENDANTNODE([").append(GlgfConstants.QUESTIONS_BASE_DATA_NODE)
                .append(GlgfConstants.STANDARD_QUESTIONS_DATA_NODE).append("]) ");
        query.append("ORDER BY r.questionName");
        return query.toString();
    }
}
