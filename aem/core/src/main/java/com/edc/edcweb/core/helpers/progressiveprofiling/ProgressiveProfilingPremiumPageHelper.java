package com.edc.edcweb.core.helpers.progressiveprofiling;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.edc.edcweb.core.helpers.Constants;
import com.edc.edcweb.core.helpers.EbookHelper;
import com.edc.edcweb.core.helpers.LanguageUtil;
import com.edc.edcweb.core.helpers.LinkResolver;
import com.edc.edcweb.core.helpers.Request;
import com.edc.edcweb.core.helpers.constants.ConstantsEbook;

/**
 * @author ACN
 * @version
 * @since
 *
 *
 *        Helper class to perform operations on premium page(s).
 *
 *
 */
public class ProgressiveProfilingPremiumPageHelper {
    
    private ProgressiveProfilingPremiumPageHelper () {
        // SonarQube
    }

    private static final Logger log = LoggerFactory.getLogger(ProgressiveProfilingPremiumPageHelper.class);
    public static final String ACCESS_GRANTED = "accessGranted";

    public static String getInternalPremiumUrlFromTeaser(SlingHttpServletRequest request) {

        Page currentPage = Request.adaptToPage(request);
        String pageLanguage = LanguageUtil.getLanguageAbbreviationFromPath(currentPage.getPath(),
                Constants.SupportedLanguages.ENGLISH.getLanguageAbbreviation());
        String teaserUrl = currentPage.getPath();
        String contentType = getContentTypeTargetted(request.getResource());
        String url = "";

        if (contentType.isEmpty() || contentType.equalsIgnoreCase(Constants.HTML_EXTENTION)) {
            int last = teaserUrl.indexOf("/edc/" + pageLanguage + "/");
            url = new StringBuilder(teaserUrl).insert(last + 7, Constants.Paths.PROGRESSIVEPROFILING_PREMIUMROOT)
                    .toString();
            url = LinkResolver.addHtmlExtension(url);

        } else {
            // http://localhost:4502/content/edc/en/testpremium1/testpremiumasset1/premiumasset1.html
            // http://localhost:4502/content/dam/edc/en/premium/testpremium1/testpremiumasset1/premiumasset1.pdf
            int last = teaserUrl.indexOf("/edc/" + pageLanguage + "/");
            url = new StringBuilder(teaserUrl).insert(last + 7, Constants.Paths.PROGRESSIVEPROFILING_PREMIUMROOT)
                    .toString();
            url = new StringBuilder(url).insert(last, "/dam").toString();
            url = url.replaceFirst("\\.html", contentType);

            if (!url.contains(contentType)) {
                url = url.concat(contentType);
            }
        }

        return url;
    }

    private static String getContentTypeTargetted(Resource pprofilingcomponent) {
        // get the resource it should be the pp component,
        // check that the dialog is set to asset or link...

        String val = "";
        ValueMap prop = pprofilingcomponent.adaptTo(ValueMap.class);

        if (prop != null) {
            val = (String) prop.getOrDefault("contenttype", "");
        }

        return val;
    }

    private static boolean isAssetResource(SlingHttpServletRequest request) {

        Resource resource = request.getResource();
        String resType = resource.getResourceType();
        return resType.equalsIgnoreCase("dam:Asset");
    }

    public static String getDocIDFromPremiumUrl(SlingHttpServletRequest request, String premiumurl) {

        String result = "";
        if (premiumurl.contains(Constants.Paths.PROGRESSIVEPROFILING_PREMIUMROOT + "/ebook/")) {
            // for ebooks. we need get docID from teaser
            String teaserUrl = premiumurl.replaceFirst(Constants.Paths.PROGRESSIVEPROFILING_PREMIUMROOT + "/", "/");
            teaserUrl = teaserUrl.substring(0, teaserUrl.lastIndexOf('/'));
            ResourceResolver resourceResolver = request.getResourceResolver();
            Resource pageRes = resourceResolver.getResource(teaserUrl);
            // BUG 84635 PageRes can be null
            if (pageRes != null) {
                Page teaserPage = pageRes.adaptTo(Page.class);
                result = EbookHelper.getAttributeFromRegularEBookTeaserPageSneakPeekPPComp(teaserPage, "docID");
            }

        } else if (premiumurl
                .contains(Constants.Paths.PROGRESSIVEPROFILING_PREMIUMROOT + Constants.Paths.EXPORT_HELP_HUB + "/")) {
            String landingUrl = premiumurl.substring(0, premiumurl.lastIndexOf(Constants.Paths.EXPORT_HELP_HUB + "/"))
                    + Constants.Paths.EXPORT_HELP_HUB + Constants.HTML_EXTENTION;
            result = getAttributeFromPremiumUrl(request, landingUrl, "eloquaDocId");
        } else {
            // regular premium asset
            result = getAttributeFromPremiumUrl(request, premiumurl, "eloquaDocId");
        }
        return result;

    }

    private static String getAttributeFromPremiumUrl(SlingHttpServletRequest request, String premiumurl,
            String attrName) {

        String attrVal = "";
        String pagePath = premiumurl;

        if (pagePath.endsWith(Constants.HTML_EXTENTION)) {
            pagePath = pagePath.substring(0, pagePath.length() - 5);
        }

        PageManager pageManager = request.getResourceResolver().adaptTo(PageManager.class);
        Page page = pageManager.getContainingPage(pagePath);

        if (page != null) {
            ValueMap properties = page.getProperties();
            attrVal = properties.get(attrName, String.class);
        } else {
            Resource res = request.getResourceResolver().resolve(premiumurl);

            if (!ResourceUtil.isNonExistingResource(res)) {
                Resource rescontent = res.getChild("jcr:content");

                if (rescontent != null && !ResourceUtil.isNonExistingResource(rescontent)) {
                    ValueMap properties = rescontent.getValueMap();
                    attrVal = properties.get(attrName, String.class);
                }
            }

        }

        return attrVal;
    }

    public static int getAssetTierFromPremiumUrl(SlingHttpServletRequest request, String premiumurl) {

        String val = getAttributeFromPremiumUrl(request, premiumurl, Constants.Properties.ASSET_TIER);

        if (val == null || val.isEmpty()) {
            return 0;
        }
        return Integer.parseInt(val);
    }

    public static String getInternalTeaserUrlFromPremium(SlingHttpServletRequest request) {

        String url = "";
        String target = request.getRequestURI();

        // Some premium pages have special teaser page url strategy. For example:
        // - EBOOK: /premium/ebook/bookA/chapter1.html -> ebook/bookA.html
        // - EHH Search: /premium/tool/export-help-hub/search.html ->
        // /tool/export-help-hub.html
        // - EHH Questions: /premium/tool/export-help-hub/questions.ehh.12.html ->
        // /tool/export-help-hub.html
        // - EHH Other pages:
        // premium/tool/export-help-hub/country-name/category-name.html ->
        // /tool/export-help-hub.html
        // - InList Profile: /premium/tool/inlist/profile.10.html -> /tool/inlist.html

        boolean isNeedSpecialTeaser = false;

        for (String specialPremium : Constants.ArrayValues.PREMIUM_PATH_WITH_SPECIAL_TEASER_URL.toArray()) {
            if (target.indexOf(Constants.Paths.PROGRESSIVEPROFILING_PREMIUMROOT + specialPremium) > -1) {
                isNeedSpecialTeaser = true;
                url = target.replaceFirst(Constants.Paths.PROGRESSIVEPROFILING_PREMIUMROOT, "");
                if (specialPremium.equals("/ebook/")) {
                    url = url.substring(0, url.lastIndexOf('/'));
                } else {
                    url = url.substring(0, url.lastIndexOf(specialPremium) + specialPremium.length() - 1);
                }
                url = url.concat(Constants.HTML_EXTENTION);
                break;
            }
        }

        if (!isNeedSpecialTeaser) {
            if (isAssetResource(request)) {
                // DAM Resource
                // For example:
                // /content/dam/edc/en/premium/testpremium1/testpremiumasset1/premiumasset1.pdf
                // We remove the /premium/ folder and the /dam:
                // /edc/en/testpremium1/testpremiumasset1/premiumasset1.html
                url = target.replaceFirst(Constants.Paths.PROGRESSIVEPROFILING_PREMIUMROOT + "/", "/");
                url = url.replaceFirst("/dam/", "/");
                url = url.replaceFirst("\\.pdf", Constants.HTML_EXTENTION);
            } else {
                // Page Resource: just remove the /premium/ folder ...
                url = target.replaceFirst(Constants.Paths.PROGRESSIVEPROFILING_PREMIUMROOT + "/", "/");
                url = LinkResolver.addHtmlExtension(url);
            }
        }

        return url;
    }

    public static void updateSessionAccessGranted(SlingHttpServletRequest request, String email, String docID) {

        String attr = (String) request.getSession().getAttribute(ACCESS_GRANTED);

        if (attr == null) {
            attr = email;
        }

        String cleanDocID = docID.trim();
        List<String> list = new ArrayList<>(Arrays.asList(attr.split(",")));

        if (!list.contains(cleanDocID)) {
            list.add(cleanDocID);
            String attrUpdate = String.join(",", list);
            request.getSession().setAttribute(ACCESS_GRANTED, attrUpdate);
        }

    }

    public static boolean checkSessionAccessGranted(SlingHttpServletRequest request, String docID) {

        boolean result = false;
        String attr = (String) request.getSession().getAttribute(ACCESS_GRANTED);
        String docIDMEAPrefix = Constants.Properties.PROGRESSIVEPROFILING_MODE_MEA_DOCID;

        if (attr != null) {

            List<String> list = new ArrayList<>(Arrays.asList(attr.split(",")));

            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).equalsIgnoreCase(docID)) {
                    return true;
                } else {
                    // Handle MEA docID: MEA-*
                    if (!list.get(i).equalsIgnoreCase("MEA-USA")
                            && list.get(i).toUpperCase().startsWith(docIDMEAPrefix)
                            && docID.toUpperCase().startsWith(docIDMEAPrefix)) {
                        return true;
                    }
                }
            }
        }

        return result;

    }

    /**
     * Check if the profile type is valid for GRC features
     * 
     * @param request the SlingHttpServletRequest
     * @return true if the profile type has access to GRC features
     */
    public static boolean isValidProfileType(SlingHttpServletRequest request) {
        List<String> acceptedProfiles = Arrays.asList(Constants.ArrayValues.ACCEPTED_PROFILES.toArray());
        String profileType = (String) request.getSession().getAttribute(Constants.Properties.PROFILE_TYPE);
        return acceptedProfiles.contains(profileType);
    }

    /**
     * Check if user is trying to access GRC features restricted for certain profile
     * types
     * 
     * @param request the SlingHttpServletRequest
     * @return true if the user has access to the feature
     */
    public static boolean hasGRCAccess(SlingHttpServletRequest request) {
        String path = request.getRequestURI();
        boolean hasAccess = true;

        if (path.contains(Constants.Paths.GLOBAL_RISK_CHECK + Constants.HTML_EXTENTION)
                || path.contains(Constants.Paths.GLOBAL_RISK_CHECK_ALIAS + Constants.HTML_EXTENTION)) {
            hasAccess = isValidProfileType(request);
        }

        return hasAccess;
    }

    public static void updateSessionEmail(SlingHttpServletRequest request, String email) {
        // check that the session first item is the correct email.
        // Else delete the data in session.
        String attr = (String) request.getSession().getAttribute(ACCESS_GRANTED);

        if (attr != null) {
            List<String> list = new ArrayList<>(Arrays.asList(attr.split(",")));

            if (!list.isEmpty() && !list.get(0).contains(email)) {
                request.getSession().removeAttribute(ACCESS_GRANTED);
                log.debug("Remove accessGranted attribute because email {} is not same as current email : {}",
                        list.get(0), email);
            }
        }
    }

    public static void updateSessionSneakPeek(SlingHttpServletRequest request, String docID, String sneakPeak) {

        request.getSession().removeAttribute(ConstantsEbook.SessionAttr.PREVIEW_ONLY_ATTR);
        if (sneakPeak != null && !sneakPeak.isEmpty() && docID != null && !docID.isEmpty()) {
            request.getSession().setAttribute(ConstantsEbook.SessionAttr.PREVIEW_ONLY_ATTR, docID);
        }
    }

    public static void updateSessionPersonaEbook(SlingHttpServletRequest request, String personaId, String docID) {

        request.getSession().setAttribute(ConstantsEbook.SessionAttr.PERSONA_ATTR, personaId);
        request.getSession().setAttribute(ConstantsEbook.SessionAttr.DOCID_ATTR, docID);
    }

}