package com.edc.edcweb.core.models;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.day.cq.tagging.Tag;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.policies.ContentPolicy;
import com.edc.edcweb.core.helpers.ContentPolicyUtil;
import com.edc.edcweb.core.helpers.LinkResolver;
import com.edc.edcweb.core.helpers.SearchForPages;
import com.edc.edcweb.core.models.countryGrid.CountryGridItem;
import com.edc.edcweb.core.helpers.Constants;

/**
 * <h1>CountryGrid</h1> Sling model to provide support for the MEA Card Grid
 * component. initModel() @PostConstruct with compoent's main logic.
 * getCountryGridTitle() Returns The Component'ss Title. getGridList() Returns
 * the Country Grid List. getHasPolicy() Returns if the required policy is in
 * place. getLoadMoreButton() Returns the "Load More" button text,
 **/

@Model(adaptables = SlingHttpServletRequest.class)
public class CountryGrid {

    @Self
    private SlingHttpServletRequest request;

    @Inject
    private Page currentPage;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String loadMoreButton;

    private String nonPremiumPath;

    private String nonPremumTitle;

    private String premumTitle;

    private Map<String, List<CountryGridItem>> gridList = new HashMap<>();

    /**
     * This method is used to retrieve, sort and set the variables to return on the
     * getter methods Will check if the component is on a premium page and will set
     * the search path and title accordingly. Based on the search path, creates a
     * List of the items to show.
     * 
     **/

    @PostConstruct
    public void initModel() {
        // If no loadMoreButton don't event bother
        if (loadMoreButton != null && !loadMoreButton.isEmpty()) {
            // get policy properties
            ContentPolicy contentPolicy = ContentPolicyUtil.resolveLocalizedContentPolicy(request, currentPage);
            if (contentPolicy != null) {
                ValueMap properties = contentPolicy.getProperties();
                nonPremiumPath = properties.get(Constants.Properties.NON_PREMIUM_PATH,
                        String.class);
                nonPremumTitle = properties.get(Constants.Properties.NON_PREMIUM_TITLE,
                        String.class);
                premumTitle = properties.get(Constants.Properties.PREMIUM_TITLE,
                        String.class);
            }
            // get root page from the proper path
            if (nonPremiumPath != null) {
                Locale pageLocale = currentPage.getLanguage(true);
                String[] tagsToMatch = { Constants.tagIds.REGION_TAG_ID };
                // filter by Country Tag
                List<Page> pages = SearchForPages.getPagesByTagIds(request, tagsToMatch, true, nonPremiumPath);
                // loop over the pages and map properties as needed
                for (Page thisPage : pages) {
                    Tag[] thisPageTags = thisPage.getTags();
                    String thisCountryTitle = null;
                    String thisRegionTitle = null;
                    List<CountryGridItem> countryRegionList = new LinkedList<>();
                    // loop over the tags to create the region / country tag
                    for (Tag thisTag : thisPageTags) {
                        String thisTagId = thisTag.getTagID();
                        if (thisTagId.startsWith(Constants.tagIds.REGION_TAG_ID)) {
                            Tag parentTag = thisTag.getParent();
                            String parentTagId = parentTag.getParent().getTagID();
                            if (!parentTagId.equals(Constants.tagIds.REGION_TAG_ID)) {
                                parentTag = parentTag.getParent();
                            }
                            // localized from tags
                            thisCountryTitle = thisTag.getLocalizedTitle(pageLocale);
                            thisRegionTitle = parentTag.getLocalizedTitle(pageLocale);
                            // check the title
                            if (thisRegionTitle != null) {
                                CountryGridItem thisCountry = new CountryGridItem();
                                if (gridList.containsKey(thisRegionTitle)) {
                                    countryRegionList = gridList.get(thisRegionTitle);
                                }
                                // Set the Title and link
                                thisCountry.setCountryName(thisCountryTitle);
                                thisCountry.setCountryLink(LinkResolver.addHtmlExtension(thisPage.getPath()));
                                // get the images from page properties
                                ValueMap pageProperties = thisPage.getProperties();
                                // Set the values
                                thisCountry.setCountryImage(pageProperties
                                        .get(Constants.Properties.TEASER_IMAGE_SOURCE, String.class));
                                thisCountry.setCountryImageAltTxt(pageProperties
                                        .get(Constants.Properties.ARTICLE_IMAGE_ALT_TEXT, String.class));
                                // add the object to the list
                                countryRegionList.add(thisCountry);
                                // sort alphabetically
                                countryRegionList.sort(Comparator.comparing(CountryGridItem::getCountryName));
                                // add it to the grid list
                                gridList.put(thisRegionTitle, countryRegionList);
                            }
                            // break so we skip other tags
                            break;
                        }
                    }
                }
            }
        }
    }

    /**
     * This method is used to return the List of items to display. It is grouped by
     * region to create the different tabs
     * 
     * @return Map<String, List<CountryGridItem>> Map<Region Title, Country List);
     */
    public Map<String, List<CountryGridItem>> getGridList() {
        return gridList;
    }

    /**
     * This method is used to return whether the component policy is set on the
     * template manager. Title and paths are needed for component's function
     * 
     * @return Boolean true if policy is set, false otherwise
     */
    public Boolean getHasPolicy() {
        return (nonPremiumPath != null && !nonPremiumPath.isEmpty()) && (premumTitle != null && !premumTitle.isEmpty())
                && (nonPremumTitle != null && !nonPremumTitle.isEmpty());
    }

    /**
     * This method is used to return the Component's Non Premium title which is set
     * on the component's policy and show / hide depending on visitor's clearance
     * 
     * @return String with the non premium title
     */
    public String getNonPremumTitle() {
        return nonPremumTitle;
    }

    /**
     * This method is used to return the Component's Premium title which is set on
     * the component's policy and show / hide depending on visitor's clearance
     * 
     * @return String with the premium title
     */
    public String getPremumTitle() {
        return premumTitle;
    }

    /**
     * This method is used to return the "Load More" button text required for
     * components functionality Title and paths are needed for component's function
     * 
     * @return String with the authored text
     */
    public String getLoadMoreButton() {
        return loadMoreButton;
    }

    /**
     * This method is used to return if the pages is a premium page
     * 
     * @return boolean true if premium, false if not
     */
    public boolean isPremiumPage() {
        return currentPage.getPath().contains(Constants.Paths.PROGRESSIVEPROFILING_PREMIUMROOT);
    }

}
