package com.edc.edcweb.core.servlets;

import javax.servlet.Servlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.mailer.MessageGatewayService;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.policies.ContentPolicy;
import com.edc.edcweb.core.helpers.ContentPolicyUtil;
import com.edc.edcweb.core.helpers.constants.ConstantsInList;
import com.edc.edcweb.core.helpers.Request;
import com.edc.edcweb.core.helpers.ResourceResolverHelper;
import com.edc.edcweb.core.helpers.constants.ConstantsEmailSender;
import com.edc.edcweb.core.helpers.email.EmailSender;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.api.wrappers.ValueMapDecorator;

@SuppressWarnings({"CQRules:CQBP-75", "squid:S1948"})
@Component(immediate = true, service = Servlet.class, property = {
    "sling.servlet.extensions=html",
    "sling.servlet.methods=post",
    "sling.servlet.paths=" + ConstantsInList.Paths.FEEDBACK_FORM_SERVLET})

public class FeedbackFormServlet extends SlingAllMethodsServlet {

    // Inject a MessageGatewayService
    @Reference
    private MessageGatewayService messageGatewayService;

    private static final Logger log = LoggerFactory.getLogger(FeedbackFormServlet.class);

    @Override
    protected final void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        Resource currPage = Request.getCurrentPageResource(request, null);
        Page currentPage = currPage.adaptTo(Page.class);
        Resource feedbackFormRsc = ResourceResolverHelper.getResourceByType(currentPage, ConstantsInList.Paths.FEEDBACK_FORM_TYPE);
        ValueMap propertiesValues = feedbackFormRsc.getValueMap();
        ValueMap propertiesPolicy = new ValueMapDecorator(new HashMap<>());
        ContentPolicy policy = ContentPolicyUtil.resolveLocalizedContentPolicy(request.getResourceResolver(), feedbackFormRsc, currentPage);

        if (policy != null) {
            propertiesPolicy = policy.getProperties();
        }

        String feedback = request.getParameter(ConstantsInList.InListProperties.FEEDBACK_FORM_COMMENT);
        String emailCc = request.getParameter(ConstantsInList.InListProperties.FEEDBACK_FORM_EMAIL_ADDRESS);
        String msg = "Feedback: " + feedback + "\n";

        if (StringUtils.isNotEmpty(emailCc)) {
            msg += "Email: " + emailCc + "\n";
        }

        Map<String, Object> emailValues = new HashMap<>();
        emailValues.put(ConstantsEmailSender.Properties.EMAIL_TO,
                getCorrectValue(propertiesValues.get(ConstantsInList.InListProperties.FEEDBACK_FORM_EMAIL, String.class), propertiesPolicy.get(ConstantsInList.InListProperties.FEEDBACK_FORM_EMAIL, String.class)));
        emailValues.put(ConstantsEmailSender.Properties.EMAIL_CC, emailCc);
        emailValues.put(ConstantsEmailSender.Properties.SUBJECT,
                getCorrectValue(propertiesValues.get(ConstantsInList.InListProperties.FEEDBACK_FORM_SUBJECT, String.class), propertiesPolicy.get(ConstantsInList.InListProperties.FEEDBACK_FORM_SUBJECT, String.class)));
        emailValues.put(ConstantsEmailSender.Properties.MESSAGE, msg);

        EmailSender.sendSimpleEmail(messageGatewayService, response, emailValues);
    }

    /**
     * Return property value if it was set, otherway return policy value
     * @param property
     * @param policy
     * @return String with correct value
     */
    private String getCorrectValue(String property, String policy) {
        if (StringUtils.isNotEmpty(property)) {
            return property;
        } else {
            return policy;
        }
    }
}
