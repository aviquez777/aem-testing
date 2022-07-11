package com.edc.edcweb.core.workflow;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.settings.SlingSettingsService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowData;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.DamConstants;
import com.day.cq.dam.commons.util.AssetReferenceSearch;
import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationException;
import com.day.cq.replication.ReplicationOptions;
import com.day.cq.replication.ReplicationStatus;
import com.day.cq.replication.Replicator;
import com.day.cq.tagging.Tag;
import com.day.cq.wcm.api.Page;
import com.day.crx.JcrConstants;
import com.edc.edcweb.core.service.EDCEMailService;
import com.edc.edcweb.core.service.EDCSystemConfigurationService;
import com.edc.edcweb.core.workflow.utils.FYINotifications;
import com.edc.edcweb.core.workflow.utils.WorkflowConstants;
import com.edc.edcweb.core.workflow.utils.WorkflowUtils;

/**
 * <h1>PublishPageAndAssets</h1> Custom WorkflowProcess to Publish Assets and
 * Tags when alongside with the page
 **/

@Component(service = WorkflowProcess.class, property = { "process.label=EDC: Publish Page and Assets" })
public class PublishPageAndAssets implements WorkflowProcess {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    @Reference
    private Replicator replicator;

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    // Inject a MessageGatewayService
    @Reference
    private EDCEMailService emailService;

    // Required to resolve Server Name on Emails
    @Reference
    private SlingSettingsService slingSettingsService;

    // Required for the email from
    @Reference
    private EDCSystemConfigurationService edcSystemConfigurationService;

    private List<String> foundPathList;
    private List<String> replicatePathList;

    /**
     * Method execute Get the Assets and tags from the resource and activate them
     * for replication.
     * 
     */
    @Override
    public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap metaDataMap)
            throws WorkflowException {
        // Inject a ResourceResolver - make sure to whitelist the bundle
        Session session = workflowSession.adaptTo(Session.class);
        ResourceResolver resourceResolver = null;
        // Clear the lists
        foundPathList = new ArrayList<>();
        replicatePathList = new ArrayList<>();
        try {
            resourceResolver = resourceResolverFactory
                    .getResourceResolver(Collections.singletonMap("user.jcr.session", (Object) session));
            // Set the Metadata provided arguments, check the WorkflowConstants for key
            // names ARGS_*
            Map<String, String> metadataArgs = WorkflowUtils.getMetadaArgForNotifications(metaDataMap,
                    resourceResolver);
            String emailType = metadataArgs.get(WorkflowConstants.ARGS_EMAIL_KEY);
            WorkflowData workflowData = workItem.getWorkflowData();
            String currentpagePath = workflowData.getPayload().toString();
            log.debug("EDC: Publish Page and Assets Payload path: {} ", currentpagePath);
            // Getting Asset References via Resource
            Resource resource = resourceResolver.getResource(currentpagePath);
            // If node exists, go for it
            if (null != resource) {
                // Adapt to node for replication
                Node resourceNode = resource.adaptTo(Node.class);
                // Replicate Page
                replicateNode(resourceNode, session);
                // Check for Assets
                checkForAssets(resourceNode, resourceResolver, session);
                // Check for Tags
                checkForTags(resource, session);
            } else {
                log.debug(
                        "EDC: Publish Page and Assets getAssetFromPaylod: asset {} in payload of workflow {} does not exist.",
                        currentpagePath, workItem.getWorkflow().getId());
            }
            // Debug the total found list
            if (!foundPathList.isEmpty()) {
                String pathList = String.join(", ", foundPathList);
                log.debug("EDC: Publish Page and Assets Total paths found {}.", pathList);
            }
            // add a list of assets included, if any
            if (!replicatePathList.isEmpty()) {
                String comment = workItem.getWorkflowData().getMetaDataMap().get("comment", "");
                String pathList = String.join(", ", replicatePathList);
                comment += " *Assests Included:* " + pathList;
                workItem.getWorkflowData().getMetaDataMap().put("comment", comment.trim());
                // Add the message to the
                metadataArgs.put(WorkflowConstants.HISTORY_EXTRA_MESSAGE_KEY, comment);
                // Send the Emails
                if (WorkflowConstants.EMAIL_TYPE_FYI.equals(emailType)) {
                    String emailFrom = edcSystemConfigurationService.getEmailFrom();
                    FYINotifications.sendNotifications(workItem, workflowSession, metadataArgs, emailFrom,
                            slingSettingsService, emailService);
                }
                log.debug("EDC: Publish Page and Assets Replicated paths {}.", pathList);
            } else {
                log.debug("EDC: Publish Page and Assets No items to activate on page {}.", currentpagePath);
            }
        } catch (LoginException e1) {
            log.error("PublishPageAndAssets LoginException", e1);
        } finally {
            if (resourceResolver != null) {
                resourceResolver.close();
            }
        }
    }

    /**
     * checkForAssets Check if there are Assets that need to be activated
     * 
     * @param resourceNode
     * @param resourceResolver
     */
    private void checkForAssets(Node resourceNode, ResourceResolver resourceResolver, Session session) {
        AssetReferenceSearch assetRefSearch = new AssetReferenceSearch(resourceNode, DamConstants.MOUNTPOINT_ASSETS,
                resourceResolver);
        Map<String, Asset> allAssetReference = assetRefSearch.search();
        // Loop Over the assets and set the replication accordingly
        for (Map.Entry<String, Asset> entry : allAssetReference.entrySet()) {
            Asset asset = entry.getValue();
            Node assetNode = asset.adaptTo(Node.class);
            try {
                checkNodeStatusAndReplicate(assetNode, session);
            } catch (RepositoryException e) {
                log.error("EDC: Publish Page and Assets checkForAssets error", e);
            }
        }
    }

    /**
     * checkForTags Check if there are tags that need to be activated
     * 
     * @param resource
     */
    private void checkForTags(Resource resource, Session session) {
        Page page = resource.adaptTo(Page.class);
        if (page != null) {
            // Get Page Tags
            Tag[] pageTags = page.getTags();
            // Loop over the tags and check replication status
            for (Tag tag : pageTags) {
                Node tagNode = tag.adaptTo(Node.class);
                if (tagNode != null) {
                    try {
                        checkNodeStatusAndReplicate(tagNode, session);
                    } catch (RepositoryException e) {
                        log.error("EDC: Publish Page and Assets checkForTags {}", e.getMessage());
                    }
                }
            }
        }
    }

    /**
     * checkReplicationStatus Check if the asset needs to be replicated, and sets is
     * accordingly
     * 
     * @param node
     * 
     * @return nothing
     * @throws RepositoryException
     * @throws ReplicationException
     */
    private void checkNodeStatusAndReplicate(Node node, Session session)
            throws RepositoryException {
        // Check if the asset/image is already replicated
        if (node.hasProperty(ReplicationStatus.NODE_PROPERTY_LAST_REPLICATION_ACTION)
                && (!node.getProperty(ReplicationStatus.NODE_PROPERTY_LAST_REPLICATION_ACTION).getValue().getString()
                        .equalsIgnoreCase(ReplicationActionType.ACTIVATE.getName()))) {
            replicateNode(node, session);
        }
        // Check if the asset/image has been modified after being replicated
        else if (node.hasProperty(ReplicationStatus.NODE_PROPERTY_LAST_REPLICATED)
                && node.hasProperty(JcrConstants.JCR_LASTMODIFIED)) {
            Calendar lastReplicated = node.getProperty(ReplicationStatus.NODE_PROPERTY_LAST_REPLICATED).getValue()
                    .getDate();
            Calendar lastModified = node.getProperty(JcrConstants.JCR_LASTMODIFIED).getValue().getDate();
            if (lastReplicated.compareTo(lastModified) < 0) {
                replicateNode(node, session);
            }
        } else {
            replicateNode(node, session);
        }
        // add to the total found list
        foundPathList.add(node.getPath());
    }

    /**
     * @param nodeToReplicate Activates the replication property on the node
     * 
     * @return
     * @throws ReplicationException, RepositoryException
     */
    private void replicateNode(Node nodeToReplicate, Session session) {
        // Create leanest replication options for activation
        ReplicationOptions options = new ReplicationOptions();
        // Do not create new versions as this adds to overhead
        options.setSuppressVersions(true);
        // Avoid sling job overhead by forcing synchronous. Note this will result in
        // serial activation.
        options.setSynchronous(true);
        // Do NOT suppress status update of resource (set replication properties
        // accordingly)
        options.setSuppressStatusUpdate(false);
        try {
            String nodePath = nodeToReplicate.getPath();
            replicator.replicate(session, ReplicationActionType.ACTIVATE, nodePath);
            replicatePathList.add(nodePath);
            log.info("PublishPageAndAssets successfully replicated {}", nodePath);
        } catch (ReplicationException | RepositoryException e) {
            log.error("PublishPageAndAssets replicateNode error ", e);
        }
    }

}
