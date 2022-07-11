package com.edc.edcweb.core.servlets;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.Servlet;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.edc.edcweb.core.helpers.Constants;
import com.edc.edcweb.core.helpers.ServletResponseHelper;
import com.edc.edcweb.core.covid19triagetool.service.TriageDAOService;
import com.edc.edcweb.core.covid19triagetool.helpers.TriageToolConstants;

@Component(immediate = true, service = Servlet.class, property = {
        "sling.servlet.extensions=" + Constants.Properties.JSON,
        "sling.servlet.resourceTypes=" + TriageToolConstants.SERVLET_RESOURCE_TYPE,
        "sling.servlet.selectors=" + TriageToolConstants.SERVLET_SELECTOR,
        "sling.servlet.methods=" + HttpConstants.METHOD_GET })

public class Covid19TriageToolServlet extends SlingAllMethodsServlet {
    private static final long serialVersionUID = 2561784542653986916L;

    @Reference
    @Inject
    private TriageDAOService triageDAOService;

    @Override
    protected final void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        String json = triageDAOService.getFormJson(request);
        ServletResponseHelper.writeResponse(response, json);
    }
}