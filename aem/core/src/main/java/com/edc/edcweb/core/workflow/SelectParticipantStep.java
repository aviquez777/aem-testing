package com.edc.edcweb.core.workflow;

import java.util.Map;

import javax.jcr.RepositoryException;

import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.settings.SlingSettingsService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.ParticipantStepChooser;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.edc.edcweb.core.service.EDCEMailService;
import com.edc.edcweb.core.service.EDCSystemConfigurationService;
import com.edc.edcweb.core.workflow.utils.EventNotifications;
import com.edc.edcweb.core.workflow.utils.FYINotifications;
import com.edc.edcweb.core.workflow.utils.WorkflowConstants;
import com.edc.edcweb.core.workflow.utils.WorkflowParticipantUtils;
import com.edc.edcweb.core.workflow.utils.WorkflowUtils;

/**
 * 
 * Return user or group if present as argument and valid, otherwise returns the
 * current user
 *
 */
@Component(service = ParticipantStepChooser.class, property = { "chooser.label=EDC: Workflow Participant Step" })
public class SelectParticipantStep implements ParticipantStepChooser {

    protected final Logger log = LoggerFactory.getLogger(SelectParticipantStep.class);

    // Inject a MessageGatewayService
    @Reference
    private EDCEMailService emailService;

    // Required to resolve Server Name on Emails
    @Reference
    private SlingSettingsService slingSettingsService;

    // Required for the email from
    @Reference
    private EDCSystemConfigurationService edcSystemConfigurationService;

    @Override
    public String getParticipant(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap metaDataMap)
            throws WorkflowException {
        ResourceResolver resourceResolver = workflowSession.adaptTo(ResourceResolver.class);
        // Set the Metadata provided arguments, check the WorkflowConstants for key
        // names ARGS_*
        Map<String, String> metadataArgs = WorkflowUtils.getMetadaArgForNotifications(metaDataMap, resourceResolver);
        // Set variables for later use
        String userId;
        String sessionUserId = WorkflowParticipantUtils.getCurrentUser(resourceResolver);
        boolean isValid = false;
        // Get this Variables from argument
        String emailType = metadataArgs.get(WorkflowConstants.ARGS_EMAIL_KEY);
        String idFromArgument = metadataArgs.get(WorkflowConstants.ARGS_USER_GROUP_ID_KEY);
        // check if there's an argument
        if (StringUtils.isNotBlank(idFromArgument)) {
            // check if Group Exists
            try {
                Authorizable checkAuth = WorkflowParticipantUtils.getAuthorizable(idFromArgument, resourceResolver);
                // if there;s an authorizable is not null, is a valid user or group
                isValid = (checkAuth != null);
            } catch (RepositoryException e) {
                log.error("SelectParticipantStep getParticipant nvalid group {}", idFromArgument);
            }
        }
        if (isValid) {
            // Use provided id
            userId = idFromArgument;
        } else {
            // Use current user
            userId = sessionUserId;
        }
        String emailFrom = edcSystemConfigurationService.getEmailFrom();
        // Send the Emails
        if (WorkflowConstants.EMAIL_TYPE_FYI.equalsIgnoreCase(emailType)) {
            FYINotifications.sendNotifications(workItem, workflowSession, metadataArgs, emailFrom, slingSettingsService,
                    emailService);
        } else if (WorkflowConstants.EMAIL_TYPE_NOTIFICATION.equalsIgnoreCase(emailType)) {
            EventNotifications.sendNotifications(workItem, workflowSession, metadataArgs, emailFrom, slingSettingsService,
                    emailService);
        }
        return userId;
    }

}