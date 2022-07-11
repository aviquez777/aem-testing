package com.edc.edcweb.core.workflow.utils;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.jcr.RepositoryException;
import javax.mail.MessagingException;

import org.apache.commons.mail.EmailException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.settings.SlingSettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowData;
import com.edc.edcweb.core.service.EDCEMailService;

/**
 * : Custom WorkflowProcess to Send custom emails
 *
 * Note the Arguments from Step are expected as a simple comma delimited string:
 * i.e. sendToGroupId,emailSubject,template
 * 
 * sendToGroupId: aem group id i.e. "edc-self-publishing-authors" emailSubject:
 * Edits have been made on -title-of-page- Very Important "-title-of-page-" will
 * be replaced by the actual page title. template: fully qualified template i.e.
 * "/conf/global/settings/workflow/notification/email/edc/ready-for-review/en.txt"
 * 
 **/

public class EventNotifications {

    private EventNotifications() {
        // SonarLInt
    }

    // Default log.
    static final Logger log = LoggerFactory.getLogger(EventNotifications.class);

    public static void sendNotifications(WorkItem workItem, WorkflowSession workflowSession,
            Map<String, String> metadataArgs, String emailFrom, SlingSettingsService slingSettingsService,
            EDCEMailService emailService) {
        try {
            ResourceResolver resourceResolver = workflowSession.adaptTo(ResourceResolver.class);
            if (resourceResolver != null) {
                // Get the work flow data
                WorkflowData workflowData = workItem.getWorkflowData();
                // Get the list
                List<String> sendToList = getSendToList(workItem, metadataArgs, resourceResolver);
                // Remove any empty items on list
                sendToList.removeAll(Arrays.asList("", null));
                // if the send to is not empty keep going
                if (!sendToList.isEmpty()) {
                    // Create a MAP to populate the email template variables
                    Map<String, String> templateTokens = WorkflowEmailUtils.getTemplateTokens(workflowData,
                            emailFrom, resourceResolver, slingSettingsService);
                    // Add the user and Message if any
                    Map<String, String> messageData = WorkflowUtils.getMessageData(workItem, workflowSession,
                            resourceResolver);
                    // Send the emails
                    WorkflowEmailUtils.sendEmails(sendToList, metadataArgs, messageData, templateTokens,
                            resourceResolver, emailService);
                } else {
                    // If the send to is empty no point on keep going
                    log.debug("FYINotifications List empty for user or sendToList {}", sendToList);
                }
            }
        } catch (EmailException | IOException | MessagingException e) {
            log.error("FYINotifications Big Boo Boo {}", e.getStackTrace());
        }
    }

    /**
     * getSendToList get the group or user to send the email to send the email to
     * the Initiator must use "initiator" as userGroupId
     * 
     * @param workItem
     * @param metadataArgs
     * @param resourceResolver
     * @return email list, empty otherwise
     */
    private static List<String> getSendToList(WorkItem workItem, Map<String, String> metadataArgs,
            ResourceResolver resourceResolver) {
        List<String> sendToList = new LinkedList<>();
        String userGroupId = metadataArgs.get(WorkflowConstants.ARGS_USER_GROUP_ID_KEY);
        try {
            // Check to whom we have to send it to
            if (WorkflowConstants.INITIAL_AUTHOR.equals(userGroupId)) {
                // If we have to contact the Initial Author
                Map<String, String> sendUserInfo = WorkflowParticipantUtils
                        .getUserInfo(workItem.getWorkflow().getInitiator(), resourceResolver);
                // Add to the sendToList
                sendToList.add(sendUserInfo.get(WorkflowConstants.EMAIL));
            } else {
                // Get the participants email from the group and add to the sendToList
                sendToList = WorkflowParticipantUtils.getGroupEmails(userGroupId, resourceResolver);
            }
        } catch (RepositoryException e) {
            log.error("EventNotifications getSendToList error {}", e.getStackTrace());
        }
        return sendToList;
    }
}
