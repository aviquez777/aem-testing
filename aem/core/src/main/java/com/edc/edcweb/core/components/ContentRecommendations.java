package com.edc.edcweb.core.components;

import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.wcm.api.policies.ContentPolicy;
import com.edc.edcweb.core.helpers.Constants;
import com.edc.edcweb.core.helpers.ContentPolicyUtil;
import com.edc.edcweb.core.helpers.LinkResolver;

/**
 * @author Peter Crummey
 * @version 0.0.5
 * @since 0.0.5
 * 
 * 
 *        Backing Java code for the Content Recommendations (both premium and
 *        non-premium) component used by the EDC web site.
 * 
 * 
 */
public class ContentRecommendations extends WCMUsePojo {
    private static final Logger log = LoggerFactory.getLogger(ContentRecommendations.class);

    private String headline = "";
    private String externalUrl;

    @Override
    public void activate() throws Exception {
        log.info("Activate - start");
        ValueMap properties;
        ContentPolicy contentPolicy = ContentPolicyUtil.resolveLocalizedContentPolicy(getRequest(), getCurrentPage());
        if (contentPolicy != null) {
            properties = contentPolicy.getProperties();
            this.headline = properties.get(Constants.Properties.HEADLINE, String.class);
        }
        log.info("Headline from template: {}", this.headline);

        properties = getResource().getValueMap();
        externalUrl = LinkResolver.addHtmlExtension(properties.get(Constants.Properties.EXTERNAL_URL, String.class));
        // BUG 327312l map cannot adapt to ModifiableValueMap on publisher. (Caused by
        // BUG 237599)
        Resource resource = getResource();
        String listFrom = properties.get(Constants.LIST_FROM, String.class);
        // Just add the attribute if is a tag based list and is not present
        if (Constants.LIST_FROM_TAGS.equalsIgnoreCase(listFrom)) {
            ModifiableValueMap map = resource.adaptTo(ModifiableValueMap.class);
            try {
                map.putIfAbsent(Constants.Properties.TAGS_MATCH, Constants.TAGS_MATCH_ANY);
                resource.getResourceResolver().commit();
            } catch (NullPointerException e) {
                log.error("ContentRecommendations couldn't save deafult attribute from tags", e);
            }
        }
    }

    public String getHeadlineFromPolicy() {
        return this.headline;
    }

    public String getExternalUrl() {
        return this.externalUrl;
    }
}
