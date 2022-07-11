/**
 * 
 */
package com.edc.edcweb.core.servlets;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.settings.SlingSettingsService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.WCMMode;
import com.edc.edcweb.core.consentrequestgating.helpers.CRGHelper;
import com.edc.edcweb.core.gatedcontentaccess.helpers.GatedContentAccessConstats;
import com.edc.edcweb.core.helpers.Constants;
import com.edc.edcweb.core.helpers.LinkResolver;
import com.edc.edcweb.core.helpers.Request;
import com.edc.edcweb.core.helpers.progressiveprofiling.ProgressiveProfilingHelper;
import com.edc.edcweb.core.helpers.progressiveprofiling.ProgressiveProfilingPremiumPageHelper;
import com.edc.edcweb.core.service.EloquaService;

/**
 * @author ACN
 * @version 1.0.0
 * @since 1.0.0
 * 
 *        PremiumContentAssetFilterServlet is used to act as gate for premium
 *        asset pdf. It filter request of asset of resource type pdf under
 *        /dam/edc/.../premium/. If the sessionattribute accessGranted is
 *        defined, the the filter removes the attribute and let the asset be
 *        loaded Else it will redirect the user to the related teaser premium
 *        page.
 */

@Component(configurationPid = "com.edc.edcweb.core.servlets.PremiumContentAssetFilterServlet", service = {
        Filter.class }, property = { "sling.filter.scope=request",
                "sling.filter.pattern=(.*)/dam/edc/(.*)" + Constants.Paths.PROGRESSIVEPROFILING_PREMIUMROOT + "/(.*)",
                "service.ranking:Integer=" + Integer.MAX_VALUE, })
public class PremiumContentAssetFilterServlet implements Filter {

    private Logger logger = LoggerFactory.getLogger(PremiumContentAssetFilterServlet.class);

    @Reference
    private SlingSettingsService slingSettingsService;

    @Reference
    private EloquaService eloquaService;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.debug("init");

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        final SlingHttpServletRequest slingRequest = (SlingHttpServletRequest) servletRequest;
        final SlingHttpServletResponse slingResponse = (SlingHttpServletResponse) servletResponse;

        logger.debug("OSGi Filter: request for getResourcePath {}",
                slingRequest.getRequestPathInfo().getResourcePath());
        logger.debug("OSGi Filter: request for URI {}", slingRequest.getRequestURI());
        logger.debug("OSGi Filter: request for URL {}", slingRequest.getRequestURL());
        logger.debug("OSGi Filter: request for SelectorString {}",
                slingRequest.getRequestPathInfo().getSelectorString());

        if (slingRequest.getRequestPathInfo().getSelectorString() != null) {
            logger.debug("getSelectorString not null {}, filter done.",
                    slingRequest.getRequestPathInfo().getSelectorString());
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        // do not control access for author in edit or preview mode
        // BUg 176711 supersedes BUG 98938
        boolean isAuthor = slingSettingsService.getRunModes().toString().contains(Constants.RUN_MODE_AUTHOR);
        if (isAuthor) {
            filterChain.doFilter(slingRequest, slingResponse);
            return;
        }

        WCMMode mode = WCMMode.fromRequest(slingRequest);
        if (mode == WCMMode.PREVIEW || mode == WCMMode.EDIT) {
            logger.debug("mode is PREVIEW OR EDIT, filter done.");
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        logger.debug("OSGi Filter:getAuthType {}", slingRequest.getAuthType());
        logger.debug("OSGi Filter:getRequestPathInfo().getSuffix {}", slingRequest.getRequestPathInfo().getSuffix());
        logger.debug("OSGi Filter: getContentType {}", slingRequest.getContentType());
        logger.debug("OSGi Filter: getExtension {}", slingRequest.getRequestPathInfo().getExtension());
        slingRequest.getRequestPathInfo().getSelectorString();

        String premiumUrl = Request.getPagePath(slingRequest);
        String docId = ProgressiveProfilingPremiumPageHelper.getDocIDFromPremiumUrl(slingRequest, premiumUrl);
        String internalTeaserUrl = null;
        // check MyEDC Session first
        boolean accessGranted = ProgressiveProfilingHelper.checkMyEdcSession(slingRequest);
        logger.debug("My EDC accessGranted: {}", accessGranted);

        // not CRG required by default
        boolean checkCRG = false;
        // Get the resource to check for redirect url
        Resource res = slingRequest.getResourceResolver().getResource(slingRequest.getResource().getPath());
        if (res != null) {
            Resource rescontent = res.getChild(GatedContentAccessConstats.JCR_CONTENT);
            internalTeaserUrl = rescontent.getValueMap().get(GatedContentAccessConstats.PDF_LANDING_PAGE, String.class);
            checkCRG = Boolean.parseBoolean(rescontent.getValueMap().get(GatedContentAccessConstats.PDF_IS_CRG, String.class));
        }
        // We have access myedc already, must check consent request also
        if (accessGranted) {
            // Check CRG if necessary
            if (checkCRG && StringUtils.isNotBlank(internalTeaserUrl)) {
                Resource pageRes = slingRequest.getResourceResolver().resolve(internalTeaserUrl);
                if(pageRes != null) {
                    Page currentPage = pageRes.adaptTo(Page.class);
                    String crgTeaserUrl = CRGHelper.checkCRGAccess(currentPage, slingRequest, eloquaService);
                    // No teaser url is returned, means we have access, else we do not.
                    accessGranted =  StringUtils.isBlank(crgTeaserUrl);
                }
                logger.debug("Consent Request Gated accessGranted: {}", accessGranted);
            }
        }

        // BUG 184180 if not CRG required, check normal access 
        if (!checkCRG && !accessGranted) {
            // Check normal Progressive Profiling access
            accessGranted = ProgressiveProfilingPremiumPageHelper.checkSessionAccessGranted(slingRequest, docId);
        }
        // Still no access, then redirect to teaser page
        if (!accessGranted) {
            String extension = slingRequest.getRequestPathInfo().getExtension();
                if (StringUtils.isNotBlank(internalTeaserUrl)) {
                    // use the one on the PDF schema
                    internalTeaserUrl = LinkResolver.addHtmlExtension(internalTeaserUrl);
                } else {
                    // Use old method
                    if (extension == null && slingRequest.getRequestPathInfo().getResourcePath().endsWith(Constants.PDF_EXT)) {
                        internalTeaserUrl = ProgressiveProfilingPremiumPageHelper.getInternalTeaserUrlFromPremium(slingRequest);
                    }
                }
            String externalTeaserUrl = LinkResolver.changeInternalURLtoExternal(slingRequest, internalTeaserUrl);
            logger.debug("redirecting to teaserUrl {}", externalTeaserUrl);
            slingResponse.sendRedirect(externalTeaserUrl);
        } else {
            // We have access, gie the file...
            filterChain.doFilter(servletRequest, servletResponse);
            logger.debug("check Session - Access Granted OK!!, filter done.");
            return;
        }
    }

    @Override
    public void destroy() {
        // TODO Auto-generated method stub

    }
}
