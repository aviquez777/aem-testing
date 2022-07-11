package com.edc.edcweb.core.servlets;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.edc.edcweb.core.helpers.Base64EnconderHelper;
import com.edc.edcweb.core.helpers.ResourceResolverHelper;
import com.edc.edcweb.core.helpers.ServletResponseHelper;
import com.edc.edcweb.core.upcomingwebinars.UWConstants;
import com.edc.edcweb.core.upcomingwebinars.UWDataBindingUtil;
import com.edc.edcweb.core.upcomingwebinars.UWServletUtils;
import com.edc.edcweb.core.upcomingwebinars.pojo.UWResponse;

/**
 * @author Dennis Bonilla - ACN
 * @version 1.0.0
 * @since 1.0.0
 *
 */
@Component(name = "Upcoming Webinar And Business Connection Events", service = Servlet.class, property = {
        "sling.servlet.extensions=" + UWConstants.JSON_EXT, "sling.servlet.methods=" + HttpConstants.METHOD_GET,
        "sling.servlet.paths=" + UWConstants.SERVLET_URL })

public class UpcomingWBCEventsRetrieverServlet extends SlingSafeMethodsServlet {

    private static final Logger log = LoggerFactory.getLogger(UpcomingWBCEventsRetrieverServlet.class);
    private static final long serialVersionUID = -8891384134597214593L;

    @Override
    protected final void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {

        UWResponse resObject = new UWResponse();
        Page currentPage = this.getCurrentPage(request);

        if (currentPage != null) {
            ResourceResolver resolver = currentPage.getContentResource().getResourceResolver();
            Resource upcomingEventsComponent = this.getUpcomingEventsComponent(currentPage);
            // Check if upcomingEventsComponent was found
            if (upcomingEventsComponent != null) {
                ValueMap vm = upcomingEventsComponent.getValueMap();
                String[] paths = vm.get(UWConstants.PATH_DIALOG_FIELD, String[].class);
                String[] tags = vm.get(UWConstants.TAGS_DIALOG_FIELD, String[].class);
                // Get the Response Object
                resObject = UWServletUtils.getUWResponse(paths, tags, resolver, currentPage.getLanguage(false));
            } else {
                resObject.setErrorMessage("");
                resObject.setPageItems(null);
                log.error("UpcomingWBCEventsRetrieverServlet doGet no upcomingEventsComponent found on page {}",
                        currentPage.getPath());
            }
        }

        // Create the Json Reseponse
        String json = UWDataBindingUtil.uWResponseToJson(resObject);
        // Log only on lower environments
        ServletResponseHelper.writeResponse(response, json);
    }

    private Page getCurrentPage(SlingHttpServletRequest request) {
        String encodedValue = request.getParameter(UWConstants.REQ_PARAMETER);
        String pageUrl = Base64EnconderHelper.decodeString(encodedValue);
        Page currentPage;
        if (StringUtils.isNotBlank(pageUrl)) {
            Resource resource = request.getResource();
            PageManager pm = resource.getResourceResolver().adaptTo(PageManager.class);
            currentPage = pm.getPage(pageUrl);
        } else {
            currentPage = null;
        }
        return currentPage;
    }

    private Resource getUpcomingEventsComponent(Page currentPage) {
        return ResourceResolverHelper.getResourceByTypeAndNode(currentPage, UWConstants.RESOURCE_TYPE,
                UWConstants.NODE);
    }
}
