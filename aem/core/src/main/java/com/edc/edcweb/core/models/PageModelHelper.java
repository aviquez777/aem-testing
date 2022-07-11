package com.edc.edcweb.core.models;

import java.net.URL;
import java.util.Calendar;

import javax.inject.Inject;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;

import com.day.cq.wcm.api.Page;
import com.edc.edcweb.core.helpers.ArticlePageHelper;
import com.edc.edcweb.core.helpers.Constants;
import com.edc.edcweb.core.helpers.LinkResolver;

/**
 * <h1>Page Model Helper</h1> Helper functions to call from the HTL. getUrl():
 * Parses a give path and returns the url. getDate(): Retrieves the proper date
 * to display for the page;
 **/

@Model(adaptables = SlingHttpServletRequest.class)
public class PageModelHelper {

    @Inject
    @Optional
    private String path;

    public String getUrl() {
        /**
         * This method is used to transform a given path into a properly formatted URL.
         * 
         * @param path
         *            String containing the page path.
         * 
         * @return String properly formatted URL.
         */
        String url = LinkResolver.addHtmlExtension(path);
       // Validate the URL
       // If no protocol, might be internal, check if starts from the AEM's content root, 
       // If we limit to /content/edc could limit the DAM
        if (!url.startsWith(Constants.Paths.CONTENT) && !isValid(url)) {
            url = null;
        }

        return url;
    }

    @Inject
    @Optional
    private Page page;

    @Inject
    @Optional
    @Default(booleanValues = false)
    private Boolean authorOverride;

    public Calendar getDate() {
        /**
         * This method is used to retrieve the proper date to display for the page;
         * 
         * @param page
         *            Page to extract the date from.
         * @param authorOverride
         *            Boolean to always return a date, regardless author setting on page
         *            Default false: use author setting.
         * @return Calendar The requested date. Null if "Do not display a date" was
         *         selected by the author on Page properties and authorOverride is false
         */

        Calendar pageDate = null;
        ValueMap pageProperties = page.getProperties();
        Calendar defaultDate = page.getProperties().get(Constants.Properties.JCR_CREATED, Calendar.class);

        if (pageProperties.containsKey(Constants.Properties.PUBLISH_DATE)) {

            String publishDate = pageProperties.get(Constants.Properties.PUBLISH_DATE, String.class);
            if ((publishDate != null && !publishDate.equals(Constants.Properties.NO_DATE)) || authorOverride) {
                pageDate = ArticlePageHelper.getArticlePageSortDate(page, pageProperties, defaultDate);
            }

        }
        // If no date and requests override date, give default date 
        // 29536 AC #1 News releases always show the date
        if(pageDate == null && authorOverride) {
            pageDate = defaultDate;
        }

        return pageDate;
    }

    /**
     * Internal method is used to check if the parsed url is correct;
     * 
     * @param url
     *            String to check if given url is correct;
     * @param authorOverride
     *            Boolean to always return a date, regardless author setting on page
     *            Default false: use author setting.
     * @return true if url is valid, false otherwise false
     */
    private static boolean isValid(String url) {
        // if object can be created is a valid url
        try {
            new URL(url).toURI();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}