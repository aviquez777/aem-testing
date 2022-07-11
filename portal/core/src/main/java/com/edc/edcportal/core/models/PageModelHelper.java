package com.edc.edcportal.core.models;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edc.edcportal.core.helpers.Constants;
import com.edc.edcportal.core.helpers.LinkResolver;

/**
 * <h1>Page Model Helper</h1> Helper functions to call from the HTL. getUrl():
 * Parses a give path and returns the url. getDate(): Retrieves the proper date
 * to display for the page;
 **/

@Model(adaptables = SlingHttpServletRequest.class)
public class PageModelHelper {

    private static final Logger log = LoggerFactory.getLogger(PageModelHelper.class);

    @Inject
    @Optional
    private String path;

    public String getUrl() {
        /**
         * This method is used to transform a given path into a properly formatted URL.
         * 
         * @param path String containing the page path.
         * 
         * @return String properly formatted URL.
         */
        String contentBase = Constants.Paths.CONTENT_MYEDC;
        if (StringUtils.isNotBlank(path) && !path.contains(contentBase)) {
            // link might be from edcweb
            contentBase = Constants.Paths.CONTENT_EDC;
        }
        String url = LinkResolver.addHtmlExtension(path, contentBase);
        // Validate the URL
        // If no protocol, might be internal, check if starts from the AEM's content
        // root,
        // If we limit to /content/edc could limit the DAM
        if (!url.startsWith(Constants.Paths.CONTENT) && !isValid(url)) {
            url = null;
        }

        return url;
    }

    /**
     * Internal method is used to check if the parsed url is correct;
     * 
     * @param url            String to check if given url is correct;
     * @param authorOverride Boolean to always return a date, regardless author
     *                       setting on page Default false: use author setting.
     * @return true if url is valid, false otherwise false
     */
    private static boolean isValid(String url) {
        // if object can be created is a valid url
        try {
            new URL(url).toURI();
            return true;
            // Task 22143 squid:S2221
        } catch (URISyntaxException | MalformedURLException e) {
            log.error("PageModelHelper isValid url: {} ", url, e);
            return false;
        }
    }
}