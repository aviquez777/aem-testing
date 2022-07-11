package com.edc.edcweb.core.helpers;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edc.edcweb.core.helpers.formvalidation.FieldValidators;
import com.edc.edcweb.core.helpers.progressiveprofiling.ProgressiveProfilingHelper;

/**
 * <h1>Sign In Helper</h1> Helper functions to call from the HTL.
 * buildSignInQueryString(): Builds a myEDC login url with query string based on
 * url and parameters. getLoginUrl(): Returns the myEDC login url with product
 * parameters. getSignUpUrl(): Returns the myEDC signup url with product
 * parameters.
 **/

@Model(adaptables = SlingHttpServletRequest.class)
public class SignInHelper {

    private static final Logger log = LoggerFactory.getLogger(SignInHelper.class);

    @Self
    private SlingHttpServletRequest request;

    // Build url with product parameters
    public static String buildSignInQueryString(SlingHttpServletRequest request, String url, String productTypeValue,
            String productDescValue, boolean isRegister, boolean emailHasAccount, Integer accessLevel,
            boolean noRedirectBack) {

        // Get SignIn Constants
        String target = Constants.Properties.TARGET;
        String productIntent = Constants.Properties.QUERY_PARAM_PRODUCT_INTENT;
        String delim = Constants.Properties.PARAM_DELIMITER;
        String queryString = null;

        String pageLanguage = getLanguage(request, url);
        String basePath = getBasePath(pageLanguage);
        String logInUrl = getLoginPageUrl(pageLanguage);
        String signUpUrl = getSignupPageUrl(pageLanguage);
        String myEdcHome = basePath + getHomePageUrl(pageLanguage);
        String myedcLogIn = basePath + logInUrl;
        String myedcSignUp = basePath + signUpUrl;
        url = LinkResolver.addHtmlExtension(url);

        if (StringUtils.isNotBlank(url)) {
            // bug 183199 use alias if French
            if (Constants.SupportedLanguages.FRENCH.getLanguageAbbreviation().equalsIgnoreCase(pageLanguage)) {
                ResourceResolver resourceResolver = request.getResourceResolver();
                url = resourceResolver.map(request, url);
            }
            String qs = request.getQueryString();
            // Bug 389379 do not add refPremiumURL to the Query String as the search term prevents redirect
            if (StringUtils.isNotBlank(qs) && !FieldValidators.containsWord(qs, Constants.Properties.INVALID_URL_QS.split(","))) {
                // BUG 100755 decode the query string if any
                try {
                    qs = URLDecoder.decode(qs, StandardCharsets.UTF_8.name());
                    // replace spaces for "+"
                    qs = qs.replace(" ", Constants.PLUS_SIGN);
                } catch (UnsupportedEncodingException e) {
                    log.error("SignInHelper error decoding query string {}, error {}", qs, e.getStackTrace());
                }
                qs = StringUtils.replace(qs, Constants.Properties.SUB_QUERY_PARAM_ACTION_TYPE_AMPERSAND,
                        Constants.Properties.SUB_QUERY_PARAM_ACTION_TYPE_DELIMITER);
                url = url.concat("?").concat(qs);
            }

            myEdcHome = myEdcHome + Constants.HTML_EXTENTION;
            myedcLogIn = myedcLogIn + Constants.HTML_EXTENTION;
            myedcSignUp = myedcSignUp + Constants.HTML_EXTENTION;

            // Build query string with product type and desc parameters

            // Escape spaces, just to be on the safe side
            productTypeValue = productTypeValue.replace(" ", Constants.PLUS_SIGN);
            productDescValue = productDescValue.replace(" ", Constants.PLUS_SIGN);

            if (url.contains(logInUrl) || url.contains(signUpUrl)) {
                queryString = productTypeValue + delim + productDescValue + delim;
                url = url + Constants.HTML_EXTENTION;
                // Level 1 not logged in go thru
                if (emailHasAccount || accessLevel != 1) {
                    url = url + "?" + target + "=" + myEdcHome + "?" + productIntent + "=" + queryString;
                }
            } else {
                queryString = productTypeValue + delim + productDescValue;
                // Add the redirect to unless the check box was marked as true
                // If logged in must redirect back
                boolean isLoggedIn = ProgressiveProfilingHelper.checkMyEdcSession(request);
                if (!noRedirectBack || isLoggedIn) {
                    queryString += delim + url;
                }
                if (emailHasAccount || accessLevel != 1) {
                    url = (isRegister ? myedcSignUp : myedcLogIn) + "?" + target + "=" + myEdcHome + "?" + productIntent
                            + "=" + queryString;
                }
            }
        }
        return url;
    }

    public String getLoginUrl() {
        String pageLanguage = getLanguage(request);
        String basePath = getBasePath(pageLanguage);
        String logInUrl = getLoginPageUrl(pageLanguage);
        String myedcLogIn = basePath + logInUrl;
        return buildSignInQueryString(request, myedcLogIn, Constants.Properties.SUBSCRIPTION,
                Constants.Properties.ACCOUNT_CREATION, false, true, null, false);
    }

    public String getSignUpUrl() {
        String pageLanguage = getLanguage(request);
        String basePath = getBasePath(pageLanguage);
        String signUpUrl = getSignupPageUrl(pageLanguage);
        String myedcSignUp = basePath + signUpUrl;
        return buildSignInQueryString(request, myedcSignUp, Constants.Properties.SUBSCRIPTION,
                Constants.Properties.ACCOUNT_CREATION, false, true, null, false);
    }

    // Get page language
    private static String getLanguage(SlingHttpServletRequest request, String url) {
        String currentPage = StringUtils.isNotBlank(url) ? url : Request.getPagePath(request);
        String pageLanguage = LanguageUtil.getLanguageAbbreviationFromPath(currentPage,
                Constants.SupportedLanguages.ENGLISH.getLanguageAbbreviation());
        return pageLanguage.toLowerCase();
    }

    // Get page language
    private static String getLanguage(SlingHttpServletRequest request) {
        return getLanguage(request, "");
    }

    // Get base path with correct language based on current page
    private static String getBasePath(String pageLanguage) {
        return pageLanguage.equals("en") ? Constants.Myedc.MYACCOUNT_EN : Constants.Myedc.MYACCOUNT_FR;
    }

    // Get login page name with correct language based on current page
    private static String getLoginPageUrl(String pageLanguage) {
        return pageLanguage.equals("en") ? Constants.Paths.MYEDC_LOGIN_EN : Constants.Paths.MYEDC_LOGIN_FR;
    }

    // Get signup page name with correct language based on current page
    private static String getSignupPageUrl(String pageLanguage) {
        return pageLanguage.equals("en") ? Constants.Paths.MYEDC_SIGNUP_EN : Constants.Paths.MYEDC_SIGNUP_FR;
    }

    // Get home page name with correct language based on current page
    private static String getHomePageUrl(String pageLanguage) {
        return pageLanguage.equals("en") ? Constants.Paths.MYEDC_HOME_PAGE : Constants.Paths.MYEDC_HOME_PAGE_FR;
    }

}