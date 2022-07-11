package com.edc.edcportal.core.servlets.businessconnections;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edc.edcportal.core.businessconnections.BCJsonDataBindingUtil;
import com.edc.edcportal.core.businessconnections.helpers.BCConstants;
import com.edc.edcportal.core.businessconnections.pojo.BCTokenResponse;
import com.edc.edcportal.core.businessconnections.services.BCDAOService;
import com.edc.edcportal.core.helpers.Constants;
import com.edc.edcportal.core.helpers.ServletResponseHelper;

@SuppressWarnings("CQRules:CQBP-75")
@Component(immediate = true, service = Servlet.class, property = { "sling.servlet.methods=" + HttpConstants.METHOD_GET,
        "sling.servlet.paths=" + BCConstants.BC_TOKEN_SERVICE_SERVLET_URL })
public class BCTokenService extends SlingSafeMethodsServlet {

    private static final Logger log = LoggerFactory.getLogger(BCTokenService.class);
    
    private static final long serialVersionUID = 8456290777727594837L;

    @Reference
    @Inject
    private BCDAOService bcDAOService;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {
        BCTokenResponse bcTokenResponse = bcDAOService.getBCToken(request);
        String json = BCJsonDataBindingUtil.bcTokenResponseToJson(bcTokenResponse);
        // only lower environments
        log.debug("BC Token Service RESPONSE externalid: {} response data: {}", request.getHeader(Constants.Properties.HEADER_EXTERNAL_ID), json);
        ServletResponseHelper.writeResponse(response, json);
    }
}