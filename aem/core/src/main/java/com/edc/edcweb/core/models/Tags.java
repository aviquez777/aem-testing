package com.edc.edcweb.core.models;

import javax.inject.Inject;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;

import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.edc.edcweb.core.helpers.Constants.ArticleContentType;
import com.edc.edcweb.core.helpers.Request;
import com.edc.edcweb.core.helpers.TagHelper;

/**
 * @author Peter Crummey
 * @version 1.0.0
 * @since 1.0.0
 * 
 * 
 *        This class is the model helper class to resolve tags used by the EDC web
 *        site.
 * 
 * 
 */
@Model(adaptables = SlingHttpServletRequest.class)
public class Tags {

    @Inject
    @Default(values = "")
    private String tagId;

    @Self
    private SlingHttpServletRequest request;

    public String getTitleByTagId() {
        /**
         * This method is used to return the localized title for given tag id.
         * 
         * @param tagId
         *            String containing the tag id
         * 
         * @return String with the localized title.
         */

        String title = null;
        // ---------------------------------------------------------------------
        TagManager tagManager = TagHelper.getTagManager(request);
        // --------------------------------------------------------------------
        Page page = Request.adaptToPage(this.request);
        // ---------------------------------------------------------------------
        title = TagHelper.getLocalizedTagTitle(tagManager, page, this.tagId);
        // ---------------------------------------------------------------------
        return title;
    }

    public Tag getTagByTagId() {
        /**
         * This method is used to return the Tag object for given tag id.
         * 
         * @param tagId
         *            String the tag id
         * 
         * @return Tag object
         */

        Tag tag = null;
        // ---------------------------------------------------------------------
        TagManager tagManager = TagHelper.getTagManager(request);
        // ---------------------------------------------------------------------
        if (tagManager != null) {
            tag = tagManager.resolve(this.tagId);
        }
        // ---------------------------------------------------------------------
        return tag;
    }

    public String getTagClassName() {
        /**
         * This method is used to return the class name used to display the associated
         * color's bullet from design's UI kit.
         * 
         * @param tagId
         *            String containing the tag id
         * 
         * @return String the class name
         */

        String classNamme = null;
        ArticleContentType articleContentType = ArticleContentType.getArticleContentTypeFromTagId(this.tagId);
        if (articleContentType != null) {
            classNamme = articleContentType.getClassName();
        }
        return classNamme;
    }
}
