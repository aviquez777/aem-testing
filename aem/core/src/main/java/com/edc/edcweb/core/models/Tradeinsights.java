package com.edc.edcweb.core.models;

import static java.util.Map.Entry.comparingByValue;
import static java.util.stream.Collectors.toMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.edc.edcweb.core.helpers.Constants;
import com.edc.edcweb.core.helpers.LanguageUtil;
import com.edc.edcweb.core.helpers.SearchForPages;
import com.edc.edcweb.core.helpers.TagHelper;

/**
 * @author lauren.alfaro
 * @version 1.0.0
 * @since 1.0.0
 *
 *
 * Model to retrieve information for "TradeInsights Article Search"
 *
 *
 */
@Model(adaptables = SlingHttpServletRequest.class)
public class Tradeinsights {

    private static final Logger log = LoggerFactory.getLogger(Tradeinsights.class);

    @Self
    SlingHttpServletRequest request;

    @ScriptVariable
    private ValueMap properties;

    @Inject
    @Optional
    private Page currentPage;

    private List<Page> pages;
    private List<TagFilter> categories;
    private List<TagFilter> industries;
    private List<TagFilter> formats;
    private List<TagFilter> regions;
    private List<TagFilter> trending;
    private Map<String, Integer> filterOrder = new LinkedHashMap<>();
    private String lang;
    private String[] tagIds = {Constants.TAGS_EDC_ACCESSTYPE};

    @PostConstruct
    public void initModel() {
        lang = LanguageUtil.getLanguageAbbreviationFromPath(currentPage.getPath(), Constants.Properties.ENGLISH_ABBR);

        try {
            //---------------------------------------------------------------------
            loadPages();
            loadFilters();
            orderFilter();
        } catch (Exception e) {
            log.error("error ", e);
        }
    }

    private void loadFilters() {
        categories = TagHelper.getTagsList(request, Constants.Paths.CATEGORY_TAGS, true, lang, 2, null);
        industries = TagHelper.getTagsList(request, Constants.Paths.INDUSTRY_TAGS, true, lang, 1, null);
        formats = TagHelper.getTagsList(request, Constants.Paths.CONTENT_TYPE_TAGS, true, lang, 1, new HashSet<>(Arrays.asList("News release", "News Release")));
        regions = TagHelper.getTagsList(request, Constants.Paths.REGION_TAGS, true, lang, 1, null);
        trending = getTagsbyListPaths(properties.get(Constants.Properties.TAG_TRENDING_IDS, String[].class));
    }

    private List<TagFilter> getTagsbyListPaths(String[] paths) {
        List<TagFilter> listTag = new ArrayList<>();
        Locale locale = new Locale(lang);
        TagManager tagManager = request.getResourceResolver().adaptTo(TagManager.class);

        if (tagManager != null) {
            for (String path : paths) {
                Tag tag = tagManager.resolve(path);

                // Check if the tag exist and shouldn't be exclude from list
                if (tag != null && tag.getCount() > 0) {
                    String label = tag.getLocalizedTitle(locale);
                    String datalayer = tag.getTitle(Locale.ENGLISH);
                    List<TagFilter> subTags = new ArrayList<>();

                    TagFilter tagFilter = new TagFilter(StringUtils.isNotBlank(label) ? label : tag.getTitle(), StringUtils.isNotBlank(datalayer) ? datalayer : tag.getTitle(), tag.getTagID().replace(Constants.TAGS_EDC_NAMESPACE, ""), subTags, tag.getTagID());

                    listTag.add(tagFilter);
                }
            }
        }

        TagHelper.sortByValue(listTag, locale);

        return listTag;
    }

    private void loadPages() {
        //---------------------------------------------------------------------
        List<Page> pagesFromSearch =  SearchForPages.getPagesByTagIds(request, tagIds, true, properties.get(Constants.Properties.ARTICLES_PATH, String.class));
        //---------------------------------------------------------------------
        int maxItems = properties.get(Constants.Properties.ARTICLES_PER_PAGE, Constants.CARD_GRID_DEFAULT_ARTICLES_PER_PAGE);
        //---------------------------------------------------------------------
        this.pages = new ArrayList<>();
        for (Page page : pagesFromSearch) {
            // Bug 373842 TagID not used
            Set<String> pageTags = new HashSet<>(Arrays.asList(page.getProperties().get(Constants.Properties.CQ_TAGS, String[].class)));

            //-----------------------------------------------------------------
            // Do not add the current page to the results
            //-----------------------------------------------------------------
            if (((currentPage == null) || !page.getPath().equals(currentPage.getPath())) && (pageTags.contains(Constants.TAGS_EDC_PREMIUM) || pageTags.contains(Constants.TAGS_EDC_NONPREMIUM))) {
                this.pages.add(page);
            }
            //-----------------------------------------------------------------
            // If we have maxItems pages in the list, we're done...
            //-----------------------------------------------------------------
            if (this.pages.size() == maxItems) {
                break;
            }
        }
    }

    private void orderFilter() {
        filterOrder.put("category", properties.get(Constants.Properties.TRADEINSIGHTS_CATEGORYORDER, Constants.TRADEINSIGHTS_DEFAULT_ORDER));
        filterOrder.put("trending", properties.get(Constants.Properties.TRADEINSIGHTS_TRENDINGORDER, Constants.TRADEINSIGHTS_DEFAULT_ORDER));
        filterOrder.put("industry", properties.get(Constants.Properties.TRADEINSIGHTS_INDUSTRYORDER, Constants.TRADEINSIGHTS_DEFAULT_ORDER));
        filterOrder.put("region", properties.get(Constants.Properties.TRADEINSIGHTS_REGIONORDER, Constants.TRADEINSIGHTS_DEFAULT_ORDER));
        filterOrder.put("format", properties.get(Constants.Properties.TRADEINSIGHTS_FORMATORDER, Constants.TRADEINSIGHTS_DEFAULT_ORDER));

        filterOrder = filterOrder.entrySet()
                .stream()
                .sorted(comparingByValue())
                .collect(
                    toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                        LinkedHashMap::new));
    }

    public List<Page> getPages() {
        return this.pages;
    }

    public List<TagFilter> getCategories() {
        return this.categories;
    }

    public List<TagFilter> getIndustries() {
        return this.industries;
    }

    public List<TagFilter> getRegions() {
        return this.regions;
    }

    public List<TagFilter> getFormats() {
        return this.formats;
    }

    public List<TagFilter> getTrending() {
        return this.trending;
    }

    public Map<String, Integer> getFilterOrder() {
        return filterOrder;
    }
}
