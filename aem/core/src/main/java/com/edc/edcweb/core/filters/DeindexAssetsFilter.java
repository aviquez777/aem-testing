package com.edc.edcweb.core.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.settings.SlingSettingsService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edc.edcweb.core.helpers.Constants;
import com.edc.edcweb.core.helpers.constants.DeindexAssetsConstants;

@Component(configurationPid = "com.edc.edcweb.core.servlets.DeindexAssetsImpl", 
service = { Filter.class }, property = {
    "sling.filter.scope=request", 
    "sling.filter.pattern=" + DeindexAssetsConstants.FILTER_PATTERN,
    "service.ranking:Integer=" + Integer.MAX_VALUE 
    }
)

public class DeindexAssetsFilter implements Filter {

    private Logger logger = LoggerFactory.getLogger(DeindexAssetsFilter.class);

    @Reference
    private SlingSettingsService slingSettingsService;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.debug("DeindexPDF init");
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

        Resource res = slingRequest.getResourceResolver().getResource(slingRequest.getResource().getPath());
        if (res != null) {
            Resource rescontent = res.getChild(DeindexAssetsConstants.JCR_CONTENT);
            String seoExcludeProp = rescontent.getValueMap().get(DeindexAssetsConstants.SEO_EXCLUDE_METADATA_PROPERTY,
                    String.class);
            boolean seoExclude = Boolean.parseBoolean(seoExcludeProp);
            if (seoExclude) {
                slingResponse.addHeader(DeindexAssetsConstants.X_ROBOTS_HEADER_TAG,
                        DeindexAssetsConstants.NO_INDEX_NO_FOLLOW);
            }
        }
        chain.doFilter(slingRequest, slingResponse);
    }

    @Override
    public void destroy() {
        // NO USED
    }
}
