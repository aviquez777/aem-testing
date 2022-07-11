package com.edc.edcweb.core.datasources.gatedleadgenform;

import java.util.Arrays;
import java.util.Iterator;

import javax.annotation.PostConstruct;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Workspace;
import javax.jcr.query.Query;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.ui.components.ds.DataSource;
import com.day.cq.wcm.api.Page;
import com.edc.edcweb.core.helpers.gatedleadgenform.GlgfConstants;
import com.edc.edcweb.core.helpers.gatedleadgenform.GlgfSQL2Queries;
import com.edc.edcweb.core.helpers.gatedleadgenform.CglgfUtils;

@Model(adaptables = SlingHttpServletRequest.class)
public class GlgfOpenQuestions {

    private static final Logger log = LoggerFactory.getLogger(GlgfOpenQuestions.class);

    @Self
    private SlingHttpServletRequest request;

    @Reference
    private Page currentPage;

    @PostConstruct
    public void initModel() {
        Resource cpRes = request.getResourceResolver().getResource(GlgfConstants.POLICY_BASE_DATA_NODE);
        Resource configured = cpRes.getChild(GlgfConstants.QUESTION_CONFIGURED_MULTIFIELD);
        // check pre-loaded questions first time
        if (configured == null) {
            checkPreloadedQuestions(cpRes);
        }
        // check if a new question needs to be added
        checkForNewQuestions(cpRes);
        // get the "used" questions so they can be removed from the available list
        String used = CglgfUtils.getUsedQuestions(cpRes, false);
        ResourceResolver resolver = request.getResourceResolver();
        DataSource ds = getGenericUnusedQuestions(resolver, used);
        this.request.setAttribute(DataSource.class.getName(), ds);
    }

    private void checkPreloadedQuestions(Resource cpRes) {
        ResourceResolver resourceResolver = request.getResourceResolver();
        Session session = resourceResolver.adaptTo(Session.class);
        Workspace workspace = session.getWorkspace();
        String sourcePath = GlgfConstants.QUESTIONS_BASE_DATA_NODE.concat(GlgfConstants.FORWARD_SLASH)
                .concat(GlgfConstants.QUESTION_CONFIGURED_MULTIFIELD);
        String destinationPath = cpRes.getPath().concat(GlgfConstants.FORWARD_SLASH)
                .concat(GlgfConstants.QUESTION_CONFIGURED_MULTIFIELD);
        try {
            workspace.copy(sourcePath, destinationPath);
        } catch (RepositoryException e) {
            log.error("CglgfModel checkPreloadedQuestions error ", e);
        }
    }

    private void checkForNewQuestions(Resource cpRes) {
        if (cpRes != null) {
            ValueMap properties = cpRes.getValueMap();
            String questionName = properties.get(GlgfConstants.QUESTION_NAME, String.class);
            String questionType = properties.get(GlgfConstants.QUESTION_TYPE, String.class);
            if (StringUtils.isNotBlank(questionName) && StringUtils.isNotBlank(questionType)) {
                addNewQuestion(cpRes);
            }
        }
    }

    private DataSource getGenericUnusedQuestions(ResourceResolver resolver, String used) {
        String queryText = GlgfSQL2Queries.getGenericUnusedQuestionsQueryStatement(used);
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

    private void addNewQuestion(Resource cpRes) {
        ResourceResolver resourceResolver = request.getResourceResolver();
        Node clearPolNode = cpRes.adaptTo(Node.class);
        String checkPath = cpRes.getPath();
        Resource multifield = cpRes.getChild(GlgfConstants.QUESTION_CONFIGURED_MULTIFIELD);
        Session session = resourceResolver.adaptTo(Session.class);
        boolean canSetProperty = false;
        try {
            canSetProperty = session.hasPermission(checkPath, Session.ACTION_ADD_NODE);
            ValueMap properties = cpRes.getValueMap();
            String questionTouse = properties.get(GlgfConstants.QUESTION_TO_USE, String.class);
            String questionName = properties.get(GlgfConstants.QUESTION_NAME, String.class);
            String questionType = properties.get(GlgfConstants.QUESTION_TYPE, String.class);
            Node multifieldNode = null;
            if (multifield != null) {
                multifieldNode = multifield.adaptTo(Node.class);
            } else {
                multifieldNode = clearPolNode.addNode(GlgfConstants.QUESTION_CONFIGURED_MULTIFIELD);
            }
            if (multifieldNode != null) {
                String nodeName = getNodeName(multifield);
                Node newQuestionNode = multifieldNode.addNode(nodeName);
                Resource openQuesionRes = resourceResolver.getResource(questionTouse);
                String fieldName = openQuesionRes.getValueMap().get(GlgfConstants.QUESTION_FIELD_NAME, String.class);
                newQuestionNode.setProperty(GlgfConstants.QUESTION_TO_USE, questionTouse);
                newQuestionNode.setProperty(GlgfConstants.QUESTION_FIELD_NAME, fieldName);
                newQuestionNode.setProperty(GlgfConstants.QUESTION_NAME, questionName);
                newQuestionNode.setProperty(GlgfConstants.QUESTION_TYPE, questionType);

                if (session.hasPermission(cpRes.getPath(), Session.ACTION_SET_PROPERTY)) {
                    clearPolNode.setProperty(GlgfConstants.QUESTION_TO_USE, "");
                    clearPolNode.setProperty(GlgfConstants.QUESTION_NAME, "");
                    clearPolNode.setProperty(GlgfConstants.QUESTION_TYPE, "");
                }
                if (canSetProperty) {
                    session.save();
                }
            }
        } catch (RepositoryException e) {
            log.error("CglgfModel addNewQuestion error ", e);
        }
    }

    private String getNodeName(Resource multifield) {
        String name = "item";
        int count = 0;
        if (multifield != null) {
            Iterator<Resource> iterator = multifield.listChildren();
            while (iterator.hasNext()) {
                count++;
                iterator.next();
            }
        }
        return name.concat(Integer.toString(count));
    }
}
