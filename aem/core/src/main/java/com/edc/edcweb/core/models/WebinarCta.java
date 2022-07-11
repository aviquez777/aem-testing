package com.edc.edcweb.core.models;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.policies.ContentPolicy;
import com.edc.edcweb.core.helpers.ContentPolicyUtil;
import com.edc.edcweb.core.helpers.ResourceResolverHelper;
import com.edc.edcweb.core.helpers.SignInHelper;
import com.edc.edcweb.core.helpers.constants.ConstantsPageHeroBanner;
import com.edc.edcweb.core.helpers.constants.ConstantsWebinars;

/**
 * 
 * Model to Retrieve the button's policy and make the values available to the
 * HTL. Handle Special case when the button is embedded on the Page Hero Banner.
 *
 */

@Model(adaptables = SlingHttpServletRequest.class)
public class WebinarCta {

    @Self
    private SlingHttpServletRequest request;

    @Inject
    private Page currentPage;

    @SlingObject
    private ResourceResolver resourceResolver;

    private String webinarshowkey;
    private String webinaroverride;

    private boolean hasPolicy = false;

    private String donthaveanaccounttext;
    private String createaccountlinktext;
    private String defaulterrortext;

    // Upcoming
    private String loggedoutupcomingbtntext;
    private String loggedinupcomingbtntext;
    private String registeredtoupcomingtext;
    // Live
    private String loggedoutlivebtntext;
    private String loggedinlivebtntext;
    // On Demand
    private String loggedoutondemandbtntext;
    private String loggedinondemandbtntext;

    private String loginbtnurl;
    private String registerbtnurl;

    @PostConstruct
    public void initModel() {
        // Get values from page properties
        ValueMap pageProperties = currentPage.getProperties();
        // Webinar showKey and override
        webinarshowkey = pageProperties.get(ConstantsWebinars.SHOW_KEY, String.class);
        webinaroverride = pageProperties.get(ConstantsPageHeroBanner.PHBProperties.WEBINAR_OVERRIDE, String.class);
        ContentPolicy contentPolicy = null;
        // If the WebinarCTA is embedded on the Page Hero Banner, use the page template
        // to get the button policy
        if (ConstantsWebinars.PAGE_HERO_BANNER_RESOURCE.equals(request.getResource().getParent().getResourceType())) {
            String structPath = currentPage.getTemplate().getPath().concat(ConstantsWebinars.POLICY_STRUCTURE);
            Resource resPage = request.getResourceResolver().getResource(structPath);
            Page templatePage = resPage.adaptTo(Page.class);
            Resource webinarCtaRes = ResourceResolverHelper.getResourceByType(templatePage,
                    ConstantsWebinars.WEBNAR_CTA_RESOURCE);
            if (webinarCtaRes != null) {
                contentPolicy = ContentPolicyUtil.resolveLocalizedContentPolicy(webinarCtaRes.getResourceResolver(),
                        webinarCtaRes, currentPage);
            }
        } else {
            contentPolicy = ContentPolicyUtil.resolveLocalizedContentPolicy(request, currentPage);
        }
        hasPolicy = contentPolicy != null;
        if (hasPolicy) {
            ValueMap policy = contentPolicy.getProperties();
            // General Text
            donthaveanaccounttext = policy.get(ConstantsWebinars.DONT_HAVE_ACCOUNT, String.class);
            createaccountlinktext = policy.get(ConstantsWebinars.CREATE_ACCOUNT, String.class);
            defaulterrortext = policy.get(ConstantsWebinars.DEFAULT_ERROR, String.class);
            // Upcoming
            loggedoutupcomingbtntext = policy.get(ConstantsWebinars.LOGGEDOUT_UPCOMING_BTNTEXT, String.class);
            loggedinupcomingbtntext = policy.get(ConstantsWebinars.LOGGEDIN_UPCOMING_BTNTEXT, String.class);
            registeredtoupcomingtext = policy.get(ConstantsWebinars.REGISTERED_TO_UPCOMING_TEXT, String.class);
            // Live
            loggedoutlivebtntext = policy.get(ConstantsWebinars.LOGGEDOUT_LIVE_BTNTEXT, String.class);
            loggedinlivebtntext = policy.get(ConstantsWebinars.LOGGEDIN_LIVE_BTNTEXT, String.class);
            // On Demand
            loggedoutondemandbtntext = policy.get(ConstantsWebinars.LOGGEDOUT_ONDEMAND_BTNTEXT, String.class);
            loggedinondemandbtntext = policy.get(ConstantsWebinars.LOGGEDIN_ONDEMAND_BTNTEXT, String.class);
            // Register and Login LInks
            loginbtnurl = SignInHelper.buildSignInQueryString(request, this.currentPage.getPath(),
                    ConstantsWebinars.PRODUCT_TYPE, this.currentPage.getName(), false, true, 2, false);
            registerbtnurl = SignInHelper.buildSignInQueryString(request, this.currentPage.getPath(),
                    ConstantsWebinars.PRODUCT_TYPE, this.currentPage.getName(), true, true, 2, false);
        }
    }

    public SlingHttpServletRequest getRequest() {
        return request;
    }

    public Page getCurrentPage() {
        return currentPage;
    }

    public Boolean getHasPolicy() {
        return hasPolicy;
    }

    public String getDonthaveanaccounttext() {
        return donthaveanaccounttext;
    }

    public String getCreateaccountlinktext() {
        return createaccountlinktext;
    }

    public String getDefaulterrortext() {
        return defaulterrortext;
    }

    public String getLoggedoutupcomingbtntext() {
        return loggedoutupcomingbtntext;
    }

    public String getLoggedinupcomingbtntext() {
        return loggedinupcomingbtntext;
    }

    public String getRegisteredtoupcomingtext() {
        return registeredtoupcomingtext;
    }

    public String getLoggedoutlivebtntext() {
        return loggedoutlivebtntext;
    }

    public String getLoggedinlivebtntext() {
        return loggedinlivebtntext;
    }

    public String getLoggedoutondemandbtntext() {
        return loggedoutondemandbtntext;
    }

    public String getLoggedinondemandbtntext() {
        return loggedinondemandbtntext;
    }

    public String getLoginbtnurl() {
        return loginbtnurl;
    }

    public String getRegisterbtnurl() {
        return registerbtnurl;
    }

    public String getWebinarshowkey() {
        return webinarshowkey;
    }

    public String getWebinaroverride() {
        return webinaroverride;
    }
}
