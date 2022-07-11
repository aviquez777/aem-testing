/**
 * @author Lauren Alfaro
 * @version 1.0.0
 * @since 1.0.0
 *
 *        SiteMapServlet is based on ACS AEM Commons but customize by EDC
 *
 */
package com.edc.edcweb.core.servlets;

import java.io.IOException;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;

import com.day.cq.commons.inherit.HierarchyNodeInheritanceValueMap;
import com.day.cq.commons.inherit.InheritanceValueMap;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageFilter;
import com.day.cq.wcm.api.PageManager;
import com.edc.edcweb.core.helpers.LinkExternalizer;
import com.edc.edcweb.core.restful.RestClientConstants;
import com.edc.edcweb.core.helpers.Constants;
import com.edc.edcweb.core.service.EDCSystemConfigurationService;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;
import javax.servlet.Servlet;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@Component(
        immediate = true,
        service = Servlet.class,
        property = {
            "sling.servlet.methods=get",
            "sling.servlet.paths=/bin/sitemapgenerator"
        }, configurationPid = "com.edc.edcweb.core.servlets.SiteMapServlet")

@Designate(ocd = SiteMapServlet.SiteMapConfiguration.class)

public final class SiteMapServlet extends SlingSafeMethodsServlet {

    private static final long serialVersionUID = -3108000187767714325L;

    /**
     * OSGi configuration
     */
    @ObjectClassDefinition(name = "EDC Site Map Servlet")
    public @interface SiteMapConfiguration {

        @AttributeDefinition(name = "Exclude from Sitemap Property", description = "[cq:Page]/jcr:content properties name which indicates if the Page should be hidden from the Sitemap. Fixed values: hideInNav, excludepage, redirectTarget and cq:redirectTarget", type = AttributeType.STRING)
        String[] exclude_from_sitemap_property();

        @AttributeDefinition(name = "Extensionless URLs", description = "If true, page links included in sitemap are generated without .html extension and the path is included with a trailing slash, e.g. /content/geometrixx/en/.", type = AttributeType.BOOLEAN)
        boolean extensionless_urls() default DEFAULT_EXTENSIONLESS_URLS;

        @AttributeDefinition(name = "Remove Trailing Slash from Extensionless URLs", description = "Only relevant if Extensionless URLs is selected.  If true, the trailing slash is removed from extensionless page links, e.g. /content/geometrixx/en.", type = AttributeType.BOOLEAN)
        boolean remove_trailing_slash() default DEFAULT_REMOVE_TRAILING_SLASH;

        @AttributeDefinition(name = "Character Encoding", description = "If not set, the container's default is used (ISO-8859-1 for Jetty)", type = AttributeType.STRING)
        String character_encoding_property();

        @AttributeDefinition(name = "Schema version", description = "If not set, the default is used (http://www.sitemaps.org/schemas/sitemap/0.9)", type = AttributeType.STRING)
        String schema_version_property() default DEFAULT_SCHEMA_VERSION;
    }

    private static final boolean DEFAULT_EXTENSIONLESS_URLS = false;
    private static final boolean DEFAULT_REMOVE_TRAILING_SLASH = false;

    private static final String[] DEFAULT_EMPTY_STRING_ARRAY = new String[0];
    private static final String DEFAULT_SCHEMA_VERSION = "http://www.sitemaps.org/schemas/sitemap/0.9";

    @Reference
    @Inject
    private EDCSystemConfigurationService edcSystemConfiguration;

    private String[] excludeFromSiteMapConfigurableProperties = DEFAULT_EMPTY_STRING_ARRAY;
    private Set<String> excludeFromSiteMapProperties = new HashSet<>(Arrays.asList("excludepage", "redirectTarget", NameConstants.PN_REDIRECT_TARGET));
    private String[] excludeFromSiteMapInheritProperty = {"excludesubpages"};
    private String characterEncoding;
    private boolean extensionlessUrls;
    private boolean removeTrailingSlash;
    private String ns;

    @Activate
    @Modified
    protected void activate(final SiteMapConfiguration siteMapConfiguration) {
        this.characterEncoding = siteMapConfiguration.character_encoding_property();
        this.extensionlessUrls = siteMapConfiguration.extensionless_urls();
        this.removeTrailingSlash = siteMapConfiguration.remove_trailing_slash();
        this.excludeFromSiteMapConfigurableProperties = siteMapConfiguration.exclude_from_sitemap_property();
        this.ns = siteMapConfiguration.schema_version_property();
    }

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType(Constants.Properties.TEXT_SLASH_XML);
        response.setHeader(RestClientConstants.CACHE_CONTROL_HEADER, RestClientConstants.CACHE_CONTROL_HEADER_VALUE);
        response.setHeader(RestClientConstants.PRAGMA_HEADER, RestClientConstants.PRAGMA_HEADER_NO_CACHE_VALUE);
        response.setDateHeader(RestClientConstants.EXPIRES_HEADER, 0);

        if(this.excludeFromSiteMapConfigurableProperties != null) {
            this.excludeFromSiteMapProperties.addAll(Arrays.asList(this.excludeFromSiteMapConfigurableProperties));
        }

        if (StringUtils.isNotEmpty(this.characterEncoding)) {
            response.setCharacterEncoding(characterEncoding);
        }
        ResourceResolver resourceResolver = request.getResourceResolver();
        PageManager pageManager = resourceResolver.adaptTo(PageManager.class);

        // Generated from the root node of the website. This is not configurable.
        Page page = pageManager.getPage(Constants.Paths.CONTENT_EDC);

        XMLOutputFactory outputFactory = XMLOutputFactory.newFactory();
        try {
            XMLStreamWriter stream = outputFactory.createXMLStreamWriter(response.getWriter());
            stream.writeStartDocument("1.0");

            stream.writeStartElement("", "urlset", ns);
            stream.writeNamespace("", ns);

            // first do the current page
            write(page, stream, request);

            for (Iterator<Page> children = page.listChildren(new PageFilter(false, true), true); children.hasNext();) {
                write(children.next(), stream, request);
            }

            stream.writeEndElement();

            stream.writeEndDocument();
        } catch (XMLStreamException e) {
            throw new IOException(e);
        }
    }

    private void write(Page page, XMLStreamWriter stream, SlingHttpServletRequest request) throws XMLStreamException {
        if (isExcluded(page)) {
            return;
        }
        stream.writeStartElement(ns, "url");
        String loc = "";

        // Added by EDC team
        String campaignFormat = "(\\/en\\/|\\/fr\\/)campaign\\/";
        String htmlFormat = ".html$";
        ResourceResolver resourceResolver = request.getResourceResolver();
        String pagePath = resourceResolver.map(page.getPath()); // Get the Alias

        // Check if this replaces or aggregates attribute.
        request.setAttribute("srcPath", pagePath);
        request.setAttribute("scheme", request.getScheme());
        LinkExternalizer linkExternalizer = request.adaptTo(LinkExternalizer.class);

        if (!extensionlessUrls) {
            loc = String.format("%s.html", linkExternalizer.getPublishPageUrl());
        } else {
            String urlFormat = removeTrailingSlash ? "%s" : "%s/";
            loc = String.format(urlFormat, linkExternalizer.getPublishPageUrl());
        }

        // Added by EDC team (transform campaign urls)
        if (isCampaign(page.getPath())) {
            loc = loc.replaceAll(campaignFormat, "/").replaceAll(htmlFormat, "");
        }

        writeElement(stream, "loc", loc);

        stream.writeEndElement();
    }

    // Added/Modified by EDC team
    private boolean isExcluded(final Page page) {
        for (String property : this.excludeFromSiteMapProperties) {
            if (page.getProperties().containsKey(property)) {
                return true;
            }
        }

        if (this.excludeFromSiteMapInheritProperty.length > 0) {
            InheritanceValueMap inheritedProp = new HierarchyNodeInheritanceValueMap(page.getContentResource());
            for (String property : this.excludeFromSiteMapInheritProperty) {
                if (!page.getProperties().containsKey(property) && inheritedProp.getInherited(property, false)) {
                    return true;
                }
            }
        }

        return false;
    }

    // Added by EDC team - Check if it's campaign page
    private boolean isCampaign(String pagePath) {
        return pagePath.contains("/en/campaign/") || pagePath.contains("/fr/campaign/");
    }

    private void writeElement(final XMLStreamWriter stream, final String elementName, final String text)
            throws XMLStreamException {
        stream.writeStartElement(ns, elementName);
        stream.writeCharacters(text);
        stream.writeEndElement();
    }
}
