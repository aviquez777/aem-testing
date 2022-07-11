package com.edc.edcportal.core.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edc.edcportal.core.apim.services.ApimDAOService;
import com.edc.edcportal.core.helpers.Constants;
import com.edc.edcportal.core.helpers.LoginRequestHeadersUtil;
import com.edc.edcportal.core.restful.RestClientConstants;
import com.edc.edcportal.core.services.MyEDCConfigSystemService;

@SuppressWarnings("CQRules:CQBP-75")
@Component(
        immediate = true,
        service = Servlet.class,
        property = {
                "sling.servlet.extensions=json",
                "sling.servlet.methods=GET",
                "sling.servlet.paths="+Constants.Paths.USER_STATE_URL,
                "sling.servlet.paths="+Constants.Paths.USER_STATE_JS_URL,
        },
        configurationPid = "com.edc.edcportal.core.servlets.UserLoginState"
)

public class UserLoginState extends SlingSafeMethodsServlet {
    private static final Logger log = LoggerFactory.getLogger(UserLoginState.class);
    private static final long serialVersionUID = 6483773290569461529L;

    @Reference
    @Inject
    private MyEDCConfigSystemService myEDCConfigSystemService;
    
    @Reference
    @Inject
    private ApimDAOService apimDAOService;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException
    { 
        response.setCharacterEncoding(Constants.UTF_8);
        response.setHeader("Cache-Control", "private, no-store, no-cache, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader(RestClientConstants.EXPIRES_HEADER, 0);
        PrintWriter out = response.getWriter();
        // Return object / mime type as requested
        if(request.getPathInfo().contains(Constants.Paths.USER_STATE_JS_URL)) {
            response.setContentType("application/javascript");
            out.append("userState("+createjSON(request).toString()+");"); 
        } else {
            response.setContentType(RestClientConstants.APPLICATION_JSON);
            out.append(createjSON(request).toString());
        }
        out.flush();
    }

    private JSONObject createjSON(SlingHttpServletRequest request) {
        JSONObject userInfo = new JSONObject();
        try {
            Map<String, String> headers = LoginRequestHeadersUtil.getHeaders(request);
            userInfo.put(Constants.Properties.HEADER_FIRST_NAME, headers.get(Constants.Properties.HEADER_FIRST_NAME));
            userInfo.put(Constants.Properties.HEADER_LAST_NAME, headers.get(Constants.Properties.HEADER_LAST_NAME));
            // Constants.Properties not followed
            userInfo.put("email", headers.get(Constants.Properties.HEADER_EMAIL_ID));
            userInfo.put("externalID", headers.get(Constants.Properties.HEADER_EXTERNAL_ID));
         // Task 22143 squid:S2221
        } catch (JSONException e) {
            log.error("UserLoginState createjSON error", e);
        }
        return userInfo;
    }

    @Activate
    @Modified
    private void activate() {
        log.info("UserLoginState Servlet ------- Activated");
    }
}

