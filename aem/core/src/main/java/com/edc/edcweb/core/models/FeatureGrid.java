package com.edc.edcweb.core.models;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import com.day.cq.wcm.api.policies.ContentPolicy;
import com.edc.edcweb.core.helpers.ContentPolicyUtil;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.day.cq.wcm.api.Page;
import com.edc.edcweb.core.helpers.constants.ConstantsAccessibility;

@Model(adaptables = SlingHttpServletRequest.class)
public class FeatureGrid {
    private static final Logger log = LoggerFactory.getLogger(FeatureGrid.class);

    @Self
    private SlingHttpServletRequest request;

    @Inject
    private Page currentPage;

    // Policies
    private String policy_videoplaybtn;

    @PostConstruct
    public void initModel() {
        this.populateFromPolicy();
    }

    private void populateFromPolicy() {
        ContentPolicy contentPolicy = ContentPolicyUtil.resolveLocalizedContentPolicy(this.request, this.currentPage);

        if (contentPolicy != null) {
            ValueMap policy_properties = contentPolicy.getProperties();

            // Accessibility policies
            this.policy_videoplaybtn = policy_properties.get(ConstantsAccessibility.CAProperties.ACCESSIBILITY_VIDEOPLAYBTN, String.class);
        }
    }

    public String getPolicy_videoplaybtn() {
        return policy_videoplaybtn;
    }
}