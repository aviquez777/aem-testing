package com.edc.edcweb.core.servlets;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import com.edc.edcweb.core.helpers.ServletResponseHelper;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edc.edcweb.core.helpers.Constants;
import com.edc.edcweb.core.helpers.LinkResolver;
import com.edc.edcweb.core.helpers.constants.ConstantsForm;
import com.edc.edcweb.core.helpers.progressiveprofiling.ProgressiveProfilingHelper;
import com.edc.edcweb.core.helpers.progressiveprofiling.ProgressiveProfilingPremiumPageHelper;
import com.edc.edcweb.core.helpers.progressiveprofiling.ProgressiveProfilingUserAccess;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ACN
 * @version 1.0.0
 * @since 1.0.0
 * 
 *        SessionInfoServlet is the post servlet to get information about
 *        attributes in the session
 * 
 */

@Component(immediate = true, service = Servlet.class, property = { "sling.servlet.extensions=post",
        "sling.servlet.paths=" + Constants.Paths.PROGRESSIVEPROFILING_SESSIONINFOSERVLET })
public class SessionInfoServlet extends SlingAllMethodsServlet {

    private static final long serialVersionUID = -3522134777775086197L;
    private static final Logger log = LoggerFactory.getLogger(SessionInfoServlet.class);

    @Override
    protected final void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {
        log.debug("SessionInfoServlet doGet: ");
    }

    @Override
    protected final void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {
        // Implement custom handling of POST requests
        log.debug("SessionInfoServlet doPost: ");

        // Get Request parameter value
        String name = request.getParameter("name");
        String value = request.getParameter("value");
        String operation = request.getParameter("operation");
        log.debug("name:{}  ", name);
        log.debug("value:{}  ", value);
        log.debug("operation:{}  ", operation);

        if (operation.equalsIgnoreCase("isInSessionList")) {
            String json = this.buildJsonInSessionList(existInSessionList(request, name, value));
            ServletResponseHelper.writeResponse(response,json);
        } else {
            log.debug(" unknown operation {}  ", operation);
        }
    }

    // list mean the session attribute is comma separated list.
    private boolean existInSessionList(SlingHttpServletRequest request, String name, String value) {
        // check MyEDC session first
        boolean result = ProgressiveProfilingHelper.checkMyEdcSession(request);
        if (!result) {
            if (name == null || value == null) {
                log.error("existInSessionList null arguments.");
                return result;
            }

            String attr = (String) request.getSession().getAttribute(name);
            if (attr != null) {
                String cleanVal = value.trim().replace("*", ".*?");
                List<String> list = new ArrayList<>(Arrays.asList(attr.split(",")));

                for (int i = 0; i < list.size(); i++) {
                    // change country grid to unlock status only we have MEA-* (anything else but no
                    // MEA-USA) in session's accessGranted attribute
                    if (!list.get(i).equalsIgnoreCase("MEA-USA") && list.get(i).matches(cleanVal)) {
                        return true;
                    }
                }
            }
        }
        return result;
    }

    private String buildJsonInSessionList(boolean isInSession) {

        String json = "";
        String val = Constants.NO;
        if (isInSession) {
            val = Constants.YES;
        }

        JSONObject mainjson = new JSONObject();
        try {
            mainjson.put("response", val);
            json = mainjson.toString(3);
        } catch (JSONException e) {
            log.error("Cannot create json", e);
        }
        log.debug("buildJsonInSessionList {}", json);
        return json;
    }

}