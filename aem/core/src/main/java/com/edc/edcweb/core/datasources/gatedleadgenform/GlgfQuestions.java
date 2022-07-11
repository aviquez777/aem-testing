package com.edc.edcweb.core.datasources.gatedleadgenform;

import java.util.Arrays;
import java.util.Iterator;

import javax.annotation.PostConstruct;
import javax.jcr.query.Query;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;

import com.adobe.granite.ui.components.ds.DataSource;
import com.edc.edcweb.core.helpers.gatedleadgenform.GlgfConstants;
import com.edc.edcweb.core.helpers.gatedleadgenform.GlgfSQL2Queries;
import com.edc.edcweb.core.helpers.gatedleadgenform.CglgfUtils;

@Model(adaptables = SlingHttpServletRequest.class)
public class GlgfQuestions {

    @Self
    private SlingHttpServletRequest request;

    @PostConstruct
    public void initModel() {
        ResourceResolver resolver = request.getResourceResolver();
        Resource cpRes = request.getResourceResolver().getResource(GlgfConstants.POLICY_BASE_DATA_NODE);
        // get the "used" questions so they can be added to the available list
        String used = CglgfUtils.getUsedQuestions(cpRes, true);
        DataSource ds = getConfiguredQuestions(resolver, used);
        this.request.setAttribute(DataSource.class.getName(), ds);
    }

    private DataSource getConfiguredQuestions(ResourceResolver resolver, String used) {
        String queryText = GlgfSQL2Queries.getConfiguredQuestionsQueryStatement(used);
        DataSource ds = null;
        // Execute the query
        Iterator<Resource> questionList = resolver.findResources(queryText, Query.JCR_SQL2);
        if (questionList.hasNext()) {
            ds = CglgfUtils.getFromResource(resolver, questionList);
        } else {
            String array[] = new String[] {
                    "".concat(GlgfConstants.MULTIPLE_DELIMITER).concat(GlgfConstants.NO_QUESTIONS_FOUND) };
            Iterator<String> arrIterator = Arrays.asList(array).iterator();
            ds = CglgfUtils.getFromArray(resolver, arrIterator);
        }
        return ds;
    }
}
