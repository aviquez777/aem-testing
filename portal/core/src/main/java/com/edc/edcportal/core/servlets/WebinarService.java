package com.edc.edcportal.core.servlets;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;
import com.edc.edcportal.core.apim.APIMConstants;
import com.edc.edcportal.core.apim.pojo.InfoPaymentStatusDO;
import com.edc.edcportal.core.apim.services.ApimDAOService;
import com.edc.edcportal.core.appauth.services.AppAuthDAOService;
import com.edc.edcportal.core.arkadin.pojo.ArkadinRegistrationDO;
import com.edc.edcportal.core.arkadin.pojo.OpCodeResult;
import com.edc.edcportal.core.arkadin.services.ArkadinConfigService;
import com.edc.edcportal.core.arkadin.services.ArkadinDAOService;
import com.edc.edcportal.core.exception.EDCAPIMSystemException;
import com.edc.edcportal.core.helpers.Constants;
import com.edc.edcportal.core.helpers.Request;
import com.edc.edcportal.core.helpers.ServletResponseHelper;
import com.edc.edcportal.core.helpers.TranslationHelper;
import com.edc.edcportal.core.helpers.WebinarsHelper;
import com.edc.edcportal.core.helpers.constants.ConstantsWebinars;
import com.edc.edcportal.core.services.MyEDCConfigSystemService;
import com.edc.edcportal.core.servlets.webinar.WebinarDataBindingUtil;
import com.edc.edcportal.core.servlets.webinar.pojo.WebinarResponseDO;
import com.edc.edcportal.core.transactionhistory.services.THDAOService;

@SuppressWarnings("CQRules:CQBP-75")
@Component(immediate = true, service = Servlet.class, property = { "sling.servlet.extensions=json",
        "sling.servlet.paths=" + ConstantsWebinars.WEBINAR_SERVICE_SERVLET_URL })

public class WebinarService extends SlingAllMethodsServlet {

    private static final Logger log = LoggerFactory.getLogger(WebinarService.class);
    private static final long serialVersionUID = 6483773590569461526L;

    @Reference
    @Inject
    private MyEDCConfigSystemService myEDCConfigSystemService;

    @Inject
    @Reference
    private AppAuthDAOService appAuthDAOService;

    @Inject
    @Reference
    private ArkadinDAOService arkadinDAOService;

    @Inject
    @Reference
    private ArkadinConfigService arkadinConfigService;

    @Inject
    @Reference
    private ApimDAOService apimDAOService;

    @Inject
    @Reference
    private THDAOService thDAOService;

    @SuppressWarnings("squid:S2221")
    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {

        String externalId = request.getHeader(Constants.Properties.HEADER_EXTERNAL_ID);
        Resource currentPage = Request.getCurrentPageResource(request, null);
        Page myPage = currentPage != null ? currentPage.adaptTo(Page.class) : null;
        // WebinarResponseDO object
        WebinarResponseDO webinarResponseDO = new WebinarResponseDO();
        HttpSession session = request.getSession(false);
        if (session != null) {
            Object sessionId = session.getAttribute(Constants.Properties.SHIBBOLETH_SESSION_VAR);
            if (sessionId != null) {
                webinarResponseDO.setSessionId(sessionId.toString());
            }
        }

        if (myPage != null) {
            // Get pageProperties
            ValueMap pageProperties = myPage.getProperties();
            // Task 230762 Decommissioned Webinar
            String webinarshowlink = pageProperties.get(ConstantsWebinars.DECOM_VIDEO_URL, String.class);
            try {
                // Get showKey from pageProperties
                String showKey = pageProperties.get(ConstantsWebinars.SHOW_KEY, String.class);
                // BUG 347630 Add the time status, so it's always present on JSON
                int windowTime = myEDCConfigSystemService.getWebinarsWindowTime();
                String timeStatus = WebinarsHelper.getWebinarStatus(myPage, windowTime);
                webinarResponseDO.setTimeStatus(timeStatus);
                // If there is an Arkadin account get registered status for the user
                if (appAuthDAOService.appAuthAccountProvisioned(externalId)) {
                    webinarResponseDO.setAccountProvisioned(true);
                    // Get registered status for user
                    boolean registeredToWebinar = getWebinarRegistrationStatus(externalId, showKey);
                    if (registeredToWebinar) {
                        webinarResponseDO.setUserRegistered(true);
                        // Send URL only if live or ondemand and registered
                        if (!ConstantsWebinars.WEBINAR_STATE_UPCOMING.equals(timeStatus)
                                && getMyEDCCompletionStatus(externalId)) {
                            if (StringUtils.isBlank(webinarshowlink)) {
                                webinarshowlink = arkadinConfigService.getShowBaseUrl().concat(showKey);
                            }
                            webinarResponseDO.setWebinarUrl(webinarshowlink);
                        }
                    }
                }
            } catch (Exception e) {
                // Since the "Decomissioned Webinars" have an invalid id, we must check
                // if that's the intent and add the video URL
                if (StringUtils.isNotBlank(webinarshowlink)) {
                    webinarResponseDO.setWebinarUrl(webinarshowlink);
                    webinarResponseDO.setUserRegistered(true);
                } else {
                    log.error("error on WebinarService {}", e.getStackTrace());
                    webinarResponseDO.setErrorMessage(TranslationHelper
                            .getI18nTranslation(ConstantsWebinars.WEBINAR_API_ERROR_KEY, request, null));
                }
            }
        } else {
            log.error("error on WebinarService Page Resource is null");
            webinarResponseDO.setErrorMessage(
                    TranslationHelper.getI18nTranslation(ConstantsWebinars.WEBINAR_PAGE_LOAD_ERROR_KEY, request, null));
        }
        // Send response

        String jsonstring = WebinarDataBindingUtil.webinarResponseDOToJson(webinarResponseDO);
        ServletResponseHelper.writeResponse(response, jsonstring);
    }

    @SuppressWarnings("squid:S2221")
    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {
        String externalId = request.getHeader(Constants.Properties.HEADER_EXTERNAL_ID);
        Resource currentPage = Request.getCurrentPageResource(request, null);
        Page myPage = currentPage != null ? currentPage.adaptTo(Page.class) : null;
        boolean showError = false;
        // WebinarResponseDO object
        WebinarResponseDO webinarResponseDO = new WebinarResponseDO();
        HttpSession session = request.getSession(false);
        if (session != null) {
            Object sessionId = session.getAttribute(Constants.Properties.SHIBBOLETH_SESSION_VAR);
            if (sessionId != null) {
                // Page could not be resolved
                if (myPage != null) {
                    // Get pageProperties
                    ValueMap pageProperties = myPage.getProperties();
                    // Task 230762 Decommissioned Webinar
                    String webinarshowlink = pageProperties.get(ConstantsWebinars.DECOM_VIDEO_URL, String.class);
                    // BUG 347630 Add the time status, so it's always present on JSON
                    int windowTime = myEDCConfigSystemService.getWebinarsWindowTime();
                    String timeStatus = WebinarsHelper.getWebinarStatus(myPage, windowTime);
                    webinarResponseDO.setTimeStatus(timeStatus);
                    try {
                        webinarResponseDO.setSessionId(sessionId.toString());
                        // Get showKey from pageProperties
                        String showKey = pageProperties.get(ConstantsWebinars.SHOW_KEY, String.class);
                        // Register user to webinar
                        boolean registerToWebinar = false;
                        // If there is no Arkadin account, create one, if there is, set to true
                        if (!appAuthDAOService.appAuthAccountProvisioned(externalId)) {
                            if (appAuthDAOService.createAppAuthAccount(externalId)) {
                                webinarResponseDO.setAccountProvisioned(true);
                                // Register user to webinar
                                registerToWebinar = registerUserToWebinar(externalId, showKey);
                            }
                        } else {
                            webinarResponseDO.setAccountProvisioned(true);
                            // Register user to webinar
                            registerToWebinar = registerUserToWebinar(externalId, showKey);
                        }
                        if (registerToWebinar) {
                            // Send URL only if live or ondemand
                            if (!ConstantsWebinars.WEBINAR_STATE_UPCOMING
                                    .equals(timeStatus)
                                    && getMyEDCCompletionStatus(externalId)) {
                                // Task 230762 Decommissioned Webinar
                                if (StringUtils.isBlank(webinarshowlink)) {
                                    webinarshowlink = arkadinConfigService.getShowBaseUrl().concat(showKey);
                                }
                                webinarResponseDO.setWebinarUrl(webinarshowlink);
                            }
                            webinarResponseDO.setUserRegistered(true);
                            // Partners stuff
                            thDAOService.doWebinarTrackingRecord(request, myPage);
                        }
                    } catch (Exception e) {
                        // Since the "Decomissioned Webinars" have an invalid id, we must check
                        // if that's the intent and add the video URL
                        if (StringUtils.isNotBlank(webinarshowlink)) {
                            webinarResponseDO.setWebinarUrl(webinarshowlink);
                            webinarResponseDO.setUserRegistered(true);
                            showError = false;
                        } else {
                            log.error("error on WebinarService {}", e.getStackTrace());
                            showError = true;
                        }
                    }
                } else {
                    log.error("error on WebinarService Page Resource is null");
                    showError = true;
                }
                // Session is blank
            } else {
                // Show error to force session refresh
                log.error("error on WebinarService Session is null");
                showError = true;
            }
        }
        if (showError) {
            webinarResponseDO.setErrorMessage(
                    TranslationHelper.getI18nTranslation(ConstantsWebinars.WEBINAR_PAGE_LOAD_ERROR_KEY, request, null));
        }
        // Set response
        String jsonstring = WebinarDataBindingUtil.webinarResponseDOToJson(webinarResponseDO);
        ServletResponseHelper.writeResponse(response, jsonstring);
    }

    /**
     * Return webinar register success status
     *
     * @param externalId - string
     * @param showKey    - string
     * @return Boolean with webinar register success status
     */
    private Boolean registerUserToWebinar(String externalId, String showKey) {
        ArkadinRegistrationDO arkadinRegistrationDO = arkadinDAOService.registerUserToWebinar(externalId, showKey);
        Boolean success = false;
        for (OpCodeResult result : arkadinRegistrationDO.getOpCodeResults()) {
            // error 44 = user is already registered
            if ((result != null) && (result.getMessage().equals(ConstantsWebinars.WEBINAR_API_OK)
                    || result.getStatus().equals(ConstantsWebinars.WEBINAR_API_ALREADY_REGISTERED_CODE))) {
                success = true;
            }
        }
        return success;
    }

    /**
     * Return webinar current register status
     *
     * @param externalId - string
     * @param showKey    - string
     * @return Boolean with webinar current register status
     */
    private boolean getWebinarRegistrationStatus(String externalId, String showKey) {
        ArkadinRegistrationDO arkadinRegistrationDO = arkadinDAOService.getUserRegistrationStatus(externalId, showKey);
        Boolean success = false;
        if (arkadinRegistrationDO.getOpCodesInError().equals(ConstantsWebinars.WEBINAR_API_ZERO)) {
            for (OpCodeResult result : arkadinRegistrationDO.getOpCodeResults()) {
                if ((result != null) && (result.getMessage().equals(ConstantsWebinars.WEBINAR_API_OK))) {
                    success = true;
                }
            }
        }
        return success;
    }

    /**
     * Return MyEDC profile completion status
     *
     * @param request - servlet request
     * @return Boolean MyEDC profile completion status
     */
    private boolean getMyEDCCompletionStatus(String externalId) {
        Boolean isComplete = null;
        try {
            InfoPaymentStatusDO infoPaymentStatusDO = apimDAOService.getInfoPaymentStatus(externalId);
            boolean apimValid = APIMConstants.InfoPaymentResults.ACTIVE.getResult()
                    .equals(infoPaymentStatusDO.getResult());
            isComplete = false;
            if (apimValid) {
                isComplete = true;
            }
        } catch (EDCAPIMSystemException e) {
            log.error(e.toString());
        }
        return isComplete;
    }
}
