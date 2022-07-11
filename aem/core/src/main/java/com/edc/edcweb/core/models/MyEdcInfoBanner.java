package com.edc.edcweb.core.models;

import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.WCMMode;
import com.edc.edcweb.core.helpers.constants.ConstantsMyEdc;

/**
 * <h1>MyEdcInfoBanner</h1> The MyEdcInfoBanner class check It checks the
 * Infobanner's Component exists and makes sure so no error appears when the in
 * place editor starts the first time
 * 
 * Also check if the nodes have been configured
 */
@Model(adaptables = SlingHttpServletRequest.class)
public class MyEdcInfoBanner {

    @Self
    private SlingHttpServletRequest request;

    @Self
    @Via("resource")
    private Resource resource;

    @SlingObject
    private ResourceResolver resourceResolver;

    @Inject
    private Page currentPage;

    private Map<String, String> resources;

    boolean displayContainer = false;

    /**
     * This is the main method. It checks the Infobanner's Component exists and
     * makes sure the two RTE component node's
     * 
     * Also sets the Boolean configured variable to check if the component has been
     * configured or not
     * 
     * @param none
     *            all are injected or derived from the context
     * @return Nothing.
     * @exception RepositoryException
     *                on error.
     * @see RepositoryException
     */
    @PostConstruct
    public void initModel() throws RepositoryException {
        // make sure we have the components to avoid "on activation" errors
        Session session = resourceResolver.adaptTo(Session.class);
        Node currentNode = resource.adaptTo(Node.class);
        // Make sure qe have the contact profile node, should't happen, but just in case
        if (currentNode == null) {
            Node rootNode = currentPage.getContentResource().adaptTo(Node.class);
            currentNode = rootNode.addNode(ConstantsMyEdc.NodeNames.INFO_BANNER_NODE_NAME,
                    ConstantsMyEdc.Properties.NT_UNSTRUCTURED);
            currentNode.setProperty(ConstantsMyEdc.Properties.SLING_COLON_RESOURCE_TYPE,
                    ConstantsMyEdc.ResourceTypes.INFO_BANNER_RESOURCE_TYPE);
            // save nodes
            session.save();
        }
        // check for children, add them if author deleted any
        resources = ConstantsMyEdc.resources();
        for (Map.Entry<String, String> entry : resources.entrySet()) {
            Node node = null;
            String nodeName = entry.getKey();
            String resType = entry.getValue();
            //check if node exists since it can be deleted now
            if (currentNode.hasNode(nodeName)) {
                node = currentNode.getNode(nodeName);
            } else {
                // does not exist, create it
                node = currentNode.addNode(nodeName, ConstantsMyEdc.Properties.NT_UNSTRUCTURED);
            }
            //check for resource type property and add it if necessary
            if (!(node.hasProperty(ConstantsMyEdc.Properties.SLING_COLON_RESOURCE_TYPE) &&
                    node.getProperty(ConstantsMyEdc.Properties.SLING_COLON_RESOURCE_TYPE).getValue()
                    .getString().equals(resType))) {
                node.setProperty(ConstantsMyEdc.Properties.SLING_COLON_RESOURCE_TYPE, resType);
                // if the nodes is RTE, set the flag
                if (resType.endsWith("text")) {
                    node.setProperty("textIsRich", "true");
                }
                // save node
                session.save();
            }
            // also check if we need to display the Container on publish
            if (node != null && node.hasProperty("text")
                    && node.getProperty("text").getValue().getString().trim().length() > 0) {
                displayContainer = true;
            }
        }

    }

    /**
     * This method returns if there's at least one RTE available to display when no
     * wcmmode
     * 
     * @param none
     * @return Boolean displayContainer. True if any of the components are
     *         configured, False otherwise
     * 
     */
    public Boolean getDisplayContainer() {
        // check for WCMMode
        WCMMode wcmMode = WCMMode.fromRequest(request);
        // always show on edit and preview
        if ((wcmMode.equals(WCMMode.EDIT) || wcmMode.equals(WCMMode.PREVIEW))) {
            displayContainer = true;
        }
        return displayContainer;
    }

    /**
     * This is the main method which makes use of addNum method.
     * 
     * @param none
     * @return Map<String, String> resources for the resources to be included on the
     *         htl's data-sly-resource build. So the resource types will be
     *         consistent.
     * 
     */
    public Map<String, String> getResources() {
        // if not has been declared yet, read it from the Contstants
        if (resources == null) {
            resources = ConstantsMyEdc.resources();
        }
        return resources;
    }
}
