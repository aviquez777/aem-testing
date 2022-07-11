package com.edc.edcweb.core.helpers.email;

import com.day.cq.dam.api.Asset;
import com.day.cq.mailer.MessageGateway;
import com.day.cq.mailer.MessageGatewayService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.SimpleEmail;
import com.edc.edcweb.core.helpers.constants.ConstantsEmailSender;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import javax.activation.DataSource;
import javax.jcr.Node;
import javax.mail.util.ByteArrayDataSource;
import org.apache.commons.mail.MultiPartEmail;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import com.day.crx.JcrConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmailSender {

    private static final Logger log = LoggerFactory.getLogger(EmailSender.class);

    private EmailSender() {
    }

    /**
     * Send a simple email
     *
     * @param messageGatewayService
     * @param response
     * @param values are all necessary values for send a simple email
     */
    public static void sendSimpleEmail(MessageGatewayService messageGatewayService, SlingHttpServletResponse response, Map values) {
        String emailTo = values.containsKey(ConstantsEmailSender.Properties.EMAIL_TO) ? (String) values.get(ConstantsEmailSender.Properties.EMAIL_TO) : "";
        String emailCc = values.containsKey(ConstantsEmailSender.Properties.EMAIL_CC) ? (String) values.get(ConstantsEmailSender.Properties.EMAIL_CC) : "";
        String subject = values.containsKey(ConstantsEmailSender.Properties.SUBJECT) ? (String) values.get(ConstantsEmailSender.Properties.SUBJECT) : "";
        String msg = values.containsKey(ConstantsEmailSender.Properties.MESSAGE) ? (String) values.get(ConstantsEmailSender.Properties.MESSAGE) : "";

        if (StringUtils.isNotEmpty(emailTo) && StringUtils.isNotEmpty(subject) && StringUtils.isNotEmpty(msg)) {
            try {
                // Declare a MessageGateway service
                MessageGateway<SimpleEmail> messageGateway;

                // Create a new the email
                Email email = new SimpleEmail();
                email.setCharset(org.apache.commons.mail.EmailConstants.UTF_8);

                // Set Email to (Multiple emails allowed if they are sepate with comma)
                email.addTo(emailTo.split(","));

                // Set Email CC (Multiple emails allowed if they are sepate with comma)
                if (StringUtils.isNotEmpty(emailCc)) {
                    email.addCc(emailCc.split(","));
                }

                // Set subject
                email.setSubject(subject);

                // Set body message
                email.setMsg(msg);

                // Send email
                messageGateway = messageGatewayService.getGateway(SimpleEmail.class);
                messageGateway.send((SimpleEmail) email);
            } catch (Exception e) {
                log.error(ConstantsEmailSender.Properties.EXCEPTION_MESSAGE, e);
                response.setStatus(500);
            }
        } else {
            response.setStatus(500);
        }
    }

    /**
     * Send a multipart email
     *
     * @param messageGatewayService
     * @param response
     * @param values are all necessary values for send a Multipart email
     */
    public static void sentMultiPartEmail(MessageGatewayService messageGatewayService, SlingHttpServletResponse response, Map values) {
        String emailTo = values.containsKey(ConstantsEmailSender.Properties.EMAIL_TO) ? (String) values.get(ConstantsEmailSender.Properties.EMAIL_TO) : null;
        String emailCc = values.containsKey(ConstantsEmailSender.Properties.EMAIL_CC) ? (String) values.get(ConstantsEmailSender.Properties.EMAIL_CC) : null;
        String subject = values.containsKey(ConstantsEmailSender.Properties.SUBJECT) ? (String) values.get(ConstantsEmailSender.Properties.SUBJECT) : "";
        String msg = values.containsKey(ConstantsEmailSender.Properties.MESSAGE) ? (String) values.get(ConstantsEmailSender.Properties.MESSAGE) : "";
        String emailFrom = values.containsKey(ConstantsEmailSender.Properties.EMAIL_FROM) ? (String) values.get(ConstantsEmailSender.Properties.EMAIL_FROM) : "";

        // Attachements parameters
        String[] allowedMimeTypes = values.containsKey(ConstantsEmailSender.Properties.ALLOWED_MIME_TYPES) ? (String[]) values.get(ConstantsEmailSender.Properties.ALLOWED_MIME_TYPES) : new String[0];
        Object attaches = values.containsKey(ConstantsEmailSender.Properties.ATTACHES) ? values.get(ConstantsEmailSender.Properties.ATTACHES) : null;
        String attachesFilter = values.containsKey(ConstantsEmailSender.Properties.ATTACHES_FILTER) ? (String) values.get(ConstantsEmailSender.Properties.ATTACHES_FILTER) : "";

        if (StringUtils.isNotEmpty(emailTo) && StringUtils.isNotEmpty(subject) && StringUtils.isNotEmpty(msg)) {
            try {
                // Declare a MessageGateway service
                MessageGateway<MultiPartEmail> messageGateway;

                // Create a new the email
                MultiPartEmail email = new MultiPartEmail();
                email.setCharset(org.apache.commons.mail.EmailConstants.UTF_8);

                // Set Email from
                if (StringUtils.isNotEmpty(emailFrom)) {
                    email.setFrom(emailFrom);
                }

                // Set Email to (Multiple emails allowed if they are sepate with comma)
                email.addTo(emailTo.split(","));

                // Set Email CC (Multiple emails allowed if they are sepate with comma)
                if (StringUtils.isNotEmpty(emailCc)) {
                    email.addCc(emailCc.split(","));
                }

                // Set subject
                email.setSubject(subject);

                // Set body message
                email.setMsg(msg);

                // Set attach message
                // APSG form attachments
                if (attaches != null && attaches instanceof Map) {
                    Map<String, RequestParameter[]> attachesMap = (Map<String, RequestParameter[]>) attaches;
                    for (final Map.Entry<String, RequestParameter[]> pairs : attachesMap.entrySet()) {
                        if (StringUtils.isEmpty(attachesFilter) || (StringUtils.isNotEmpty(attachesFilter) && pairs.getKey().contains(attachesFilter))) {
                            final RequestParameter[] pArr = pairs.getValue();
                            final RequestParameter param = pArr[0];
                            final InputStream is = param.getInputStream();
                            String fileName = param.getFileName();

                            if (fileName != null && !fileName.isEmpty()) {
                                String contentType = param.getContentType();
                                String acceptFile = Arrays.stream(allowedMimeTypes)
                                        .filter(x -> x.equals(param.getContentType())).findFirst().orElse(null);

                                if (acceptFile != null) {
                                    // Inject a MessageGateway Service and send the message
                                    DataSource source = new ByteArrayDataSource(is, contentType);
                                    // add the attachment
                                    email.attach(source, fileName, fileName);
                                }
                            }
                        }
                    }
                } else if (attaches != null && attaches instanceof Asset) {
                    // Broker form attachments
                    Asset pdf = (Asset) attaches;
                    InputStream pdfData = pdf.getOriginal().getStream();
                    String name = pdf.getName() + ".pdf";

                    ByteArrayDataSource pdfDS = new ByteArrayDataSource(pdfData, "application/pdf");
                    email.attach(pdfDS, name, name);
                }

                // Send email
                messageGateway = messageGatewayService.getGateway(MultiPartEmail.class);
                messageGateway.send((MultiPartEmail) email);
            } catch (Exception e) {
                log.error(ConstantsEmailSender.Properties.EXCEPTION_MESSAGE, e);
                response.setStatus(500);
            }
        } else {
            response.setStatus(500);
        }
    }

    public static void sendHtmlEmail (MessageGatewayService messageGatewayService, SlingHttpServletRequest request, SlingHttpServletResponse response, String templatePath, Map<String, String> emailValues, Map<String, String> bodyValues) {
        String emailTo = emailValues.getOrDefault(ConstantsEmailSender.Properties.EMAIL_TO, null);
        String emailCc = emailValues.getOrDefault(ConstantsEmailSender.Properties.EMAIL_CC, null);
		String emailBcc = emailValues.getOrDefault(ConstantsEmailSender.Properties.EMAIL_BCC, null);
        String subject = emailValues.getOrDefault(ConstantsEmailSender.Properties.SUBJECT, "");
        String emailFrom = emailValues.getOrDefault(ConstantsEmailSender.Properties.EMAIL_FROM, "");
		

        if (StringUtils.isNotEmpty(emailTo) && StringUtils.isNotEmpty(subject)) {
            try {
                // Declare a MessageGateway service
                MessageGateway<HtmlEmail> messageGateway;

                // Get email template content
                ResourceResolver resResolver = request.getResourceResolver();
                Resource templateRsrc = resResolver.getResource(templatePath);
                Node jcnode = templateRsrc.adaptTo(Node.class).getNode(JcrConstants.JCR_CONTENT);
                String templateContent = jcnode.getProperty(JcrConstants.JCR_DATA).getValue().getString();

                //Email body
                HtmlEmail email = new HtmlEmail();

                email.setHtmlMsg(substituteEmail(templateContent, bodyValues));
                email.setCharset(org.apache.commons.mail.EmailConstants.UTF_8);

                // Set Email from
                if (StringUtils.isNotEmpty(emailFrom)) {
                    email.setFrom(emailFrom);
                }

                // Set Email to (Multiple emails allowed if they are comma separated)
                email.addTo(splitAndTrim(emailTo));

                // Set Email CC (Multiple emails allowed if they are comma separated)
                if (StringUtils.isNotEmpty(emailCc)) {
                    email.addCc(splitAndTrim(emailCc));
                }
                // Set Email BCC (Multiple emails allowed if they are comma separated)
                if (StringUtils.isNotEmpty(emailBcc)) {
                    email.addBcc(splitAndTrim(emailBcc));
                }               
                
                // Set subject
                email.setSubject(subject);

                // Send email
                messageGateway = messageGatewayService.getGateway(HtmlEmail.class);
                messageGateway.send(email);
            } catch (Exception e) {
                log.error(ConstantsEmailSender.Properties.EXCEPTION_MESSAGE, e);
                response.setStatus(500);
            }
        } else {
            response.setStatus(500);
        }
    }

    private static String substituteEmail(String emailTamlate, Map<String, String> params) {
    	String result = "";
    	StrSubstitutor sub = new StrSubstitutor(params);
    	result = sub.replace(emailTamlate);
    	return result;

    }

    private static String[] splitAndTrim(String emails) {
    	ArrayList<String> result = new ArrayList<String>();
    	if( emails != null && !emails.isEmpty() && emails.trim().length()>0) {
    		String[] splits = emails.split(",");
    		for(String emailAddr : splits ) {
    			if(emailAddr.trim().length()>0) {
    	   			result.add(emailAddr.trim());
    			}
    		}
    	}
    	
    	return result.toArray(new String[result.size()]);
    }
}
