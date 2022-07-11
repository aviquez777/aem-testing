package com.edc.edcweb.core.servlets.shareyourdetails;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;
import javax.servlet.Servlet;

import com.edc.edcweb.core.helpers.ServletResponseHelper;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edc.edcweb.core.gatedcontentaccess.DataObjectTransformationHelper;
import com.edc.edcweb.core.gatedcontentaccess.GatedContentAccessDataBindingUtil;
import com.edc.edcweb.core.gatedcontentaccess.helpers.GatedContentAccessConstats;
import com.edc.edcweb.core.gatedcontentaccess.helpers.GatedContentAccessHelper;
import com.edc.edcweb.core.gatedcontentaccess.pojo.GatedContentAccesJsonResponse;
import com.edc.edcweb.core.helpers.Base64EnconderHelper;
import com.edc.edcweb.core.helpers.Constants;
import com.edc.edcweb.core.restful.RestClient;
import com.edc.edcweb.core.restful.RestClientConstants;
import com.edc.edcweb.core.service.EloquaService;
import com.edc.edcweb.core.serviceImpl.pojo.EloquaContact;

@Component(immediate = true, service = Servlet.class, property = { "sling.servlet.extensions=json",
        "sling.servlet.paths=/" + GatedContentAccessConstats.SHARE_DETAILS_SERVLET_PATH })

public class ShareYourDetailsServlet extends SlingAllMethodsServlet {

    private static final long serialVersionUID = -2265285796271592977L;
    private static final Logger log = LoggerFactory.getLogger(ShareYourDetailsServlet.class);

    @Reference
    @Inject
    private EloquaService eloquaService;

    /**
     * GET the client's record and any show any missing fields from the record
     * Redirect to gated resource if all the form is OK
     */
    @Override
    protected final void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        String email = request.getParameter(GatedContentAccessConstats.EMAIL_ADDRESS_FIELD);
        String encodedValue = request.getParameter(GatedContentAccessConstats.OVERRIDE_PATH_FIELD);
        String overridePath = Base64EnconderHelper.decodeString(encodedValue);
        String encodedResourceType = request.getParameter(GatedContentAccessConstats.RESOURCE_TYPE_FIELD);
        String resourceType = Base64EnconderHelper.decodeString(encodedResourceType);
        GatedContentAccesJsonResponse jsonResponse = new GatedContentAccesJsonResponse();
        Resource formRes = GatedContentAccessHelper.getFormRes(resourceType, request, overridePath);
        if (formRes != null) {
            if (StringUtils.isNotBlank(email)) {
                // Get email from Eloqua
                EloquaContact eloquaContact = eloquaService.getEloquaContact(email);
                Resource questions = getQuestionResouce(formRes);
                jsonResponse = DataObjectTransformationHelper.populateJsonResponse(eloquaContact, questions);
            } else {
                jsonResponse.setError(500);
                jsonResponse.setErrorText(
                        formRes.getValueMap().get(GatedContentAccessConstats.SUBMIT_ERROR_TEXT_PROPERTY, String.class));
                log.error("GatedContentAccessServlet: email not received");
            }
        } else {
            jsonResponse.setError(500);
            jsonResponse.setErrorText("No form Found");
            log.error("GatedContentAccessServlet: not form found on referrer page {}", formRes);
        }
        String json = GatedContentAccessDataBindingUtil.jsonResponseToJson(jsonResponse);
        ServletResponseHelper.writeResponse(response, json);
    }

    /**
     * POST (Update) the client's record directly to Eloqua using EloquaForm's API
     * Redirect to gated resource if all the form is OK
     */
    @Override
    protected final void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        GatedContentAccesJsonResponse jsonResponse = new GatedContentAccesJsonResponse();
        // Bug 159966 form not found in a vanity url
        String encodedValue = request.getParameter(GatedContentAccessConstats.OVERRIDE_PATH_FIELD);
        String overridePath = Base64EnconderHelper.decodeString(encodedValue);        
        String encodedResourceType = request.getParameter(GatedContentAccessConstats.RESOURCE_TYPE_FIELD);
        String resourceType = Base64EnconderHelper.decodeString(encodedResourceType);
        // Get the fields to re-submit
        @SuppressWarnings("unchecked")
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, String> formParams = new HashMap<>();
        for (Entry<String, String[]> entry : parameterMap.entrySet()) {
            String key = entry.getKey();
            String[] arrVar = entry.getValue();
            if (ArrayUtils.isNotEmpty(arrVar)) {
                String value = String.join("::", arrVar);
                formParams.put(key, value);
            }
        }
        // Override the CASL to always yes
        formParams.put(GatedContentAccessConstats.CASL_CONSENT_PROPERTY, GatedContentAccessConstats.CASL_CONSENT_VALUE);
        formParams.put(GatedContentAccessConstats.ELQ_SITE_ID_PROPERTY, eloquaService.getSiteId());
        String endPoint = eloquaService.getFormSubmitURL();
        String formParamsStr = StringUtils.join(formParams);
        log.debug("GatedContentAccessServlet: posting to {}, with data {}", formParamsStr, endPoint);
        // Submit the form and get the result
        int result = RestClient.doSimpleHTTPPost(endPoint, formParams);
        // Use overridePath previously set
        Resource formRes = GatedContentAccessHelper.getFormRes(resourceType, request, overridePath);
        if (formRes != null) {
            if (result == RestClientConstants.ERROR_STATUS) {
                jsonResponse.setError(result);
                jsonResponse.setErrorText(
                        formRes.getValueMap().get(GatedContentAccessConstats.SUBMIT_ERROR_TEXT_PROPERTY, String.class));
                log.error("GatedContentAccessServlet: cound not submt to Eloqua {}", result);
            }
        } else {
            jsonResponse.setError(RestClientConstants.ERROR_STATUS);
            jsonResponse.setErrorText("No form Found");
            log.error("GatedContentAccessServlet: form not found on referrer page {}", formRes);
        }
        String json = GatedContentAccessDataBindingUtil.jsonResponseToJson(jsonResponse);
        ServletResponseHelper.writeResponse(response, json);
    }

    /**
     * Get Question Multifield Resource
     * 
     * @param formRes
     * @return Resource questionsRes null otherwise
     */
    private Resource getQuestionResouce(Resource formRes) {
        Resource questionsRes = null;
        if (formRes != null) {
            questionsRes = formRes.getChild(GatedContentAccessConstats.QUESTION_MULTIFIELD_PROPERTY);
        }
        return questionsRes;
    }
}
