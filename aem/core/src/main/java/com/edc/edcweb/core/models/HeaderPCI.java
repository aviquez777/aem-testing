package com.edc.edcweb.core.models;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.policies.ContentPolicy;
import com.edc.edcweb.core.helpers.Constants;
import com.edc.edcweb.core.helpers.ContentPolicyUtil;
import com.edc.edcweb.core.helpers.LanguageToggleUtil;
import com.edc.edcweb.core.helpers.LinkResolver;
import com.edc.edcweb.core.service.EDCSystemConfigurationService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Source;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 * @author Silvia Bolaños Prada
 * @version 1.0.0
 * @since 1.0.0
 *
 *
 *        This class is the model class for the Header PCI component used by the EDC
 *        web site.
 *
 *
 */
@Model(adaptables = SlingHttpServletRequest.class, resourceType = "edc/components/structure/header")
public class HeaderPCI {

    @Inject
    @Source("sling-object")
    protected ResourceResolver resolver;

    @Inject
    protected SlingHttpServletRequest request;

    @Inject
    protected Page currentPage;

    @Inject
    private EDCSystemConfigurationService edcSystemConfiguration;

    protected String logoUrl;
    protected String logoImage;
    protected String logoAltText;
    protected String logoTarget;
    protected String loggedInText;
    protected String loggedOutText;
    protected String loginUrl;
    protected String loginTarget;

    protected String skipText;

    protected String logoutUrl;


    /**
     * Populates the Model with values from the applicable ContentPolicy (based on
     * current path language).
     *
     */
    @PostConstruct
    public void initModel() {
        LanguageToggleUtil langUtil = LanguageToggleUtil.getInstance();
        // ---------------------------------------------------------------------
        ContentPolicy contentPolicy = ContentPolicyUtil.resolveLocalizedContentPolicy(this.request, this.currentPage);
        if (contentPolicy != null) {
            ValueMap properties = contentPolicy.getProperties();
            this.logoUrl = LinkResolver.addHtmlExtension(properties.get(Constants.Properties.LOGO_URL, String.class));
            this.logoImage = properties.get(Constants.Properties.LOGO_IMAGE, String.class);
            this.logoAltText = properties.get(Constants.Properties.LOGO_ALT_TEXT, String.class);
            this.logoTarget = properties.get(Constants.Properties.LOGO_TARGET, String.class);
            this.loggedInText = properties.get(Constants.Properties.LOGGEDIN_TEXT, String.class);
            this.loggedOutText = properties.get(Constants.Properties.LOGGEDOUT_TEXT, String.class);
            this.loginUrl = LinkResolver.addHtmlExtension(properties.get(Constants.Properties.LOGIN_URL, String.class));
            this.logoutUrl = LinkResolver.addHtmlExtension(properties.get(Constants.Properties.LOGOUT_URL, String.class));
            this.loginTarget = properties.get(Constants.Properties.LOGIN_TARGET, String.class);
            this.skipText = properties.get(Constants.Properties.MENU_SKIP, String.class);

        }
    }

    /**
     * Get the URL to be loaded when the logo is clicked.
     *
     */
    public String getLogoUrl() {
        return this.logoUrl;
    }

    /**
     * Get the file reference (asset in the DAM) for the logo image.
     *
     */
    public String getLogoImage() {
        return this.logoImage;
    }

    /**
     * Get the alternate text for the logo image.
     *
     */
    public String getLogoAltText() {
        return this.logoAltText;
    }

    /**
     * Get the logo image target (existing window, new window, etc.).
     *
     */
    public String getLogoTarget() {
        return this.logoTarget;
    }

    /**
     * Get the text to display in the Login link.
     *
     */
    public String getLoggedInText() {
        return this.loggedInText;
    }

    public String getLoggedOutText() {
        return this.loggedOutText;
    }

    /**
     * Get the URL of the login page.
     *
     */
    public String getLoginUrl() {
        return this.loginUrl;
    }

    /**
     * Get the login target (existing window, new window, etc.).
     *
     */
    public String getLoginTarget() {
        return this.loginTarget;
    }


    /**
     * For accesibility | Get the text for the action to skip the navigation
     *
     */
    public String getSkipText() {
        return this.skipText;
    }


}
