package com.edc.edcweb.core.servlets;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import com.edc.edcweb.core.helpers.*;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.edc.edcweb.core.helpers.Constants;
import com.edc.edcweb.core.helpers.EbookHelper;
import com.edc.edcweb.core.helpers.LinkResolver;
import com.edc.edcweb.core.helpers.emailHasEloquaAccount;
import com.edc.edcweb.core.helpers.constants.ConstantsForm;
import com.edc.edcweb.core.helpers.progressiveprofiling.ProgressiveProfilingHelper;
import com.edc.edcweb.core.helpers.progressiveprofiling.ProgressiveProfilingPremiumPageHelper;
import com.edc.edcweb.core.helpers.progressiveprofiling.ProgressiveProfilingUserAccess;

/**
 * @author ACN
 * @version 1.0.0
 * @since 1.0.0
 *
 *        ProgressiveProfilingFormServlet is the post servlet attached to the
 *        premium content page resource that is used by the component to
 *        validate the form content and result. when access is granted the
 *        session attribute accessgranted is added to the session and the client
 *        is redirected to the premium content. to call the post of the form
 *        premniumurl/_jcr_content.progressiveprofiling.post
 *
 */

@Component(immediate = true, service = Servlet.class, property = { "sling.servlet.extensions=post",
        "sling.servlet.paths=" + Constants.Paths.PROGRESSIVEPROFILING_POSTSERVLET })
public class ProgressiveProfilingFormServlet extends SlingAllMethodsServlet {

    private static final long serialVersionUID = -3522134777945086197L;
    private static final Logger log = LoggerFactory.getLogger(ProgressiveProfilingFormServlet.class);
    private static final String ACTION_TYPE_PARAM = "actionType";
    private static final String EMAIL_ADDRESS_PARAM ="emailAddress";
    private static final String PREMIUM_URL_PARAM = "premiumUrl";
    private static final String TEASER_URL_PARAM = "teaserUrl";
    private static final String RESOURCE_PATH_PARAM = "resourcePath";

    @Override
    protected final void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {
        log.debug("doGet: ");

    }

    @Override
    protected final void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {
        // Implement custom handling of POST requests
        log.debug("doPost: ");

        // Get Request parameter value
        String actionType = request.getParameter(ACTION_TYPE_PARAM);
        log.debug("actionType:{}  ", actionType);

        // Get Request parameter value
        String email = request.getParameter(EMAIL_ADDRESS_PARAM);
        log.debug("email:{}  ", email);

        // Get Request parameter value
        String premiumUrl = request.getParameter(PREMIUM_URL_PARAM);
        log.debug("premiumUrl:{}  ", premiumUrl);

        // Get Request parameter value
        String teaserUrl = request.getParameter(TEASER_URL_PARAM);
        log.debug("teaserUrl:{}  ", teaserUrl);

        // Get Request parameter value
        String mode = request.getParameter("mode");
        log.debug("mode:{}  ", mode);

        // Get Request parameter value
        // Export help request form question
        String paragraphTextTAS = request.getParameter("paragraphTextTAS");
        log.debug("paragraphTextTAS:{}  ", paragraphTextTAS);

        // Get Request parameter value
        String resourcePath = request.getParameter(RESOURCE_PATH_PARAM);
        log.debug("resourcePath:{}  ", resourcePath);

        // Get sneakPeak from request parameter
        String sneakPeek = request.getParameter("sneakPeek");
        log.debug("sneakPeak:{}  ", sneakPeek);

        // Get persona from request parameter
        String personaId = request.getParameter("personaId");
        log.debug("personaId:{}  ", personaId);

        String refPremiumURL = request.getParameter("refPremiumURL");
        log.debug("refPremiumURL:{}  ", refPremiumURL);

        // Get page title from request parameter
        String pageTitle = request.getParameter("pageTitle");
        log.debug("pageTitle:{}  ", pageTitle);

        boolean isSneakPeek = false;
        if (sneakPeek != null && sneakPeek.equalsIgnoreCase("yes")) {
            isSneakPeek = true;
        }

        boolean emailHasAccount = false;
        String docID = "";
        int accessLevel = 0;

        if (mode.equalsIgnoreCase(Constants.Properties.PROGRESSIVEPROFILING_MODE_MEA)) {
            premiumUrl = "";
            docID = Constants.Properties.PROGRESSIVEPROFILING_MODE_MEA_MEAUNLOCKED_DOCID;
            accessLevel = 4;
        } else if (mode.equalsIgnoreCase(Constants.Properties.PROGRESSIVEPROFILING_MODE_EBOOK_REGULAR)) {

            docID = request.getParameter("docID");
            String level = request.getParameter("assetTier");
            if (isSneakPeek) {
                // if sneakPeek, we take the user to chapter 1
                accessLevel = 1;
            } else if (level != null) {
                accessLevel = Integer.parseInt(level);
            }
        } else if (mode.equalsIgnoreCase(Constants.Properties.PROGRESSIVEPROFILING_MODE_EXPORT_HELP_REQUEST)) {
            premiumUrl = "";
            docID = "";
            accessLevel = 5;
        } else if (mode.equalsIgnoreCase(Constants.Properties.PROGRESSIVEPROFILING_MODE_EBOOK_PERSONA)) {
            docID = request.getParameter("docID");
            accessLevel = Integer.parseInt(request.getParameter("assetTier"));
            premiumUrl = EbookHelper.getPersonaFirstChapterURLFromTeaser(request, teaserUrl, personaId);
            premiumUrl = LinkResolver.addHtmlExtension(premiumUrl);
        } else {
            // This is for regular progressive profiling usage and Export Help Hub mode.
            // get premium page url, accesslevel and docid.
            accessLevel = ProgressiveProfilingPremiumPageHelper.getAssetTierFromPremiumUrl(request, premiumUrl);
            docID = ProgressiveProfilingPremiumPageHelper.getDocIDFromPremiumUrl(request, premiumUrl);
        }
        log.debug(" getDocIDFromPremiumUrl {}  ", docID);

        Map<String, Object> map = new LinkedHashMap<>();
        map.putAll(request.getParameterMap());

        if (actionType.equalsIgnoreCase("email-submit")) {
            emailHasAccount = emailHasEloquaAccount.hasAccount(email);

            log.debug("---email-submit: {}  ", email);
            ProgressiveProfilingUserAccess userAccess;

            if (!mode.equalsIgnoreCase(Constants.Properties.PROGRESSIVEPROFILING_MODE_EXPORT_HELP_REQUEST)) {
                ProgressiveProfilingPremiumPageHelper.updateSessionEmail(request, email);
                if (isSneakPeek) {
                    // these should only happen when sneakPeek triggered in teaser page of 'regular
                    // ebook' and 'mea'
                    ProgressiveProfilingPremiumPageHelper.updateSessionSneakPeek(request, docID, "true");
                } else {
                    ProgressiveProfilingPremiumPageHelper.updateSessionSneakPeek(request, docID, null);
                }
                userAccess = ProgressiveProfilingHelper.validateAccess(request, mode, email, teaserUrl, premiumUrl,
                        docID, accessLevel);
            } else {
                // Add the question when is Export Help Request Form
                userAccess = ProgressiveProfilingHelper.validateAccess(request, mode, email, teaserUrl, premiumUrl,
                        docID, accessLevel, paragraphTextTAS);
            }

            if ((userAccess != null && userAccess.getAccessAllowed()) || emailHasAccount) {
                log.debug("Access Allowed: {}  ", premiumUrl);

                try {
                    String json = "";

                    if (!mode.equalsIgnoreCase(Constants.Properties.PROGRESSIVEPROFILING_MODE_EXPORT_HELP_REQUEST)) {
                        // ** Bypass any Eloqua updates if user has account **
                        if (!emailHasAccount) {
                            // For regular PP, we update the doc history in Eloqua and update accessGranted
                            // in session.
                            // For sneakpeek in regular ebook, we don't do that.Otherwise, current user has
                            // access for that ebook.
                            // For PAU(Knowledge customer), we don't update the PAU date in CDO in Email
                            // submission if have access
                            if (!(mode.equalsIgnoreCase(Constants.Properties.PROGRESSIVEPROFILING_MODE_EBOOK_REGULAR)
                                    && isSneakPeek)) {
                                userAccess.setUpdateDocHistory(true);

                                ProgressiveProfilingHelper.updateUserDataToEloqua(request, userAccess, false);
                                ProgressiveProfilingPremiumPageHelper.updateSessionAccessGranted(request, email, docID);
                            }

                            if (personaId != null && mode
                                    .equalsIgnoreCase(Constants.Properties.PROGRESSIVEPROFILING_MODE_EBOOK_PERSONA)) {
                                ProgressiveProfilingPremiumPageHelper.updateSessionPersonaEbook(request, personaId,
                                        docID);
                            }
                        }
                        // ** End of bypass any Eloqua updates if user has account **
                        premiumUrl = LinkResolver.addHtmlExtension(premiumUrl);
                        log.debug("Access Allowed: {} Has MyEdc Account: {}", premiumUrl, emailHasAccount);
                        json = ProgressiveProfilingHelper.buildJsonAccessAllowed(request, premiumUrl,
                                userAccess, refPremiumURL, true, emailHasAccount, accessLevel);
                    } else {
                        json = this.buildJsonRespForHelpRequestForm(true);
                    }
                    ServletResponseHelper.writeResponse(response,json);

                } catch (IOException e) {
                    log.error("Unable to redirect to premium url.", e);
                }
            } else if (userAccess != null && !userAccess.getAccessAllowed()) {
                log.debug("Access not Allowed: {}  ", premiumUrl);
                String json = "";
                json = ProgressiveProfilingHelper.buildJsonRequiredField(userAccess);

                ServletResponseHelper.writeResponse(response,json);

            } else if (userAccess == null) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }

        } else if (actionType.equalsIgnoreCase("form-submit")) {
            log.debug("---form-submit: {}  ", email);

            // Get Request parameter value
            map.remove(EMAIL_ADDRESS_PARAM);
            map.remove(PREMIUM_URL_PARAM);
            map.remove(TEASER_URL_PARAM);
            map.remove(ACTION_TYPE_PARAM);
            map.remove("mode");
            map.remove(RESOURCE_PATH_PARAM);

            if (mode.equalsIgnoreCase(Constants.Properties.PROGRESSIVEPROFILING_MODE_KNOWLEDGE_COSTUMER)) {
                map.put("PremiumAccessUserFlag", new String[] { "Y" });
            }

            // Validate all fields before going forward
            if (validateFormFields(map)) {
                // build the access data and submitted fields from the json post request
                // parameters.
                ProgressiveProfilingUserAccess userAccess = ProgressiveProfilingHelper.buildFromPostRequestParams(map,
                        email, premiumUrl, teaserUrl, docID, accessLevel);
                userAccess.setMode(mode);
                userAccess.setUpdateDocHistory(true);

                // userAccess is filled with the form intput... and the hidden fields and update
                // Eloqua.
                // For PAU(Knowledge customer), we need update the PAU date in DAO
                boolean updatePAUDate = false;
                if (mode.equalsIgnoreCase(Constants.Properties.PROGRESSIVEPROFILING_MODE_KNOWLEDGE_COSTUMER)) {
                    updatePAUDate = true;
                }

                String elqStatusCode = ProgressiveProfilingHelper.updateUserDataToEloqua(request, userAccess,
                        updatePAUDate);
                if (elqStatusCode != null && elqStatusCode.equalsIgnoreCase("200")) {

                    String json = "";

                    if (mode.equalsIgnoreCase(Constants.Properties.PROGRESSIVEPROFILING_MODE_EXPORT_HELP_REQUEST)) {
                        json = this.buildJsonRespForHelpRequestForm(true);
                    } else {
                        // update the session with docID.
                        ProgressiveProfilingPremiumPageHelper.updateSessionAccessGranted(request, email, docID);

                        if (personaId != null && mode
                                .equalsIgnoreCase(Constants.Properties.PROGRESSIVEPROFILING_MODE_EBOOK_PERSONA)) {
                            ProgressiveProfilingPremiumPageHelper.updateSessionPersonaEbook(request, personaId, docID);

                        }

                        // redirect to premium content
                        json = ProgressiveProfilingHelper.buildJsonAccessAllowed(request, premiumUrl,
                                userAccess, refPremiumURL, false, emailHasAccount, accessLevel);
                    }
                    ServletResponseHelper.writeResponse(response,json);

                } else {
                    response.setStatus(Integer.parseInt(elqStatusCode));
                }
            } else {
                log.error("Backend validation failed.");
                response.setStatus(500);
            }
        } else if (actionType.equalsIgnoreCase("next")) {

            log.debug("---next: {}  ", email);
            map.remove(EMAIL_ADDRESS_PARAM);
            map.remove(PREMIUM_URL_PARAM);
            map.remove(TEASER_URL_PARAM);
            map.remove(ACTION_TYPE_PARAM);
            map.remove("mode");
            map.remove(RESOURCE_PATH_PARAM);

            // Validate all fields before going forward
            if (validateFormFields(map)) {
                // build the access data and submitted fields from the json post request
                // parameters.
                ProgressiveProfilingUserAccess userAccess = ProgressiveProfilingHelper.buildFromPostRequestParams(map,
                        email, premiumUrl, teaserUrl, docID, accessLevel);
                userAccess.setMode(mode);
                userAccess.setUpdateDocHistory(false);
                // userAccess is filled with the form intput... and the hidden fields and update
                // Eloqua.
                String elqStatusCode = ProgressiveProfilingHelper.updateUserDataToEloqua(request, userAccess, false);
                if (elqStatusCode != null && elqStatusCode.equalsIgnoreCase("200")) {
                    response.getWriter().write("200");
                    response.getWriter().flush();
                } else {
                    response.setStatus(Integer.parseInt(elqStatusCode));
                }
            } else {
                log.error("Backend validation failed.");
            }
        }
    }


    private boolean validateFormFields(Map<String,Object> requestMap) {
        //for each parameter we need to add the name and the value
        for (Map.Entry<String,Object> entry : requestMap.entrySet()) {

            String key = entry.getKey();
            String[] strArr = (String [])entry.getValue();

            if (key.equalsIgnoreCase(ConstantsForm.FormProperties.FORM_PROGRESSIVEPROFILEING_FNAME) ||
                key.equalsIgnoreCase(ConstantsForm.FormProperties.FORM_PROGRESSIVEPROFILEING_LNAME) ||
                key.equalsIgnoreCase(ConstantsForm.FormProperties.FORM_PROGRESSIVEPROFILEING_TITLE) ||
                key.equalsIgnoreCase(ConstantsForm.FormProperties.FORM_PROGRESSIVEPROFILEING_TRADESTATUS) ||
                key.equalsIgnoreCase(ConstantsForm.FormProperties.FORM_PROGRESSIVEPROFILEING_COMPANYNAME) ||
                key.equalsIgnoreCase(ConstantsForm.FormProperties.FORM_PROGRESSIVEPROFILEING_MAINPHONE) ||
                key.equalsIgnoreCase(ConstantsForm.FormProperties.FORM_PROGRESSIVEPROFILEING_COMPANYADDR1) ||
                key.equalsIgnoreCase(ConstantsForm.FormProperties.FORM_PROGRESSIVEPROFILEING_COMPANYADDR2) ||
                key.equalsIgnoreCase(ConstantsForm.FormProperties.FORM_PROGRESSIVEPROFILEING_COMPANYCITY) ||
                key.equalsIgnoreCase(ConstantsForm.FormProperties.FORM_PROGRESSIVEPROFILEING_COMPANYPROVINCE) ||
                key.equalsIgnoreCase(ConstantsForm.FormProperties.FORM_PROGRESSIVEPROFILEING_COMPANYPOSTAL) ||
                key.equalsIgnoreCase(ConstantsForm.FormProperties.FORM_PROGRESSIVEPROFILEING_COMPANYCOUNTRY) ||
                key.equalsIgnoreCase(ConstantsForm.FormProperties.FORM_PROGRESSIVEPROFILEING_ANNUALSALES) ||
                key.equalsIgnoreCase(ConstantsForm.FormProperties.FORM_PROGRESSIVEPROFILEING_EMPOLYEES) ||
                key.equalsIgnoreCase(ConstantsForm.FormProperties.FORM_PROGRESSIVEPROFILEING_PAINPOINT) ||
                key.equalsIgnoreCase(ConstantsForm.FormProperties.FORM_PROGRESSIVEPROFILEING_INDUSTRY) ||
                key.equalsIgnoreCase(ConstantsForm.FormProperties.FORM_PROGRESSIVEPROFILEING_MARKETSINS)) {
                // SonarQube fix produced bug 150436
                if (!ProgressiveProfilingHelper.isValidFormField(key, strArr, true, -1)) {
                    String arrStr = Arrays.toString(strArr);
                    log.error("Backend validation failed on field: {} = {}", key, arrStr);
                    return false;
                }
            }
        }

        return true;
    }

    private String buildJsonRespForHelpRequestForm(boolean successful) {

        String json = "";
        String val = Constants.NO;
        if (successful) {
            val = Constants.YES;
        }

        JSONObject mainjson = new JSONObject();
        JSONObject levelinfo = new JSONObject();

        try {
            mainjson.put("response", val);
            levelinfo.put("userLevel", 5);
            mainjson.put("levelInfo", levelinfo);
            json = mainjson.toString(3);
        } catch (JSONException e) {
            log.error("Cannot create json", e);
        }
        log.debug("buildJsonInSessionList {}", json);
        return json;
    }
}