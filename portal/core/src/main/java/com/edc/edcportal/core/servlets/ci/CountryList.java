package com.edc.edcportal.core.servlets.ci;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.Servlet;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edc.edcportal.core.ci.CiConstants;
import com.edc.edcportal.core.ci.services.CiDAOService;
import com.edc.edcportal.core.exception.EDCAPIMSystemException;
import com.edc.edcportal.core.helpers.Constants;

@Component(immediate = true, service = Servlet.class, property = {
        "sling.servlet.extensions=" + Constants.Properties.JSON,
        "sling.servlet.resourceTypes=" + CiConstants.SERVLET_RESOURCE_TYPE,
        "sling.servlet.selectors=" + CiConstants.SERVLET_SELECTOR, 
        "sling.servlet.methods=" + HttpConstants.METHOD_GET
        }
)

public class CountryList extends SlingAllMethodsServlet {
    private static final long serialVersionUID = 5984231611166784516L;
    private static final Logger log = LoggerFactory.getLogger(CountryList.class);

    @Reference
    @Inject
    private CiDAOService ciDAOService;

    @Override
    protected final void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        String json = null;
        try {
            json = ciDAOService.getCountries();
        } catch (EDCAPIMSystemException e) {
            log.error("CompanyInsight error getCountries {} ", e);
        }

        CiServletHelper.writeResponse(response, json);
    }
}