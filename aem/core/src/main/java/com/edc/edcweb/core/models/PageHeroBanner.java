package com.edc.edcweb.core.models;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Source;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.policies.ContentPolicy;
import com.edc.edcweb.core.helpers.Constants;
import com.edc.edcweb.core.helpers.ContentPolicyUtil;
import com.edc.edcweb.core.helpers.LinkResolver;
import com.edc.edcweb.core.helpers.constants.ConstantsAccessibility;
import com.edc.edcweb.core.helpers.constants.ConstantsPageHeroBanner;
import com.edc.edcweb.core.helpers.constants.ConstantsWebinars;
import com.edc.edcweb.core.helpers.webinars.WebinarsHelper;

@Model(adaptables = SlingHttpServletRequest.class)
public class PageHeroBanner {

    @Self
    private SlingHttpServletRequest request;

    @Inject
    @Source("sling-object")
    private ResourceResolver resourceResolver;

    @Inject
    private Page currentPage;

    @ScriptVariable
    private ValueMap properties;

    // Model
    private String bannerType;
    private Boolean colorBlock;
    private Boolean hideAnchor;
    private String fileReference;
    private String tabletFileReference;
    private String teaserimage;
    private String imagealttext;
    private String videoid;
    private String videowebm;
    private String videomp4;
    private String videoogg;
    private String videoclose;
    private String tagline;
    private String pageTitle;
    private String articlesynopsis;
    private String textalignment;
    private String imagealignment;
    private String linktext;
    private String linkurl;
    private String linktarget;
    private String linkdatatarget;
    private String anchortarget;
    private String backgroundicon;
    private String policyVideoplaybtn = "";
    private String policyVideoclosebtn = "";
    private String policyVideonotsupport = "";
    // Webinar
    private String webinarshowkey;
    private String webinaroverride;
    private String iswebinarpage;
    private String webinarupcomingtagtext;
    private String webinarlivetagtext;
    private String webinarondemandtagtext;
    private String arkadinmodaltitle;
    private String arkadinmodaltext;
    private String specialmodaltext;
    private String specialarkadinmodaltext;
    private String selfidmodaltext;
    private String arkadinagreebuttontext;
    private String arkadincancelbuttontext;
    private String arkadinmodalariaclose;
    private String arkadinmodalariascroll;
    private static String[] selfIdMultifieldValues;

    @PostConstruct
    public void initModel() throws RepositoryException {
        this.populateModel();
        this.populateFromPolicy();
        populateSelfIdMultifieldValues(request, currentPage);
    }

    private void populateFromPolicy() {
        ContentPolicy contentPolicy = ContentPolicyUtil.resolveLocalizedContentPolicy(this.request, this.currentPage);

        if (contentPolicy != null) {
            ValueMap policy = contentPolicy.getProperties();

            // Accessibility policies
            this.policyVideoplaybtn = policy.get(ConstantsAccessibility.CAProperties.ACCESSIBILITY_VIDEOPLAYBTN,
                    String.class);
            this.policyVideoclosebtn = policy.get(ConstantsAccessibility.CAProperties.ACCESSIBILITY_VIDEOCLOSEBTN,
                    String.class);
            this.policyVideonotsupport = policy.get(ConstantsAccessibility.CAProperties.ACCESSIBILITY_VIDEONOTSUPPORT,
                    String.class);
            // Webinar
            this.webinarupcomingtagtext = policy.get(ConstantsWebinars.UPCOMING_TAGTEXT, String.class);
            this.webinarlivetagtext = policy.get(ConstantsWebinars.LIVE_TAGTEXT, String.class);
            this.webinarondemandtagtext = policy.get(ConstantsWebinars.ONDEMAND_TAGTEXT, String.class);
            // Modal
            this.arkadinmodaltitle = policy.get(ConstantsWebinars.ARKADIN_MODAL_TITLE, String.class);
            this.arkadinmodaltext = policy.get(ConstantsWebinars.ARKADIN_MODAL_TEXT, String.class);
            this.arkadinagreebuttontext = policy.get(ConstantsWebinars.ARKADIN_AGREE_BUTTON_TEXT, String.class);
            this.arkadincancelbuttontext = policy.get(ConstantsWebinars.ARKADIN_CANCEL_BUTTON_TEXT, String.class);
            this.arkadinmodalariaclose = policy.get(ConstantsWebinars.ARKADIN_MODAL_ARIA_CLOSE, String.class);
            this.arkadinmodalariascroll = policy.get(ConstantsWebinars.ARKADIN_MODAL_ARIA_SCROLL, String.class);
        }
    }

    private void populateModel() throws RepositoryException {
        // Get values from component properties
        this.bannerType = properties.get(ConstantsPageHeroBanner.PHBProperties.BANNER_TYPE, String.class);
        this.colorBlock = properties.get(ConstantsPageHeroBanner.PHBProperties.COLOR_BLOCK, Boolean.class);
        this.hideAnchor = properties.get(ConstantsPageHeroBanner.PHBProperties.HIDE_ANCHOR, Boolean.class);
        this.fileReference = properties.get(ConstantsPageHeroBanner.PHBProperties.FILE_REFERENCE, String.class);
        this.tabletFileReference = properties.get(ConstantsPageHeroBanner.PHBProperties.TABLET_FILE_REFERENCE,
                String.class);
        this.teaserimage = properties.get(ConstantsPageHeroBanner.PHBProperties.TEASER_IMAGE_SOURCE, String.class);
        this.imagealttext = properties.get(ConstantsPageHeroBanner.PHBProperties.ARTICLE_IMAGE_ALT_TEXT, String.class);
        this.videoid = properties.get(ConstantsPageHeroBanner.PHBProperties.VIDEOID, String.class);
        this.videowebm = properties.get(ConstantsPageHeroBanner.PHBProperties.VIDEO_WEBM, String.class);
        this.videomp4 = properties.get(ConstantsPageHeroBanner.PHBProperties.VIDEO_MP4, String.class);
        this.videoogg = properties.get(ConstantsPageHeroBanner.PHBProperties.VIDEO_OGG, String.class);
        this.videoclose = properties.get(ConstantsPageHeroBanner.PHBProperties.VIDEO_CLOSE, String.class);
        this.tagline = properties.get(ConstantsPageHeroBanner.PHBProperties.TAGLINE, String.class);
        this.pageTitle = properties.get(ConstantsPageHeroBanner.PHBProperties.PAGE_TITLE, String.class);
        this.articlesynopsis = properties.get(ConstantsPageHeroBanner.PHBProperties.ARTICLE_SYNOPSIS, String.class);
        this.textalignment = properties.get(ConstantsPageHeroBanner.PHBProperties.TEXT_ALIGNMENT, String.class);
        this.imagealignment = properties.get(ConstantsPageHeroBanner.PHBProperties.IMAGE_ALIGNMENT, String.class);
        this.linktext = properties.get(ConstantsPageHeroBanner.PHBProperties.VIDEOCTATEXT, String.class);
        this.linkurl = LinkResolver
                .addHtmlExtension(properties.get(ConstantsPageHeroBanner.PHBProperties.LINK_URL, String.class));
        this.linktarget = properties.get(ConstantsPageHeroBanner.PHBProperties.LINK_TARGET, String.class);
        this.linkdatatarget = properties.get(ConstantsPageHeroBanner.PHBProperties.LINK_DATA_TARGET, String.class);
        this.anchortarget = properties.get(ConstantsPageHeroBanner.PHBProperties.ANCHOR_TARGET, String.class);
        this.backgroundicon = properties.get(ConstantsPageHeroBanner.PHBProperties.BACKGROUND_ICON, String.class);

        // Get values from page properties
        ValueMap pageProperties = this.currentPage.getProperties();

        if (this.fileReference == null) {
            this.fileReference = pageProperties.get(ConstantsPageHeroBanner.PHBProperties.FILE_REFERENCE, String.class);
        }

        if (this.tabletFileReference == null) {
            this.tabletFileReference = pageProperties.get(ConstantsPageHeroBanner.PHBProperties.TABLET_FILE_REFERENCE,
                    String.class);
        }

        if (this.teaserimage == null) {
            this.teaserimage = pageProperties.get(ConstantsPageHeroBanner.PHBProperties.TEASER_IMAGE_SOURCE, String.class);
        }

        if (this.imagealttext == null) {
            this.imagealttext = pageProperties.get(ConstantsPageHeroBanner.PHBProperties.ARTICLE_IMAGE_ALT_TEXT,
                    String.class);
        }

        if (this.backgroundicon == null) {
            this.backgroundicon = pageProperties.get(ConstantsPageHeroBanner.PHBProperties.BACKGROUND_ICON, String.class);
        }

        if (this.videoid == null) {
            this.videoid = pageProperties.get(ConstantsPageHeroBanner.PHBProperties.VIDEOID, String.class);
        }

        if (this.videowebm == null) {
            this.videowebm = pageProperties.get(ConstantsPageHeroBanner.PHBProperties.VIDEO_WEBM, String.class);
        }

        if (this.videomp4 == null) {
            this.videomp4 = pageProperties.get(ConstantsPageHeroBanner.PHBProperties.VIDEO_MP4, String.class);
        }

        if (this.videoogg == null) {
            this.videoogg = pageProperties.get(ConstantsPageHeroBanner.PHBProperties.VIDEO_OGG, String.class);
        }

        if (this.tagline == null) {
            this.tagline = pageProperties.get(ConstantsPageHeroBanner.PHBProperties.TAGLINE, String.class);
        }

        if (this.pageTitle == null) {
            this.pageTitle = pageProperties.get(ConstantsPageHeroBanner.PHBProperties.PAGE_TITLE, String.class);
        }

        if (this.articlesynopsis == null) {
            this.articlesynopsis = pageProperties.get(ConstantsPageHeroBanner.PHBProperties.ARTICLE_SYNOPSIS,
                    String.class);
        }

        if (this.imagealignment == null) {
            this.imagealignment = pageProperties.get(ConstantsPageHeroBanner.PHBProperties.IMAGE_ALIGNMENT, String.class);
        }

        if (this.linktarget == null || this.linktext == null) {
            this.linktarget = pageProperties.get(ConstantsPageHeroBanner.PHBProperties.LINK_TARGET, String.class);
        }

        if (this.linkurl == null || this.linkurl.isEmpty()) {
            this.linkurl = LinkResolver
                    .addHtmlExtension(pageProperties.get(ConstantsPageHeroBanner.PHBProperties.LINK_URL, String.class));
        }

        if (this.linktext == null) {
            this.linktext = pageProperties.get(ConstantsPageHeroBanner.PHBProperties.VIDEOCTATEXT, String.class);
        }

        if (this.anchortarget == null) {
            this.anchortarget = pageProperties.get(ConstantsPageHeroBanner.PHBProperties.ANCHOR_TARGET, String.class);
        }

        // Webinar
        // Component's property Override
        this.webinaroverride = pageProperties.get(ConstantsPageHeroBanner.PHBProperties.WEBINAR_OVERRIDE, String.class);
        // if webinaroverride is null or empty, it should be either "true" or "false"
        // string) force the property default value to true
        if (StringUtils.isBlank(webinaroverride)) {
            // set it to true to enforce default
            webinaroverride = ConstantsPageHeroBanner.PHBProperties.STRING_TRUE;
            // Try to save the property to the JCR to help the author, is possible
            Session session = resourceResolver.adaptTo(Session.class);
            Node pageContentNode = currentPage.getContentResource().adaptTo(Node.class);
            if (session != null && pageContentNode != null) {
                boolean canSetProperty = session.hasPermission(pageContentNode.getPath(), Session.ACTION_SET_PROPERTY);
                // make sure we can set properties here
                if (canSetProperty) {
                    // if we have both session and content node, add the property
                    pageContentNode.setProperty(ConstantsPageHeroBanner.PHBProperties.WEBINAR_OVERRIDE, webinaroverride);
                    // save it
                    session.save();
                }
            }
        }

        this.webinarshowkey = pageProperties.get(ConstantsWebinars.SHOW_KEY, String.class);
        this.iswebinarpage = StringUtils.contains(WebinarsHelper.getWebinarPageTypePath(currentPage),
                Constants.Paths.EVENT_TEMPLATE_URL) ? "true" : "";
        this.specialmodaltext = pageProperties.get(ConstantsWebinars.SPECIAL_MODAL_TEXT, String.class);
        this.specialarkadinmodaltext = pageProperties.get(ConstantsWebinars.SPECIAL_ARKADIN_MODAL_TEXT, String.class);
        this.selfidmodaltext = pageProperties.get("selfidmodaltext", String.class);
    }

    private List<ValueMap> populateFromMultifields(String multifieldName) {
        List<ValueMap> values = new LinkedList<>();
        // Get the Multi field node from the list
        Resource pageRes = currentPage.getContentResource().getChild(multifieldName);
        // Populate from values
        if (pageRes != null) {
            Iterator<Resource> fieldsIterator = pageRes.listChildren();
            while (fieldsIterator.hasNext()) {
                values.add(fieldsIterator.next().getValueMap());
            }
        }
        return values;
    }

    private static void populateSelfIdMultifieldValues(SlingHttpServletRequest request, Page currentPage) {
        ArrayList<String> valuesList = new ArrayList<>();
        ContentPolicy contentPolicy = ContentPolicyUtil.resolveLocalizedContentPolicy(request, currentPage);
        if (contentPolicy != null) {
            Resource policyResource = contentPolicy.adaptTo(Resource.class);
            if (policyResource != null) {
                Resource multiRes = policyResource.getChild(Constants.Properties.SELF_ID_LOV);
                // Populate from values
                if (multiRes != null) {
                    Iterator<Resource> fieldsIterator = multiRes.listChildren();
                    while (fieldsIterator.hasNext()) {
                        valuesList.add(fieldsIterator.next().getChild(Constants.Properties.SELF_ID_LOV_VALUE).adaptTo(String.class));
                    }
                    valuesList.add(Constants.Properties.PREFER_NOT_TO_DISCLOSE);
                }
            }
        }
        selfIdMultifieldValues = valuesList.toArray(new String[valuesList.size()]);
    }

    public String getBannerType() {
        return this.bannerType;
    }

    public Boolean getColorBlock() {
        return this.colorBlock;
    }

    public Boolean getHideAnchor() {
        return this.hideAnchor;
    }

    public String getFileReference() {
        return this.fileReference;
    }

    public String getTabletFileReference() {
        return this.tabletFileReference;
    }

    public String getTeaserimage() {
        return this.teaserimage;
    }

    public String getImagealttext() {
        return this.imagealttext;
    }

    public String getVideoid() {
        return this.videoid;
    }

    public String getVideowebm() {
        return this.videowebm;
    }

    public String getVideomp4() {
        return this.videomp4;
    }

    public String getVideoogg() {
        return this.videoogg;
    }

    public String getVideoclose() {
        return this.videoclose;
    }

    public String getTagline() {
        return this.tagline;
    }

    public String getPageTitle() {
        return this.pageTitle;
    }

    public String getArticlesynopsis() {
        return this.articlesynopsis;
    }

    public String getTextalignment() {
        return this.textalignment;
    }

    public String getImagealignment() {
        return this.imagealignment;
    }

    public String getLinktext() {
        return this.linktext;
    }

    public String getLinkurl() {
        return this.linkurl;
    }

    public String getLinktarget() {
        return this.linktarget;
    }

    public String getAnchortarget() {
        return this.anchortarget;
    }

    public String getLinkdatatarget() {
        return this.linkdatatarget;
    }

    public String getBackgroundicon() {
        return this.backgroundicon;
    }

    public String getPolicyVideoplaybtn() {
        return policyVideoplaybtn;
    }

    public String getPolicyVideoclosebtn() {
        return policyVideoclosebtn;
    }

    public String getPolicyVideonotsupport() {
        return policyVideonotsupport;
    }

    // Webinar
    public String getWebinarupcomingtagtext() {
        return this.webinarupcomingtagtext;
    }

    public String getWebinarlivetagtext() {
        return this.webinarlivetagtext;
    }

    public String getWebinarondemandtagtext() {
        return this.webinarondemandtagtext;
    }

    public String getWebinarshowkey() {
        return this.webinarshowkey;
    }

    public String getWebinaroverride() {
        return webinaroverride;
    }

    public String getIswebinarpage() {
        return iswebinarpage;
    }

    public String getWebinarstatusurl() {
        return ConstantsWebinars.WEBINAR_STATUS_SERVLET_URL;
    }

    public String getWebinarregisterurl() {
        return ConstantsWebinars.WEBINAR_REGISTER_SERVLET_URL;
    }

    public String getWebinarerror() {
        return ConstantsWebinars.PAGE_LOAD_ERROR;
    }

    public String getArkadinmodaltitle() {
        return arkadinmodaltitle;
    }

    public String getArkadinmodaltext() {
        return arkadinmodaltext;
    }

    public String getSpecialmodaltext() {
        return specialmodaltext;
    }

    public String getSpecialarkadinmodaltext() {
        return specialarkadinmodaltext;
    }

    public String getSelfidmodaltext() {
        return selfidmodaltext;
    }

    public String getArkadinagreebuttontext() {
        return arkadinagreebuttontext;
    }

    public String getArkadincancelbuttontext() {
        return arkadincancelbuttontext;
    }

    public String getArkadinmodalariaclose() {
        return arkadinmodalariaclose;
    }

    public String getArkadinmodalariascroll() {
        return arkadinmodalariascroll;
    }

    public List<ValueMap> getPartners() {
        return this.populateFromMultifields(ConstantsWebinars.WEBINAR_PARTNERS_MULTIFIELD);
    }

    public List<ValueMap> getWitRadios() {
        return this.populateFromMultifields(ConstantsWebinars.WEBINAR_WIT_RADIOS);
    }

    public static String[] getSelfIdMultifieldValues() {
        return selfIdMultifieldValues;
    }

}
