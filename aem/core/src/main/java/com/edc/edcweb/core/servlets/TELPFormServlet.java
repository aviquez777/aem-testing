package com.edc.edcweb.core.servlets;

import java.io.IOException;
import java.util.Set;

import javax.inject.Inject;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import com.edc.edcweb.core.helpers.ServletResponseHelper;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.settings.SlingSettingsService;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.edc.edcweb.core.helpers.Constants;
import com.edc.edcweb.core.models.questionnaire.QestionnaireDO;
import com.edc.edcweb.core.telp.TELPJsonDataBindingUtil;
import com.edc.edcweb.core.telp.helpers.DataObjectTransformationHelper;
import com.edc.edcweb.core.telp.helpers.TelpConstants;
import com.edc.edcweb.core.telp.service.TelpDAOService;

@Component(immediate = true, service = Servlet.class, property = {
        "sling.servlet.extensions=json",
        "sling.servlet.paths=" + Constants.Paths.TELPFORM_TELPFORM_SERVLET,
        })

public class TELPFormServlet extends SlingAllMethodsServlet {
    private static final long serialVersionUID = 2598426539166784516L;

    @Reference
    @Inject
    private TelpDAOService telpDAOService;

    @Reference
    @Inject
    private SlingSettingsService slingSettingsService;

    @Override
    protected final void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        String[] pageSelectors = request.getRequestPathInfo().getSelectors();
        String nodeName = null;
        if (pageSelectors.length == 1) {
            nodeName = pageSelectors[0];
        }
        QestionnaireDO qestionnaireDO = DataObjectTransformationHelper.populateTelpQuestionnaire(request, nodeName);
        String json = TELPJsonDataBindingUtil.telpQestionnaireDOToJson(qestionnaireDO);
        ServletResponseHelper.writeResponse(response, json);
    }

    @Override
    protected final void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {
        JSONObject responseJson = telpDAOService.submitForm(request);
        boolean result = false;
        result = (responseJson.has(TelpConstants.JSON_ERROR_MESSAGE_KEY)
                && responseJson.isNull(TelpConstants.JSON_ERROR_MESSAGE_KEY));
        if (!result) {
            response.setStatus(500);
        }
        String responseText = "{\"result\":\"OK\"}";
        // Return debug if not PROD
        Set<String> runModes = this.slingSettingsService.getRunModes();
        if (runModes != null && !runModes.contains("prod")) {
            responseText = responseJson.toString();
        }
        ServletResponseHelper.writeResponse(response, responseText);
    }
}