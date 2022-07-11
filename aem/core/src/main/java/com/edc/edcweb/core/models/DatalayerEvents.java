package com.edc.edcweb.core.models;

import com.day.cq.wcm.api.Page;
import com.edc.edcweb.core.helpers.Constants;
import com.edc.edcweb.core.helpers.LanguageUtil;
import com.edc.edcweb.core.helpers.constants.ConstantsDatalayer;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.Source;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.RepositoryException;

/**
 * @author Monica Castillo
 * @version 1.0.0
 * @since 1.0.0
 *
 *
 * This class is the model to get component level data for the analytics datalayer javaScript object.
 *
 *
 */
@Model(adaptables = SlingHttpServletRequest.class)
public class DatalayerEvents {
    private static final Logger log = LoggerFactory.getLogger(DatalayerEvents.class);

    @Inject
    @Source("sling-object")
    private ResourceResolver resolver;

    @Self
    private SlingHttpServletRequest request;

    @ScriptVariable
    private ValueMap properties;

    @Inject
    @Optional
    private Page currentPage;

    private String componentTitle;
    private String pageName;

    @PostConstruct
    public void initModel() {
        String pagePath = currentPage.getPath();
        String languageAbbreviation = LanguageUtil.getLanguageAbbreviationFromPath(pagePath, ConstantsDatalayer.SupportedLanguages.ENGLISH.getLanguageAbbreviation());
        String pathRegExp = "(" + ConstantsDatalayer.Paths.CONTENT + ")?(" + ConstantsDatalayer.Paths.EDC + ")?(/" + languageAbbreviation + ")?/?";
        String path = pagePath.replaceFirst(pathRegExp, "");

        this.pageName = path.replaceAll("/", ":");

        try {
            String resourceType = this.properties.get(Constants.Properties.SLING_COLON_RESOURCE_TYPE, String.class);
            this.componentTitle = getComponentName(resourceType, this.resolver);
        } catch (RepositoryException repositoryException) {
            this.componentTitle = "";
        }

    }

    private String getComponentName(String resourcePath, ResourceResolver resolver) throws RepositoryException {
        Resource childResource = resolver.getResource(resourcePath);

        if (childResource != null) {
            Node childNode = childResource.adaptTo(Node.class);
            if (childNode != null) {
                return childNode.getProperty(Constants.Properties.JCR_TITLE).getValue().toString();
            } else {
                return "";
            }
        } else {
            return "";
        }
    }

    public String getComponentTitle() {
        return this.componentTitle;
    }

    public String getPageName() {
        return this.pageName;
    }
}