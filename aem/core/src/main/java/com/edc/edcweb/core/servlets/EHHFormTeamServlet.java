package com.edc.edcweb.core.servlets;

import com.day.cq.mailer.MessageGatewayService;
import com.day.cq.wcm.api.Page;
import com.edc.edcweb.core.exception.EDCEloquaSystemException;
import com.edc.edcweb.core.helpers.Constants;
import com.edc.edcweb.core.helpers.LanguageUtil;
import com.edc.edcweb.core.helpers.Request;
import com.edc.edcweb.core.helpers.ResourceResolverHelper;
import com.edc.edcweb.core.helpers.constants.*;
import com.edc.edcweb.core.helpers.email.EmailSender;
import com.edc.edcweb.core.myedc.eloqua.pojo.EloquaDataItem;
import com.edc.edcweb.core.myedc.eloqua.pojo.EloquaUserProfileDO;
import com.edc.edcweb.core.myedc.eloqua.services.EloquaDAOService;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.servlet.Servlet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component(immediate = true, service = Servlet.class, property = {
        "sling.servlet.extensions=html",
        "sling.servlet.methods=post",
        "sling.servlet.paths=" + ConstantsEHH.EHHProperties.EHH_PATH_TEAM_FORM_SERVLET})

public class EHHFormTeamServlet extends SlingAllMethodsServlet {

    private static final long serialVersionUID = 4522134777775086197L;
    
    // Inject a MessageGatewayService
    @Reference
    @Inject
    private MessageGatewayService messageGatewayService;

    @Inject
    @Reference
    private EloquaDAOService eloquaDAOService;

    public static final String FORM_URL_LABEL = "formURLLabel";
    public static final String FORM_URL_TEXT = "urlFormText";
    public static final String QUESTION_LABEL = "questionLabel";
    public static final String QUESTION_LABEL_PROP = "howCanWeHelpYouLabel";
    public static final String QUESTION_TEXT = "howCanWeHelpYouText";
    public static final String MARKET_OPTION_LABEL = "marketOptionLabel";
    public static final String MARKET_OPTION_LABEL_PROP = "marketLabel";
    public static final String MARKET_OPTION_TEXT = "MarketsText";
    public static final String EMAIL_ADDRESS_LABEL = "businessEmailAddressLabel";
    public static final String MARKETS_INTEREST_LABEL = "countriesPlanningSell2YearsLabel";
    public static final String MARKETS_INTEREST_TEXT = "countriesPlanningSell2YearsText";

    private static final Logger log = LoggerFactory.getLogger(EHHFormTeamServlet.class);

    @Override
    protected final void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        Resource currPage = Request.getCurrentPageResource(request, null);
        Page currentPage = currPage.adaptTo(Page.class);
        Resource ehhRequestFormRsc = ResourceResolverHelper.getResourceByType(currentPage, ConstantsEHH.EHHProperties.EHH_REQUESTFORM_RESOURCE_TYPE);
        ValueMap propertiesValues = ehhRequestFormRsc.getValueMap();

        // Date / time of request
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        Date date = new Date();
        String dateOfRequest = dateFormat.format(date);

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
        String subject =  (String) propertiesValues.getOrDefault(ConstantsInList.InListProperties.FEEDBACK_FORM_SUBJECT, ConstantsEHH.EHHProperties.EHH_REQUESTFORM_TEMPLATE_DEFAULTSUBJECT);
        Map<String, String> emailValues = new HashMap<>();

        //Sender Information
        emailValues.put(ConstantsEmailSender.Properties.EMAIL_TO, propertiesValues.get(ConstantsInList.InListProperties.FEEDBACK_FORM_EMAIL, String.class));
        emailValues.put(ConstantsEmailSender.Properties.EMAIL_BCC, propertiesValues.get(ConstantsInList.InListProperties.FEEDBACK_FORM_EMAIL_BCC, String.class));
        emailValues.put(ConstantsEmailSender.Properties.SUBJECT, subject);

        // Email content
        Map<String, String> bodyValues = new HashMap<>();

        // Title and description
        bodyValues.put(ConstantsEHH.EHHProperties.EHH_REQUESTFORM_TEMPLATE_EMAILLABEL, propertiesValues.get(ConstantsEHH.EHHProperties.EHH_REQUESTFORM_TEMPLATE_EMAILLABEL, String.class));
        bodyValues.put(ConstantsEHH.EHHProperties.EHH_REQUESTFORM_TEMPLATE_INTERNALUSELABEL, propertiesValues.get(ConstantsEHH.EHHProperties.EHH_REQUESTFORM_TEMPLATE_INTERNALUSELABEL, String.class));

        //Sender information
        bodyValues.put(ConstantsEHH.EHHProperties.EHH_REQUESTFORM_TEMPLATE_REQUESTDATELABEL, propertiesValues.get(ConstantsEHH.EHHProperties.EHH_REQUESTFORM_TEMPLATE_REQUESTDATELABEL, String.class));
        bodyValues.put(ConstantsEHH.EHHProperties.EHH_REQUESTFORM_TEMPLATE_REQUESTDATETEXT, dateOfRequest);
        bodyValues.put(ConstantsEHH.EHHProperties.EHH_REQUESTFORM_TEMPLATE_LANGUAGELABEL, propertiesValues.get(ConstantsEHH.EHHProperties.EHH_REQUESTFORM_TEMPLATE_LANGUAGELABEL, String.class));
        bodyValues.put(ConstantsEHH.EHHProperties.EHH_REQUESTFORM_TEMPLATE_LANGUAGETEXT, displayLanguage);
        bodyValues.put(FORM_URL_LABEL,  propertiesValues.get(FORM_URL_LABEL, String.class));
        bodyValues.put(FORM_URL_TEXT, getParameterFromRequest(request, "formURL"));

        // Question table
        bodyValues.put(ConstantsEHH.EHHProperties.EHH_REQUESTFORM_TEMPLATE_EHHQUESTIONREQUESTTITLELABEL, propertiesValues.get(ConstantsEHH.EHHProperties.EHH_REQUESTFORM_TEMPLATE_EHHQUESTIONREQUESTTITLELABEL, String.class));
        bodyValues.put(QUESTION_LABEL, propertiesValues.get(QUESTION_LABEL_PROP, String.class));
        bodyValues.put(QUESTION_TEXT, getParameterFromRequest(request, "comment"));
        bodyValues.put(MARKET_OPTION_LABEL, propertiesValues.get(MARKET_OPTION_LABEL_PROP, String.class));
        bodyValues.put(MARKET_OPTION_TEXT, getParameterFromRequest(request, "market"));

        // User's Profile table
        bodyValues.put(ConstantsEHH.EHHProperties.EHH_REQUESTFORM_TEMPLATE_USERPROFILETITLELABEL, propertiesValues.get(ConstantsEHH.EHHProperties.EHH_REQUESTFORM_TEMPLATE_USERPROFILETITLELABEL, String.class));

        /*
         * MyEDC data
         * */
        String externalId = getParameterFromRequest(request, Constants.Properties.HEADER_EXTERNAL_ID);
        EloquaUserProfileDO eloquaUserProfileDO;
        String firstName  = Constants.Properties.EMAIL_NOTAPPLICABLE_DEFAULT;
        String lastName  = Constants.Properties.EMAIL_NOTAPPLICABLE_DEFAULT;
        String jobTitle = Constants.Properties.EMAIL_NOTAPPLICABLE_DEFAULT;
        String tradeStatus = Constants.Properties.EMAIL_NOTAPPLICABLE_DEFAULT;
        String companyName = Constants.Properties.EMAIL_NOTAPPLICABLE_DEFAULT;
        String companyPhone = Constants.Properties.EMAIL_NOTAPPLICABLE_DEFAULT;
        String address = Constants.Properties.EMAIL_NOTAPPLICABLE_DEFAULT;
        String city = Constants.Properties.EMAIL_NOTAPPLICABLE_DEFAULT;
        String province = Constants.Properties.EMAIL_NOTAPPLICABLE_DEFAULT;
        String postalCode = Constants.Properties.EMAIL_NOTAPPLICABLE_DEFAULT;
        String country = Constants.Properties.EMAIL_NOTAPPLICABLE_DEFAULT;
        String annualSales = Constants.Properties.EMAIL_NOTAPPLICABLE_DEFAULT;
        String employees = Constants.Properties.EMAIL_NOTAPPLICABLE_DEFAULT;
        String painPoint = Constants.Properties.EMAIL_NOTAPPLICABLE_DEFAULT;
        String industry = Constants.Properties.EMAIL_NOTAPPLICABLE_DEFAULT;
        String markets = Constants.Properties.EMAIL_NOTAPPLICABLE_DEFAULT;

        try {
            eloquaUserProfileDO = eloquaDAOService.getUserProfileByExternalId(externalId);
            Map<String, EloquaDataItem> fields = eloquaUserProfileDO.getFormFieldsValues();
            // Validate if we have data to prevent null pointer error
            if(!fields.isEmpty()) {
                firstName = getValueFromEloqua(fields, eloquaDAOService.getEloquaService().getFirstNameFieldId());
                lastName = getValueFromEloqua(fields, eloquaDAOService.getEloquaService().getLastNameFieldId());
                jobTitle = getValueFromEloqua(fields, eloquaDAOService.getEloquaService().getJobTitleFieldId());
                tradeStatus = getValueFromEloqua(fields, eloquaDAOService.getEloquaService().getTradeStatusFieldId());
                companyName = getValueFromEloqua(fields, eloquaDAOService.getEloquaService().getCompanyIdFieldId());
                companyPhone = getValueFromEloqua(fields, eloquaDAOService.getEloquaService().getMainPhoneFieldId());
                address = getValueFromEloqua(fields, eloquaDAOService.getEloquaService().getCompanyAddress1FieldId());
                city = getValueFromEloqua(fields, eloquaDAOService.getEloquaService().getCompanyCityFieldId());
                province = getValueFromEloqua(fields, eloquaDAOService.getEloquaService().getCompanyProvinceFieldId());
                postalCode = getValueFromEloqua(fields, eloquaDAOService.getEloquaService().getCompanyPostalFieldId());
                country = getValueFromEloqua(fields, eloquaDAOService.getEloquaService().getCompanyCountryFieldId());
                annualSales = getValueFromEloqua(fields, eloquaDAOService.getEloquaService().getAnnualSalesFieldId());
                employees = getValueFromEloqua(fields, eloquaDAOService.getEloquaService().getEmployeesFieldId());
                painPoint = getValueFromEloqua(fields, eloquaDAOService.getEloquaService().getPainPointFieldId());
                industry = getValueFromEloqua(fields, eloquaDAOService.getEloquaService().getIndustryFieldId());
                markets = getValueFromEloqua(fields, eloquaDAOService.getEloquaService().getMarketsIntFieldId());
            } else {
                log.debug("No Data for user externalId {}", externalId);
                // If the N/A s are a problem, do not allow submission if no profile found
                response.setStatus(500);
            }
        } catch (EDCEloquaSystemException | NullPointerException e) {
            log.debug("getUserProfileByExternalId Exception", e);
        }

        bodyValues.put(ConstantsEHH.EHHProperties.EHH_REQUESTFORM_TEMPLATE_FIRSTNAMELABEL, propertiesValues.get(ConstantsEHH.EHHProperties.EHH_REQUESTFORM_TEMPLATE_FIRSTNAMELABEL, String.class));
        bodyValues.put(ConstantsEHH.EHHProperties.EHH_REQUESTFORM_TEMPLATE_FIRSTNAMETEXT, firstName);

        bodyValues.put(ConstantsEHH.EHHProperties.EHH_REQUESTFORM_TEMPLATE_LASTNAMELABEL, propertiesValues.get(ConstantsEHH.EHHProperties.EHH_REQUESTFORM_TEMPLATE_LASTNAMELABEL, String.class));
        bodyValues.put(ConstantsEHH.EHHProperties.EHH_REQUESTFORM_TEMPLATE_LASTNAMETEXT, lastName);

        bodyValues.put(EMAIL_ADDRESS_LABEL, propertiesValues.get(EMAIL_ADDRESS_LABEL, String.class));
        bodyValues.put(ConstantsEHH.EHHProperties.EHH_REQUESTFORM_TEMPLATE_EMAILTEXT, getParameterFromRequest(request, "userEmail"));

        bodyValues.put(ConstantsEHH.EHHProperties.EHH_REQUESTFORM_TEMPLATE_JOBTITLELABEL, propertiesValues.get(ConstantsEHH.EHHProperties.EHH_REQUESTFORM_TEMPLATE_JOBTITLELABEL, String.class));
        bodyValues.put(ConstantsEHH.EHHProperties.EHH_REQUESTFORM_TEMPLATE_JOBTITLETEXT, jobTitle);

        bodyValues.put(ConstantsEHH.EHHProperties.EHH_REQUESTFORM_TEMPLATE_TRADESTATUSLABEL, propertiesValues.get(ConstantsEHH.EHHProperties.EHH_REQUESTFORM_TEMPLATE_TRADESTATUSLABEL, String.class));
        bodyValues.put(ConstantsEHH.EHHProperties.EHH_REQUESTFORM_TEMPLATE_TRADESTATUSTEXT, tradeStatus);

        bodyValues.put(ConstantsEHH.EHHProperties.EHH_REQUESTFORM_TEMPLATE_COMPANYNAMELABEL, propertiesValues.get(ConstantsEHH.EHHProperties.EHH_REQUESTFORM_TEMPLATE_COMPANYNAMELABEL, String.class));
        bodyValues.put(ConstantsEHH.EHHProperties.EHH_REQUESTFORM_TEMPLATE_COMPANYNAMETEXT, companyName);

        bodyValues.put(ConstantsEHH.EHHProperties.EHH_REQUESTFORM_TEMPLATE_COMPANYPHONELABEL, propertiesValues.get(ConstantsEHH.EHHProperties.EHH_REQUESTFORM_TEMPLATE_COMPANYPHONELABEL, String.class));
        bodyValues.put(ConstantsEHH.EHHProperties.EHH_REQUESTFORM_TEMPLATE_COMPANYPHONETEXT, companyPhone);

        bodyValues.put(ConstantsEHH.EHHProperties.EHH_REQUESTFORM_TEMPLATE_ADDRESSLABEL, propertiesValues.get(ConstantsEHH.EHHProperties.EHH_REQUESTFORM_TEMPLATE_ADDRESSLABEL, String.class));
        bodyValues.put(ConstantsEHH.EHHProperties.EHH_REQUESTFORM_TEMPLATE_ADDRESSTEXT, address);

        bodyValues.put(ConstantsEHH.EHHProperties.EHH_REQUESTFORM_TEMPLATE_CITYLABEL, propertiesValues.get(ConstantsEHH.EHHProperties.EHH_REQUESTFORM_TEMPLATE_CITYLABEL, String.class));
        bodyValues.put(ConstantsEHH.EHHProperties.EHH_REQUESTFORM_TEMPLATE_CITYTEXT, city);

        bodyValues.put(ConstantsEHH.EHHProperties.EHH_REQUESTFORM_TEMPLATE_PROVINCELABEL, propertiesValues.get(ConstantsEHH.EHHProperties.EHH_REQUESTFORM_TEMPLATE_PROVINCELABEL, String.class));
        bodyValues.put(ConstantsEHH.EHHProperties.EHH_REQUESTFORM_TEMPLATE_PROVINCETEXT, province);

        bodyValues.put(ConstantsEHH.EHHProperties.EHH_REQUESTFORM_TEMPLATE_POSTALCODELABEL, propertiesValues.get(ConstantsEHH.EHHProperties.EHH_REQUESTFORM_TEMPLATE_POSTALCODELABEL, String.class));
        bodyValues.put(ConstantsEHH.EHHProperties.EHH_REQUESTFORM_TEMPLATE_POSTALCODETEXT, postalCode);

        bodyValues.put(ConstantsEHH.EHHProperties.EHH_REQUESTFORM_TEMPLATE_COUNTRYLABEL, propertiesValues.get(ConstantsEHH.EHHProperties.EHH_REQUESTFORM_TEMPLATE_COUNTRYLABEL, String.class));
        bodyValues.put(ConstantsEHH.EHHProperties.EHH_REQUESTFORM_TEMPLATE_COUNTRYTEXT, country);

        bodyValues.put(ConstantsEHH.EHHProperties.EHH_REQUESTFORM_TEMPLATE_ANNUALSALESLABEL, propertiesValues.get(ConstantsEHH.EHHProperties.EHH_REQUESTFORM_TEMPLATE_ANNUALSALESLABEL, String.class));
        bodyValues.put(ConstantsEHH.EHHProperties.EHH_REQUESTFORM_TEMPLATE_ANNUALSALESTEXT, annualSales);

        bodyValues.put(ConstantsEHH.EHHProperties.EHH_REQUESTFORM_TEMPLATE_EMPLOYEESLABEL, propertiesValues.get(ConstantsEHH.EHHProperties.EHH_REQUESTFORM_TEMPLATE_EMPLOYEESLABEL, String.class));
        bodyValues.put(ConstantsEHH.EHHProperties.EHH_REQUESTFORM_TEMPLATE_EMPLOYEESTEXT, employees);

        bodyValues.put(ConstantsEHH.EHHProperties.EHH_REQUESTFORM_TEMPLATE_PAINPOINTLABEL, propertiesValues.get(ConstantsEHH.EHHProperties.EHH_REQUESTFORM_TEMPLATE_PAINPOINTLABEL, String.class));
        bodyValues.put(ConstantsEHH.EHHProperties.EHH_REQUESTFORM_TEMPLATE_PAINPOINTTEXT, painPoint);

        bodyValues.put(ConstantsEHH.EHHProperties.EHH_REQUESTFORM_TEMPLATE_INDUSTRYLABEL, propertiesValues.get(ConstantsEHH.EHHProperties.EHH_REQUESTFORM_TEMPLATE_INDUSTRYLABEL, String.class));
        bodyValues.put(ConstantsEHH.EHHProperties.EHH_REQUESTFORM_TEMPLATE_INDUSTRYTEXT, industry);

        bodyValues.put(MARKETS_INTEREST_LABEL, propertiesValues.get(MARKETS_INTEREST_LABEL, String.class));
        bodyValues.put(MARKETS_INTEREST_TEXT, markets);

        bodyValues.put(ConstantsEHH.EHHProperties.EHH_REQUESTFORM_TEMPLATE_EDCCOOKIESLABEL, propertiesValues.get(ConstantsEHH.EHHProperties.EHH_REQUESTFORM_TEMPLATE_EDCCOOKIESLABEL, String.class));

        EmailSender.sendHtmlEmail(messageGatewayService, request, response, ConstantsEHH.EHHProperties.EHH_PATH_TEAM_FORM_EMAIL_TEMPLATE, emailValues, bodyValues);
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

    private String getValueFromEloqua(Map<String, EloquaDataItem> fields, String eloquaFieldId) {
        String value = fields.get(eloquaFieldId).getEloquaValue();
        if(StringUtils.isBlank(value)) {
            value = Constants.Properties.EMAIL_NOTAPPLICABLE_DEFAULT;
        }
        return value;
    }

}
