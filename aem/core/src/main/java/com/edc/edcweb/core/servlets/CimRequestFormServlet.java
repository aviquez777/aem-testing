package com.edc.edcweb.core.servlets;

import com.day.cq.mailer.MessageGatewayService;
import com.day.cq.wcm.api.Page;
import com.edc.edcweb.core.helpers.LanguageUtil;
import com.edc.edcweb.core.helpers.Request;
import com.edc.edcweb.core.helpers.ResourceResolverHelper;
import com.edc.edcweb.core.helpers.constants.ConstantsEmailSender;
import com.edc.edcweb.core.helpers.constants.ConstantsForm;
import com.edc.edcweb.core.helpers.constants.ConstantsGRC;
import com.edc.edcweb.core.helpers.constants.ConstantsInList;
import com.edc.edcweb.core.helpers.email.EmailSender;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@SuppressWarnings({"CQRules:AMSCORE-553", "CQRules:CQBP-75", "squid:S1948"})
@Component(immediate = true, service = Servlet.class, property = {
        "sling.servlet.extensions=html",
        "sling.servlet.methods=post",
        "sling.servlet.paths=" + ConstantsGRC.GRCProperties.CIMREQUESTFORM_SERVLET})

public class CimRequestFormServlet extends SlingAllMethodsServlet {
    private static final Logger log = LoggerFactory.getLogger(CimRequestFormServlet.class);
    private static final long serialVersionUID = 3217256974666589215L;

    // Inject a MessageGatewayService
    @Reference
    private MessageGatewayService messageGatewayService;

    @Override
    protected final void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        Resource currPage = Request.getCurrentPageResource(request, null);
        Page currentPage = currPage.adaptTo(Page.class);
        Resource cimRequestFormRsc = ResourceResolverHelper.getResourceByType(currentPage, ConstantsGRC.GRCProperties.CIMREQUESTFORM_RESOURCE_TYPE);
        ValueMap propertiesValues = cimRequestFormRsc.getValueMap();

        // Date / time of request
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        Date date = new Date();
        String dateOfRequest = dateFormat.format(date);

        // Reference number format ISV-<user inputted company name here>-<6 digit random number>
        String delimiter =  ConstantsGRC.GRCProperties.CIMREQUESTFORM_DELIMITER;
        String referenceNumber = ConstantsGRC.GRCProperties.CIMREQUESTFORM_IDENTIFIER + delimiter + getParameterFromRequest(request, "companyName") + delimiter + randomIdentifier(6);

        // Display language
        String displayLanguage;

        if (currentPage != null) {
            String pagePath = currentPage.getPath();
            String languageAbbreviation = LanguageUtil.getLanguageAbbreviationFromPath(pagePath, ConstantsForm.SupportedLanguages.ENGLISH.getLanguageAbbreviation());
            displayLanguage = languageAbbreviation.equalsIgnoreCase(ConstantsForm.FormProperties.FORM_FR) ? ConstantsForm.FormProperties.FORM_HIDDEN_FRENCH : ConstantsForm.FormProperties.FORM_HIDDEN_ENGLISH;
        } else {
            displayLanguage = ConstantsForm.FormProperties.FORM_HIDDEN_ENGLISH;
        }

        // Data to send email
        String subject =  (String) propertiesValues.getOrDefault(ConstantsInList.InListProperties.FEEDBACK_FORM_SUBJECT, ConstantsGRC.GRCProperties.CIMREQUESTFORM_TEMPLATE_DEFAULTSUBJECT);
        subject += ": " + referenceNumber;

        Map<String, String> emailValues = new HashMap<>();
        emailValues.put(ConstantsEmailSender.Properties.EMAIL_TO, propertiesValues.get(ConstantsInList.InListProperties.FEEDBACK_FORM_EMAIL, String.class));
        emailValues.put(ConstantsEmailSender.Properties.EMAIL_BCC, propertiesValues.get(ConstantsInList.InListProperties.FEEDBACK_FORM_EMAIL_BCC, String.class));
        emailValues.put(ConstantsEmailSender.Properties.SUBJECT, subject);

        // Email content
        Map<String, String> bodyValues = new HashMap<>();

        // Title and description
        bodyValues.put(ConstantsGRC.GRCProperties.CIMREQUESTFORM_TEMPLATE_TITLE, propertiesValues.get(ConstantsGRC.GRCProperties.CIMREQUESTFORM_TEMPLATE_TITLE, String.class));
        bodyValues.put(ConstantsGRC.GRCProperties.CIMREQUESTFORM_TEMPLATE_DESCRIPTION, propertiesValues.get(ConstantsGRC.GRCProperties.CIMREQUESTFORM_TEMPLATE_DESCRIPTION, String.class));

        // Sender information
        bodyValues.put(ConstantsGRC.GRCProperties.CIMREQUESTFORM_TEMPLATE_FIRSTNAMELABEL, propertiesValues.get(ConstantsGRC.GRCProperties.CIMREQUESTFORM_TEMPLATE_FIRSTNAMELABEL, String.class));
        bodyValues.put(ConstantsGRC.GRCProperties.CIMREQUESTFORM_TEMPLATE_FIRSTNAMETEXT, getParameterFromRequest(request, "userName"));
        bodyValues.put(ConstantsGRC.GRCProperties.CIMREQUESTFORM_TEMPLATE_EMAILLABEL, propertiesValues.get(ConstantsGRC.GRCProperties.CIMREQUESTFORM_TEMPLATE_EMAILLABEL, String.class));
        bodyValues.put(ConstantsGRC.GRCProperties.CIMREQUESTFORM_TEMPLATE_EMAILTEXT, getParameterFromRequest(request, "userEmail"));
        bodyValues.put(ConstantsGRC.GRCProperties.CIMREQUESTFORM_TEMPLATE_REQUESTDATELABEL, propertiesValues.get(ConstantsGRC.GRCProperties.CIMREQUESTFORM_TEMPLATE_REQUESTDATELABEL, String.class));
        bodyValues.put(ConstantsGRC.GRCProperties.CIMREQUESTFORM_TEMPLATE_REQUESTDATETEXT, dateOfRequest);
        bodyValues.put(ConstantsGRC.GRCProperties.CIMREQUESTFORM_TEMPLATE_REFERENCENUMBERLABEL, propertiesValues.get(ConstantsGRC.GRCProperties.CIMREQUESTFORM_TEMPLATE_REFERENCENUMBERLABEL, String.class));
        bodyValues.put(ConstantsGRC.GRCProperties.CIMREQUESTFORM_TEMPLATE_REFERENCENUMBERTEXT, referenceNumber);
        bodyValues.put(ConstantsGRC.GRCProperties.CIMREQUESTFORM_TEMPLATE_LANGUAGELABEL, propertiesValues.get(ConstantsGRC.GRCProperties.CIMREQUESTFORM_TEMPLATE_LANGUAGELABEL, String.class));
        bodyValues.put(ConstantsGRC.GRCProperties.CIMREQUESTFORM_TEMPLATE_LANGUAGETEXT, displayLanguage);

        // Company information
        bodyValues.put(ConstantsGRC.GRCProperties.CIMREQUESTFORM_TEMPLATE_COMPANYINFOTITLE, propertiesValues.get(ConstantsGRC.GRCProperties.CIMREQUESTFORM_TEMPLATE_COMPANYINFOTITLE, String.class));
        bodyValues.put(ConstantsGRC.GRCProperties.CIMREQUESTFORM_TEMPLATE_COMPANYNAMELABEL, propertiesValues.get(ConstantsGRC.GRCProperties.CIMREQUESTFORM_TEMPLATE_COMPANYNAMELABEL, String.class));
        bodyValues.put(ConstantsGRC.GRCProperties.CIMREQUESTFORM_TEMPLATE_COMPANYNAMETEXT, getParameterFromRequest(request, "companyName"));
        bodyValues.put(ConstantsGRC.GRCProperties.CIMREQUESTFORM_TEMPLATE_COMPANYWEBSITELABEL, propertiesValues.get(ConstantsGRC.GRCProperties.CIMREQUESTFORM_TEMPLATE_COMPANYWEBSITELABEL, String.class));
        bodyValues.put(ConstantsGRC.GRCProperties.CIMREQUESTFORM_TEMPLATE_COMPANYWEBSITETEXT, getParameterFromRequest(request, "website1"));

        // Headquarters information
        bodyValues.put(ConstantsGRC.GRCProperties.CIMREQUESTFORM_TEMPLATE_HEADQUARTERSTITLE, propertiesValues.get(ConstantsGRC.GRCProperties.CIMREQUESTFORM_TEMPLATE_HEADQUARTERSTITLE, String.class));
        bodyValues.put(ConstantsGRC.GRCProperties.CIMREQUESTFORM_TEMPLATE_COUNTRYLABEL, propertiesValues.get(ConstantsGRC.GRCProperties.CIMREQUESTFORM_TEMPLATE_COUNTRYLABEL, String.class));
        bodyValues.put(ConstantsGRC.GRCProperties.CIMREQUESTFORM_TEMPLATE_COUNTRYTEXT, getParameterFromRequest(request, "companyCountry"));
        bodyValues.put(ConstantsGRC.GRCProperties.CIMREQUESTFORM_TEMPLATE_ADDRESSLABEL, propertiesValues.get(ConstantsGRC.GRCProperties.CIMREQUESTFORM_TEMPLATE_ADDRESSLABEL, String.class));
        bodyValues.put(ConstantsGRC.GRCProperties.CIMREQUESTFORM_TEMPLATE_ADDRESSTEXT, getParameterFromRequest(request, "companyAddress1"));
        bodyValues.put(ConstantsGRC.GRCProperties.CIMREQUESTFORM_TEMPLATE_CITYLABEL, propertiesValues.get(ConstantsGRC.GRCProperties.CIMREQUESTFORM_TEMPLATE_CITYLABEL, String.class));
        bodyValues.put(ConstantsGRC.GRCProperties.CIMREQUESTFORM_TEMPLATE_CITYTEXT, getParameterFromRequest(request, "companyCity"));
        bodyValues.put(ConstantsGRC.GRCProperties.CIMREQUESTFORM_TEMPLATE_PROVINCELABEL, propertiesValues.get(ConstantsGRC.GRCProperties.CIMREQUESTFORM_TEMPLATE_PROVINCELABEL, String.class));
        bodyValues.put(ConstantsGRC.GRCProperties.CIMREQUESTFORM_TEMPLATE_PROVINCETEXT, getParameterFromRequest(request, "companyProvince"));
        bodyValues.put(ConstantsGRC.GRCProperties.CIMREQUESTFORM_TEMPLATE_POSTALCODELABEL, propertiesValues.get(ConstantsGRC.GRCProperties.CIMREQUESTFORM_TEMPLATE_POSTALCODELABEL, String.class));
        bodyValues.put(ConstantsGRC.GRCProperties.CIMREQUESTFORM_TEMPLATE_POSTALCODETEXT, getParameterFromRequest(request, "companyPostal"));

        try {
            JSONObject responseInfo = new JSONObject();
            String responseStr;
            responseInfo.put(ConstantsGRC.GRCProperties.CIMREQUESTFORM_IDENTIFIER.toLowerCase(), referenceNumber);
            responseStr = responseInfo.toString();
            PrintWriter out = response.getWriter();
            out.append(responseStr);
            out.flush();
        } catch (JSONException | IOException e) {
            log.error("Error writing ISV code response", e);
        }

        EmailSender.sendHtmlEmail(messageGatewayService, request, response, ConstantsGRC.GRCProperties.CIMREQUESTFORM_EMAIL_TEMPLATE, emailValues, bodyValues);
    }

    private String getParameterFromRequest(SlingHttpServletRequest request, String paramName) {
        String paramValue = request.getParameter(paramName) != null ? request.getParameter(paramName) : "";

        /*
         * If company province or postal code are not filled out front end sends "DEFAULT" for Eloqua forms
         * for this form set empty string instead of "DEFAULT"
         */
        if ((paramName.equalsIgnoreCase("companyProvince") || paramName.equalsIgnoreCase("companyPostal")) && paramValue.equalsIgnoreCase("DEFAULT")) {
            paramValue = "";
        }

        return paramValue;
    }

    /**
     * Generate a random number with the amount of digits specified
     * @param length the amount of digits
     * @return n-digit random number
     */
    public String randomIdentifier(int length) {
        Random rand = new Random(System.currentTimeMillis());
        String[] digit = new String[length];
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < length; i++) {
            digit[i] = Integer.toString(rand.nextInt( 9 ));
            result.append(digit[i]);
        }

        return result.toString();
    }
}
