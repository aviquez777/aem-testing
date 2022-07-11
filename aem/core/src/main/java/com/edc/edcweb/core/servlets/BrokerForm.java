package com.edc.edcweb.core.servlets;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.jcr.api.SlingRepository;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.dam.api.Asset;
import com.day.cq.mailer.MessageGatewayService;
import com.edc.edcweb.core.gatedcontentaccess.helpers.GatedContentAccessHelper;
import com.edc.edcweb.core.helpers.Constants;
import com.edc.edcweb.core.helpers.EncryptUtils;
import com.edc.edcweb.core.helpers.apsgForm.APSGFormHelper;
import com.edc.edcweb.core.helpers.apsgForm.APSGFormUserAccess;
import com.edc.edcweb.core.helpers.constants.ConstantsBrokerForm;
import com.edc.edcweb.core.helpers.constants.ConstantsEmailSender;
import com.edc.edcweb.core.helpers.email.EmailSender;
import com.edc.edcweb.core.helpers.formvalidation.FieldValidators;
import com.edc.edcweb.core.helpers.formvalidation.FormCleaner;
import com.edc.edcweb.core.servlets.brokerform.service.BrokerFormConfigService;

@Component(immediate = true, service = Servlet.class, property = {
        "sling.servlet.paths=" + Constants.Paths.BROKERFORM_BROKERFORMSERVLET })

public class BrokerForm extends SlingAllMethodsServlet {
    private static final long serialVersionUID = 2598426539166784515L;
    private static final Logger log = LoggerFactory.getLogger(BrokerForm.class);

    // Inject a MessageGatewayService
    @Reference
    @Inject
    private MessageGatewayService messageGatewayService;

    @Reference
    @Inject
    protected SlingRepository repository;

    @Reference
    @Inject
    private ResourceResolverFactory resolverFactory;

    @Reference
    @Inject
    private BrokerFormConfigService brokerFormService;

    @Override
    protected final void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {
        log.debug("SessionInfoServlet doGet: ");
    }

    @Override
    protected final void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        // Hope for the best
        int statusCode = 200;

        String emailUser = FormCleaner.cleanAll(request.getParameter(ConstantsBrokerForm.EMAIL_ADDRESS_FIELD));
        boolean isValidEmail = FieldValidators.validateEmail(emailUser);

        if (isValidEmail) {
            String docID = FormCleaner.cleanAll(request.getParameter(ConstantsBrokerForm.DOC_ID_FIELD));
            String resourcePath = EncryptUtils
                    .decryptString(request.getParameter(ConstantsBrokerForm.CHECK_PATH_FIELD));

            ValueMap formProperties = getFormProperties(request, resourcePath);
            if (formProperties != null) {
                // Get properties
                String pdfAttachment = formProperties.get(ConstantsBrokerForm.PDF_ATTACHMENT_PROPERTY, String.class);
                String emailSubject = formProperties.get(ConstantsBrokerForm.EMAIL_SUBJECT_PROPERTY, String.class);
                String emailMessage = formProperties.get(ConstantsBrokerForm.EMAIL_MESSAGE_PROPERTY, String.class);
                // Send email, unfortunately this method does not return success or fail
                sendEmailToUser(emailUser, pdfAttachment, emailSubject, emailMessage, request, response);
                // Send to Eloqua
                boolean eloquaOK = sendDataToEloqua(emailUser, docID, resourcePath, request);
                if (!eloquaOK) {
                    statusCode = 500;
                }
            } else {
                log.error("Broker Form Servlet Could not find form resourcePath {}", resourcePath);
                statusCode = 500;
            }
        } else {
            log.error("Broker Form Servlet Invalid email found {}", emailUser);
            statusCode = 500;
        }
        // Servlet does not return anything
        response.setStatus(statusCode);
        log.debug("Broker Form Servlet Finished OK");
    }

    private boolean verifyValue(String value) {
        return StringUtils.isNotBlank(value);
    }

    private void sendEmailToUser(String emailUser, String pdfAttachment, String emailSubject, String emailMessage,
            SlingHttpServletRequest request, SlingHttpServletResponse response) {

        String emailFrom = brokerFormService.getEmailFrom();
        Map<String, Object> emailValues = new HashMap<>();

        emailValues.put(ConstantsEmailSender.Properties.EMAIL_FROM, emailFrom);
        emailValues.put(ConstantsEmailSender.Properties.EMAIL_TO, emailUser);
        emailValues.put(ConstantsEmailSender.Properties.SUBJECT, emailSubject);
        emailValues.put(ConstantsEmailSender.Properties.MESSAGE, emailMessage);
        emailValues.put(ConstantsEmailSender.Properties.ATTACHES, getAsset(pdfAttachment, request));
        // Send Email
        EmailSender.sentMultiPartEmail(messageGatewayService, response, emailValues);
    }

    @SuppressWarnings("unchecked")
    private boolean sendDataToEloqua(String emailAddress, String docID, String resourcePath,
            SlingHttpServletRequest request) {
        boolean formOK = true;
        Map<String, Object> map = new LinkedHashMap<>();
        map.putAll(request.getParameterMap());

        // Remove unnecessary fields from MAP
        map.remove(ConstantsBrokerForm.EMAIL_ADDRESS_FIELD);
        map.remove(ConstantsBrokerForm.DOC_ID_FIELD);
        map.remove(ConstantsBrokerForm.CASL_CONSENT_FIELD);

        // Add caslConsent with the correct value
        String caslConsentValue = request.getParameter(ConstantsBrokerForm.CASL_CONSENT_FIELD);
        if (verifyValue(caslConsentValue)) {
            caslConsentValue = "yes";
        } else {
            caslConsentValue = "no";
        }
        map.put(ConstantsBrokerForm.CASL_CONSENT_FIELD, new String[] {caslConsentValue});
        // Build the access data and submitted fields from the form post request
        // parameters.
        APSGFormUserAccess userAccess = APSGFormHelper.buildFromPostRequestParams(map, emailAddress, docID);

        // UserAccess is filled with the form input... and the hidden fields and update
        // Eloqua.
        String elqStatusCode = APSGFormHelper.updateUserDataToEloqua(request, userAccess, resourcePath);
        if (elqStatusCode == null || !elqStatusCode.equals("200")) {
            formOK = false;
            log.error("Eloqua Exception An error occurred at the moment to send data to Eloqua");
        }
        return formOK;
    }

    private Asset getAsset(String pdfPath, SlingHttpServletRequest request) {
        Asset asset = null;
        ResourceResolver resourceResolver = request.getResourceResolver();
        Resource resource = resourceResolver.getResource(pdfPath);
        if (resource != null) {
            asset = resource.adaptTo(Asset.class);
        }
        return asset;
    }

    private ValueMap getFormProperties(SlingHttpServletRequest request, String overridePath) {
        Resource formRes = null;
        ValueMap formProperties = null;
        try {
            formRes = GatedContentAccessHelper.getFormRes(ConstantsBrokerForm.RESOURCE_TYPE, request, overridePath);
            formProperties = formRes.getValueMap();
        } catch (NullPointerException | IllegalArgumentException e) {
            log.debug("BrokerFormServlet getFormRes error", e);
        }
        return formProperties;
    }
}