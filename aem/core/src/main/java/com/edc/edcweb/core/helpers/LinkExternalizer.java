package com.edc.edcweb.core.helpers;

import javax.inject.Inject;

import com.edc.edcweb.core.helpers.LinkResolver;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.Source;
import com.edc.edcweb.core.service.EDCSystemConfigurationService;
import com.edc.edcweb.core.helpers.Constants;

/**
 * @author ACN
 * @version 1.0.0
 * @since 1.0.0
 *
 *  Request to externalize a link. Inject srcPath and scheme (https,http) from html.
 *
 */
@Model(adaptables = SlingHttpServletRequest.class)
public class LinkExternalizer
{
    @Inject @Source("sling-object")
    private ResourceResolver resourceResolver;

    @Inject
    private EDCSystemConfigurationService edcSystemConfiguration;

    @Inject
    @Optional
    private String srcPath="";

    @Inject
    @Optional
    private String scheme;

    public String getSrcPath() {
        return srcPath;
    }

    public String getPublishLink() {

        String link = srcPath;

        if (link != null && !link.isEmpty() ) {
            link = scheme + "://" + Constants.WHOS_ON_DOMAIN_PROD + srcPath ;
        }

        return link;
    }

    public String getLinkWithHtmlExtension() {
        String link = srcPath;

        if (link != null && !link.isEmpty() ) {
            link = LinkResolver.addHtmlExtension(srcPath);
        }

        return link;
    }

    public String getPublishPageUrl() {
        String url = null;
        String path = srcPath;
        String siteDomain = edcSystemConfiguration.getSiteDomainName();

        if (path != null && !path.isEmpty() ) {
            path = path.replaceAll(Constants.Paths.CONTENT_EDC, "");

            if (!path.contains("en/homepage")){
                url = scheme + "://" + siteDomain + LinkResolver.addHtmlExtension(path);
            } else {
                url = scheme + "://" + siteDomain;
            }
        }

        return url;
    }

    public String getPublishPageRelUrl() {
        String url = null;
        String path = srcPath;

        if (path != null && !path.isEmpty() ) {
            path = path.replaceAll(Constants.Paths.CONTENT_EDC, "");
            url = LinkResolver.addHtmlExtension(path);
        }

        return url;
    }
}
