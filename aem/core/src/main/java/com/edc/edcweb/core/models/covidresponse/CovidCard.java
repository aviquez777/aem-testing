package com.edc.edcweb.core.models.covidresponse;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

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
import com.edc.edcweb.core.helpers.constants.ConstantsCovid;

/**
 * @author Oscar Hernandez
 * @version 1.0.0
 * @since 1.0.0
 *
 */

@Model(adaptables = SlingHttpServletRequest.class)
public class CovidCard {

    @ScriptVariable
    private ValueMap properties;

    @Self
    private SlingHttpServletRequest request;

    @Inject
    private Resource resource;

    @Inject
    private Page currentPage;

    private String tags;
    private String[] tagsArr;

    @PostConstruct
    public void initModel() {
        String[] orgTags = properties.get(ConstantsCovid.ORGANIZATION_TAGS, String[].class);
        String[] otherTags = properties.get(ConstantsCovid.FILTER_TAGS, String[].class);
        tagsArr = ArrayUtils.addAll(orgTags, otherTags);
        if (tagsArr != null && tagsArr.length > 0) {
            tags = String.join(ConstantsCovid.FILTER_SEPARATOR, tagsArr);
        }
    }

    public String getFirstTagValue() {
        String firstTagValue = null;
        if (tagsArr != null && tagsArr.length > 0) {
            String firsttag = tagsArr[0];
            ResourceResolver resolver = this.resource.getResourceResolver();
            TagManager tm = resolver.adaptTo(TagManager.class);
            Tag tag = tm.resolve(firsttag);
            if (tag != null) {
                firstTagValue = tag.getLocalizedTitle(currentPage.getLanguage());
            }
        }
        return firstTagValue;
    }

    public String getTags() {
        return tags;
    }
}
