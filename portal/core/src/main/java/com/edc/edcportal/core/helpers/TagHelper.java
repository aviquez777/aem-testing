package com.edc.edcportal.core.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.edc.edcportal.core.helpers.Constants.ArticleContentType;

/**
 * @author Peter Crummey
 * @version 0.0.3
 * @since 0.0.3
 * 
 * 
 * Helper functions for tags.
 * 
 */
public class TagHelper
{
    private static final Logger log = LoggerFactory.getLogger(TagHelper.class);
    private TagHelper()
    {

    }

    /**
     * Iterates over sub tags to get list of tags
     * @param request
     * @param itr
     * @return Map object with the list of tags
     */
    private static Map<String, String> getChildTags(SlingHttpServletRequest request, Iterator<Tag> itr) {
        Map<String, String> tagsList = new HashMap<>();
        TagManager tagManager = request.getResourceResolver().adaptTo(TagManager.class);
        Page page = Request.adaptToPage(request);

        if (itr.hasNext()) {

            while (itr.hasNext()) {
                Tag tag = itr.next();
                Iterator<Tag> childTags = tag.listAllSubTags();

                if (childTags.hasNext()) {
                    Map<String,String> tempTags = getChildTags(request, childTags);
                    tagsList.putAll(tempTags);
                } else {
                    tagsList.put(tag.getName(), TagHelper.getLocalizedTagTitle(tagManager, page, tag.getTagID()));
                }
            }
        }

        return tagsList;
    }
    private static Map<String, String> getChildTagsUntilLeaf(SlingHttpServletRequest request, Iterator<Tag> itr, List<String> listOfLeafToStopAt) {
        Map<String, String> tagsList = new HashMap<>();
        TagManager tagManager = request.getResourceResolver().adaptTo(TagManager.class);
        Page page = Request.adaptToPage(request);

        if (itr.hasNext()) {

            while (itr.hasNext()) {
                Tag tag = itr.next();
                Iterator<Tag> childTags = tag.listChildren();
                String testTag = tag.getPath();
                String testTagID = tag.getTagID();
                log.debug(testTag);
                log.debug(testTagID);
                if (childTags.hasNext() && !listOfLeafToStopAt.contains(tag.getPath())) {
                    Map<String,String> tempTags = getChildTagsUntilLeaf(request, childTags, listOfLeafToStopAt);
                    tagsList.putAll(tempTags);
                } else {
                    tagsList.put(tag.getName(), TagHelper.getLocalizedTagTitle(tagManager, page, tag.getTagID()));
                }
            }
        }

        return tagsList;
    }

    

    /**
     * Returns sorted Map by value
     * @param map
     * @return Map object with sorted by value
     */
    private static Map<String, String> sortByValue(Map<String, String> map) {
        List<Map.Entry<String, String>> list = new ArrayList<>(map.entrySet());
        list.sort(Map.Entry.comparingByValue());

        Map<String, String> result = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }

    /**
     * Get the localized (i.e., language-appropriate) title for the given tag id. Note that if a title matching the page's language
     * cannot be found, the title of the tag is returned instead.
     *
     * @param  tagManager  Instantiated TagManager object.
     * @param  page  Page containing the tag (used to determine the appropriate language).
     * @param  tagId  Id of the tag whose title is to be retrieved (e.g., "edc:category/enter-target-markets/delivering-your-goods").
     * @return Given tag id's localized title.
     */
    public static String getLocalizedTagTitle(TagManager tagManager, Page page, String tagId)
    {
        String title="";
        Tag    tag=null;
        if(tagManager != null)
        {
            tag = tagManager.resolve(tagId);

            title = getLocalizedTagTitle(page, tag);
        }
        return title;
    }

    /**
     * Get the localized (i.e., language-appropriate) title for the given tag id. Note that if a title matching the page's language
     * cannot be found, the title of the tag is returned instead.
     *
     * @param  page  Page containing the tag (used to determine the appropriate language).
     * @param  tagId  Id of the tag whose title is to be retrieved (e.g., "edc:category/enter-target-markets/delivering-your-goods").
     * @return Given tag id's localized title.
     */
    public static String getLocalizedTagTitle(Page page, Tag tag)
    {
        String title="";
        if(tag != null)
        {
            if(page != null)
            {
                title = tag.getLocalizedTitle(page.getLanguage());
            }
            if(title == null)
            {
                title = tag.getTitle();
            }
        }
        return title;
    }

    /**
     * Get a TagManager object from the request
     *
     * @param  request  Sling HTTP servlet request.
     * @return TagManager from the given request.
     */
    public static TagManager getTagManager(SlingHttpServletRequest request)
    {
        return request.getResource().getResourceResolver().adaptTo(TagManager.class);
    }


    //If ANY of the tag id is found for the page


    //If ANY of the tag id is found for the page
    public static Boolean isPageTagged(Page page,  List<ArticleContentType> tagIds )
    {
        log.debug("isPageTagged");
         Boolean isTagged = false;

         if(page != null)
        {
            Tag[] tags = page.getTags();

            for(Tag tag : tags)
            {
                log.debug("page tag path: {} - tag id{}" , tag.getPath(), tag.getTagID());

                for(ArticleContentType contentType : tagIds)
                {	log.debug("input contentType id {}", contentType.getTagId());
                    if ( contentType.getTagId().equalsIgnoreCase(tag.getTagID()))
                    {
                        isTagged = true;
                        log.debug("found path {}", tag.getTagID());
                        return isTagged;

                    }
                }

            }
        }

        log.debug("tag was not found");
        return isTagged;
    }
    public static Boolean isPageTagged(Page page,  String tagPath )
    {
        log.debug("isPageTagged tagPath: {}",tagPath);
        Boolean isTagged = false;

        if(page != null)
        {
            Tag[] tags = page.getTags();

            for(Tag tag : tags)
            {
                log.debug("page tag path: {} - tag id{}" , tag.getPath(), tag.getTagID());
                if ( tagPath.equalsIgnoreCase(tag.getPath()))
                {
                    isTagged = true;
                    log.debug("found path {}", tag.getPath());
                    return isTagged;
                }
            }
        }

        log.debug("tag was not found");
        return isTagged;
    }

    /**
     *
     * @param tags
     * @param tagpath
     * @return the name of the tags with the same base path separated by comma
     */
    public static String getTagsByName(Tag[] tags, String tagpath) {
        String tagList = "";
        if (tags != null && tags.length > 0) {
            for (Tag tag : tags) {
                if (tag.getPath().contains(tagpath)) {
                    String tagTitle = tag.getTitle();
                    tagList = tagList.equals("") ? tagTitle : tagList.concat(",").concat(tagTitle);
                }
            }
        }
        return tagList;
    }

    /**
     *
     * @param tags
     * @return the name of the category tags separated by comma
     */
    public static String getCategoryTags(Tag[] tags, ResourceResolver resolver) {
        Matcher matcher;
        String categoryTagPath;
        Tag categoryTag;

        String tagList = "";
        Pattern pattern = Pattern.compile("/etc/tags/edc/category/.+/");
        TagManager tagMgr = resolver.adaptTo(TagManager.class);

        if (tags != null && tags.length > 0) {
            for (Tag tag : tags) {
                matcher = pattern.matcher(tag.getPath());
                if (matcher.find()) {
                    categoryTagPath = matcher.group(0);
                    categoryTag = tagMgr.resolve(categoryTagPath);
                    if (categoryTag != null) {
                        tagList = tagList.equals("") ? categoryTag.getTitle() : tagList.concat(",").concat(categoryTag.getTitle());
                    }
                }
            }
        }

        return tagList;
    }

    /**
     * Returns the list of tags under the given path, eg: "/etc/tags/edc/category"
     * @param request
     * @param tagPath
     * @return Map object with the list of tags
     */
    public static Map<String, String> getTagsList(SlingHttpServletRequest request, String tagPath, boolean sort) {
        Map<String, String> allTags = new HashMap<>();
        Resource tagResource = request.getResourceResolver().getResource(tagPath);

        if (tagResource!= null) {
            Iterable<Resource> children = tagResource.getChildren();
            Iterator<Resource> itr = children.iterator();

            TagManager tagManager = request.getResourceResolver().adaptTo(TagManager.class);
            Page page = Request.adaptToPage(request);

            while (itr.hasNext()) {
                Resource item = itr.next();
                Tag tag = item.adaptTo(Tag.class);

                if (tag != null) {
                    Iterator<Tag> childitr = tag.listAllSubTags();

                    if (childitr.hasNext()) {
                        allTags.putAll(getChildTags(request, childitr));
                    } else {
                        allTags.put(tag.getName(), TagHelper.getLocalizedTagTitle(tagManager, page, tag.getTagID()));
                    }
                }
            }
        }

        if (sort) {
            allTags = sortByValue(allTags);
        }

        return allTags;
    }
    
    public static Map<String, String> getTagsListUntilLeaf(SlingHttpServletRequest request, String tagPath, boolean sort, List<String> listOfLeaf) {
        Map<String, String> allTags = new HashMap<>();
        Resource tagResource = request.getResourceResolver().getResource(tagPath);

        if (tagResource!= null) {
            Iterable<Resource> children = tagResource.getChildren();
            Iterator<Resource> itr = children.iterator();

            TagManager tagManager = request.getResourceResolver().adaptTo(TagManager.class);
            Page page = Request.adaptToPage(request);

            while (itr.hasNext()) {
                Resource item = itr.next();
                Tag tag = item.adaptTo(Tag.class);

                if (tag != null) {
                    Iterator<Tag> childitr = tag.listChildren();

                    if (childitr.hasNext() && !listOfLeaf.contains(tag.getPath())) {
                        allTags.putAll(getChildTagsUntilLeaf(request, childitr, listOfLeaf));
                    } else {
                        allTags.put(tag.getName(), TagHelper.getLocalizedTagTitle(tagManager, page, tag.getTagID()));
                    }
                }
            }
        }

        if (sort) {
            allTags = sortByValue(allTags);
        }

        return allTags;
    }
    
}