package com.edc.edcweb.core.myedcgating;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.WCMMode;
import com.edc.edcweb.core.helpers.Constants;
import com.edc.edcweb.core.helpers.LinkResolver;
import com.edc.edcweb.core.helpers.progressiveprofiling.ProgressiveProfilingHelper;
import com.edc.edcweb.core.myedcgating.helpers.PagePathsHelper;

/**
 * Intercepts the page load and if the user has no access, get the teaser URL to
 * do the redirect, if the user has access, then do nothing, continue loading
 * the page
 *
 */
@Model(adaptables = SlingHttpServletRequest.class)
public class MyEDCGatingController {

    private static final Logger log = LoggerFactory.getLogger(MyEDCGatingController.class);

    @Self
    private SlingHttpServletRequest request;

    @SlingObject
    private SlingHttpServletResponse response;

    @Inject
    private Page currentPage;

    @PostConstruct
    public void initModel() {
        // do not control access for author in edit or preview mode
        WCMMode mode = WCMMode.fromRequest(request);
        log.debug(" mode {}", mode);
        if (mode == WCMMode.EDIT || mode == WCMMode.PREVIEW) {
            log.debug("mode is EDIT or PREVIEW, not controlling access...");
            return;
        }
        // Check the access
        boolean accessGranted = ProgressiveProfilingHelper.checkMyEdcSession(request);
        // If no access get the teaser URL to do the redirect, if the user has access,
        // then o nothing, continue loading the page
        if (!accessGranted) {
            // Get the teaser path from the page property
            String teaserPath = currentPage.getProperties().get(Constants.Paths.MYEDC_GATING_LANDING_PAGE,
                    String.class);
            // If no page exists try to figure out
            if (StringUtils.isBlank(teaserPath)) {
                teaserPath = PagePathsHelper.guessTeaserPage(currentPage.getPath());
            }
            // Once we figured out the path create the link
            String redirectTo = LinkResolver.changeInternalURLtoExternal(request,
                    LinkResolver.addHtmlExtension(teaserPath));
            // Do the redirect
            try {
                this.response.sendRedirect(redirectTo);
            } catch (IOException e) {
                log.error("MyEDCGatingController cannot do the redirect for: {}", redirectTo);
            }
        }
    }
}