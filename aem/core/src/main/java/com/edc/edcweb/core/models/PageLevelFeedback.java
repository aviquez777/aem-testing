package com.edc.edcweb.core.models;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import com.day.cq.wcm.api.Page;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;

import com.day.cq.wcm.api.policies.ContentPolicy;
import com.edc.edcweb.core.helpers.Constants;
import com.edc.edcweb.core.helpers.ContentPolicyUtil;

/**
 * @author ACN
 * @version 1.0.0
 * @since 1.0.0
 * 
 * @see ContentPolicyUtil
 * 
 * 
 *      This class provides model support for the AEM Page Level Feedback
 *      component. The model is populated from English or French content
 *      policies.
 * 
 *      The ContentPolicyUtil class is used to load the correct policy based
 *      upon the language of the current request.
 * 
 */
@Model(adaptables = SlingHttpServletRequest.class)
public class PageLevelFeedback {

    @Inject
    private SlingHttpServletRequest request;

    @Inject
    private Page currentPage;

    @ScriptVariable
    private ValueMap properties;

    private String idPageLevelFeedback;

    @PostConstruct
    public void initModel() {
        idPageLevelFeedback = properties.get(Constants.Properties.PAGE_LEVEL_FEEDBACK, String.class);
        // Check if empty property
        if (StringUtils.isBlank(idPageLevelFeedback)) {
            // empty property check policy
            ContentPolicy contentPolicy = ContentPolicyUtil.resolveLocalizedContentPolicy(request, currentPage);
            if (contentPolicy != null) {
                ValueMap policy = contentPolicy.getProperties();
                // If policy is not there, set default
                idPageLevelFeedback = policy.get(Constants.Properties.PAGE_LEVEL_FEEDBACK, Constants.Properties.PAGE_LEVEL_FEEDBACK);
            }
            // If still blank, set default
            if (StringUtils.isBlank(idPageLevelFeedback)) {
                idPageLevelFeedback = Constants.Properties.PAGE_LEVEL_FEEDBACK;
            }
        }
    }

    public String getIdPageLevelFeedback() {
        return idPageLevelFeedback;
    }

}
