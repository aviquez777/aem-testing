package com.edc.edcweb.core.filters;

import java.io.IOException;

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

import com.day.cq.dam.api.Asset;
import com.day.cq.wcm.api.Page;
import com.edc.edcweb.core.gatedcontentaccess.helpers.GatedContentAccessConstats;
import com.edc.edcweb.core.gatedcontentaccess.helpers.GatedContentAccessHelper;
import com.edc.edcweb.core.helpers.Constants;
import com.edc.edcweb.core.helpers.LinkResolver;

/**
 * Watch for the "gated" folder and apply logic accordingly **Important** In
 * order to provide a better author experience, the "Gating" of the Gated
 * Content Page **will not work** on the author instance. In other words, the
 * page will be always accessible on the author.
 */
@Component(configurationPid = "com.edc.edcweb.core.servlets.GatedContentAccessFilterImpl", service = {
        Filter.class }, property = { "sling.filter.scope=request",
                "sling.filter.pattern=" + GatedContentAccessConstats.FILTER_PATTERN,
                "service.ranking:Integer=" + Integer.MAX_VALUE })

public class GatedContentAccessFilter implements Filter {

    private Logger log = LoggerFactory.getLogger(GatedContentAccessFilter.class);

    private String landingPage = null;
    private String assetGroupName = null;
    private String resPath = null;

    @Reference
    private SlingSettingsService slingSettingsService;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.debug("GatedContentAccessFilter init");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        final SlingHttpServletRequest slingRequest = (SlingHttpServletRequest) request;
        final SlingHttpServletResponse slingResponse = (SlingHttpServletResponse) response;
        // do not control access for author in edit or preview mode
        boolean isAuthor = slingSettingsService.getRunModes().toString().contains(Constants.RUN_MODE_AUTHOR);
        if (isAuthor) {
            chain.doFilter(slingRequest, slingResponse);
            return;
        }
        // Set the private landingPage, assetGroupName and resPath variables
        setVariables(slingRequest);
        if (StringUtils.isNotBlank(landingPage) && StringUtils.isNotBlank(assetGroupName)) {
            boolean accessGranted = GatedContentAccessHelper.checkSessionAccessGranted(slingRequest, assetGroupName);
            if (!accessGranted) {
                try {
                    slingResponse.sendRedirect(LinkResolver.changeInternalURLtoExternal(slingRequest,
                            LinkResolver.addHtmlExtension(landingPage)));
                    return;
                } catch (IOException e) {
                    log.error("GatedContentAccessController could not redirect to {} full error {}", landingPage,
                            e.getStackTrace());
                }
            }
        } else {
            String error = "landingPage=".concat(landingPage).concat("assetGroupName=").concat(assetGroupName);
            log.error("GatedContentAccessFilter error {} on asset {}", error, resPath);
            slingResponse.sendError(404);
        }
        chain.doFilter(slingRequest, slingResponse);
    }

    @Override
    public void destroy() {
        log.debug("GatedContentAccessFilter destroy");
    }

    /**
     * Get and set the private landingPage, assetGroupName and resPath variables
     * based on the asset type variables will be empty if no resource found
     * 
     * @param slingRequest
     */
    private void setVariables(SlingHttpServletRequest slingRequest) {
        Resource res = slingRequest.getResource();
        String resType = res.getResourceType();
        switch (resType) {
        case GatedContentAccessConstats.CQ_PAGE:
            Page currentPage = res.adaptTo(Page.class);
            this.landingPage = currentPage.getProperties().get(GatedContentAccessConstats.LANDING_PAGE_PROPERTY,
                    String.class);
            Resource formRes = GatedContentAccessHelper.getFormRes(GatedContentAccessConstats.FORM_RESOURCE_TYPE, slingRequest, landingPage);
            if (formRes == null) { 
                // Check for Gated Lead Generation Form
                formRes = GatedContentAccessHelper.getFormRes(GatedContentAccessConstats.FORM_GLGF_RESOURCE_TYPE, slingRequest, landingPage);
            }
            if (formRes != null) {
                this.assetGroupName = formRes.getValueMap().get(GatedContentAccessConstats.ASSET_GROUP_NAME_PROPERTY,
                        String.class);
            } else {
                this.assetGroupName = GatedContentAccessConstats.NOT_FOUND;
                log.error("GatedContentAccessFilter: form not found on referrer page {}", formRes);
            }
            break;
        case GatedContentAccessConstats.DAM_ASSET:
            Asset currentAsset = res.adaptTo(Asset.class);
            this.landingPage = currentAsset.getMetadataValue(GatedContentAccessConstats.LANDING_PAGE_PROPERTY);
            this.assetGroupName = currentAsset.getMetadataValue(GatedContentAccessConstats.ASSET_GROUP_NAME_PROPERTY);
            break;
        default:
            // used for debugging proposes
            this.resPath = res.getPath();
            this.landingPage = GatedContentAccessConstats.NOT_FOUND;
            this.assetGroupName = GatedContentAccessConstats.NOT_FOUND;
            break;
        }
    }
}