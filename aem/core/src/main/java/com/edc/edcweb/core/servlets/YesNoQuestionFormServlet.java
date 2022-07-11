package com.edc.edcweb.core.servlets;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.json.JSONArray;
import org.json.JSONException;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.mailer.MessageGatewayService;
import com.day.cq.wcm.api.Page;
import com.edc.edcweb.core.exception.EDCEloquaSystemException;
import com.edc.edcweb.core.helpers.Constants;
import com.edc.edcweb.core.helpers.LanguageUtil;
import com.edc.edcweb.core.helpers.Request;
import com.edc.edcweb.core.helpers.ResourceResolverHelper;
import com.edc.edcweb.core.helpers.constants.ConstantsEmailSender;
import com.edc.edcweb.core.helpers.constants.ConstantsForm;
import com.edc.edcweb.core.helpers.constants.ConstantsGRC;
import com.edc.edcweb.core.helpers.constants.ConstantsInList;
import com.edc.edcweb.core.helpers.email.EmailSender;
import com.edc.edcweb.core.myedc.eloqua.pojo.EloquaDataItem;
import com.edc.edcweb.core.myedc.eloqua.pojo.EloquaUserProfileDO;
import com.edc.edcweb.core.myedc.eloqua.services.EloquaDAOService;

@Component(immediate = true, service = Servlet.class, property = {
    "sling.servlet.extensions=html",
    "sling.servlet.methods=post",
    "sling.servlet.paths=" + Constants.Paths.YESNOQUESTION_FORM_SERVLET})

public class YesNoQuestionFormServlet extends SlingAllMethodsServlet {

    private static final long serialVersionUID = 1L;

    // Inject a MessageGatewayService
    @Inject
    @Reference
    private MessageGatewayService messageGatewayService;

    @Inject
    @Reference
    private EloquaDAOService eloquaDAOService;

    private static final Logger log = LoggerFactory.getLogger(FeedbackFormServlet.class);

    @Override
    protected final void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        Resource currPage = Request.getCurrentPageResource(request, null);
        Page currentPage = currPage.adaptTo(Page.class);
        Resource formRsc = ResourceResolverHelper.getResourceByType(currentPage, Constants.Paths.YESNOQUESTION_FORM_TYPE);
        ValueMap propertiesValues = formRsc.getValueMap();

        Map<String, String> emailValues = new HashMap<>();
        String subject = (String) propertiesValues.getOrDefault(ConstantsInList.InListProperties.FEEDBACK_FORM_SUBJECT, ConstantsGRC.GRCProperties.CIMREQUESTFORM_TEMPLATE_DEFAULTSUBJECT);

        emailValues.put(ConstantsEmailSender.Properties.EMAIL_TO, propertiesValues.get(ConstantsInList.InListProperties.FEEDBACK_FORM_EMAIL, String.class));
        emailValues.put(ConstantsEmailSender.Properties.SUBJECT, subject);

        // Email content
        Map<String, String> bodyValues = new HashMap<>();
        String displayLanguage;

        // Date / time of request
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        Date date = new Date();
        String dateOfRequest = dateFormat.format(date);

        //Full name
        String fullName = getParameterFromRequest(request, Constants.Properties.NAME) + " " + getParameterFromRequest(request, Constants.Properties.PROGRESSIVEPROFILING_ELOQUANAME_LNAME);

        // Display language
        if (currentPage != null) {
            String pagePath = currentPage.getPath();
            String languageAbbreviation = LanguageUtil.getLanguageAbbreviationFromPath(pagePath, ConstantsForm.SupportedLanguages.ENGLISH.getLanguageAbbreviation());
            displayLanguage = languageAbbreviation.equalsIgnoreCase(ConstantsForm.FormProperties.FORM_FR) ? ConstantsForm.FormProperties.FORM_HIDDEN_FRENCH : ConstantsForm.FormProperties.FORM_HIDDEN_ENGLISH;
        } else {
            displayLanguage = ConstantsForm.FormProperties.FORM_HIDDEN_ENGLISH;
        }

        /*
        * Title and description
        * */
        bodyValues.put(ConstantsGRC.GRCProperties.CIMREQUESTFORM_TEMPLATE_TITLE, propertiesValues.get(ConstantsGRC.GRCProperties.CIMREQUESTFORM_TEMPLATE_TITLE, String.class));
        bodyValues.put(ConstantsGRC.GRCProperties.CIMREQUESTFORM_TEMPLATE_DESCRIPTION, propertiesValues.get(ConstantsGRC.GRCProperties.CIMREQUESTFORM_TEMPLATE_DESCRIPTION, String.class));

        /*
        * Sender information
        * */
        bodyValues.put(ConstantsGRC.GRCProperties.CIMREQUESTFORM_TEMPLATE_REQUESTDATELABEL, propertiesValues.get(ConstantsGRC.GRCProperties.CIMREQUESTFORM_TEMPLATE_REQUESTDATELABEL, String.class));
        bodyValues.put(ConstantsGRC.GRCProperties.CIMREQUESTFORM_TEMPLATE_REQUESTDATETEXT, dateOfRequest);
        bodyValues.put(ConstantsGRC.GRCProperties.CIMREQUESTFORM_TEMPLATE_LANGUAGELABEL, propertiesValues.get(ConstantsGRC.GRCProperties.CIMREQUESTFORM_TEMPLATE_LANGUAGELABEL, String.class));
        bodyValues.put(ConstantsGRC.GRCProperties.CIMREQUESTFORM_TEMPLATE_LANGUAGETEXT, displayLanguage);
        bodyValues.put(ConstantsGRC.GRCProperties.CIMREQUESTFORM_TEMPLATE_FULLNAMELABEL, propertiesValues.get(ConstantsGRC.GRCProperties.CIMREQUESTFORM_TEMPLATE_FULLNAMELABEL, String.class));
        bodyValues.put(ConstantsGRC.GRCProperties.CIMREQUESTFORM_TEMPLATE_FULLNAMETEXT, fullName);
        bodyValues.put(ConstantsGRC.GRCProperties.CIMREQUESTFORM_TEMPLATE_EMAILLABEL, propertiesValues.get(ConstantsGRC.GRCProperties.CIMREQUESTFORM_TEMPLATE_EMAILLABEL, String.class));
        bodyValues.put(ConstantsGRC.GRCProperties.CIMREQUESTFORM_TEMPLATE_EMAILTEXT, getParameterFromRequest(request, ConstantsForm.FormProperties.FORM_PROGRESSIVEPROFILEING_EMAIL));

        /*
        * MyEDC data
        * */
        String externalId = getParameterFromRequest(request, Constants.Properties.HEADER_EXTERNAL_ID);
        EloquaUserProfileDO eloquaUserProfileDO;
        String companyName = Constants.Properties.EMAIL_NOTAPPLICABLE_DEFAULT;
        String industry = Constants.Properties.EMAIL_NOTAPPLICABLE_DEFAULT;
        String markets = Constants.Properties.EMAIL_NOTAPPLICABLE_DEFAULT;
        String painPoint = Constants.Properties.EMAIL_NOTAPPLICABLE_DEFAULT;

        try {
            eloquaUserProfileDO = eloquaDAOService.getUserProfileByExternalId(externalId);
            Map<String, EloquaDataItem> fields = eloquaUserProfileDO.getFormFieldsValues();
            companyName = getValueFromEloqua(fields, eloquaDAOService.getEloquaService().getCompanyIdFieldId());
            industry = getValueFromEloqua(fields, eloquaDAOService.getEloquaService().getIndustryFieldId());
            markets = getValueFromEloqua(fields, eloquaDAOService.getEloquaService().getMarketsIntFieldId());
            painPoint = getValueFromEloqua(fields, eloquaDAOService.getEloquaService().getPainPointFieldId());
        } catch (EDCEloquaSystemException | NullPointerException e) {
            log.debug("Exception", e);
        }

        bodyValues.put(ConstantsGRC.GRCProperties.CIMREQUESTFORM_TEMPLATE_COMPANYLABEL, propertiesValues.get(ConstantsGRC.GRCProperties.CIMREQUESTFORM_TEMPLATE_COMPANYLABEL, String.class));
        bodyValues.put(ConstantsGRC.GRCProperties.CIMREQUESTFORM_TEMPLATE_COMPANYTEXT, companyName);
        bodyValues.put(ConstantsGRC.GRCProperties.CIMREQUESTFORM_TEMPLATE_INDUSTRYLABEL, propertiesValues.get(ConstantsGRC.GRCProperties.CIMREQUESTFORM_TEMPLATE_INDUSTRYLABEL, String.class));
        bodyValues.put(ConstantsGRC.GRCProperties.CIMREQUESTFORM_TEMPLATE_INDUSTRYTEXT, industry);
        bodyValues.put(ConstantsGRC.GRCProperties.CIMREQUESTFORM_TEMPLATE_MARKETSLABEL, propertiesValues.get(ConstantsGRC.GRCProperties.CIMREQUESTFORM_TEMPLATE_MARKETSLABEL, String.class));
        bodyValues.put(ConstantsGRC.GRCProperties.CIMREQUESTFORM_TEMPLATE_MARKETSTEXT, markets);
        bodyValues.put(ConstantsGRC.GRCProperties.CIMREQUESTFORM_TEMPLATE_PAINPOINTLABEL, propertiesValues.get(ConstantsGRC.GRCProperties.CIMREQUESTFORM_TEMPLATE_PAINPOINTLABEL, String.class));
        bodyValues.put(ConstantsGRC.GRCProperties.CIMREQUESTFORM_TEMPLATE_PAINPOINTTEXT, painPoint);
        bodyValues.put(ConstantsGRC.GRCProperties.CIMREQUESTFORM_TEMPLATE_EHHQUESTIONLABEL, propertiesValues.get(ConstantsGRC.GRCProperties.CIMREQUESTFORM_TEMPLATE_EHHQUESTIONLABEL, String.class));
        bodyValues.put(ConstantsGRC.GRCProperties.CIMREQUESTFORM_TEMPLATE_EHHQUESTIONTEXT, getParameterFromRequest(request, Constants.Properties.SEARCHQUERY));

        /*
        * User's feedback
        * */
        String userInput = getParameterFromRequest(request, Constants.Properties.USERINPUT);
        String answersYes = getParameterFromRequest(request, Constants.Properties.ANSWERS_YES);
        String answersNo = getParameterFromRequest(request, Constants.Properties.ANSWERS_NO);

        bodyValues.put(Constants.Properties.YESNOQUESTION_EMAIL_FEEDBACKQUESTION, getParameterFromRequest(request, Constants.Properties.QUESTION));
        bodyValues.put(Constants.Properties.YESNOQUESTION_EMAIL_USERINFO, buildTableRowsFromJSONArrayString(userInput));
        bodyValues.put(Constants.Properties.YESNOQUESTION_EMAIL_FEEDBACKONE_QUESTIONANSWER, getParameterFromRequest(request, Constants.Properties.QUESTION_YES));
        bodyValues.put(Constants.Properties.YESNOQUESTION_EMAIL_FEEDBACKONE_OPTIONS, buildTableRowsFromJSONArrayString(answersYes));
        bodyValues.put(Constants.Properties.YESNOQUESTION_EMAIL_FEEDBACKTWO_QUESTIONANSWER, getParameterFromRequest(request, Constants.Properties.QUESTION_NO));
        bodyValues.put(Constants.Properties.YESNOQUESTION_EMAIL_FEEDBACKTWO_OPTIONS, buildTableRowsFromJSONArrayString(answersNo));

        EmailSender.sendHtmlEmail(messageGatewayService, request, response, Constants.Paths.YESNOQUESTION_EMAIL_TEMPLATE, emailValues, bodyValues);
    }

    private String buildTableRowsFromJSONArrayString(String jsonArrayString) {
        try {
            JSONArray userInputObj = new JSONArray(jsonArrayString);
            StringBuilder result = new StringBuilder();

            for (int i = 0; i < userInputObj.length(); i++) {
                String option = userInputObj.getJSONObject(i).getString(Constants.Properties.OPTION);
                String answer = userInputObj.getJSONObject(i).getString(Constants.Properties.ANSWER);
                result.append(buildTableRow(option, answer));
            }

            return result.toString();
        } catch (JSONException e) {
            return "";
        }
    }

    private String buildTableRow(String cell1, String cell2) {
        return "<tr style=\"border-collapse: collapse;\">" +
            "<td style=\"width: 35%; border: 1px solid #000000; border-collapse: collapse; padding: 5px 0; background-color: #d9d9d9;\">" + cell1 + "</td>" +
            "<td style=\"border: 1px solid #000000; border-collapse: collapse; padding: 5px 0;\">" + cell2 + "</td>" +
            "</tr>";
    }

    private String getParameterFromRequest(SlingHttpServletRequest request, String paramName) {
        return request.getParameter(paramName) != null ? new String(request.getParameter(paramName).getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8) : "";
    }

    private String getValueFromEloqua(Map<String, EloquaDataItem> fields, String eloquaFieldId) {

        String value = fields.get(eloquaFieldId).getEloquaValue();

        return value != null && !value.equals("") ? value : Constants.Properties.EMAIL_NOTAPPLICABLE_DEFAULT;
    }
}
