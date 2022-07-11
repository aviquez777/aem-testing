package com.edc.edcportal.core.servlets;

import java.io.IOException;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edc.edcportal.core.apim.helpers.DataObjectTransformationHelper;
import com.edc.edcportal.core.apim.pojo.InfoPaymentDO;
import com.edc.edcportal.core.apim.services.ApimDAOService;
import com.edc.edcportal.core.appauth.services.AppAuthDAOService;
import com.edc.edcportal.core.arkadin.services.ArkadinDAOService;
import com.edc.edcportal.core.eloqua.EloquaConnectionManagerUtil;
import com.edc.edcportal.core.eloqua.pojo.EloquaUserProfileDO;
import com.edc.edcportal.core.eloqua.services.EloquaDAOService;
import com.edc.edcportal.core.exception.EDCAPIMSystemException;
import com.edc.edcportal.core.exception.EDCEloquaSystemException;
import com.edc.edcportal.core.helpers.Constants;
import com.edc.edcportal.core.helpers.CookieHelper;
import com.edc.edcportal.core.helpers.FormValidationHelper;
import com.edc.edcportal.core.helpers.LanguageUtil;
import com.edc.edcportal.core.helpers.LinkResolver;
import com.edc.edcportal.core.helpers.LoginRequestHeadersUtil;
import com.edc.edcportal.core.helpers.PagePathsHelper;
import com.edc.edcportal.core.helpers.QueryParamsHelper;
import com.edc.edcportal.core.helpers.RedirectHelper;
import com.edc.edcportal.core.helpers.SessionHelper;
import com.edc.edcportal.core.helpers.WebinarsHelper;
import com.edc.edcportal.core.helpers.formvalidation.FormCleaner;
import com.edc.edcportal.core.services.FieldMappingConfigService;
import com.edc.edcportal.core.services.MyEDCConfigSystemServiceUtils;
import com.edc.edcportal.core.transactionhistory.services.THDAOService;

@SuppressWarnings("CQRules:CQBP-75")
@Component(immediate = true, 
            service = Servlet.class, 
            property = { 
                    "sling.servlet.extensions=post",
                    "sling.servlet.paths=" + Constants.Paths.FRONT_CONTROLLER_SERVLET_URL
                    }
)

public class FrontController extends SlingAllMethodsServlet {

    private static final long serialVersionUID = -3522134777775086197L;
    private static final Logger log = LoggerFactory.getLogger(FrontController.class);

    @Reference
    @Inject
    private EloquaDAOService eloquaDAOService;

    @Reference
    @Inject
    private FieldMappingConfigService fieldMappingConfigService;

    @Reference
    @Inject
    private ApimDAOService apimDAOService;

    @Reference
    @Inject
    private AppAuthDAOService appAuthDAOService;

    @Reference
    @Inject
    private ArkadinDAOService arkadinDAOService;

    @Reference
    @Inject
    private THDAOService thDAOService;

    @Override
    protected final void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {
        // Default Language and default action to null to validate later
        String language = null;
        String actionType = null;
        boolean result = false;
        // Get values from Query String parameter
        String[] queryParams = QueryParamsHelper
                .getUpdateBasicInfoParams(request.getParameter(Constants.Properties.QUERY_PARAM_ACTION_TYPE));
        language = queryParams[0];
        actionType = queryParams[1];
        // Remove any Query String from referrer
        String referer = request.getParameterMap().containsKey(Constants.Properties.REFERER)
                ? request.getHeader(Constants.Properties.REFERER).replaceFirst("\\?.*$", "")
                : "";
        // redirect variables
        String redirectTo = null;
        // Language is blank get it from referrer, if any
        if (StringUtils.isBlank(language)) {
            language = LanguageUtil.getLanguageAbbreviationFromPath(referer, null);
        }
        // Language still blank, get it from profile
        if (StringUtils.isBlank(language)) {
            // get the external id
            Map<String, String> headers = LoginRequestHeadersUtil.getHeaders(request);
            String externalId = headers.get(Constants.Properties.HEADER_EXTERNAL_ID);
            try {
                EloquaUserProfileDO eloquaUserProfileDO = eloquaDAOService.getUserProfileByExternalId(externalId);
                String langFieldId = eloquaDAOService.getEloquaConfigService().getLangdId();
                language = eloquaUserProfileDO.getFormFieldsValues().get(langFieldId).getEloquaValue();
            } catch (EDCEloquaSystemException e) {
                log.error("Not externalId on headers {}", headers);
                // if no external id, redirect back to login
                RedirectHelper.redirectTo(response,
                        MyEDCConfigSystemServiceUtils.getMyEDCConfigSystemService().getLoginUrl());
            }
        }
        // Get external system error page path
        String externalErrorPath = Constants.Paths.MYACCOUNT_SYSTEM_ERROR_PATH_EN;
        if (Constants.SupportedLanguages.FRENCH.getLanguageAbbreviation().equalsIgnoreCase(language)) {
            externalErrorPath = Constants.Paths.MYACCOUNT_SYSTEM_ERROR_PATH_FR;
        }
        // Right now both actions go to the same page, the only difference is the eloqua
        // update on actionType= password no update is done
        try {
            log.debug("GET referrer: {} actionType: {}", referer, actionType);
            if (Constants.Properties.QUERY_PARAM_ACTION_TYPE_BASIC.equals(actionType)) {
                EloquaUserProfileDO eloquaUserProfileDO = B2CServletActions.updateBasicProfile(request,
                        eloquaDAOService);
                result = eloquaUserProfileDO.getUpdateSucessfull();
                // Task 314271
                String marketsIntId = eloquaDAOService.getEloquaConfigService().getMarketsIntFieldId();
                String marketsOfInterest = eloquaUserProfileDO.getFormFieldsValues().get(marketsIntId).getEloquaValue();
                String profileId = eloquaDAOService.getEloquaConfigService().getProfileFieldId();
                String profileType = eloquaUserProfileDO.getFormFieldsValues().get(profileId).getEloquaValue();
                boolean hasMarkets = FormValidationHelper.checkMarketsOfInterest(profileType, marketsOfInterest);
                // end Task 314271
                boolean hasPainPoints = FormValidationHelper.checkPainPoints(eloquaUserProfileDO, eloquaDAOService);
                if (!hasMarkets) {
                    log.error("Not Markets Of interest on RecordID: {}", eloquaUserProfileDO.getCdoRecordId());
                }
                if (!hasPainPoints) {
                    log.error("No PainPoints on RecordID: {}", eloquaUserProfileDO.getCdoRecordId());
                }
                if (result && eloquaUserProfileDO.getPopReady()) {
                    // If all info went OK to Eloqua, always update POP Task #99000
                    InfoPaymentDO infoPaymentDO = DataObjectTransformationHelper
                            .populateInfoPymtDOFromEloquaUserProfileDO(eloquaUserProfileDO, fieldMappingConfigService,
                                    request);
                    result = apimDAOService.submitInfoPayment(infoPaymentDO);
                    log.debug("updated POP snaphsot on GET result: {} hasMarkets: {}", result, hasMarkets);
                }
            }
            redirectTo = B2CServletActions.redirectUserPath(request, language, actionType, result);
            RedirectHelper.redirectTo(response, redirectTo);
        } catch (EDCEloquaSystemException | EDCAPIMSystemException e) {
            log.error(e.toString());
            redirectTo = externalErrorPath;
            RedirectHelper.redirectTo(response, redirectTo);
        }
    }

    @Override
    protected final void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {
        Map<String, String> headers = LoginRequestHeadersUtil.getHeaders(request);
        String externalId = headers.get(Constants.Properties.HEADER_EXTERNAL_ID);
        Map<String, String> headersForEloqua = LoginRequestHeadersUtil.getHeadersForEloqua(headers,
                eloquaDAOService.getEloquaConfigService());
        // Check for data share consent
        String dataShareConsentVal = request.getParameter(Constants.Properties.DATA_SHARE_CONSENT_FIELD);
        headersForEloqua.put(eloquaDAOService.getEloquaConfigService().getDataShareConsent(), dataShareConsentVal);
        String guiID = request.getParameter(Constants.Properties.GUID_FIELD) == null ? ""
                : request.getParameter(Constants.Properties.GUID_FIELD);
        headersForEloqua.put(eloquaDAOService.getEloquaConfigService().getGuidId(), guiID);
        // if no external id, redirect back to login
        if (StringUtils.isBlank(externalId)) {
            RedirectHelper.redirectTo(response,
                    MyEDCConfigSystemServiceUtils.getMyEDCConfigSystemService().getLoginUrl());
        }
        // Remove any Query String from referrer
        String referer = request.getHeader(Constants.Properties.REFERER).replaceFirst("\\?.*$", "");
        // redirect variables
        String pageLanguage = LanguageUtil.getLanguageAbbreviationFromPath(referer,
                Constants.SupportedLanguages.ENGLISH.getLanguageAbbreviation());
        String basePath = PagePathsHelper.getBasePath(pageLanguage);
        String redirectTo = Constants.Paths.PROFILE;
        String fileName = referer.substring(referer.lastIndexOf('/') + 1, referer.length());
        String pageName = fileName.substring(0, fileName.lastIndexOf('.'));
        String redirectUrl = QueryParamsHelper.getRedirectUrl(request);
        // Get external system error page path
        String externalErrorPath = Constants.Paths.MYACCOUNT_SYSTEM_ERROR_PATH_EN;
        String qs = request.getParameter(Constants.Properties.FORM_FIELD_QS_TEXT);
        if (Constants.SupportedLanguages.FRENCH.getLanguageAbbreviation().equalsIgnoreCase(pageLanguage)) {
            externalErrorPath = Constants.Paths.MYACCOUNT_SYSTEM_ERROR_PATH_FR;
        }
        try {
            // get the record from the external id
            EloquaUserProfileDO eloquaUserProfileDO = eloquaDAOService.getUserProfileByExternalId(externalId);
            // flag to process the internal url
            boolean internalUrl = true;
            // flag to register user to webinar after profile completion
            String webinarRegPending = request.getParameter("webinarRegPending");
            if (pageLanguage.equalsIgnoreCase(Constants.SupportedLanguages.FRENCH.getLanguageAbbreviation())) {
                pageName = PagePathsHelper.getResourceNameFromAlias(request, pageName);
            }
            // ***Save Profile Type ***
            if (StringUtils.contains(Constants.Paths.SELECT_PROFILE, pageName)) {
                redirectTo = ServletActions.selectProfileType(request, eloquaDAOService, eloquaUserProfileDO,
                        headersForEloqua);
                // Add all the qs param
                // Fixing Side effect of bug 390888 .html is added on ServletActions.selectProfileType
                if (StringUtils.isNotBlank(qs)) {
                    redirectTo += "?".concat(qs);
                }
            } else if (StringUtils.contains(Constants.Paths.REGISTER, pageName)) {
                // Save Register Page
                // UI 84500 Update Traffic src from Eloqua
                String trafficsrc = CookieHelper.getCookieValue(request, Constants.Properties.TRAFFIC_SOURCE_COOKIE_NAME);
                if (StringUtils.isNotBlank(trafficsrc)) {
                    headersForEloqua.put(eloquaDAOService.getEloquaConfigService().getFinalTrafficSrc(), trafficsrc);
                }
                // *** Save Profile Info ***
                // BUG-65916 Account Creation - Directed to profile page, not dashboard
                redirectTo = ServletActions.updateProfile(request, eloquaDAOService, eloquaUserProfileDO,
                        headersForEloqua, fieldMappingConfigService, Constants.ACTION_NEW_PROFILE);
                // If webinarRegPending, register user to webinar
                if (StringUtils.isNotBlank(webinarRegPending) && StringUtils.isNotBlank(redirectUrl)) {
                    WebinarsHelper.registerNewUserToWebinar(request, redirectUrl, externalId, appAuthDAOService,
                            arkadinDAOService);
                }
                // Terms and conditions not saved
                String[] termsConds = FormCleaner
                        .cleanArray(request.getParameterValues(Constants.Properties.TERMS_CONDITIONS_FIELD));
                if (ArrayUtils.isEmpty(termsConds)) {
                    redirectTo = Constants.Paths.REGISTER;
                } else {
                    // Finished registration back to wherever necessary
                    if (StringUtils.isNotBlank(redirectUrl)) {
                        redirectTo = redirectUrl;
                        redirectUrl = null;
                        internalUrl = false;
                    }
                }
            } else if (StringUtils.contains(Constants.Paths.EDIT_PROFILE, pageName)) {
                // *** Update profile Info ***
                redirectTo = ServletActions.updateProfile(request, eloquaDAOService, eloquaUserProfileDO,
                        headersForEloqua, fieldMappingConfigService, Constants.ACTION_UPDATE_PROFILE);
                
            } else if (StringUtils.contains(Constants.Paths.RENEWAL, pageName)) {
                // *** Renewal profile Info ***
                redirectTo = ServletActions.updateProfile(request, eloquaDAOService, eloquaUserProfileDO,
                        headersForEloqua, fieldMappingConfigService, Constants.ACTION_RENEW_PROFILE);
            }
            // Complete the path if we are staying local
            if (internalUrl) {
                redirectTo = basePath.concat(redirectTo);
                // Bug 029063 LinkResolver.addHtmlExtension assumes adds html extension even if url has extension
                redirectTo = LinkResolver.changeInternalURLtoExternal(request, redirectTo);
            }
            // Update and keep going only POP only id eloquaUserProfileDO is OK
            if (eloquaUserProfileDO.getPopReady()) {
                // Add debugging data
                String debugQs = request.getParameter(Constants.Properties.QUERY_PARAM_ACTION_TYPE);
                log.debug("POST referrer: {} queryString: {}", referer, debugQs);
                // If all info went OK to Eloqua, always update POP Task #99000
                InfoPaymentDO infoPaymentDO = DataObjectTransformationHelper.populateInfoPymtDOFromEloquaUserProfileDO(
                        eloquaUserProfileDO, fieldMappingConfigService, request);
                boolean popOK = apimDAOService.submitInfoPayment(infoPaymentDO);
                log.debug("updated POP snaphsot on POST result {} ", popOK);
                // Add the query string param only if it is not the same page
                if (StringUtils.isNotBlank(redirectUrl) && !redirectTo.equalsIgnoreCase(redirectUrl)) {
                    redirectTo += "?redirectUrl=" + redirectUrl;
                    if (StringUtils.isNotBlank(webinarRegPending)) {
                        redirectTo += "&webinarRegPending=" + webinarRegPending;
                    }
                }
                // Add the query string if exists, override redirectUrl as it should be there
                // Do not append the qs if profile has successfully updated (session is not
                // null)
                String updStatus = (String) request.getSession()
                        .getAttribute(Constants.Properties.PROFILE_STATUS_SESSION_VAR);
                if (StringUtils.isNotBlank(qs) && StringUtils.isBlank(updStatus)) {
                    redirectTo += "?".concat(qs);
                }
                // Add the dataShareConsentValue to the redirectTo url when it's the profileType
                // page and the value is in the qstext field
                if (StringUtils.isNotBlank(qs) && qs.contains(Constants.Properties.DATA_SHARE_CONSENT_FIELD)
                        && StringUtils.contains(Constants.Paths.SELECT_PROFILE, pageName)) {
                    String qsSepatator = redirectTo.contains("?") ? "&" : "?";
                    String[] params = qs.split("&");
                    if (params.length > 1) {
                        redirectTo = redirectTo.concat(qsSepatator).concat(params[1]);
                    }
                }
                // The user has a full profile, create the session
                String profileType = EloquaConnectionManagerUtil.getELoquaValueFromDO(eloquaUserProfileDO,
                        eloquaDAOService.getEloquaConfigService().getProfileFieldId());
                // Start the session if all is OK
                SessionHelper.startSession(request, profileType, externalId);
            }
            // Prepare the redirect
            redirectTo = PagePathsHelper.removeContentPath(redirectTo);
            log.debug("FrontController redirect to {} ", redirectTo);
            RedirectHelper.redirectTo(response, redirectTo);
        } catch (EDCEloquaSystemException | EDCAPIMSystemException e) {
            log.error(e.toString());
            redirectTo = externalErrorPath;
            RedirectHelper.redirectTo(response, redirectTo);
        }
    }
}