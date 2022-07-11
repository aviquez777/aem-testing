package com.edc.edcweb.core.serviceImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.jcr.Session;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import org.apache.commons.lang.text.StrLookup;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.SimpleEmail;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.day.cq.commons.mail.MailTemplate;
import com.day.cq.mailer.MessageGateway;
import com.day.cq.mailer.MessageGatewayService;
import com.edc.edcweb.core.helpers.constants.ConstantsEmailSender;
import com.edc.edcweb.core.service.EDCEMailService;

@Component(immediate = true, service = EDCEMailService.class)
public class EDCEMailServiceImpl implements EDCEMailService {
    private static final Logger log = LoggerFactory.getLogger(EDCEMailServiceImpl.class);
    // Inject a MessageGatewayService
    @Reference
    private MessageGatewayService messageGatewayService;
    private List<String> failedList = new ArrayList<>();

    // Template Email
    @Override
    public List<String> sendEmail(List<String> emailToList, String emailSubject, String templatePath,
            Map<String, String> templateTokens, ResourceResolver resourceResolver)
            throws IOException, MessagingException, EmailException {
        // Check the email list
        if (emailToList != null && !emailToList.isEmpty()) {
            sendEmails(emailToList, emailSubject, templatePath, templateTokens, resourceResolver, null);
        }
        return failedList;
    }

    // Plain Text email
    @Override
    public List<String> sendEmail(List<String> emailToList, String emailSubject, String emailBody)
            throws EmailException, IOException, MessagingException {
        // Check the email list
        if (emailToList != null && !emailToList.isEmpty()) {
            sendEmails(emailToList, emailSubject, null, null, null, emailBody);
        }
        return failedList;
    }

    private void sendEmails(List<String> emailToList, String emailSubject, String templatePath,
            Map<String, String> templateTokens, ResourceResolver resourceResolver, String emailBody)
            throws IOException, MessagingException, EmailException {
        // Check the email list
        if (emailToList != null && !emailToList.isEmpty()) {
            List<InternetAddress> addresses = checkEmails(emailToList);
            if (!addresses.isEmpty()) {
                String emailFrom = null;
                if (templateTokens != null) {
                    emailFrom = templateTokens.get(ConstantsEmailSender.Properties.EMAIL_FROM);
                }
                // Send the based on variables provided
                if (templatePath != null && resourceResolver != null) {
                    // Lets prepare the template
                    MailTemplate mailTemplate = getMailTemplate(resourceResolver, templatePath);
                    // Send Html emails
                    failedList = sendHtmlMail(addresses, emailSubject, mailTemplate, templateTokens, emailFrom);
                } else {
                    // Send Text emails
                    failedList = sendTextMail(addresses, emailSubject, emailBody, emailFrom);
                }
            } else {
                log.debug("No Valid Emails to send");
            }
        } else {
            log.debug("No Emails to send");
        }
    }

    // Do some email "pre-check"
    private List<InternetAddress> checkEmails(List<String> emailToList) {
        List<InternetAddress> addresses = new ArrayList<>();
        for (String recipient : emailToList) {
            try {
                InternetAddress address = new InternetAddress(recipient);
                addresses.add(address);
            } catch (AddressException e) {
                log.warn("Malformed Email [{}].", recipient);
            }
        }
        return addresses;
    }

    private MailTemplate getMailTemplate(ResourceResolver resourceResolver, String templatePath) {
        MailTemplate mailTemplate = null;
        mailTemplate = MailTemplate.create(templatePath, resourceResolver.adaptTo(Session.class));
        if (mailTemplate == null) {
            throw new IllegalArgumentException("Invalid template path " + templatePath);
        }
        return mailTemplate;
    }

    private List<String> sendHtmlMail(List<InternetAddress> recipiants, String subject, MailTemplate mailTemplate,
            Map<String, String> templateParams, String emailFrom)
            throws IOException, MessagingException, EmailException {
        // falied emails
        List<String> failureList = new ArrayList<>();
        // Declare a MessageGateway service
        MessageGateway<HtmlEmail> messageGateway;
        for (final InternetAddress recipient : recipiants) {
            try {
                // Get a new email per recipient to avoid duplicate attachments
                Email htmlEmail = mailTemplate.getEmail(StrLookup.mapLookup(templateParams), HtmlEmail.class);
                htmlEmail.addTo(recipient.getAddress());
                htmlEmail.setSubject(subject);
                // Set Email from
                if (StringUtils.isNotEmpty(emailFrom)) {
                    htmlEmail.setFrom(emailFrom);
                }
                // Inject a MessageGateway Service and send the message
                messageGateway = messageGatewayService.getGateway(HtmlEmail.class);
                messageGateway.send((HtmlEmail) htmlEmail);
            } catch (Exception e) {
                String address = recipient.getAddress();
                failureList.add(address);
                log.error("Error sending email to {} full error {}", address, e.getStackTrace());
            }
        }
        return failureList;
    }

    private List<String> sendTextMail(List<InternetAddress> recipiants, String subject, String emailBody,
            String emailFrom) throws EmailException {
        // failed emails
        List<String> failureList = new ArrayList<>();
        // Declare a MessageGateway service
        MessageGateway<Email> messageGateway;
        for (final InternetAddress recipient : recipiants) {
            try {
                // Get a new email per recipient to avoid duplicate attachments
                Email email = new SimpleEmail();
                email.addTo(recipient.getAddress());
                email.setSubject(subject);
                email.setMsg(emailBody);
                // Set Email from
                if (StringUtils.isNotEmpty(emailFrom)) {
                    email.setFrom(emailFrom);
                }
                // Inject a MessageGateway Service and send the message
                messageGateway = messageGatewayService.getGateway(Email.class);
                messageGateway.send(email);
            } catch (Exception e) {
                String address = recipient.getAddress();
                failureList.add(address);
                log.error("Error sending email to {} Error {} ", address, e.getStackTrace());
            }
        }
        return failureList;
    }
}