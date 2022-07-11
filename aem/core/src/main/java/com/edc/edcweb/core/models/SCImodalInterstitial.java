package com.edc.edcweb.core.models;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.settings.SlingSettingsService;
import org.osgi.service.component.annotations.Reference;

import com.edc.edcweb.core.helpers.Constants;
import com.edc.edcweb.core.helpers.LinkResolver;

@Model(adaptables = SlingHttpServletRequest.class)
public class SCImodalInterstitial {

    @Self
    private SlingHttpServletRequest request;

    @Reference
    @Inject
    private SlingSettingsService slingSettingsService;

    @Inject
    @Named("btnLink")
    @Optional
    private String btnLink;

    @Inject
    @Named("dataAnchorForScroll")
    @Optional
    private String dataAnchorForScroll;

    private String link;

    @PostConstruct
    public void initModel() {
        link = LinkResolver.changeInternalURLtoExternal(request, LinkResolver.addHtmlExtension(btnLink));
        // check if link was created
        if (StringUtils.isNotBlank(link)) {
            // Use "?" as default separator
            String querySeparator = Constants.QUESTION_MARK;
            // Always add the wcmmode=disabled if author
            boolean isAuthor = slingSettingsService.getRunModes().toString().contains(Constants.RUN_MODE_AUTHOR);
            if (isAuthor) {
                link = link.concat(querySeparator).concat(Constants.WCMMODE_EQUALS_DISABLED);
            }
            // Check if we have to concatenate the dataAnchorForScroll
            if (StringUtils.isNotBlank(dataAnchorForScroll)) {
                // Check if we have a question mark on the url
                if (link.contains(querySeparator)) {
                    querySeparator = Constants.AMPERSAND_SIGN;
                }
                // Concatenate our Anchor
                link = link.concat(querySeparator).concat(Constants.DATA_ANCHOR_FOR_SCROLL_PARAM)
                        .concat(Constants.EQUAL_SIGN).concat(dataAnchorForScroll);
            }
        }
    }

    public String getLink() {
        return link;
    }

}
