package com.edc.edcweb.core.models;

import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.edc.edcweb.core.helpers.Constants;
import com.edc.edcweb.core.helpers.LanguageUtil;
import com.edc.edcweb.core.helpers.ResourceResolverHelper;
import com.edc.edcweb.core.helpers.TagHelper;
import com.edc.edcweb.core.insightsarticlesearch.helpers.InsightsSearchConstants;
import com.edc.edcweb.core.insightsarticlesearch.pojo.FilterSection;
import com.edc.edcweb.core.helpers.SearchForPages;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.Source;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import com.day.cq.wcm.api.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedList;

/**
 * @author oscar.hernandez
 * @version 1.0.0
 * @since 1.0.0
 *
 *
 * Model to retrieve information for "Insights Article Search"
 *
 *
 */
@Model(adaptables = SlingHttpServletRequest.class)
public class InsightsSearch {

    private static final Logger log = LoggerFactory.getLogger(InsightsSearch.class);

    @Inject
    @Source("sling-object")
    private ResourceResolver resolver;

    @Self
    SlingHttpServletRequest request;

    @ScriptVariable
    private ValueMap properties;

    @Inject
    @Optional
    private Page currentPage;

    private List<Page> pages;
    private String lang;
    private String[] tagIds = {Constants.TAGS_EDC_ACCESSTYPE};
    private List<FilterSection> filterSections = new LinkedList<>();


    @PostConstruct
    public void initModel() {
        lang = LanguageUtil.getLanguageAbbreviationFromPath(currentPage.getPath(), Constants.Properties.ENGLISH_ABBR);

        try {
            //---------------------------------------------------------------------
            loadPages();
            loadFilters();
        } catch (Exception e) {
            log.error("error ", e);
        }
    }

    private void loadFilters() {
        Resource filters = getFiltersResource();
        if (filters != null) {
            Iterator<Resource> filtersList = filters.listChildren();
            TagManager tagMgr = resolver.adaptTo(TagManager.class);

            while (filtersList.hasNext()) {
                //Create filters sections from multifield
                Resource filter = filtersList.next();
                FilterSection filterSection = new FilterSection();

                String parentTagVal = filter.getValueMap().get(InsightsSearchConstants.PARENT_TAG, String.class);
                Tag parentTag = tagMgr.resolve(parentTagVal);

                String filterName = parentTag.getName().toLowerCase();
                filterSection.setFilterName(filterName);

                String filterLabel = TagHelper.getLocalizedTagTitle(currentPage, parentTag);
                filterSection.setFilterLabel(filterLabel);

                boolean showMore = Boolean.parseBoolean(filter.getValueMap().get(InsightsSearchConstants.SHOW_MORE, String.class));
                filterSection.setShowMore(showMore);

                boolean isTrending = Boolean.parseBoolean(filter.getValueMap().get(InsightsSearchConstants.IS_TRENDING, String.class));
                filterSection.setTrending(isTrending);

                List<TagFilter> trendingTopics = null;
                if (isTrending) {
                    String trendingLabel = filter.getValueMap().get(InsightsSearchConstants.TRENDING_LABEL, String.class);
                    filterSection.setTrendingLabel(trendingLabel);

                    String[] trendingTopicsVal = filter.getValueMap().get(InsightsSearchConstants.TRENDING_TOPICS, String[].class);
                    if (ArrayUtils.isNotEmpty(trendingTopicsVal)) {
                        trendingTopics = getTagsbyListPaths(trendingTopicsVal);
                    }
                }
   
                String parentTagPath = parentTag.getPath();
                int depth = filter.getValueMap().get(InsightsSearchConstants.DEPTH, 1);
                String[] excludeVal = filter.getValueMap().get(InsightsSearchConstants.EXCLUDE, String[].class);
                Set<String> excludeList = new HashSet<>(Arrays.asList());
                if (ArrayUtils.isNotEmpty(excludeVal)) {
                    List<TagFilter> excludeTags = getTagsbyListPaths(excludeVal);
                    for (TagFilter exclude : excludeTags) {
                        // Bug 373842 use TagID
                        excludeList.add(exclude.getTagId());
                    }
                }
                List<TagFilter> subFilters = TagHelper.getTagsList(request, parentTagPath, true, lang, depth, excludeList);

                // If Trending use trendingTopics as subfilter
                if (isTrending) {
                    subFilters = trendingTopics;
                }
                filterSection.setSubFilters(subFilters);

                // Add filter section
                filterSections.add(filterSection);
            }
        }
    }

    private Resource getFiltersResource() {
        Resource compRes = null;
        Resource filtersRes = null;
        compRes = ResourceResolverHelper.getResourceByType(currentPage, InsightsSearchConstants.INSIGHTS_SEARCH_COMP);
        if (compRes != null) {
            filtersRes = compRes.getChild(InsightsSearchConstants.FILTER_TAGS);
        }
        return filtersRes;
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
                    String datalayer = tag.getTitle(Locale.ENGLISH);;
                    List<TagFilter> subTags = new ArrayList<>();
                    // Bug 373842 use TagID
                    TagFilter tagFilter = new TagFilter(StringUtils.isNotBlank(label) ? label : tag.getTitle(), StringUtils.isNotBlank(datalayer) ? datalayer : tag.getTitle(), tag.getTagID().replace(Constants.TAGS_EDC_NAMESPACE, ""), subTags, tag.getTagID());

                    listTag.add(tagFilter);
                }
            }
        }

        TagHelper.sortByValue(listTag, locale);

        return listTag;
    }

    private void loadPages() {
        // If selected, override the check of premium and non premium tags
        boolean showAllCards = Boolean.parseBoolean(properties.get(Constants.Properties.SHOW_ALL_CARDS, String.class));
        String[] customTagIds = properties.get(Constants.Properties.TAG_IDS, String[].class);        
        if (showAllCards && customTagIds.length != 0) {
            tagIds =  customTagIds;
        }
        //---------------------------------------------------------------------
        List<Page> pagesFromSearch =  SearchForPages.getPagesByTagIds(request, tagIds, true, properties.get(Constants.Properties.ARTICLES_PATH, String.class));
        //---------------------------------------------------------------------
        int maxItems = properties.get(Constants.Properties.ARTICLES_PER_PAGE, Constants.CARD_GRID_DEFAULT_ARTICLES_PER_PAGE);
        //---------------------------------------------------------------------
        this.pages = new ArrayList<>();
        for (Page page : pagesFromSearch) {

            Set<String> pageTags = new HashSet<>(Arrays.asList(page.getProperties().get(Constants.Properties.CQ_TAGS, String[].class)));

            if (!showAllCards) {
                showAllCards = (pageTags.contains(Constants.TAGS_EDC_PREMIUM) || pageTags.contains(Constants.TAGS_EDC_NONPREMIUM));
            }
            //-----------------------------------------------------------------
            // Do not add the current page to the results
            //-----------------------------------------------------------------
            if (((currentPage == null) || !page.getPath().equals(currentPage.getPath())) && showAllCards) {
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

    public List<Page> getPages() {
        return this.pages;
    }

    public List<FilterSection> getFilterSections() {
        return filterSections;
    }
}
