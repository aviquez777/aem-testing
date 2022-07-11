package com.edc.edcportal.core.servlets.dashboard;

import java.io.IOException;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.edc.edcportal.core.arkadin.services.ArkadinDAOService;
import com.edc.edcportal.core.eloqua.services.EloquaDAOService;
import com.edc.edcportal.core.helpers.Constants;
import com.edc.edcportal.core.helpers.LoginRequestHeadersUtil;
import com.edc.edcportal.core.helpers.ServletResponseHelper;
import com.edc.edcportal.core.helpers.TranslationHelper;
import com.edc.edcportal.core.helpers.constants.ConstantsWebinars;
import com.edc.edcportal.core.servlets.dashboard.helpers.DashboardJsonDataBindingUtil;
import com.edc.edcportal.core.servlets.dashboard.pojo.DashboardPageListDO;

@SuppressWarnings("CQRules:CQBP-75")
@Component(immediate = true, 
            service = Servlet.class, 
            property = { 
                    "sling.servlet.methods=" + HttpConstants.METHOD_GET,
                    "sling.servlet.paths=" + ConstantsWebinars.MY_RESOUCRES_TAB_SERVICE_SERVLET_URL,
                    "sling.servlet.paths=" + ConstantsWebinars.MY_WEBINARS_TAB_SERVICE_SERVLET_URL
                    }
)
public class MyDashboard extends SlingSafeMethodsServlet {

    private static final long serialVersionUID = 8456990748277727537L;

    @Reference
    @Inject
    private EloquaDAOService eloquaDAOService;

    @Reference
    @Inject
    private ArkadinDAOService arkadinDAOService;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {
        String json = null;
        Map<String, String> headers = LoginRequestHeadersUtil.getHeaders(request);
        String externalId = headers.get(Constants.Properties.HEADER_EXTERNAL_ID);
        DashboardPageListDO dashboardPageListDO = new DashboardPageListDO();
        // My Resources
        if (request.getPathInfo().contains(ConstantsWebinars.MY_RESOUCRES_TAB_SERVICE_SERVLET_URL)) {
            dashboardPageListDO = MyResourcesTab.getMyResoures(externalId, request, eloquaDAOService);
        }
        // Webinars
        if (request.getPathInfo().contains(ConstantsWebinars.MY_WEBINARS_TAB_SERVICE_SERVLET_URL)) {
            dashboardPageListDO = MyWebinarsTab.getMyWebinars(externalId, request, arkadinDAOService);
        }
        String error = dashboardPageListDO.getErrorMessage();
        if(StringUtils.isNotBlank(error)) {
            String translatedError = TranslationHelper.getI18nTranslation(error, request, null);
            dashboardPageListDO.setErrorMessage(translatedError);
        }
        // Create the Json
        json = DashboardJsonDataBindingUtil.dashboardPageListDOToJson(dashboardPageListDO);
        ServletResponseHelper.writeResponse(response, json);
    }
}