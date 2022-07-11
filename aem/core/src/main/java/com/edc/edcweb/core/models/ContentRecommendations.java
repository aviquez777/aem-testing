package com.edc.edcweb.core.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import javax.annotation.PostConstruct;
import javax.jcr.RepositoryException;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.models.List;
import com.day.cq.commons.RangeIterator;
import com.day.cq.search.Predicate;
import com.day.cq.search.SimpleSearch;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.designer.Style;
import com.edc.edcweb.core.helpers.ArticlePageHelper;
import com.edc.edcweb.core.helpers.Constants;
import com.edc.edcweb.core.helpers.TagHelper;
import com.fasterxml.jackson.annotation.JsonProperty;

@Model(adaptables = SlingHttpServletRequest.class, adapters = List.class, resourceType = ContentRecommendations.RESOURCE_TYPE)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)

public class ContentRecommendations implements List {

    protected static final String RESOURCE_TYPE = "edc/components/content/article/list";

    private static final Logger log = LoggerFactory.getLogger(ContentRecommendations.class);

    private static final int LIMIT_DEFAULT = 100;
    private static final boolean SHOW_DESCRIPTION_DEFAULT = false;
    private static final boolean SHOW_MODIFICATION_DATE_DEFAULT = false;
    private static final boolean LINK_ITEMS_DEFAULT = false;
    private static final int PN_DEPTH_DEFAULT = 1;
    private static final String PN_DATE_FORMAT_DEFAULT = "yyyy-MM-dd";
    private static final String TAGS_MATCH_ANY_VALUE = "any";
    private static final String TAGS_MATCH_ALL_VALUE = "all";

    @ScriptVariable
    private ValueMap properties;

    @ScriptVariable
    private Style currentStyle;

    @ScriptVariable
    private Page currentPage;

    @SlingObject
    private ResourceResolver resourceResolver;

    @SlingObject
    private Resource resource;

    @Self
    private SlingHttpServletRequest request;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(intValues = LIMIT_DEFAULT)
    private int limit;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(intValues = PN_DEPTH_DEFAULT)
    private int childDepth;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(values = StringUtils.EMPTY)
    private String query;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(intValues = Constants.CONTENT_REC_MAX_ITEMS)
    private int maxItems;

    private String startIn;
    private String dateFormatString;

    private boolean showDescription;
    private boolean showModificationDate;
    private boolean linkItems;

    private PageManager pageManager;
    private TagManager tagManager;
    private java.util.List<Page> listItems;

    private boolean returnOnlyPremium = false;

    private boolean matchAny;

    @PostConstruct
    private void initModel() {
        pageManager = resourceResolver.adaptTo(PageManager.class);
        tagManager = resourceResolver.adaptTo(TagManager.class);
        readProperties();
    }

    private void readProperties() {
        // read edit config properties
        startIn = properties.get(PN_SEARCH_IN, currentPage.getPath());

        // read design config properties
        showDescription = properties.get(PN_SHOW_DESCRIPTION,
                currentStyle.get(PN_SHOW_DESCRIPTION, SHOW_DESCRIPTION_DEFAULT));
        showModificationDate = properties.get(PN_SHOW_MODIFICATION_DATE,
                currentStyle.get(PN_SHOW_MODIFICATION_DATE, SHOW_MODIFICATION_DATE_DEFAULT));
        linkItems = properties.get(PN_LINK_ITEMS, currentStyle.get(PN_LINK_ITEMS, LINK_ITEMS_DEFAULT));
        dateFormatString = properties.get(PN_DATE_FORMAT, currentStyle.get(PN_DATE_FORMAT, PN_DATE_FORMAT_DEFAULT));

        this.returnOnlyPremium = this.properties
                .get(Constants.Properties.PREMIUM_OR_NOT, Constants.Properties.NON_DASH_PREMIUM)
                .equals(Constants.Properties.PREMIUM);

        Source listType = getListType();
        if (this.returnOnlyPremium && listType == Source.TAGS) {
            this.maxItems = Constants.CONTENT_REC_MAX_ITEMS_PREMIUM;
        }

    }

    @Override
    public Collection<Page> getItems() {
        if (listItems == null) {
            Source listType = getListType();
            populateListItems(listType);
        }
        return new ArrayList<>(listItems);
    }

    @Override
    @JsonProperty("linkItems")
    public boolean linkItems() {
        return linkItems;
    }

    @Override
    @JsonProperty("showDescription")
    public boolean showDescription() {
        return showDescription;
    }

    @Override
    @JsonProperty("showModificationDate")
    public boolean showModificationDate() {
        return showModificationDate;
    }

    @Override
    public String getDateFormatString() {
        return dateFormatString;
    }

    private Source getListType() {
        String listFromValue = properties.get(PN_SOURCE, currentStyle.get(PN_SOURCE, StringUtils.EMPTY));
        return Source.fromString(listFromValue);
    }

    private void populateListItems(Source listType) {
        boolean sort = true;

        switch (listType) {
        case STATIC:
            populateStaticListItems();
            // Author choose sort order by dragging in dialog
            sort = false;
            break;
        case CHILDREN:
            populateChildListItems();
            break;
        case TAGS:
            populateTagListItems();
            break;
        case SEARCH:
            populateSearchListItems();
            break;
        default:
            listItems = new ArrayList<>();
            break;
        }
        if (sort) {
            sortListItems();
        }
        setMaxItems();
    }

    private void populateStaticListItems() {
        listItems = new ArrayList<>();
        String[] pagesPaths = properties.get(PN_PAGES, new String[0]);
        for (String path : pagesPaths) {
            Page page = pageManager.getContainingPage(path);
            if ((page != null) && !this.isCurrentPage(page)) {
                listItems.add(page);
            }
        }
    }

    private void populateChildListItems() {
        listItems = new ArrayList<>();
        Page rootPage = getRootPage(PN_PARENT_PAGE);
        if (rootPage != null) {
            collectChildren(rootPage.getDepth(), rootPage);
        }
    }

    private void collectChildren(int startLevel, Page parent) {
        Iterator<Page> childIterator = parent.listChildren();
        while (childIterator.hasNext()) {
            Page child = childIterator.next();
            if ((child != null) && !this.isCurrentPage(child)) {
                listItems.add(child);
            }
            if ((child != null) && (child.getDepth() - startLevel < childDepth)) {
                collectChildren(startLevel, child);
            }
        }
    }

    private boolean isCurrentPage(Page page) {
        boolean isCurrent = false;
        if (page != null) {
            isCurrent = this.currentPage.equals(page);
        }
        return isCurrent;
    }

    private void populateTagListItems() {
        listItems = new ArrayList<>();
        String[] tags = properties.get(PN_TAGS, new String[0]);
        this.matchAny = properties.get(PN_TAGS_MATCH, TAGS_MATCH_ANY_VALUE).equals(TAGS_MATCH_ANY_VALUE);
        // If Override is true, do not addPremiumTag
        boolean override = Boolean.parseBoolean(properties.get("tagBehaviourOverrride", String.class));
        if (!override) {
            tags = this.addPremiumTag(tags);
        }
        if (log.isDebugEnabled()) {
            log.debug("We will find {} articles with {} of the these tags:",
                    this.returnOnlyPremium ? Constants.Properties.PREMIUM : Constants.Properties.NON_DASH_PREMIUM,
                    // matchAny not used and is always false, kept for future dev
                    this.matchAny ? TAGS_MATCH_ANY_VALUE : TAGS_MATCH_ALL_VALUE);
            log.debug(Arrays.toString(tags));
        }
        if (ArrayUtils.isNotEmpty(tags)) {
            queryTagManagerListItem(tags);
        }
    }

    private void queryTagManagerListItem(String[] tags) {
        Page rootPage = getRootPage(PN_TAGS_PARENT_PAGE);
        if (rootPage != null) {
            RangeIterator<Resource> resourceRangeIterator = tagManager.find(rootPage.getPath(), tags, matchAny);
            if (resourceRangeIterator != null) {
                while (resourceRangeIterator.hasNext()) {
                    Page containingPage = pageManager.getContainingPage(resourceRangeIterator.next());
                    if ((containingPage != null) && !this.isCurrentPage(containingPage)) {
                        listItems.add(containingPage);
                    }
                }
            }

        }

    }

    public String[] addPremiumTag(String[] tags) {
        String[] newTags = tags.clone();
        // ---------------------------------------------------------------------
        // Add the premium or non premium tag to the search.
        // ---------------------------------------------------------------------
        String tagPath = (this.returnOnlyPremium ? Constants.Paths.ACCESS_TYPE_PREMIUM
                : Constants.Paths.ACCESS_TYPE_NONPREMIUM);
        Tag tag = TagHelper.getTagManager(this.request).resolve(tagPath);

        if (tag != null) {
            ArrayList<String> stringArray = new ArrayList<>();
            for (String tagString : tags) {
                stringArray.add(tagString);
            }
            stringArray.add(tag.getTagID());
            newTags = stringArray.toArray(tags);
            if (log.isDebugEnabled()) {
                log.debug("Adding tag [{}] to list of desired tag(s).", tag.getTagID());
                log.debug("Here is new list of desired tags {}.", Arrays.toString(newTags));
            }
        }

        return newTags;
    }

    private void populateSearchListItems() {
        listItems = new ArrayList<>();
        if (!StringUtils.isBlank(query)) {
            SimpleSearch search = resource.adaptTo(SimpleSearch.class);
            if (search != null) {
                search.setQuery(query);
                search.setSearchIn(startIn);
                search.addPredicate(new Predicate("type", "type").set("type", NameConstants.NT_PAGE));
                search.setHitsPerPage(limit);
                try {
                    collectSearchResults(search.getResult());
                } catch (RepositoryException e) {
                    log.error("Unable to retrieve search results for query.", e);
                }
            }
        }
    }

    private void collectSearchResults(SearchResult result) throws RepositoryException {
        for (Hit hit : result.getHits()) {
            Page containingPage = pageManager.getContainingPage(hit.getResource());
            if ((containingPage != null) && !this.isCurrentPage(containingPage)) {
                listItems.add(containingPage);
            }
        }
    }

    private void sortListItems() {
        listItems.sort(new ArticlePageHelper.ListSort(ArticlePageHelper.SortOrder.DESC));
        if (log.isDebugEnabled()) {
            log.debug("Sorted " + listItems.size() + " pages - here is the sorted list:");
            for (Page page : listItems) {
                log.debug("Title [path]: {} Tags: {}", page.getPageTitle() + " [" + page.getPath() + "]",
                        Arrays.toString(page.getTags()));
            }
        }
    }

    private void setMaxItems() {
        if (maxItems != 0) {
            java.util.List<Page> tmpListItems = new ArrayList<>();
            for (Page item : listItems) {
                if (tmpListItems.size() < maxItems) {
                    tmpListItems.add(item);
                } else {
                    break;
                }
            }
            listItems = tmpListItems;
        }
    }

    private Page getRootPage(String fieldName) {
        String parentPath = properties.get(fieldName, currentPage.getPath());
        return pageManager.getContainingPage(resourceResolver.getResource(parentPath));
    }

    private enum Source {
        CHILDREN("children"), STATIC("static"), SEARCH("search"), TAGS("tags"), EMPTY(StringUtils.EMPTY);

        private String value;

        Source(String value) {
            this.value = value;
        }

        public static Source fromString(String value) {
            for (Source s : values()) {
                if (StringUtils.equals(value, s.value)) {
                    return s;
                }
            }
            return null;
        }
    }
}