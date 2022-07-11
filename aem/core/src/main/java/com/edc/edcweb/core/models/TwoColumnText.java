package com.edc.edcweb.core.models;

import javax.inject.Inject;
import javax.inject.Named;

import com.edc.edcweb.core.helpers.LinkResolver;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;

/**
 * Defines the {@code TwoColumnText} Sling Model used for the
 * {@code /apps/edc/content/pictureandtwocolumntext} component.
 *
 * @author monica.castillo
 * @since Feb 06 2018
 * @version 1.0
 */

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class TwoColumnText
{
    @Inject
    @Named("subheading")
    private String subheading;

    @Inject
    @Named("subheadingLink")
    private String subheadingLink;

    @Inject
    @Named("rtftext")
    @Optional
    private String rtftext;

    public String getSubheading() {
        return subheading;
    }

    public String getSubheadingLink() {
        return LinkResolver.addHtmlExtension(subheadingLink);
    }

    public String getRtftext() {
        return rtftext;
    }
}
