package com.edc.edcportal.core.interceptors;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.settings.SlingSettingsService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.edc.edcportal.core.apim.APIMConstants;
import com.edc.edcportal.core.apim.pojo.InfoPaymentStatusDO;
import com.edc.edcportal.core.apim.services.ApimDAOService;
import com.edc.edcportal.core.eloqua.EloquaConnectionManagerUtil;
import com.edc.edcportal.core.eloqua.pojo.EloquaUserProfileDO;
import com.edc.edcportal.core.eloqua.services.EloquaDAOService;
import com.edc.edcportal.core.exception.EDCException;
import com.edc.edcportal.core.helpers.Constants;
import com.edc.edcportal.core.helpers.FormValidationHelper;
import com.edc.edcportal.core.helpers.LanguageUtil;
import com.edc.edcportal.core.helpers.LinkResolver;
import com.edc.edcportal.core.helpers.LoginRequestHeadersUtil;
import com.edc.edcportal.core.helpers.PagePathsHelper;
import com.edc.edcportal.core.helpers.QueryParamsHelper;
import com.edc.edcportal.core.helpers.RedirectHelper;
import com.edc.edcportal.core.helpers.SessionHelper;
import com.edc.edcportal.core.models.ProfileTypeDefinition;
import com.edc.edcportal.core.services.MyEDCConfigSystemService;
import com.edc.edcportal.core.transactionhistory.services.THDAOService;

/**
 * AEM Profile Interceptor.
 *
 * Check page requests and implement accordingly
 *
 */
@Component(immediate = true, configurationPid = "com.edc.edcportal.core.servlets.AEMProfileInterceptor", service = {
        Filter.class }, property = { "sling.filter.scope=request", "sling.filter.pattern=/content/myedc/(.*)/(.*)",
                "service.ranking:Integer=" + Integer.MAX_VALUE, })
public class AEMProfileInterceptor implements Filter {
    protected static final Logger log = LoggerFactory.getLogger(AEMProfileInterceptor.class);

    @Reference
    protected EloquaDAOService eloquaDAOService;

    @Reference
    private ApimDAOService apimDAOService;

    @Reference
    private MyEDCConfigSystemService myEDCConfigSystemService;

    @Reference
    private THDAOService thDAOService;

    @Reference
    private SlingSettingsService slingSettingsService;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.debug("init");
    }

    /**
     * this is called when the profile page is page is loaded, so it will check if
     * the profile value has been set or not and redirect accordingly.
     *
     * @throws IOException
     */

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {

        final SlingHttpServletRequest request = (SlingHttpServletRequest) servletRequest;
        final SlingHttpServletResponse response = (SlingHttpServletResponse) servletResponse;

        // Skip this if author, so we can edit it
        Set<String> runModes = this.slingSettingsService.getRunModes();
        if (runModes.contains("author")) {
            filterChain.doFilter(servletRequest, servletResponse);
        }

        // Get the page from the resource
        Resource resource = request.getResource();
        PageManager pageManager = resource.getResourceResolver().adaptTo(PageManager.class);
        Page currentPage = pageManager.getContainingPage(resource);
        String externalId;
        String redirectTo = null;
        // Set path variables
        String pageLanguage = LanguageUtil.getLanguageAbbreviationFromPath(currentPage.getPath(),
                Constants.SupportedLanguages.ENGLISH.getLanguageAbbreviation());

        String selectPagePath = PagePathsHelper.getSelectPagePath(pageLanguage);
        String homePagePath = PagePathsHelper.getHomePagePath(pageLanguage);
        String registerPagePath = PagePathsHelper.getRegisterPagePath(pageLanguage);
        String renewalPagePath = PagePathsHelper.getRenewalPagePath(pageLanguage);

        // Get external system error page path
        String externalErrorPath = pageLanguage.equals("en") ? Constants.Paths.MYACCOUNT_SYSTEM_ERROR_PATH_EN
                : Constants.Paths.MYACCOUNT_SYSTEM_ERROR_PATH_FR;

        // Set to dummy profile type to validate below
        String profileType = null;
        String marketsOfInterest = null;

        // check if registration is complete
        boolean regIsComplete = false;
        boolean needRenewal = false;
        boolean apimValid = false;
        String apimResult = APIMConstants.InfoPaymentResults.NONE.getResult();
        try {
            // Get values from Query String parameter, check state first
            String queryString = request.getParameter(Constants.Properties.QUERY_PARAM_STATE);
            log.debug("AEM profile interceptor state query String: {}", queryString);
            // Check if we have some from state, if not get the ProductIntent
            if (StringUtils.isBlank(queryString)) {
                queryString = request.getParameter(Constants.Properties.QUERY_PARAM_PRODUCT_INTENT);
                log.debug("AEM profile interceptor productIntent query String: {}", queryString);
            }
            String[] queryParams = QueryParamsHelper.getProgressivProfileParams(queryString);
            String productType = queryParams[0];
            String productDesc = queryParams[1];
            String redirectUrl = queryParams[2];
            String personaEbook = queryParams[3];
            String dataShareConsent = queryParams[4];
            String webinarRegPending = queryParams[5];
            // Use the param is exists
            if (StringUtils.isBlank(redirectUrl)) {
                redirectUrl = QueryParamsHelper.getRedirectUrl(request);
            }
            if (StringUtils.isBlank(webinarRegPending)) {
                webinarRegPending = request.getParameter("webinarRegPending");
            }
            log.debug("AEMProfileInterceptor QueryParams {} path {}", queryParams, request.getPathInfo());
            // Get Campaign Query String
            String campaignQs = QueryParamsHelper
                    .getCampaignQs(request.getParameter(Constants.Properties.QUERY_PARAM_UTM), request);
            String renewalCampaignQs = null;
            // get the external id
            Map<String, String> headers = LoginRequestHeadersUtil.getHeaders(request);
            externalId = headers.get(Constants.Properties.HEADER_EXTERNAL_ID);
            // if no external id, redirect back to login
            if (StringUtils.isBlank(externalId)) {
                RedirectHelper.redirectTo(response, myEDCConfigSystemService.getLoginUrl());
            } else {
                // TASK 235050 Must check Eloqua first to avoid issues because the Stack Alignment
                // record might be valid on POP but no data on Eloqua
                EloquaUserProfileDO eloquaUserProfileDO = EloquaActions.checkEloqua(externalId, headers,
                        productType, productDesc, eloquaDAOService);
                // Check profile type if we have record
                if (!eloquaUserProfileDO.getFormFieldsValues().isEmpty()) {
                    profileType = EloquaConnectionManagerUtil.getELoquaValueFromDO(eloquaUserProfileDO,
                        eloquaDAOService.getEloquaConfigService().getProfileFieldId());
                    marketsOfInterest = EloquaConnectionManagerUtil.getELoquaValueFromDO(eloquaUserProfileDO,
                            eloquaDAOService.getEloquaConfigService().getMarketsIntFieldId());
                }
                // If profile type is blank and marketsOfInterest are blank, 
                // profile is not complete, do not check apim yet
                // Task 314271 use checkMarketsOfInterest
                if (StringUtils.isNotBlank(profileType) && FormValidationHelper.checkMarketsOfInterest(profileType, marketsOfInterest)) {
                    InfoPaymentStatusDO infoPaymentStatusDO = apimDAOService.getInfoPaymentStatus(externalId);
                    apimResult = infoPaymentStatusDO.getResult();
                    apimValid = APIMConstants.InfoPaymentResults.ACTIVE.getResult()
                            .equals(apimResult);
                } 
                // END TASK 235050
                if (apimValid) {
                    // Result: "Active" record exists, "unlock" the user
                    if (!StringUtils.isBlank(dataShareConsent)) {
                        EloquaActions.checkDataShareConsent(externalId, headers, eloquaDAOService, dataShareConsent);
                    }
                    regIsComplete = true;
                } else {
                    // Result: Other than "Active" Must renew / create the profile
                    // add data share consent to path so the value is not lost
                    redirectUrl = StringUtils.isBlank(dataShareConsent) ? redirectUrl
                            : redirectUrl.concat(Constants.Properties.SUB_QUERY_PARAM_ACTION_TYPE_AMPERSAND)
                                    .concat(Constants.Properties.DATA_SHARE_CONSENT_FIELD)
                                    .concat(Constants.Properties.SUB_QUERY_PARAM_ACTION_TYPE_EQUAL_SIGN)
                                    .concat(dataShareConsent);
                    // Result:"Expired" renew the profile
                    if (APIMConstants.InfoPaymentResults.EXPIRED.getResult()
                            .equals(apimResult)) {
                        needRenewal = true;
                        regIsComplete = true;
                    } else {
                    //Result: "None" or other status, must create the profile
                        regIsComplete = false;
                    }
                }
                // we have a profile set but registration is not complete go and complete it
                if (StringUtils.isNotBlank(profileType) && !regIsComplete
                        && !registerPagePath.equals(currentPage.getPath())) {
                    redirectTo = LinkResolver.changeInternalURLtoExternal(request,
                            LinkResolver.addHtmlExtension(registerPagePath, Constants.Paths.CONTENT_MYEDC));
                    log.debug("AEM profile interceptor we have a profile set but registration is not complete go and complete it. redirectTo {}", redirectTo);
                }
                // we should have record, with no profile set, go and set it
                if (StringUtils.isBlank(profileType) && !selectPagePath.equals(currentPage.getPath())) {
                    redirectTo = LinkResolver.changeInternalURLtoExternal(request,
                            LinkResolver.addHtmlExtension(selectPagePath, Constants.Paths.CONTENT_MYEDC));
                    log.debug("AEM profile interceptor we should have record, with no profile set, go and set it. redirectTo {}", redirectTo);
                }
                // we should have record, but expired
                if (StringUtils.isNotBlank(profileType) && regIsComplete && needRenewal
                        && !renewalPagePath.equals(currentPage.getPath())) {
                    redirectTo = LinkResolver.changeInternalURLtoExternal(request,
                            LinkResolver.addHtmlExtension(renewalPagePath, Constants.Paths.CONTENT_MYEDC));
                    renewalCampaignQs = QueryParamsHelper.getUmtsFromQs(request);
                    log.debug("AEM profile interceptor we have a profile set but registration is expired, renew. redirectTo {}", redirectTo);
                }
                // User is logged-in & has completed profile registration, use redirect
                if (StringUtils.isNotBlank(profileType) && regIsComplete && !needRenewal && StringUtils.isNotBlank(redirectUrl)
                        && !redirectUrl.contains(currentPage.getPath())) {
                    redirectTo = redirectUrl;
                    log.debug("AEM profile interceptor User is logged-in & has completed profile registration, use redirect. redirectTo {}", redirectTo);
                }
            }

            // User is unlocked, prevent to go to select profile or register or renewal pages
            // if campaignQs reload the homepage to reflect the querystring
            String[] dissalowedPages = {selectPagePath, registerPagePath, renewalPagePath};
            boolean notAllowedPage = PagePathsHelper.isAllowedPage(dissalowedPages, currentPage.getPath());
            if (StringUtils.isNotBlank(profileType) && ((regIsComplete && !needRenewal && notAllowedPage) || StringUtils.isNotBlank(campaignQs))) {
                redirectTo = LinkResolver.changeInternalURLtoExternal(request,
                        LinkResolver.addHtmlExtension(homePagePath, Constants.Paths.CONTENT_MYEDC));
            }

            // Create session only if registration is complete and does not need renewal
            if (regIsComplete && !needRenewal) {
                // The user has a full profile, create or update the session
                EloquaUserProfileDO eloquaUserProfileDO = eloquaDAOService.getUserProfileByExternalId(externalId);
                String userProfileType = EloquaConnectionManagerUtil.getELoquaValueFromDO(eloquaUserProfileDO,
                        eloquaDAOService.getEloquaConfigService().getProfileFieldId());
                // Start the session and add the tracking
                SessionHelper.startSession(request, userProfileType, externalId);
                // Add persona to session for persona ebooks
                if (!StringUtils.isBlank(personaEbook)) {
                    request.getSession().setAttribute(Constants.Properties.PERSONA_SESSION_VAR, personaEbook);
                }
            }

            // If there's a redirect, use it. Otherwise, load the page
            if (redirectTo != null) {
                // Add the query string param only if it is not the same page
                if (!redirectTo.equalsIgnoreCase(redirectUrl)) {
                    // Task 22143 squid:S864
                    if (StringUtils.isNotBlank(redirectUrl)) {
                        redirectTo = redirectTo.concat("?redirectUrl=").concat(redirectUrl);
                    }
                    // Task 22143 squid:S864
                    if (StringUtils.isNotBlank(webinarRegPending)) {
                        redirectTo = redirectTo.concat("&webinarRegPending=").concat(webinarRegPending);
                    }
                    log.debug("AEM profile interceptor Added redirectUrl Param. redirectTo {}, redirectUrl {}", redirectTo, redirectUrl);
                }
                redirectTo = PagePathsHelper.removeContentPath(redirectTo);
                if (StringUtils.isBlank(campaignQs) && StringUtils.isBlank(redirectUrl)) {
                // Use a different variable name to avoid redirect loop for condition on line 239
                // add only if we don't have a redirect URL as it should have all the parameters
                    campaignQs = renewalCampaignQs;
                }
                if (StringUtils.isNotBlank(campaignQs)) {
                    String qsSepatator = "?";
                    if (redirectTo.contains(qsSepatator)) {
                        qsSepatator = "&";
                    }
                    redirectTo = redirectTo.concat(qsSepatator).concat(campaignQs);
                }
                log.debug("AEM profile interceptor redirect from {} to {}", currentPage.getPath(), redirectTo);
                RedirectHelper.redirectTo(response, redirectTo);
            }
        } catch (EDCException e) {
            log.error(e.toString());
            redirectTo = externalErrorPath;
            RedirectHelper.redirectTo(response, redirectTo);
            return;
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
        // Sonar lint
    }
}