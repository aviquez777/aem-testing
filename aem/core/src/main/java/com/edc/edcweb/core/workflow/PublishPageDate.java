package com.edc.edcweb.core.workflow;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.settings.SlingSettingsService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.HistoryItem;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.edc.edcweb.core.service.EDCEMailService;
import com.edc.edcweb.core.service.EDCSystemConfigurationService;
import com.edc.edcweb.core.workflow.utils.FYINotifications;
import com.edc.edcweb.core.workflow.utils.WorkflowConstants;
import com.edc.edcweb.core.workflow.utils.WorkflowUtils;

/**
 * <h1>PublishPageDate</h1> Custom WorkflowProcess to Set the Date for publish
 * later step
 **/

@Component(service = WorkflowProcess.class, property = { "process.label=EDC: Get the Publish Page Date" })
public class PublishPageDate implements WorkflowProcess {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * Method execute Get the Date entered on previous step and set the absoluteTime
     * property on the meteData for Processing
     * Important Participant's Step Timeout Handler must be "Absolute Time Auto Advancer"
     * 
     */
    
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
    public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap metaDataMap)
            throws WorkflowException {
        ResourceResolver resourceResolver = workflowSession.adaptTo(ResourceResolver.class);
        // Set the Metadata provided arguments, check the WorkflowConstants for key
        // names ARGS_*
        Map<String, String> metadataArgs = WorkflowUtils.getMetadaArgForNotifications(metaDataMap, resourceResolver);
        String emailType = metadataArgs.get(WorkflowConstants.ARGS_EMAIL_KEY);
        // Declare the variable
        String activateTimeString = null;
        // Set current time as default
        ZonedDateTime activeTime = ZonedDateTime.now();
        // Get the current work flow
        List<HistoryItem> historyList = workflowSession.getHistory(workItem.getWorkflow());
        int listSize = historyList.size();
        // Get previous step to get the dialog's value
        HistoryItem lastItem = historyList.get(listSize - 1);
        // Check if the value exists
        if (lastItem.getWorkItem().getMetaDataMap().containsKey(WorkflowConstants.PUBLILSH_DATE_FIELD_NAME)) {
            activateTimeString = lastItem.getWorkItem().getMetaDataMap().get(WorkflowConstants.PUBLILSH_DATE_FIELD_NAME, String.class);
            // if we have a date time, use it
            if (activateTimeString != null && !activateTimeString.isEmpty()) {
                activeTime = ZonedDateTime.parse(activateTimeString, DateTimeFormatter.ISO_DATE_TIME);
            }
        }
        // Get milliseconds
        Long milisec = activeTime.toInstant().toEpochMilli();
        // Convert milliseconds to string
        String millsectext = milisec.toString();
        // Save to work flow's Metadata
        workItem.getWorkflowData().getMetaDataMap().put(com.adobe.granite.workflow.job.AbsoluteTimeoutHandler.ABS_TIME,
                millsectext);
        // Get comments if any
        String comment = workItem.getWorkflowData().getMetaDataMap().get(WorkflowConstants.COMMENT, "");
        // Format the date to look pretty
        DateTimeFormatter format = DateTimeFormatter.RFC_1123_DATE_TIME;
        // Parse the date
        String formatedDate = format.format(activeTime);
        // Add the date to the comments string
        comment = comment.concat(WorkflowConstants.ACTIVATE_TIME).concat(formatedDate);
        // Add the date to the Work flow comments
        workItem.getWorkflowData().getMetaDataMap().put(WorkflowConstants.COMMENT, comment.trim());
        // Add the message to the 
        metadataArgs.put(WorkflowConstants.HISTORY_EXTRA_MESSAGE_KEY, comment);
        // Log the date
        log.debug("Dialog Time String: {}, Miliseconds String {}", formatedDate, millsectext);
        // Send the Emails
        if (WorkflowConstants.EMAIL_TYPE_FYI.equals(emailType)) {
            String emailFrom = edcSystemConfigurationService.getEmailFrom();
            FYINotifications.sendNotifications(workItem, workflowSession, metadataArgs, emailFrom, slingSettingsService,
                    emailService);
        }
    }

}
