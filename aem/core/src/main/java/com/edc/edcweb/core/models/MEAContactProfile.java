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
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.policies.ContentPolicy;
import com.edc.edcweb.core.helpers.ContentPolicyUtil;
import com.edc.edcweb.core.helpers.constants.ConstantsMEA;

/**
 * The MEAContactProfile model retrieves the component's policy. Also ensures
 * the embedded RTE Nodes are already created and available for the author to
 * use and prevent errors
 */

@Model(adaptables = SlingHttpServletRequest.class)
public class MEAContactProfile {

    @Self
    private SlingHttpServletRequest request;

    @Self
    @Via("resource")
    private Resource resource;

    @SlingObject
    private ResourceResolver resourceResolver;

    @Inject
    private Page currentPage;

    private String est;
    private String hour;
    private Map<String, String> resources;

    @PostConstruct
    public void initModel() throws RepositoryException {
        ContentPolicy contentPolicy = ContentPolicyUtil.resolveLocalizedContentPolicy(request, currentPage);
        if (contentPolicy != null) {
            ValueMap properties = contentPolicy.getProperties();
            est = properties.get(ConstantsMEA.ProfileLabels.EST, String.class);
            hour = properties.get(ConstantsMEA.ProfileLabels.HOURS, String.class);
        }
        // make sure we have the components to avoid "on activation" errors
        Session session = resourceResolver.adaptTo(Session.class);
        Node currentNode = resource.adaptTo(Node.class);

        // Make sure qwe have the contact profile node
        if (session != null && currentNode == null) {
            Node rootNode = currentPage.getContentResource().adaptTo(Node.class);

            if (rootNode != null) {
                currentNode = rootNode.addNode(ConstantsMEA.NodeNames.CARD_GRID_NODE_NAME,
                        ConstantsMEA.Properties.NT_UNSTRUCTURED);
                currentNode.setProperty(ConstantsMEA.Properties.SLING_COLON_RESOURCE_TYPE,
                        ConstantsMEA.ResourceTypes.CONTACT_PROFILE_RESOURCE_TYPE);
                // save nodes
                session.save();
            }
        }
        // if no children, add them
        if (session != null && currentNode != null && !currentNode.hasNodes()) {
            resources = ConstantsMEA.resources();
            // add the internal resources
            for (Map.Entry<String, String> entry : resources.entrySet()) {
                String resType = entry.getValue();
                Node node = currentNode.addNode(entry.getKey(), ConstantsMEA.Properties.NT_UNSTRUCTURED);
                node.setProperty(ConstantsMEA.Properties.SLING_COLON_RESOURCE_TYPE, resType);
                // Set the flag for RTE's
                if (resType.endsWith("text")) {
                    node.setProperty("textIsRich", "true");
                }
            }
            // save nodes
            session.save();
        }
    }

    /**
     * This method returns the "hour" text to display from the component's language
     * based policy.
     * 
     * @param none
     * @return String "hour" text to display.
     * 
     */
    public String getHour() {
        return hour;
    }

    /**
     * This method returns the "est" text to display from the component's language
     * based policy.
     * 
     * @param none
     * @return String "est" to display.
     * 
     */
    public String getEst() {
        return est;
    }

    /**
     * This method returns a Map array with the resources's list to embed on the
     * page for authoring
     * 
     * @param none
     * @return Map<String, String> resources list to embed on the page for authoring
     * 
     */
    public Map<String, String> getResources() {
        // Make sure the variable is set, otherwise get it from Constants
        if (resources == null) {
            resources = ConstantsMEA.resources();
        }
        return resources;
    }
}
