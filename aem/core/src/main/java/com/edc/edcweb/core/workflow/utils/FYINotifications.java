package com.edc.edcweb.core.workflow.utils;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.jcr.RepositoryException;
import javax.mail.MessagingException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.EmailException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.settings.SlingSettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.HistoryItem;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowData;
import com.edc.edcweb.core.service.EDCEMailService;

/**
 * WorkflowEventNotifications: Custom WorkflowProcess to Send custom emails
 * 
 **/

public class FYINotifications {

    private FYINotifications() {
        // SonarLint
    }

    // Default log.
    static final Logger log = LoggerFactory.getLogger(FYINotifications.class);

    public static void sendNotifications(WorkItem workItem, WorkflowSession workflowSession,
            Map<String, String> metadataArgs, String emailFrom, SlingSettingsService slingSettingsService,
            EDCEMailService emailService) {
        ResourceResolver resourceResolver = workflowSession.adaptTo(ResourceResolver.class);
        try {
            if (resourceResolver != null) {
                // Get the work flow data
                WorkflowData workflowData = workItem.getWorkflowData();
                // Get the history from current work flow
                List<HistoryItem> historyList = workflowSession.getHistory(workItem.getWorkflow());
                // Check for others involved
                List<String> sendToList = getSendToList(historyList, resourceResolver, metadataArgs);
                // Get the Initial Author
                Map<String, String> initiatorUserInfo = WorkflowParticipantUtils
                        .getUserInfo(workItem.getWorkflow().getInitiator(), resourceResolver);
                // Add to the initiator to the sendToList if not already
                String initiator = initiatorUserInfo.get(WorkflowConstants.EMAIL);
                if (!sendToList.contains(initiator)) {
                    sendToList.add(initiator);
                }
                // Remove any empty items on list
                sendToList.removeAll(Arrays.asList("", null));
                // if the send to is not empty keep going
                if (!sendToList.isEmpty()) {
                    // Create a MAP to populate the email template variables
                    Map<String, String> templateTokens = WorkflowEmailUtils.getTemplateTokens(workflowData, emailFrom,
                            resourceResolver, slingSettingsService);
                    // Add the user and Message if any
                    Map<String, String> messageData = WorkflowUtils.getMessageData(workItem, workflowSession,
                            resourceResolver);
                    // Check if we have an extra messge to use
                    String extraMessage = metadataArgs.get(WorkflowConstants.HISTORY_EXTRA_MESSAGE_KEY);
                    if (StringUtils.isNotBlank(extraMessage)) {
                        messageData.put(WorkflowConstants.HISTORY_MESSAGE_KEY, extraMessage);
                    }
                    // Send the emails
                    WorkflowEmailUtils.sendEmails(sendToList, metadataArgs, messageData, templateTokens,
                            resourceResolver, emailService);
                } else {
                    // If the send to is empty no point on keep going
                    log.debug("FYINotifications List empty for user or sendToList {}", sendToList);
                }
            }
        } catch (WorkflowException | RepositoryException | EmailException | IOException | MessagingException e) {
            log.debug("FYINotifications Big Boo Boo {}", e.getStackTrace());
        }
    }

    /**
     * getSendToList checks the history for any step that contains "Takes Ownership"
     * and assign that email to the list
     * 
     * @param workItem
     * @param metadataArgs
     * @param resourceResolver
     * @return email list, empty otherwise
     */
    private static List<String> getSendToList(List<HistoryItem> historyList, ResourceResolver resourceResolver, Map<String, String> metadataArgs) {
        // Get the participants email from the group and add to the sendToList if present
        String userGroupId = metadataArgs.get(WorkflowConstants.ARGS_USER_GROUP_ID_KEY);
        List<String> sendToList = getGroupEmails(userGroupId, resourceResolver);
        for (HistoryItem thisItem : historyList) {
            Resource res = resourceResolver.getResource(thisItem.getWorkItem().getId());
            if (res != null) {
                ValueMap vm = res.getValueMap();
                String stepTitle = vm.get(WorkflowConstants.HISTORY_STEP_TITLE, WorkflowConstants.EMPTY_STRING);
                if (stepTitle.contains(WorkflowConstants.HISTORY_TAKES_OWNERSHIP)) {
                    try {
                        Map<String, String> ownerUserInfo = WorkflowParticipantUtils
                                .getUserInfo(thisItem.getWorkItem().getCurrentAssignee(), resourceResolver);
                        String mailTo = ownerUserInfo.get(WorkflowConstants.EMAIL);
                        // Add to the sendToList only if not there
                        if (!sendToList.contains(mailTo)) {
                            sendToList.add(mailTo);
                        }
                    } catch (RepositoryException e) {
                        log.error("FYINotifications error on history item {} error {}", thisItem, e.getStackTrace());
                    }
                }
            }
        }
        return sendToList;
    }

    /**
     * getGroupEmails get the Group Emails and add them to the list
     * @param userGroupId
     * @param resourceResolver
     * @return emailsList
     */
    private static List<String>  getGroupEmails(String userGroupId, ResourceResolver resourceResolver) {
        List<String> emailsList = new LinkedList<>();
        if (StringUtils.isNotEmpty(userGroupId) && !WorkflowConstants.ADMIN_USER.equalsIgnoreCase(userGroupId)) {
            try {
                emailsList = WorkflowParticipantUtils.getGroupEmails(userGroupId, resourceResolver);
            } catch (RepositoryException e) {
                log.error("FYINotifications getGroupEmails error {}", e.getStackTrace());
            }
        }
        return emailsList;
    }
}
