package com.edc.edcweb.core.models;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.Self;

import com.edc.edcweb.core.helpers.LinkResolver;

/**
 *
 *
 * This class is the model class to map PagePath
 *
 *
 */
@Model(adaptables = SlingHttpServletRequest.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ResolverPath {
    private static final Logger log = LoggerFactory.getLogger(ResolverPath.class);

    @Self
    SlingHttpServletRequest request;

    @Inject
    private String refpage;

    private String resolvedPath;

    @PostConstruct
    public void initModel() {
        resolvedPath = LinkResolver.reverseMapLink(request.getResourceResolver(), refpage);
    }

    public String getResolvedPath() {
        return resolvedPath;
    }
}
