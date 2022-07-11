package com.edc.edcweb.core.models;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.policies.ContentPolicy;
import com.edc.edcweb.core.helpers.Constants;
import com.edc.edcweb.core.helpers.ContentPolicyUtil;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 * @author Roberto Ramos
 * @version 1.0.0
 * @since 1.0.0
 *
 * @see ContentPolicyUtil
 *
 * This class provides model support for the Cookie Bannercomponent.  The model is populated from dialog or from English or French content policies.
 *
 * The ContentPolicyUtil class is used to load the correct policy based upon the language of the current request.
 *
 */

@Model(adaptables = SlingHttpServletRequest.class)
public class CookieBanner {
    private static final Logger log = LoggerFactory.getLogger(PageHeroBanner.class);

    @Self
    private SlingHttpServletRequest request;

    @Inject
    private Page currentPage;

    @ScriptVariable
    private ValueMap properties;

    // Model
    private String message;
    private String linktext;

    @PostConstruct
    public void initModel() {
        // For components already integrated, they take the data from properties
        // When a policy is defined and the phone number is not defined those values are overwritten with the value from the policy.
        this.message = properties.get(Constants.Properties.MESSAGE, String.class);
        this.linktext = properties.get(Constants.Properties.LINK_TEXT, String.class);

        ContentPolicy contentPolicy = ContentPolicyUtil.resolveLocalizedContentPolicy(this.request, this.currentPage);
        if (contentPolicy != null) {
            ValueMap policy_properties = contentPolicy.getProperties();

            this.message = policy_properties.get(Constants.Properties.MESSAGE, String.class);
            this.linktext = policy_properties.get(Constants.Properties.LINK_TEXT, String.class);
        }
    }

    public String getMessage() {
        return this.message;
    }

    public String getLinktext() {
        return this.linktext;
    }
}
