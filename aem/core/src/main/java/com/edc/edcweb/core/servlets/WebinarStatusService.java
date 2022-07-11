package com.edc.edcweb.core.servlets;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;
import com.edc.edcweb.core.helpers.Constants;
import com.edc.edcweb.core.helpers.Request;
import com.edc.edcweb.core.helpers.ServletResponseHelper;
import com.edc.edcweb.core.helpers.constants.ConstantsWebinars;
import com.edc.edcweb.core.helpers.webinars.WebinarsHelper;
import com.edc.edcweb.core.models.WebinarStatusResponse;
import com.edc.edcweb.core.service.EDCSystemConfigurationService;
import com.edc.edcweb.core.servlets.webinars.WebinarDataBindingUtil;

@Component(immediate = true, service = Servlet.class, property = { "sling.servlet.extensions=json",
        "sling.servlet.paths=" + ConstantsWebinars.WEBINAR_STATUS_SERVLET_URL })

public class WebinarStatusService extends SlingAllMethodsServlet {

    private static final Logger log = LoggerFactory.getLogger(WebinarStatusService.class);
    private static final long serialVersionUID = 7483773590569461526L;

    @Reference
    @Inject
    private EDCSystemConfigurationService edcSystemConfiguration;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {

        String timeStatus = null;
        Resource currentPageRes = Request.getCurrentPageResource(request, null);
        if (currentPageRes != null) {
            Page page = currentPageRes.adaptTo(Page.class);
            int windowTime = edcSystemConfiguration.getWebinarsWindowTime();
            timeStatus = WebinarsHelper.getWebinarStatus(page, windowTime);
        }

        // prepare and send the response
        response.setCharacterEncoding(Constants.UTF_8);
        WebinarStatusResponse webinarStatusResponse = new WebinarStatusResponse();

        if (StringUtils.isNotBlank(timeStatus)) {
            webinarStatusResponse.setTimeStatus(timeStatus);
        } else {
            log.error("WebinarStatusService unable to get TimeStatus for {}",
                    request.getHeader(Constants.Properties.REFERER));
            webinarStatusResponse.seterrorMessage(ConstantsWebinars.PAGE_LOAD_ERROR);
        }

        String jsonstring = WebinarDataBindingUtil.webinarStatusResponseToJson(webinarStatusResponse);
        ServletResponseHelper.writeResponse(response, jsonstring);
    }
}
