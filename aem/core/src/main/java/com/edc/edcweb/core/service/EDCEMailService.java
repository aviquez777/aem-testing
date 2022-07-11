package com.edc.edcweb.core.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.mail.MessagingException;
import org.apache.commons.mail.EmailException;
import org.apache.sling.api.resource.ResourceResolver;

public interface EDCEMailService {

    /**
     * Construct an email based on a template and send it to one or more recipients.
     * 
     * @param templatePath Absolute path of the template used to send the email.
     * @param emailParams  Replacement variable map to be injected in the template
     * @param recipients   recipient email addresses. Invalid email addresses are
     *                     skipped.
     * 
     * @return failureList containing list recipient's String addresses for which
     *         email sent failed
     * @throws EmailException
     * @throws MessagingException
     * @throws IOException
     */
    List<String> sendEmail(List<String> emailToList, String emailSubject, String templatePath,
            Map<String, String> templateToken, ResourceResolver resourceResolver)
            throws IOException, MessagingException, EmailException;

    /**
     * Construct an email based on a template and send it to one or more recipients.
     * 
     * @param templatePath Absolute path of the template used to send the email.
     * @param emailParams  Replacement variable map to be injected in the template
     * @param recipients   recipient email addresses. Invalid email addresses are
     *                     skipped.
     * 
     * @return failureList containing list recipient's String addresses for which
     *         email sent failed
     * @throws EmailException
     * @throws MessagingException
     * @throws IOException
     */
    List<String> sendEmail(List<String> emailToList, String emailSubject, String emailBody)
            throws EmailException, IOException, MessagingException;
}