package com.edc.edcweb.core.models.progressiveprofiling;

import java.io.IOException;
import java.util.Arrays;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.WCMMode;
import com.edc.edcweb.core.consentrequestgating.helpers.CRGHelper;
import com.edc.edcweb.core.helpers.Constants;
import com.edc.edcweb.core.helpers.EbookHelper;
import com.edc.edcweb.core.helpers.LinkResolver;
import com.edc.edcweb.core.helpers.RedirectHelper;
import com.edc.edcweb.core.helpers.Request;
import com.edc.edcweb.core.helpers.constants.ConstantsEbook;
import com.edc.edcweb.core.helpers.progressiveprofiling.ProgressiveProfilingHelper;
import com.edc.edcweb.core.helpers.progressiveprofiling.ProgressiveProfilingPremiumPageHelper;
import com.edc.edcweb.core.service.EloquaService;

/**
 * @author ACN
 * @version 1.0.0
 * @since 1.0.0
 *
 *        Used on the body.html of premium page template to control access to
 *        premium content. Check accessGranted session attribute and allow
 *        access and reset it when its valuated.
 *
 *
 */

@Model(adaptables = SlingHttpServletRequest.class)
public class PremiumPageController {
    private static final Logger log = LoggerFactory.getLogger(PremiumPageController.class);

    @Self
    private SlingHttpServletRequest request;

    @SlingObject
    private SlingHttpServletResponse response;

    @Inject
    @Optional
    private Page currentPage;

    @Inject
    private EloquaService eloquaService;

    /**
     * this is called when the premium page is loaded
     */
    @PostConstruct
    public void initModel() {

        // do not control access for author in edit or preview mode
        WCMMode mode = WCMMode.fromRequest(request);
        log.debug(" mode {}", mode);
        if (mode == WCMMode.EDIT || mode == WCMMode.PREVIEW) {
            log.debug("mode is EDIT or PREVIEW, not controlling access...");
            return;
        }

        String internalTeaserUrl = ProgressiveProfilingPremiumPageHelper.getInternalTeaserUrlFromPremium(request);
        String premiumUrl = Request.getPagePath(request);
        String docId = ProgressiveProfilingPremiumPageHelper.getDocIDFromPremiumUrl(request, premiumUrl);

        boolean accessGranted = false;
        boolean accessGrantedRegularEbookSneakPeakForFirstChapter = false;

        // This change is for referral URL feature of Progressive Profiling: EBook and
        // EHH usages only
        // reset the varible every time user access the premium value
        boolean needPremiumRefURL = false;

        // US 44669 check FITT
        String crgTeaserUrl = null;

        // Check If User Is Accessing Regular Ebook 1st Chapter from Teaser Page
        // Sneakpeek:
        // We need to check <1>Is regular ebook <2>Is first chapter <3> Session's
        // sneakpeek value(docID)
        ResourceResolver resourceResolver = currentPage.getContentResource().getResourceResolver();
        String target = request.getRequestURI();
        String urlQueryString = request.getQueryString();

        for (String specialPremium : Constants.ArrayValues.PREMIUM_PATH_WITH_SPECIAL_TEASER_URL.toArray()) {
            if (target.contains(Constants.Paths.PROGRESSIVEPROFILING_PREMIUMROOT + specialPremium)) {
                needPremiumRefURL = true;
                break;
            }
        }

        // ** Check My Edc Login State **
        accessGranted = ProgressiveProfilingHelper.checkMyEdcSession(request);
        if (accessGranted) {
            // Explicitly give access to sneak peak
            accessGrantedRegularEbookSneakPeakForFirstChapter = true;
            needPremiumRefURL = false;
            log.debug("accessGranted MyEDC Session:{}", accessGranted);
            log.debug("accessGrantedRegularEbookSneakPeakForFirstChapter MyEDC Session:{} ",
                    accessGrantedRegularEbookSneakPeakForFirstChapter);

            boolean hasGRCAccess = ProgressiveProfilingPremiumPageHelper.hasGRCAccess(request);
            if (!hasGRCAccess) {
                accessGranted = false;
                accessGrantedRegularEbookSneakPeakForFirstChapter = false;
                internalTeaserUrl = target.replace(Constants.HTML_EXTENTION,
                        Constants.Paths.GUIDE + Constants.HTML_EXTENTION);
            }
            // US 144669
            crgTeaserUrl = CRGHelper.checkCRGAccess(currentPage, request, eloquaService);
        } else {
            // Maintain original logic
            accessGranted = ProgressiveProfilingPremiumPageHelper.checkSessionAccessGranted(request, docId);
            if (target.contains(Constants.Paths.PROGRESSIVEPROFILING_PREMIUMROOT + "/ebook/")) {
                String eBookTeaserUrl = target.replaceFirst(Constants.Paths.PROGRESSIVEPROFILING_PREMIUMROOT + "/",
                        "/");
                eBookTeaserUrl = eBookTeaserUrl.substring(0, eBookTeaserUrl.lastIndexOf(Constants.Paths.FORWARD_SLASH));
                Resource thePageRes = resourceResolver.getResource(eBookTeaserUrl);

                if (thePageRes != null) {
                    Page eBookTeaserPage = thePageRes.adaptTo(Page.class);
                    String ppMode = EbookHelper.getAttributeFromRegularEBookTeaserPageSneakPeekPPComp(eBookTeaserPage,
                            "mode");

                    if (ppMode.equalsIgnoreCase(Constants.Properties.PROGRESSIVEPROFILING_MODE_EBOOK_REGULAR)) {

                        String attrValue = (String) request.getSession()
                                .getAttribute(ConstantsEbook.SessionAttr.PREVIEW_ONLY_ATTR);

                        if (attrValue != null && !attrValue.isEmpty() && attrValue.equals(docId)) {
                            String firstChapterURL = EbookHelper.getFirstChapterURLFromCurrentPage(eBookTeaserPage,
                                    null);

                            if (firstChapterURL.equals(LinkResolver.addHtmlExtension(premiumUrl))) {
                                accessGrantedRegularEbookSneakPeakForFirstChapter = true;
                            }
                        }
                    } else if (ppMode.equalsIgnoreCase(Constants.Properties.PROGRESSIVEPROFILING_MODE_EBOOK_PERSONA)) {
                        String personaAttr = (String) request.getSession()
                                .getAttribute(ConstantsEbook.SessionAttr.PERSONA_ATTR);
                        String docIdAttr = (String) request.getSession()
                                .getAttribute(ConstantsEbook.SessionAttr.DOCID_ATTR);
                        String teaserDocId = EbookHelper.getAttributeFromPersonaButton(eBookTeaserPage,
                                ConstantsEbook.PersonaButtonsProps.DOCID);

                        if (personaAttr != null && docIdAttr != null) {
                            if (docIdAttr.equalsIgnoreCase(teaserDocId)) {
                                accessGranted = ProgressiveProfilingPremiumPageHelper.checkSessionAccessGranted(request,
                                        docIdAttr);
                            }
                        } else {
                            log.debug("Error getting values from session");
                        }
                    }
                }
            }
            log.debug("accessGranted normal:{}", accessGranted);
            log.debug("accessGrantedRegularEbookSneakPeakForFirstChapter normal:{} ",
                    accessGrantedRegularEbookSneakPeakForFirstChapter);
        }

        try {
            String redirectTo = null;
            if (!accessGranted && !accessGrantedRegularEbookSneakPeakForFirstChapter) {
                log.debug("getSession redirect to internal teaser page: {}", internalTeaserUrl);
                // change the 'internal resource request URL' to 'user access URL', avoid
                // redirect error

                String externalTeaserUrl = LinkResolver.changeInternalURLtoExternal(request, internalTeaserUrl);

                if (needPremiumRefURL) {
                    externalTeaserUrl += "?refPremiumURL=" + LinkResolver.reverseMapLink(resourceResolver, target);
                }
                // This is for EOG Tracking purpose - Task 43742
                // UTM Query String needed for EH Form as well. Task 102648
                if (urlQueryString != null) {
                    String qsSeparator = "?";
                    // check if we have the refPremiumURL parameter, so we use the &
                    if (externalTeaserUrl.contains("?")) {
                        qsSeparator = "&";
                    }
                    externalTeaserUrl = externalTeaserUrl + qsSeparator + urlQueryString;
                }
                redirectTo = externalTeaserUrl;
            }
            // US 144669 redirect to Teaser if not consent given for this partner
            if (StringUtils.isNotBlank(crgTeaserUrl)) {
                redirectTo = crgTeaserUrl;
            }
            // redirect only if necessary
            if (StringUtils.isNotBlank(redirectTo)) {
                // BUG 378038 Par 2
                RedirectHelper.redirectTo(response, redirectTo);
            }

        } catch (IOException e) {
            log.debug("redirect failed for internal url {}", internalTeaserUrl);
            log.debug(Arrays.toString(e.getStackTrace()));
        }

    }
}