package com.edc.edcweb.core.models;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;
import com.edc.edcweb.core.helpers.Constants;
import com.edc.edcweb.core.servlets.json.JsonFormatter;
import com.edc.edcweb.core.servlets.json.JsonFromPages;

/**
 * The BrandCampaignSentenceBuilder model retrieves the page for the sentence
 * builder, (default and multifield ones) Creates the Json with the page list
 * information Creates a tag list to display on the drop down Creates a list of
 * the default pages to display
 * 
 */

@Model(adaptables = SlingHttpServletRequest.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class CampaignSentenceBuilder {

    private static final Logger log = LoggerFactory.getLogger(CampaignSentenceBuilder.class);

    @Self
    private SlingHttpServletRequest request;

    @ScriptVariable
    private ValueMap properties;

    @Inject
    private ResourceResolver resourceResolver;
    
    @ValueMapValue
    private String defaulttag;
    
    @ValueMapValue
    private String globallinktarget;

    @ChildResource
    private Resource dropdownoptions;

    private List<Page> defaultPages = new LinkedList<>();

    private JSONArray pagesJson = new JSONArray();

    private Set<String> tagList = new LinkedHashSet<>();
    
    private String[] fieldNames = new String[] {"page1", "page2", "page3"};

    @PostConstruct
    public void initModel() {
        //add the default tag to the list
        tagList.add(defaulttag);
        // Get Default pages
        for (String fieldName : fieldNames) {
            String defaultFieldName = "default".concat(fieldName);
            String defaultPagePath = properties.get(defaultFieldName, String.class);
            Page defaultPage = resourceResolver.resolve(defaultPagePath).adaptTo(Page.class);
            if (defaultPage != null) {
                defaultPages.add(defaultPage);
            }
        }
        // Get Drop down pages
        if (dropdownoptions != null) {
            List<Page> tempPageList = new LinkedList<>();
            Map<Integer, String[]> tagMap = new HashMap<>();
            Iterator<Resource> iterator = dropdownoptions.listChildren();
            Integer id = 1;
            while (iterator.hasNext()) {
                Resource thisTag = iterator.next();
                String tag = thisTag.getValueMap().get("label", String.class);
                // Loop over the page variables
                for (String fieldName : fieldNames) {
                    String pagePath = thisTag.getValueMap().get(fieldName, String.class);
                    if (pagePath != null) {
                        Page tagPage = resourceResolver.resolve(pagePath).adaptTo(Page.class);
                        if (tagPage != null) {
                            tagList.add(tag);
                            tempPageList.add(tagPage);
                            tagMap.put(id, new String[] { tag });
                            id++;
                        }
                    }
                }
            }
            // Create the Json (don't add the tag array yet)
            JsonFormatter jsonFormatter = new JsonFromPages(false, false);
            // Add the Tag array based on the pages found
            try {
                pagesJson = jsonFormatter.createJsonFromListOfPages(request, tempPageList);
                // Loop over the json to Add the "custom tag"
                for (int i = 0; i < pagesJson.length(); i++) {
                    JSONObject theline = pagesJson.getJSONObject(i);
                    int jsonid = theline.getInt(Constants.Properties.ID);
                    // add this tag to the tag json
                    theline.put(Constants.Properties.TAGS, new JSONArray(tagMap.get(jsonid)));
                    theline.put(Constants.Properties.TARGET, globallinktarget);
                }
            } catch (ServletException | JSONException e) {
                log.error("Json Error", e);
            }
        }

    }

    /**
     * This method returns a SET List of the tags to create the drop down.
     * Using Set to avoid duplicates while creating the list
     * 
     * @param none
     * @return Set<String> with authored tag's names.
     */
    public Set<String> getTagList() {
        return tagList;
    }

    /**
     * This method returns a List of pages entered by the author as the default
     * pages to show on page load.
     * 
     * @param none
     * @return List<Page>
     */
    public List<Page> getDefaultPages() {
        return defaultPages;
    }

    /**
     * This method returns a pages entered by the author as the pages to be loaded
     * based on the drop down tag selection.
     * 
     * @param none
     * @return List<Page>
     * 
     */
    public String getPagesJson() {
        return pagesJson.toString();
    }
}
