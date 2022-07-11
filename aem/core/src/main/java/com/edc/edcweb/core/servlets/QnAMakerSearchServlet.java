package com.edc.edcweb.core.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;

import javax.inject.Inject;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import com.edc.edcweb.core.helpers.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.policies.ContentPolicy;
import com.edc.edcweb.core.helpers.Constants;
import com.edc.edcweb.core.helpers.ContentPolicyUtil;
import com.edc.edcweb.core.helpers.LanguageUtil;
import com.edc.edcweb.core.helpers.LinkResolver;
import com.edc.edcweb.core.helpers.ResourceResolverHelper;
import com.edc.edcweb.core.helpers.constants.ConstantsEHH;
import com.edc.edcweb.core.helpers.ehh.KnowledgeServiceAnswer;
import com.edc.edcweb.core.models.AuthorBio;
import com.edc.edcweb.core.service.KnowledgeService;

/**
 * @author ACN
 * @version 1.0.0
 * @since 1.0.0
 *
 *        QnA is used to get better EHH answers for user question (query param)
 *
 */
@Component(immediate = true, service = Servlet.class, property = { "sling.servlet.extensions=json",
        "sling.servlet.methods=GET", "sling.servlet.paths=" + ConstantsEHH.EHHProperties.EHH_PATH_QNAMAKERSERVLET })
    public class QnAMakerSearchServlet extends SlingAllMethodsServlet {

    private static final long serialVersionUID = -3522134777775086197L;
    private static final Logger log = LoggerFactory.getLogger(QnAMakerSearchServlet.class);

    @Reference
    @Inject
    KnowledgeService knowledgeService;

    private String lang;
    private float scorelow = 0;
    private float scoreHigh = 0;
    private String top = "1000";
    private ArrayList<Float> scoreList = new ArrayList<>();
    String strRegEx = "<[^>]*>";

    @Override
    protected final void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) //NOSONAR
            throws ServletException, IOException { 
        boolean confidenceLevelActive = false;

        // Get query params
        String queryparam = request.getParameter(ConstantsEHH.EHHProperties.EHH_QUERY);
        queryparam = URLEncoder.encode(queryparam, "ISO-8859-1");

        try {
            // JSON object
            @SuppressWarnings("CQRules:AMSCORE-553")
            JSONObject jsonobj = new JSONObject();

            // Create JSON array
            @SuppressWarnings("CQRules:AMSCORE-553")
            JSONArray questions = new JSONArray();

            if (StringUtils.isNotEmpty(queryparam)) {
                Resource currentPage = getCurrentPageResource(request);
                String currentPagePath = currentPage != null ? currentPage.getPath() : null;

                queryparam = URLDecoder.decode(queryparam, "UTF-8");
                queryparam = removeParenthesesFromQuery(queryparam);

                ContentPolicy contentPolicy = findSearchResultPolicy(currentPage, request);
                if (contentPolicy != null) {
                    ValueMap propertiesPolicy = contentPolicy.getProperties();
                    scorelow = propertiesPolicy.get(ConstantsEHH.EHHProperties.EHH_ATTR_SCORE, 0);
                    scoreHigh = propertiesPolicy.get(ConstantsEHH.EHHProperties.EHH_ATTR_SCORE_HIGH, 0);

                }

                lang = LanguageUtil.getLanguageAbbreviationFromPath(currentPagePath,
                        ConstantsEHH.EHHProperties.ENGLISH_ABBR);

                // Call knowledgeService with query param
                ArrayList<KnowledgeServiceAnswer> answers = knowledgeService.generateAnswer(request, queryparam, lang,
                        top, String.valueOf(scorelow));

                Iterator<KnowledgeServiceAnswer> itr = answers.iterator();
                while (itr.hasNext()) {

                    KnowledgeServiceAnswer answer = itr.next();

                    // Create json object for each question result
                    JSONObject question = new JSONObject();

                    if (answer.getScore() >= scorelow) {
                        scoreList.add(answer.getScore());

                        question.put(ConstantsEHH.EHHProperties.EHH_ATTR_QUESTION, answer.getQuestion());
                        question.put(ConstantsEHH.EHHProperties.EHH_ATTR_DATALAYERQUESTION,
                                answer.getMetadata().get(ConstantsEHH.EHHProperties.EHH_ATTR_ENGLISHQUESTION));
                        question.put(ConstantsEHH.EHHProperties.EHH_ATTR_COUNTRY,
                                answer.getMetadata().get(ConstantsEHH.EHHProperties.EHH_ATTR_METACOUNTRY));
                        question.put(ConstantsEHH.EHHProperties.EHH_ATTR_TOPIC,
                                answer.getMetadata().get(ConstantsEHH.EHHProperties.EHH_ATTR_METATOPIC));
                        question.put(ConstantsEHH.EHHProperties.EHH_ATTR_ANSWER, answer.getAnswer().replace("\"", "'"));
                        question.put(ConstantsEHH.EHHProperties.EHH_ATTR_ID,
                                answer.getMetadata().get(ConstantsEHH.EHHProperties.EHH_ATTR_EHHID));
                        question.put(ConstantsEHH.EHHProperties.EHH_ATTR_SCORE, answer.getScore());
                        // Set question description
                        String questionDesc = answer.getAnswer().replace("\"", "'").replace("\n", " ")
                                .replaceAll(strRegEx, "");
                        questionDesc = questionDesc.substring(0,
                                questionDesc.length() >= 300 ? 300 : questionDesc.length());
                        question.put(ConstantsEHH.EHHProperties.EHH_ATTR_DESCRIPTION, questionDesc);

                        // Set author information calling setAuthorInfo
                        String authorTag = answer.getMetadata().get(ConstantsEHH.EHHProperties.EHH_ATTR_AUTHOR);
                        if (StringUtils.isNotEmpty(authorTag)) {
                            question.put(ConstantsEHH.EHHProperties.EHH_ATTR_AUTHOR, this.setAuthorInfo(authorTag, request));
                        }

                        // Add question object (completed in this point)
                        questions.put(question);
                    }
                }
            }

            confidenceLevelActive = false;
            double max = 0;
            for (int i = 0; i < scoreList.size(); i++) {
                if (scoreList.get(i) > max) {
                    max = scoreList.get(i);
                }
            }

            if ((max >= scoreHigh) && (!confidenceLevelActive)) {
                jsonobj.put(ConstantsEHH.EHHProperties.EHH_ATTR_CONFIDENCE_LEVEL, "high");
                confidenceLevelActive = true;
            }
            if (((max < scoreHigh) && (max > scorelow)) && (!confidenceLevelActive)) {
                jsonobj.put(ConstantsEHH.EHHProperties.EHH_ATTR_CONFIDENCE_LEVEL, "low");
                confidenceLevelActive = true; //NOSONAR
            }

            jsonobj.put(ConstantsEHH.EHHProperties.EHH_ATTR_QUESTIONS, questions);

            String jsonstring = jsonobj.toString();

            ServletResponseHelper.writeResponse(response, jsonstring);

        } catch (Exception e) {
            log.error("error ", e);
            log.warn("error on QnAMakerSearchServlet, query: {}", queryparam);
        }
    }

    /**
     * Create a JSONObject with author information based on param
     *
     * @param authorTag String with tag author
     * @return JSONObject with all author information
     * @throws JSONException
     */
    @SuppressWarnings("CQRules:AMSCORE-553")
    private JSONObject setAuthorInfo(String authorTag, SlingHttpServletRequest request) throws JSONException {
        // Create json object for author information
        JSONObject jsonobj = new JSONObject();

        // Get author details from biography page
        boolean isEnglish = lang.equalsIgnoreCase(ConstantsEHH.EHHProperties.ENGLISH_ABBR);
        String authorPath = authorTag.replaceAll(".+/",
                isEnglish ? Constants.Paths.AUTHOR_BIO_EN : Constants.Paths.AUTHOR_BIO_FR);
        ResourceResolver resourceResolver = request.getResourceResolver();
        Resource authorResource = resourceResolver.getResource(authorPath + Constants.Properties.AUTHOR_PATH_SUFFIX);

        if (authorResource != null) {
            AuthorBio author = authorResource.adaptTo(AuthorBio.class);

            String authorUrl = LinkResolver.addHtmlExtension(authorPath);
            authorUrl = resourceResolver.map(authorUrl);

            jsonobj.put(ConstantsEHH.EHHProperties.EHH_ATTR_IMAGEURL, author != null ? author.getFileReference() : "");
            jsonobj.put(ConstantsEHH.EHHProperties.EHH_ATTR_IMAGEALT, author != null ? author.getImgalt() : "");
            jsonobj.put(ConstantsEHH.EHHProperties.EHH_ATTR_NAME, author != null ? author.getAuthorname() : "");
            jsonobj.put(ConstantsEHH.EHHProperties.EHH_ATTR_POSITION, author != null ? author.getJobtitle() : "");
            jsonobj.put(ConstantsEHH.EHHProperties.EHH_ATTR_BIOURL, authorUrl);
            jsonobj.put(ConstantsEHH.EHHProperties.EHH_ATTR_LINKEDINURL, author != null ? author.getLinkedin() : "");
            jsonobj.put(ConstantsEHH.EHHProperties.EHH_ATTR_COMPANYNAME, author != null ? author.getCompany() : "");
        }

        return jsonobj;
    }

    /**
     * Find Search Result component policy
     *
     * @return ContentPolicy
     */
    private ContentPolicy findSearchResultPolicy(Resource resource, SlingHttpServletRequest request) {
        ContentPolicy policy = null;

        if (resource != null) {
            PageManager pageManager = resource.getResourceResolver().adaptTo(PageManager.class);

            if (pageManager != null) {
                Page currentPage = pageManager.getContainingPage(resource);

                // Look for the first search result on the page
                Resource rsc = ResourceResolverHelper.getResourceByType(currentPage,
                        Constants.Paths.EHH_SEARCH_RESULT_RESOURCE_TYPE);

                if (rsc != null) {
                    policy = ContentPolicyUtil.resolveLocalizedContentPolicy(request.getResourceResolver(), rsc,
                            currentPage);
                }
            }
        }

        return policy;
    }

    /**
     * Return current page resource from referer header param
     *
     * @return Resource currentPage
     */
    private Resource getCurrentPageResource(SlingHttpServletRequest request) {
        // Get the page from the referer
        Resource currentPage = null;

        try {
            URI urlPage = null;
            String referer = request.getHeader(ConstantsEHH.EHHProperties.EHH_REFERER);

            if (referer != null) {
                urlPage = new URI(referer);
            }

            if (urlPage != null) {
                String currentPagePath = urlPage.getPath().replace(".html", "");
                ResourceResolver resourceResolver = request.getResourceResolver();
                currentPage = resourceResolver.resolve(LinkResolver.changeManualExternalURLtoInternal(currentPagePath));

                if (currentPage.isResourceType(Resource.RESOURCE_TYPE_NON_EXISTING)) {
                    currentPage = null;
                }
            }
        } catch (URISyntaxException e) {
            log.error("error ", e);
            log.warn("error on findSearchResultPolicy");
        }

        return currentPage;
    }

    private String removeParenthesesFromQuery(String originalQuery) {
        String result = originalQuery;
        if (originalQuery != null && !originalQuery.isEmpty()) {
            result = originalQuery.replaceAll("[()]", "");
        }
        return result;
    }
}
