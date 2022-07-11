package com.edc.edcweb.core.models;

import com.day.cq.tagging.Tag;
import com.day.cq.wcm.api.Page;
import com.edc.edcweb.core.helpers.Constants;
import com.edc.edcweb.core.helpers.LanguageUtil;
import com.edc.edcweb.core.helpers.TagHelper;
import com.edc.edcweb.core.helpers.constants.ConstantsForm;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.Source;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

@Model(adaptables = SlingHttpServletRequest.class)
public class EloquaData {
    private static final Logger log = LoggerFactory.getLogger(EloquaData.class);

    @Inject
    @Source("sling-object")
    private ResourceResolver resolver;

    @Inject
    @Optional
    private Page currentPage;

    private String ownerTags;
    private String exportStatusTags;
    private String personaTags;
    private String subCategoryTags;
    private String categoryTags;
    private String docId;
    private String assetTier;
    private String pageLanguage;

    @PostConstruct
    public void initModel() {
        Tag[] tags = currentPage.getTags();
        ValueMap pageProperties = currentPage.getProperties();
        String pagePath = currentPage.getPath();

        String languageAbbreviation = LanguageUtil.getLanguageAbbreviationFromPath(pagePath, ConstantsForm.SupportedLanguages.ENGLISH.getLanguageAbbreviation());

        // Special String format required by eloqua form
        if (languageAbbreviation.equalsIgnoreCase(ConstantsForm.FormProperties.FORM_EN)) {
            this.pageLanguage = ConstantsForm.FormProperties.FORM_HIDDEN_ENGLISH;
        } else if (languageAbbreviation.equalsIgnoreCase(ConstantsForm.FormProperties.FORM_FR)) {
            this.pageLanguage = ConstantsForm.FormProperties.FORM_HIDDEN_FRENCH;
        }

        this.ownerTags = TagHelper.getTagsByName(tags, Constants.Paths.OWNER_TAGS);
        this.exportStatusTags = TagHelper.getTagsByName(tags, Constants.Paths.EXPORT_STATUS_TAGS);
        this.personaTags = TagHelper.getTagsByName(tags, Constants.Paths.PERSONA_TAGS);
        this.subCategoryTags = TagHelper.getTagsByName(tags, Constants.Paths.CATEGORY_TAGS);
        this.categoryTags = TagHelper.getCategoryTags(tags, resolver);
        this.assetTier = pageProperties.get(Constants.Properties.ASSET_TIER, String.class) != null ? pageProperties.get(Constants.Properties.ASSET_TIER, String.class) : "";
        this.docId = "";

        if (pageProperties.get(Constants.Properties.DOC_ID, String.class) != null) {
            this.docId = pageProperties.get(Constants.Properties.DOC_ID, String.class);
        } else if (pageProperties.get(Constants.Properties.DOC_ID_ELOQUA, String.class) != null) {
            this.docId = pageProperties.get(Constants.Properties.DOC_ID_ELOQUA, String.class);
        }

    }

    public EloquaData() {
        super();
    }

    public EloquaData(ResourceResolver resolver, Page page) {
        this.resolver = resolver;
        this.currentPage = page;
        initModel();
    }

    public String getOwnerTags() {
        return this.ownerTags;
    }

    public String getExportStatusTags() {
        return this.exportStatusTags;
    }

    public String getPersonaTags() {
        return this.personaTags;
    }

    public String getSubCategoryTags() {
        return this.subCategoryTags;
    }

    public String getCategoryTags() {
        return this.categoryTags;
    }

    public String getDocId() {
        return this.docId;
    }

    public String getAssetTier() {
        return this.assetTier;
    }

    public String getPageLanguage() {
        return pageLanguage;
    }
}
