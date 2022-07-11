package com.edc.edcweb.core.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ValueMap;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.tagging.Tag;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.policies.ContentPolicy;
import com.edc.edcweb.core.helpers.Constants;
import com.edc.edcweb.core.helpers.ContentPolicyUtil;
import com.edc.edcweb.core.helpers.LinkResolver;
import com.edc.edcweb.core.helpers.Request;
import com.edc.edcweb.core.helpers.SearchForPages;
import com.edc.edcweb.core.helpers.TagResult;
import com.edc.edcweb.core.helpers.constants.ConstantsWebinars;
import com.edc.edcweb.core.helpers.webinars.WebinarsHelper;

public class CardGrid extends WCMUsePojo {
    private List<Page> pages;

    // For country template card crid
    private HashMap<String, String> valueMapCountryMode = new HashMap<>();
    private String countryCta;
    private String countryCtaUrl;
    private String linkTarget;
    private String countryTag = "";

    private Page currentPage;
    private ValueMap properties;

    @Override
    public void activate() throws Exception {
        currentPage = Request.adaptToPage(getRequest());
        properties = getResource().getValueMap();
        // ---------------------------------------------------------------------

        initCountryMode();
        if (!this.countryTag.isEmpty()) {
            loadPagesCountryMode();
        } else {
            loadPages();
        }

    }

    private void loadPages() {
        // ---------------------------------------------------------------------
        List<Page> pagesFromSearch = TagResult.findMatchingPages(getRequest(), properties);
        // ---------------------------------------------------------------------
        int maxItems = properties.get(Constants.Properties.ARTICLES_PER_PAGE,
                Constants.CARD_GRID_DEFAULT_ARTICLES_PER_PAGE);
        // ---------------------------------------------------------------------
        this.pages = new ArrayList<>();
        // Task 71941 Check if we are on the "events & webinars landing page"
        boolean isEventPage = currentPage.getPath().endsWith(Constants.Paths.EVENTS);
        for (Page page : pagesFromSearch) {
            // Get this page's properties
            Set<?> pageTags = new HashSet<>(
                    Arrays.asList(page.getProperties().get(Constants.Properties.CQ_TAGS, String[].class)));
            // HERE If selected, override the check of premium and non premium tags
            boolean showAllCards = Boolean.parseBoolean(properties.get(Constants.Properties.SHOW_ALL_CARDS, String.class));
            if (!showAllCards) {
                showAllCards = (pageTags.contains(Constants.TAGS_EDC_PREMIUM) || pageTags.contains(Constants.TAGS_EDC_NONPREMIUM));
            }
            // -----------------------------------------------------------------
            // Do not add the current page to the results
            // -----------------------------------------------------------------
            if (!page.getPath().equals(currentPage.getPath()) && showAllCards) {
                // always add page
                boolean addPage = true;
                // Task 71941 if current page is the events & webinars landing page only (other
                // pages will user normal behavior) and if this page from the list is a webinar.
                if (isEventPage && pageTags.contains(Constants.TAGS_EDC_EVENT_TYPE)) {
                    // add this page to the gird only if the webinar status is "On Demand"
                    addPage = ConstantsWebinars.WEBINAR_STATE_ONDEMAND.equals(WebinarsHelper.getWebinarStatus(page, 0));
                }
                if (addPage) {
                    this.pages.add(page);
                }
            }
            // -----------------------------------------------------------------
            // If we have maxItems pages in the list, we're done...
            // -----------------------------------------------------------------
            if (this.pages.size() == maxItems) {
                break;
            }
        }

    }

    private void loadPagesCountryMode() {
        // ---------------------------------------------------------------------
        List<Page> pagesFromSearch = findCountryMatchingPages(getRequest(), properties);
        // ---------------------------------------------------------------------
        int maxItems = properties.get(Constants.Properties.ARTICLES_COUNTRY_PER_PAGE,
                Constants.CARD_GRID_COUNTRY_DEFAULT_ARTICLES_PER_PAGE);
        // ---------------------------------------------------------------------
        this.pages = new ArrayList<>();
        for (Page page : pagesFromSearch) {
            if ((currentPage == null) || !page.getPath().equals(currentPage.getPath())) {
                this.pages.add(page);
            }
            if (this.pages.size() == maxItems) {
                valueMapCountryMode.put(Constants.Properties.COUNTRYCTA, countryCta);
                buildCountryTagResultPath();
                valueMapCountryMode.put(Constants.Properties.COUNTRYCTAURL, countryCtaUrl);
                valueMapCountryMode.put(Constants.Properties.LINKTARGET, linkTarget);
                break;
            }
        }

    }

    private void buildCountryTagResultPath() {

        String countryPart = currentPage.getPath().substring(currentPage.getPath().lastIndexOf('/'));

        countryCtaUrl = countryCtaUrl.concat(countryPart);
        countryCtaUrl = LinkResolver.addHtmlExtension(countryCtaUrl);

    }

    private void initCountryMode() {

        ContentPolicy contentPolicy = ContentPolicyUtil.resolveLocalizedContentPolicy(getRequest(), currentPage);
        if (contentPolicy != null) {

            ValueMap propertiesPolicy = contentPolicy.getProperties();
            countryCta = propertiesPolicy.get(Constants.Properties.COUNTRYCTA, String.class);
            countryCtaUrl = propertiesPolicy.get(Constants.Properties.COUNTRYCTAURL, String.class);
            linkTarget = propertiesPolicy.get(Constants.Properties.LINKTARGET, String.class);
            if (countryCta != null && !countryCta.isEmpty() && countryCtaUrl != null) {
                countryTag = checkPageRegionTag();
            }
        }

    }

    private String checkPageRegionTag() {
        String tagPath = "";
        Tag[] tags = currentPage.getTags();

        for (Tag tag : tags) {
            tagPath = tag.getPath();
            if (tagPath != null && tagPath.startsWith(Constants.Paths.REGION_TAGS)) {
                return tagPath;
            }
        }

        return tagPath;
    }

    private List<Page> findCountryMatchingPages(SlingHttpServletRequest request, ValueMap properties) {
        String nonpremium = Constants.Paths.ACCESS_TYPE_NONPREMIUM;
        String startingPage = properties.get(Constants.Properties.ARTICLES_PATH, String.class);
        String[] tagIds = { countryTag, nonpremium };
        // ---------------------------------------------------------------------
        return SearchForPages.getPagesByTagIds(request, tagIds, false, startingPage);
    }

    public List<Page> getPages() {
        return this.pages;
    }

    public Map<String, String> getValueMapCountryMode() {
        if (valueMapCountryMode != null) {
            return valueMapCountryMode;
        }
        return null;
    }

    public boolean getCountryMode() {
        return !this.countryTag.isEmpty() && !this.pages.isEmpty();
    }

}
