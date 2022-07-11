package com.edc.edcweb.core.servlets;

import com.edc.edcweb.core.helpers.Constants;
import com.edc.edcweb.core.helpers.ServletResponseHelper;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;

import javax.servlet.Servlet;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

@Component(
        immediate = true,
        service = Servlet.class,
        property = {
                "sling.servlet.methods=get",
                "sling.servlet.paths=" + Constants.Paths.MSTLFORM_MSTLFORM_SERVLET,
        }, configurationPid = "com.edc.edcweb.core.servlets.MSTLFormServlet")


public class MSTLFormServlet extends SlingSafeMethodsServlet {

    /**
     * 
     */
    private static final long serialVersionUID = -547333120113142155L;

    @Override
    protected final void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        String jsonString = "{ \"timeStamp\" : \""+ getCurrentTimeStamp() + "\" }";
        ServletResponseHelper.writeResponse(response, jsonString);
    }

    private String getCurrentTimeStamp() {
        SimpleDateFormat sdfDateTime = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");
        SimpleDateFormat sdfTime = new SimpleDateFormat("aa");
        sdfDateTime.setTimeZone(TimeZone.getTimeZone("EST"));
        Date now = new Date();
        String ampm;
        if (sdfTime.format(now).equalsIgnoreCase("a. m.")){
            ampm = "AM";
        }else{
            ampm= "PM";
        }
        return sdfDateTime.format(now) + " " + ampm;
    }


}