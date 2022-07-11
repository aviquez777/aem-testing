package com.edc.edcweb.core.models.covidresponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.jcr.query.Query;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;

import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.edc.edcweb.core.helpers.Constants;
import com.edc.edcweb.core.helpers.constants.ConstantsCovid;

@Model(adaptables = SlingHttpServletRequest.class)
public class Filter {


    @ScriptVariable
    private ValueMap properties;

    @Self
    private SlingHttpServletRequest request;

    @Inject
    private Page currentPage;

    @Inject
    private ResourceResolver resourceResolver;

    private boolean showUsedTagsOnly;
    private Tag rooTag;
    private List<String> usedTags = new ArrayList<>();
    private Map<String, Map<String, List<String>>> filterTags = new LinkedHashMap<>();
    private Map<String, String> titleTags = new HashMap<>();
    private Map<String, String> enTitleTags = new HashMap<>();

    @PostConstruct
    public void initModel() {
        String baseTagsNamespace = properties.get(ConstantsCovid.TAG_NAMESPACE_FIELD_NAME,
                Constants.Paths.EDC_NAMESPACE_TAGS.concat("/covid"));
        TagManager tm = resourceResolver.adaptTo(TagManager.class);
        rooTag = tm.resolve(baseTagsNamespace);
        /// no tags, nothing to do
        if (rooTag != null) {
            showUsedTagsOnly = properties.get(ConstantsCovid.SHOW_USED_TAGS_FIELD_NAME, false);
            // get used if necessary
            if (showUsedTagsOnly) {
                getUsedTags();
            }
            // Get all the tags
            getAllTags();
        }
    }

    /**
     * getAllTags: Map the Tags and titles so it'll be easier to consume by HTL
     */
    private void getAllTags() {
        Locale language = currentPage.getLanguage();
        Locale enLanguage = Locale.ENGLISH.stripExtensions();
        Iterator<Tag> level1Tags = rooTag.listChildren();
        while (level1Tags.hasNext()) {
            Tag level1Tag = level1Tags.next();
            Map<String, List<String>> level2Map = new LinkedHashMap<>();
            Iterator<Tag> level2Tags = level1Tag.listChildren();
            while (level2Tags.hasNext()) {
                Tag level2Tag = level2Tags.next();
                String level2Id = level2Tag.getTagID();
                Iterator<Tag> level3Tags = level2Tag.listChildren();
                List<String> level3List = new LinkedList<>();
                while (level3Tags.hasNext()) {
                    Tag level3Tag = level3Tags.next();
                    String level3Id = level3Tag.getTagID();
                    if (!showUsedTagsOnly || usedTags.contains(level3Id)) {
                        level3List.add(level3Id);
                        String level3Title = level3Tag.getTitle(language);
                        titleTags.put(level3Id, level3Title);
                        String level3EnTitle = level3Tag.getTitle(enLanguage);
                        enTitleTags.put(level3Id, level3EnTitle);
                        // Make sure we always include the parent level 2 card
                        usedTags.add(level2Id);
                    }
                }
                if (!showUsedTagsOnly || usedTags.contains(level2Id)) {
                    String level2Title = level2Tag.getTitle(language);
                    titleTags.put(level2Id, level2Title);
                    String level2EnTitle = level2Tag.getTitle(enLanguage);
                    enTitleTags.put(level2Id, level2EnTitle);
                    level2Map.put(level2Id, level3List);
                }
            }
            String level1Id = level1Tag.getTagID();
            filterTags.put(level1Id, level2Map);
            String level1Title = level1Tag.getTitle(language);
            titleTags.put(level1Id, level1Title);
            String level2EnTitle = level1Tag.getTitle(enLanguage);
            enTitleTags.put(level1Id, level2EnTitle);
        }
    }

    /**
     * getUsedTags: get all the tags used on the cards to show only these
     */
    private void getUsedTags() {
        Iterator<Resource> cardsResources = resourceResolver
                .findResources(String.format(ConstantsCovid.QUERY, currentPage.getContentResource().getPath()), Query.JCR_SQL2);
        while (cardsResources.hasNext()) {
            Resource card = cardsResources.next();
            String[] orgTags = card.getValueMap().get(ConstantsCovid.ORGANIZATION_TAGS, String[].class);
            String[] otherTags = card.getValueMap().get(ConstantsCovid.FILTER_TAGS, String[].class);
            String[] tags = ArrayUtils.addAll(orgTags, otherTags);
            if (tags != null && tags.length > 0) {
                usedTags.addAll(Arrays.asList(tags));
            }
        }
    }

    public Map<String, Map<String, List<String>>> getFilterTags() {
        return filterTags;
    }

    public Map<String, String> getTitleTags() {
        return titleTags;
    }

    public Map<String, String> getEnTitleTags() {
        return enTitleTags;
    }
}
