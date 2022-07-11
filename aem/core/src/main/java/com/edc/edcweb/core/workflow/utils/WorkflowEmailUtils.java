package com.edc.edcweb.core.workflow.utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.mail.EmailException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.settings.SlingSettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.workflow.exec.WorkflowData;
import com.day.cq.commons.Externalizer;
import com.day.cq.wcm.api.Page;
import com.edc.edcweb.core.helpers.Constants;
import com.edc.edcweb.core.helpers.ServerNameResolverHelper;
import com.edc.edcweb.core.helpers.constants.ConstantsEmailSender;
import com.edc.edcweb.core.service.EDCEMailService;

public class WorkflowEmailUtils {

    static final Logger log = LoggerFactory.getLogger(WorkflowEmailUtils.class);

    private WorkflowEmailUtils() {
        // Sonar Lint
    }

    /**
     * getTemplateTokens create the template tokens (page and workflow data) to show
     * on the email
     * 
     * @param workflowData
     * @param resourceResolver
     * @return array with template data
     */
    public static Map<String, String> getTemplateTokens(WorkflowData workflowData, String emailFrom,
            ResourceResolver resourceResolver, SlingSettingsService slingSettingsService) {
        Map<String, String> pageVars = new HashMap<>();
        pageVars.put(ConstantsEmailSender.Properties.EMAIL_FROM, emailFrom);
        // Get Current Page and set page
        String pagePath = workflowData.getPayload().toString();
        Resource resource = resourceResolver.getResource(pagePath);
        if (resource != null) {
            Page page = resource.adaptTo(Page.class);
            // Getting Asset References via Resource
            if (page != null) {
                pageVars.put(WorkflowConstants.TEMPLATE_VAR_PAGE_TITLE, page.getTitle());
                Externalizer externalizer = resourceResolver.adaptTo(Externalizer.class);
                if (externalizer != null) {
                    // get workflow title from the workflow data, if not present use default
                    String workflowTitle = workflowData.getMetaDataMap()
                            .get(WorkflowConstants.TEMPLATE_VAR_WORKFLOW_TITLE, WorkflowConstants.EMPTY_STRING);
                    if (StringUtils.isBlank(workflowTitle)) {
                        workflowTitle = WorkflowConstants.TEMPLATE_VAR_DEFAULT_WORKFLOW_TITLE;
                    }
                    // add it to the pageVars
                    pageVars.put(WorkflowConstants.TEMPLATE_VAR_WORKFLOW_TITLE, workflowTitle);
                    // create the author url
                    String authorUrl = externalizer.authorLink(resourceResolver, page.getPath())
                            .concat(Constants.HTML_EXTENTION);
                    // create the publish url
                    String publishedUrl = externalizer.publishLink(resourceResolver, page.getPath())
                            .concat(Constants.HTML_EXTENTION);
                    pageVars.putAll(getWorkflowPageVarsLinks(authorUrl, publishedUrl, slingSettingsService));
                }
            }
        }
        return pageVars;
    }

    /**
     * 
     * @param authorUrl
     * @param publishedUrl
     * @param slingSettingsService
     * @return
     */
    public static Map<String, String> getWorkflowPageVarsLinks(String authorUrl, String publishedUrl,
            SlingSettingsService slingSettingsService) {
        Map<String, String> pageVars = new HashMap<>();
        // Get the server names
        Map<String, String> serverNames = ServerNameResolverHelper.getServerNames(slingSettingsService);
        // Empty means we are on localhost
        if (!serverNames.isEmpty()) {
            // Fix author
            String oldAuthor = ServerNameResolverHelper.getServerName(authorUrl);
            String newAuthor = serverNames.get("author");
            authorUrl = StringUtils.replace(authorUrl, oldAuthor, newAuthor);
            // Fix Publish
            String oldPublish = ServerNameResolverHelper.getServerName(publishedUrl);
            String newPublish = serverNames.get("dispatcher");
            publishedUrl = StringUtils.replace(publishedUrl, oldPublish, newPublish);
            // remove content/edc not for localost
            publishedUrl = StringUtils.replace(publishedUrl, Constants.Paths.CONTENT_EDC, "");
        }
        // Create the Edit url: add the editor.html to path
        String editUrl = StringUtils.replace(authorUrl, Constants.Paths.CONTENT_EDC, WorkflowConstants.AEM_EDITOR);
        pageVars.put(WorkflowConstants.TEMPLATE_VAR_AUTHOR_URL, editUrl);
        // Create the View As Published url: add the "?wcmmode=disabled" to the link
        String viewAsPublishedUrl = authorUrl.concat(WorkflowConstants.AEM_VIEW_AS_PUBLISHED_QS);
        pageVars.put(WorkflowConstants.TEMPLATE_VAR_VIEW_AS_PUBLISHED_URL, viewAsPublishedUrl);
     // Add the published Url
        pageVars.put(WorkflowConstants.TEMPLATE_VAR_PUBLISHED_URL, publishedUrl);
        return pageVars;
    }

    /**
     * 
     * @param sendToList
     * @param metadataArgs
     * @param previousMessageData
     * @param templateTokens
     * @param resourceResolver
     * @param emailService
     * @param slingSettingsService
     * @return
     * @throws IOException
     * @throws MessagingException
     * @throws EmailException
     */
    public static List<String> sendEmails(List<String> sendToList, Map<String, String> metadataArgs,
            Map<String, String> previousMessageData, Map<String, String> templateTokens,
            ResourceResolver resourceResolver, EDCEMailService emailService)
            throws IOException, MessagingException, EmailException {
        List<String> failed;
        if (!previousMessageData.isEmpty()) {
            String userFullName = previousMessageData.get(WorkflowConstants.HISTORY_USER_FULL_NAME_KEY);
            String message = previousMessageData.get(WorkflowConstants.HISTORY_MESSAGE_KEY);
            templateTokens.put(WorkflowConstants.TEMPLATE_VAR_USER, userFullName);
            templateTokens.put(WorkflowConstants.TEMPLATE_VAR_MESSAGE, message);
        }
        String pageTitle = templateTokens.get(WorkflowConstants.TEMPLATE_VAR_PAGE_TITLE);
        String emailSubject = metadataArgs.get(WorkflowConstants.ARGS_SUBJECT_KEY);
        String template = metadataArgs.get(WorkflowConstants.ARGS_TEMPLATE_KEY);
        // Dirty trick to have the subject as required, replace the defined string
        // placeholder with the actual page title
        String subject = StringUtils.replace(emailSubject, WorkflowConstants.EMAIL_SUBJECT_REPLACE_TITLE, pageTitle)
                .trim();
        // Populate the MAP with the values submitted from the HTL front end
        templateTokens.put(WorkflowConstants.TEMPLATE_VAR_SUBJECT, subject);

        // send the Email
        failed = emailService.sendEmail(sendToList, subject, template, templateTokens, resourceResolver);
        log.debug("WorkflowEmailUtils sendEmails Failed emails {}", failed);
        return failed;
    }

}
