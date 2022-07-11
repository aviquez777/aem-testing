package com.edc.edcportal.core.models;

import javax.annotation.PostConstruct;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;

import com.edc.edcportal.core.helpers.Constants;
import com.edc.edcportal.core.helpers.LinkResolver;

/**
 * @author Roberto Ramos
 * @version 1.0.0
 * @since 1.0.0
 *
 * This class is the model class for the Error Message component used by the EDC Portal web site.
 *
 */
@Model(adaptables = SlingHttpServletRequest.class, resourceType = "edcportal/components/content/errormesage")
public class ErrorMessage {

    @ScriptVariable
    private ValueMap properties;

    // links
    private String linkText;
    private String linkUrl;
    private String linkTarget;
    private String errorMessage;

    /**
     * This method is responsible for initial assignment of model properties.
     * Initial values are loaded from the properties.
     *
     */
    @PostConstruct
    public void initModel() {
        this.errorMessage = properties.get(Constants.Properties.ERROR_MESSAGE, String.class);
        this.linkText = properties.get(Constants.Properties.LINK_TEXT, String.class);
        this.linkUrl = LinkResolver.addHtmlExtension(properties.get(Constants.Properties.LINK_URL, String.class), Constants.Paths.CONTENT_MYEDC);
        this.linkTarget = properties.get(Constants.Properties.LINK_TARGET, String.class);
    }

    /**
     * Get the Error Message.
     *
     * @return Error Message
     *
     */
    public String getErrorMessage() {
        return this.errorMessage;
    }

    /**
     * Get the Link Text.
     *
     * @return Link text
     *
     */
    public String getLinkText() {
        return this.linkText;
    }

    /**
     * Get the URL to be loaded when the Link is clicked.
     *
     * @return Link url
     *
     */
    public String getLogoUrl() {
        return this.linkUrl;
    }
    /**
     * Get the Link Target (existing window, new window, etc.).
     *
     * @return Link target
     *
     */
    public String getLogoTarget() {
        return this.linkTarget;
    }
}
