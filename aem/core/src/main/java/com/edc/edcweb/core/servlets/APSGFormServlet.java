package com.edc.edcweb.core.servlets;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import com.edc.edcweb.core.helpers.constants.ConstantsEmailSender;
import com.edc.edcweb.core.helpers.email.EmailSender;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.mailer.MessageGatewayService;
import com.edc.edcweb.core.helpers.Constants;
import com.edc.edcweb.core.helpers.apsgForm.APSGFormHelper;
import com.edc.edcweb.core.helpers.apsgForm.APSGFormUserAccess;

/**
 * @author Roberto Ramos Vargas
 * @version 1.0.0
 * @since 1.0.0
 *
 *        APSGFormServlet is the post servlet to send the information to Eloqua
 *        and by email.
 *
 */

@SuppressWarnings({"CQRules:CQBP-75", "squid:S1948"})
@Component(immediate = true, service = Servlet.class, property = { "sling.servlet.extensions=html",
        "sling.servlet.methods=post", "sling.servlet.paths="
                + Constants.Paths.APSGFORM_APSGFORMSERVLET }, configurationPid = "com.edc.edcweb.core.servlets.APSGFormServlet")

@Designate(ocd = APSGFormServlet.Configuration.class)

public class APSGFormServlet extends SlingAllMethodsServlet {
    private static final long serialVersionUID = 2598426539166789515L;
    private static final Logger log = LoggerFactory.getLogger(APSGFormServlet.class);
    private String emailFrom;
    private String emailTo;
    private String emailCc;

    // Inject a MessageGatewayService
    @Reference
    private MessageGatewayService messageGatewayService;

    @Override
    protected final void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {
        log.debug("SessionInfoServlet doGet: ");
    }

    @Override
    protected final void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {
        final boolean isMultipart = ServletFileUpload.isMultipartContent(request);
        String emailTo = verifyValue(this.emailTo) ? this.emailTo : "cib-apsg-coverage@edc.ca";
        String emailCC = verifyValue(this.emailCc) ? this.emailCc : "webregistrants@edc.ca";
        String emailFrom = verifyValue(this.emailFrom) ? this.emailFrom : "no-reply@solutions.edc.ca";
        String emailExtraCC = verifyValue(request.getParameter("additionalEmailAddresses"))
                ? request.getParameter("additionalEmailAddresses")
                : null;
        Map<String, Object> emailValues = new HashMap<>();
        String language = verifyValue(request.getParameter("lang")) ? request.getParameter("lang") : "";

        if (isMultipart) {
            final String[] allowedMimeTypes = { "application/pdf", "application/msword",
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document", "text/plain" };
            emailValues.put(ConstantsEmailSender.Properties.ALLOWED_MIME_TYPES, allowedMimeTypes);

            try {
                // Set Email from
                if (verifyValue(emailFrom)) {
                    emailValues.put(ConstantsEmailSender.Properties.EMAIL_FROM, emailFrom);
                }

                // Set Requester's email
                String emailAddress = request.getParameter("emailAddress");
                if (verifyValue(emailAddress)) {
                    emailValues.put(ConstantsEmailSender.Properties.EMAIL_TO, emailTo + "," + emailAddress);
                } else {
                    // Set Email to
                    emailValues.put(ConstantsEmailSender.Properties.EMAIL_TO, emailTo);
                }


                // Add additinal cc if Any
                if (verifyValue(emailExtraCC)) {
                    emailValues.put(ConstantsEmailSender.Properties.EMAIL_CC, emailCC + "," + emailExtraCC);
                } else {
                    // Set Email CC AdHoc task 43279
                    emailValues.put(ConstantsEmailSender.Properties.EMAIL_CC, emailCC);
                }

                emailValues.put(ConstantsEmailSender.Properties.SUBJECT, buildSubject(request));
                if (language.equals("French")) {
                    emailValues.put(ConstantsEmailSender.Properties.MESSAGE, buildBodyFr(request));
                } else {
                    emailValues.put(ConstantsEmailSender.Properties.MESSAGE, buildBody(request));
                }
                emailValues.put(ConstantsEmailSender.Properties.ATTACHES, request.getRequestParameterMap());
                emailValues.put(ConstantsEmailSender.Properties.ATTACHES_FILTER, "BankInstrument");

                // ******************************************************************
                // Send email
                // ******************************************************************
                EmailSender.sentMultiPartEmail(messageGatewayService, response, emailValues);
            } catch (Exception e) {
                log.error("Email Exception", e);
                response.setStatus(500);
            }

            try {
                // ******************************************************************
                // Sent Data to Eloqua
                // ******************************************************************
                String emailAddress = request.getParameter("emailAddress");
                String docID = request.getParameter("docID");

                Map<String, Object> map = new LinkedHashMap<>();
                map.putAll(request.getParameterMap());

                // Remove unnecessary fields
                map.remove("emailAddress");
                map.remove("docID");
                removeKeyStartsWith(map, "BankInstrument");
                map.remove("caslConsent");

                // Add caslConsent with the correct value
                String caslConsentValue = verifyValue(request.getParameter("caslConsent")) ? "yes" : "no";
                String[] caslConsentItem = new String[1];
                caslConsentItem[0] = caslConsentValue;
                map.put("caslConsent", caslConsentItem);

                // Build the access data and submitted fields from the form post request
                // parameters.
                APSGFormUserAccess userAccess = APSGFormHelper.buildFromPostRequestParams(map, emailAddress, docID);

                // UserAccess is filled with the form input... and the hidden fields and update
                // Eloqua.
                String elqStatusCode = APSGFormHelper.updateUserDataToEloqua(request, userAccess, null);
                if (elqStatusCode == null || !elqStatusCode.equals("200")) {
                    log.error("Eloqua Exception", "An error occurred at the moment to send data to Eloqua");
                    response.setStatus(500);
                }
            } catch (Exception e) {
                log.error("Eloqua Exception", e);
                response.setStatus(500);
            }
        } else {
            log.error("Eloqua Exception", "The form should be a multipart form");
            response.setStatus(500);
        }
    }

    private String buildSubject(SlingHttpServletRequest request) {
        String subject = "";

        subject += verifyValue(request.getParameter("customerName")) ? request.getParameter("customerName") : "";
        subject += verifyValue(request.getParameter("accountPSGnumber"))
                ? (" - " + request.getParameter("accountPSGnumber"))
                : "";

        return subject;
    }

    private Boolean verifyValue(String value) {
        return value != null && !value.isEmpty();
    }

    private String buildBody(SlingHttpServletRequest request) {
        String body = "";

        body += verifyValue(request.getParameter("bank")) ? ("Bank: " + request.getParameter("bank") + "\n") : "";
        body += verifyValue(request.getParameter("otherBank"))
                ? ("Other Bank: " + request.getParameter("otherBank") + "\n")
                : "";
        body += verifyValue(request.getParameter("firstName"))
                ? ("First Name: " + request.getParameter("firstName") + "\n")
                : "";
        body += verifyValue(request.getParameter("lastName")) ? ("Last Name: " + request.getParameter("lastName") + "\n")
                : "";
        body += verifyValue(request.getParameter("emailAddress"))
                ? ("Email Address: " + request.getParameter("emailAddress") + "\n")
                : "";
        body += verifyValue(request.getParameter("customerName"))
                ? ("Customer Name: " + request.getParameter("customerName") + "\n")
                : "";
        body += verifyValue(request.getParameter("accountPSGnumber"))
                ? ("Account PSG Number: " + request.getParameter("accountPSGnumber") + "\n")
                : "";
        body += verifyValue(request.getParameter("requestforCover"))
                ? ("Request for Cover: " + request.getParameter("requestforCover") + "\n")
                : "";
        body += verifyValue(request.getParameter("edcReferenceNumber"))
                ? ("EDC Reference Number: " + request.getParameter("edcReferenceNumber") + "\n")
                : "";
        body += verifyValue(request.getParameter("specialRemarks"))
                ? ("Special Remarks: " + request.getParameter("specialRemarks") + "\n")
                : "";
        body += verifyValue(request.getParameter("requestedDueDate"))
                ? ("Requested Due Date: " + request.getParameter("requestedDueDate") + "\n")
                : "";
        body += verifyValue(request.getParameter("caslConsent"))
                ? ("CASL Consentement: yes" + "\n")
                : "CASL Consent: no";

        return body;
    }

    private String buildBodyFr(SlingHttpServletRequest request) {
        String body = "";

        body += verifyValue(request.getParameter("bank")) ? ("Banque: " + request.getParameter("bank") + "\n") : "";
        body += verifyValue(request.getParameter("otherBank"))
                ? ("Autre Banque: " + request.getParameter("otherBank") + "\n")
                : "";
        body += verifyValue(request.getParameter("firstName"))
                ? ("Prénom: " + request.getParameter("firstName") + "\n")
                : "";
        body += verifyValue(request.getParameter("lastName")) ? ("Nom de famille: " + request.getParameter("lastName") + "\n")
                : "";
        body += verifyValue(request.getParameter("emailAddress"))
                ? ("Adresse e-mail: " + request.getParameter("emailAddress") + "\n")
                : "";
        body += verifyValue(request.getParameter("customerName"))
                ? ("Nom du client: " + request.getParameter("customerName") + "\n")
                : "";
        body += verifyValue(request.getParameter("accountPSGnumber"))
                ? ("Numéro de compte PSG: " + request.getParameter("accountPSGnumber") + "\n")
                : "";
        body += verifyValue(request.getParameter("requestforCover"))
                ? ("Demande de couverture: " + request.getParameter("requestforCover") + "\n")
                : "";
        body += verifyValue(request.getParameter("edcReferenceNumber"))
                ? ("Numéro de référence EDC: " + request.getParameter("edcReferenceNumber") + "\n")
                : "";
        body += verifyValue(request.getParameter("specialRemarks"))
                ? ("Remarques spéciales: " + request.getParameter("specialRemarks") + "\n")
                : "";
        body += verifyValue(request.getParameter("requestedDueDate"))
                ? ("Date d'échéance de la demande: " + request.getParameter("requestedDueDate") + "\n")
                : "";
        body += verifyValue(request.getParameter("caslConsent"))
                ? ("CASL Consentement: yes" + "\n")
                : "CASL Consentement: no";

        return body;
    }

    private void removeKeyStartsWith(Map<String, Object> map, String prefix) {
        Set<String> set = new HashSet<> ();
        for (Map.Entry<String,Object> entry : map.entrySet()) {
                String key = entry.getKey();
                if (key.startsWith(prefix)) {
                        set.add(key);
                }
        }
        map.keySet().removeAll(set);
    }

    @Activate
    @Modified
    protected void activate(APSGFormServlet.Configuration config) {
        this.emailFrom = config.emailFrom();
        this.emailTo = config.emailTo();
        this.emailCc = config.emailCc();
    }

    @ObjectClassDefinition(name = "EDC APSG Form Servlet")
    public @interface Configuration {
        @AttributeDefinition(name = "Email from", description = "Specify the email account who send the email", type = AttributeType.STRING, defaultValue = "no-reply@solutions.edc.ca")
        String emailFrom();

        @AttributeDefinition(name = "Email to", description = "Specify the email account who receive the email", type = AttributeType.STRING, defaultValue = "cib-apsg-coverage@edc.ca")
        String emailTo();

        @AttributeDefinition(name = "Email CC", description = "Specify the email account who receive the email", type = AttributeType.STRING, defaultValue = "webregistrants@edc.ca")
        String emailCc();
    }
}