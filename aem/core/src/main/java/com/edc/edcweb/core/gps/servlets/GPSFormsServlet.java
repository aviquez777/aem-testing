package com.edc.edcweb.core.gps.servlets;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import com.edc.edcweb.core.helpers.ServletResponseHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.settings.SlingSettingsService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.edc.edcweb.core.gps.GpsJsonDataBindingUtil;
import com.edc.edcweb.core.gps.helpers.GpsServletHelper;
import com.edc.edcweb.core.gps.pojo.GpsFormObjects;
import com.edc.edcweb.core.gps.pojo.GpsResponse;
import com.edc.edcweb.core.gps.service.GpsDAOService;
import com.edc.edcweb.core.helpers.Constants;
import com.edc.edcweb.core.lovapi.service.LovApiDAOService;

@Component(immediate = true, service = Servlet.class, property = { "sling.servlet.extensions=json",
        "sling.servlet.paths=" + Constants.Paths.GPS_FORMS_SERVLET_URL, })

public class GPSFormsServlet extends SlingAllMethodsServlet {

    private static final long serialVersionUID = 758243377117721761L;

    @Reference
    @Inject
    private GpsDAOService gpsDAOService;
    
    @Reference
    @Inject
    private LovApiDAOService lovApiDAOService;

    @Reference
    @Inject
    private SlingSettingsService slingSettingsService;

    @Override
    protected final void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {
        GpsResponse gpsResponse = new GpsResponse();
        // Check form type & return the GpsFormObjects with the proper objects
        GpsFormObjects gpsFormObjects = GpsServletHelper.getFormObjects(request, lovApiDAOService);
        String formType = gpsFormObjects.getFormType();
        String formJson = gpsFormObjects.getFormJson();
        Map<String, String> errorMsgs =  gpsFormObjects.getErrorMsgs();
        // if could not identify the form type of formJson is blank, do no attempt to
        // submit
        if (errorMsgs.isEmpty() && StringUtils.isNotBlank(formType) && StringUtils.isNotBlank(formJson)) {
            // get files if any
            List<RequestParameter> validAttachmentList = gpsFormObjects.getAttachmentList();
            // Submit the form and get the response in an object
            gpsResponse = gpsDAOService.submitForm(formType, formJson, validAttachmentList, gpsFormObjects.isReturnConfNum());
        } else {
            gpsResponse.setExtErrorMessages(errorMsgs);
        }
        // Set the status as received
        response.setStatus(gpsResponse.getStatusCode());
        // If prod, remove the GpsResult node not to expose unnecessary data
        Set<String> runModes = this.slingSettingsService.getRunModes();
        if (runModes != null && runModes.contains("prod")) {
            gpsResponse.setGpsResult(null);
        }
        // Create the json for response
        String responseText = GpsJsonDataBindingUtil.pojoToJson(gpsResponse);
        ServletResponseHelper.writeResponse(response, responseText);
    }
    
    @Override
    protected final void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        response.setStatus(403);
        // Create the json for response
        String responseText = "Not Allowed";
        ServletResponseHelper.writeResponse(response, responseText);
    }


}