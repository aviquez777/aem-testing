package com.edc.edcweb.core.models;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.policies.ContentPolicy;
import com.edc.edcweb.core.helpers.Constants;
import com.edc.edcweb.core.helpers.ContentPolicyUtil;

@Model(adaptables = SlingHttpServletRequest.class)
/**
 * CentralText Gets Title and HTML Text from the component's policy and
 * returns to display.
 * 
 */
public class CentralText {

    @Self
    private SlingHttpServletRequest request;

    @Inject
    private Page currentPage;

    private String title;
    private String text;

    @PostConstruct
    public void initModel() {
        ContentPolicy contentPolicy = ContentPolicyUtil.resolveLocalizedContentPolicy(request, currentPage);
        if (contentPolicy != null) {
            ValueMap properties = contentPolicy.getProperties();
            title = properties.get(Constants.Properties.TITLE, String.class);
            text = properties.get(Constants.Properties.TEXT, String.class);
        }
    }

    /**
     * getTitle Returns title from component's policy
     * 
     * @return title
     */
    public String getTitle() {
        return title;
    }

    /**
     * getText Returns text from component's policy
     * 
     * @return String text (html)
     */
    public String getText() {
        return text;
    }
}
