package com.edc.edcweb.core.workflow;

import java.util.Map;

import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.settings.SlingSettingsService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.edc.edcweb.core.service.EDCEMailService;
import com.edc.edcweb.core.service.EDCSystemConfigurationService;
import com.edc.edcweb.core.workflow.utils.EventNotifications;
import com.edc.edcweb.core.workflow.utils.FYINotifications;
import com.edc.edcweb.core.workflow.utils.WorkflowConstants;
import com.edc.edcweb.core.workflow.utils.WorkflowUtils;

/**
 * WorkflowEventNotifications: Custom WorkflowProcess to Send custom emails
 *
 * Note the Arguments from Step are expected as comma delimited value pair i.e.
 * userGroupId=edc-self-publishing-proofreaders,
 * subject=PROOF-READ-title-of-page-,
 * template=/conf/global/settings/workflow/notification/email/edc/ready-for-proofread/en.txt,
 * emailType=notification
 * 
 **/

@Component(service = WorkflowProcess.class, property = { "process.label=EDC: Workflow Event Notifications" })
public class WorkflowEventNotifications implements WorkflowProcess {

    // Default log.
    protected final Logger log = LoggerFactory.getLogger(WorkflowEventNotifications.class);

    // Inject a MessageGatewayService
    @Reference
    private EDCEMailService emailService;

    // Required to resolve Server Name on Emails
    @Reference
    private SlingSettingsService slingSettingsService;

    // Required for the email from
    @Reference
    private EDCSystemConfigurationService edcSystemConfigurationService;

    public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap metaDataMap) {

        ResourceResolver resourceResolver = workflowSession.adaptTo(ResourceResolver.class);
        // Set the Metadata provided arguments, check the WorkflowConstants for key
        // names ARGS_*
        Map<String, String> metadataArgs = WorkflowUtils.getMetadaArgForNotifications(metaDataMap, resourceResolver);
        // Get the email type from argument
        String emailType = metadataArgs.get(WorkflowConstants.ARGS_EMAIL_KEY);
        String emailFrom = edcSystemConfigurationService.getEmailFrom();
        if (WorkflowConstants.EMAIL_TYPE_FYI.equals(emailType)) {
            FYINotifications.sendNotifications(workItem, workflowSession, metadataArgs, emailFrom, slingSettingsService,
                    emailService);
        } else {
            EventNotifications.sendNotifications(workItem, workflowSession, metadataArgs, emailFrom, slingSettingsService,
                    emailService);
        }

    }
}
