package com.edc.edcweb.core.helpers.progressiveprofiling;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.Cookie;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;
import com.edc.edcweb.core.helpers.Constants;
import com.edc.edcweb.core.helpers.LanguageUtil;
import com.edc.edcweb.core.helpers.LinkResolver;
import com.edc.edcweb.core.helpers.Request;
import com.edc.edcweb.core.helpers.SignInHelper;
import com.edc.edcweb.core.helpers.constants.ConstantsForm;
import com.edc.edcweb.core.serviceImpl.pojo.EloquaContact;

/**
 * @author ACN
 * @version
 * @since
 *
 *
 *        Helper class to perform business layer operations about progressive
 *        profiling.
 *
 *
 */
public class ProgressiveProfilingHelper {
    
    private ProgressiveProfilingHelper () {
        // SonarQube
    }
    
    private static final Logger log = LoggerFactory.getLogger(ProgressiveProfilingHelper.class);
    private static final String PAGE_LEVEL = "pageLevel";
    private static final String PAGE_FIELDS = "pageFields";

    /**
     * Validates the user access, this supports Progressive Profiling, where the
     * paragraphTextTAS is always null, and the Export Help Request form
     * 
     * @param request
     * @param mode
     * @param email
     * @param teaserurl
     * @param premiumurl
     * @param docId
     * @param accessLevel
     * @param paragraphTextTAS
     * @return The ProgressiveProfilingUserAccess object
     */
    public static ProgressiveProfilingUserAccess validateAccess(SlingHttpServletRequest request, String mode,
            String email, String teaserurl, String premiumurl, String docId, int accessLevel, String paragraphTextTAS) {
        ProgressiveProfilingUserAccess access = new ProgressiveProfilingUserAccess(email, docId, premiumurl,
                accessLevel);
        ProgressiveProfilingFormData formData = new ProgressiveProfilingFormData();
        access.setTeaserUrl(teaserurl);
        access.setMode(mode);

        if (mode.equalsIgnoreCase(Constants.Properties.PROGRESSIVEPROFILING_MODE_EXPORT_HELP_REQUEST)
                && paragraphTextTAS != null) {
            access.setParagraphTextTAS(paragraphTextTAS);
        }

        boolean eloquaOK = false;
        String response = access.initializeEloquaContact(request);
        if (response != null && response.equalsIgnoreCase("200")) {
            eloquaOK = true;
        }

        if (!eloquaOK) {
            return null;
        }

        EloquaContact contact = access.getEloquaContact();
        access.setCoreCustomer(contact.getCoreCustomer());
        access.setKnowledgeCustomerStage(contact.getKnowledgeCustomerStage());

        if (mode.equalsIgnoreCase(Constants.Properties.PROGRESSIVEPROFILING_MODE_KNOWLEDGE_COSTUMER)) {
            // knowledge costumer form - Check for Premium Access User
            String pauFl = contact.getPAUFlag();
            String pauDt = contact.getPAUDate();

            if (hasPAUAccess(pauFl, pauDt)) {
                // If it's Premium Access User then give immediate access
                access.setAccessAllowed(true);
                access.setCurrentUserLevel(5);
                access.setUserLevelAfterAccess(5);
            } else {
                // If it's not Premium Access User then ask for the 18 fields
                access.setAccessAllowed(false);
                access.setCurrentUserLevel(1);
                access.setUserLevelAfterAccess(5);
                access.updateRequiredFieldsForLevelOrUnder(formData, 5, true);
            }

        } else {
            // Other forms - Go through regular process
            calculateRequiredFields(access, formData);

            if (access.isUserAccessedDocBefore()) {
                access.setAccessAllowed(true);
                access.setUserLevelAfterAccess(access.getTargetAccessLevel());
            } else {
                if (!eloquaOK) { // NOSONAR Changes on this component are very delicate
                    access.setCurrentUserLevel(1);
                    access.setUserLevelAfterAccess(access.getCurrentUserLevel());
                    access.setAccessAllowed(false);
                    return access;
                }

                // AC 34. if the article is Level1, access is granted no need for required
                // fields.
                if (accessLevel == 1) {
                    access.setAccessAllowed(true);
                    access.setUserLevelAfterAccess(access.getCurrentUserLevel());
                    return access;
                }
            }
        }

        return access;
    }

    /**
     * Check if the user has Premium Access, when PAU flag is true and date is less
     * than a year
     * 
     * @param pauFlag true if the user has Premium Access
     * @param pauDate date of last access
     * @return true if it's a Premium Access User
     */
    private static boolean hasPAUAccess(String pauFlag, String pauDate) {
        boolean hasAccess = false;

        if (pauFlag.equalsIgnoreCase("Y") && pauDate != null && pauDate.trim().length() > 0) {
            Date pauDateObj = new Date(Long.parseLong(pauDate) * 1000);
            Date today = new Date();

            long diffInMillies = Math.abs(today.getTime() - pauDateObj.getTime());
            long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

            if (diff <= 365) {
                hasAccess = true;
            }
        }

        return hasAccess;
    }

    /**
     * Validates the user access for Progressive Profiling where paragraphTextTAS is
     * always null
     * 
     * @param request
     * @param mode
     * @param email
     * @param teaserurl
     * @param premiumurl
     * @param docId
     * @param accessLevel
     * @return The ProgressiveProfilingUserAccess object
     */
    public static ProgressiveProfilingUserAccess validateAccess(SlingHttpServletRequest request, String mode,
            String email, String teaserurl, String premiumurl, String docId, int accessLevel) {
        return validateAccess(request, mode, email, teaserurl, premiumurl, docId, accessLevel, null);
    }

    public static ProgressiveProfilingUserAccess buildFromPostRequestParams(Map<String, Object> map, String email,
            String premiumurl, String teaserUrl, String docId, int accessLevel) {

        ProgressiveProfilingUserAccess access = new ProgressiveProfilingUserAccess(email, docId, premiumurl,
                accessLevel);
        access.setTeaserUrl(teaserUrl);

        // for each parameter we need to add the name and the value
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            String[] strArr = (String[]) entry.getValue();
            access.addSubmittedField(key, strArr);
        }

        return access;

    }

    public static String buildJsonRequiredField(ProgressiveProfilingUserAccess access) {

        String result = "";
        JSONObject mainjson = new JSONObject();

        try {

            // for each required fields
            JSONObject levelinfo = new JSONObject();
            boolean isNewUser = access.getNewUser();
            levelinfo.put("userLevel", isNewUser ? 0 : access.getCurrentUserLevel());
            levelinfo.put("accessLevel", access.getTargetAccessLevel());
            levelinfo.put("docID", access.getDocID());
            levelinfo.put("userLevelAfter", access.getUserLevelAfterAccess());
            levelinfo.put("coreCustomer", access.getCoreCustomer());
            levelinfo.put("knowledgeCustomer", access.getKnowledgeCustomerStage());

            JSONArray profilingLevels = new JSONArray();

            JSONObject l2 = new JSONObject();
            JSONArray pageFieldsL2 = new JSONArray();
            l2.put(PAGE_LEVEL, "2");
            l2.put(PAGE_FIELDS, pageFieldsL2);

            JSONObject l3 = new JSONObject();
            JSONArray pageFieldsL3 = new JSONArray();
            l3.put(PAGE_LEVEL, "3");
            l3.put(PAGE_FIELDS, pageFieldsL3);

            JSONObject l4 = new JSONObject();
            JSONArray pageFieldsL4 = new JSONArray();
            l4.put(PAGE_LEVEL, "4");
            l4.put(PAGE_FIELDS, pageFieldsL4);

            JSONObject l5 = new JSONObject();
            JSONArray pageFieldsL5 = new JSONArray();
            l5.put(PAGE_LEVEL, "5");
            l5.put(PAGE_FIELDS, pageFieldsL5);

            for (Map.Entry<String, Integer> entry : access.getFieldsRequiredFromEloqua().entrySet()) {
                JSONObject el = new JSONObject();

                el.put("name", entry.getKey());
                if (entry.getValue() == 2) {
                    pageFieldsL2.put(entry.getKey());
                } else if (entry.getValue() == 3) {
                    pageFieldsL3.put(entry.getKey());
                } else if (entry.getValue() == 4) {
                    pageFieldsL4.put(entry.getKey());
                } else if (entry.getValue() == 5) {
                    pageFieldsL5.put(entry.getKey());
                }
            }

            if (pageFieldsL2.length() > 0) {
                profilingLevels.put(l2);
            }
            if (pageFieldsL3.length() > 0) {
                profilingLevels.put(l3);
            }
            if (pageFieldsL4.length() > 0) {
                profilingLevels.put(l4);
            }
            if (pageFieldsL5.length() > 0) {
                profilingLevels.put(l5);
            }

            mainjson.put("levelInfo", levelinfo);
            mainjson.put("profilingLevels", profilingLevels);

            result = mainjson.toString(3);

        } catch (JSONException e) {
            log.error("Error building json for required fields {}", e.getMessage());
        }
        log.debug("buildJsonRequiredField {}", result);

        return result;

    }

    public static String updateUserDataToEloqua(SlingHttpServletRequest request, ProgressiveProfilingUserAccess access,
            boolean needUpdatePAUDate) {
        String statusCode = "";

        access.addHiddenSubmittedFields(request);
        statusCode = access.updateEloquaContact(needUpdatePAUDate);

        return statusCode;
    }

    public static void calculateRequiredFields(ProgressiveProfilingUserAccess access,
            ProgressiveProfilingFormData form) {
        // we have a target level,
        // we have a list of the properties at each level,
        // if a field at level 3 is empty, user is at level 2 max.
        access.updateCurrentUserLevel(form);

        // has user provided all 18 fields? if so nothing else.
        if (access.checkAllFieldsValued(form)) {
            access.setAccessAllowed(true);
            access.setUserLevelAfterAccess(5);
            return;
        }

        // has user filled out all required field for this article level
        if (access.checkAllFieldsValuedForLevelOrUnder(form, access.getTargetAccessLevel(), true)) {
            // ask for the next level of user
            int level = access.getCurrentUserLevel() + 1;
            access.updateRequiredFieldsForLevelOrUnder(form, level);
            access.setUserLevelAfterAccess(level);
            access.setAccessAllowed(false);
        } else {
            // ask for this level
            access.updateRequiredFieldsForLevelOrUnder(form, access.getTargetAccessLevel());
            access.setUserLevelAfterAccess(access.getTargetAccessLevel());
            access.setAccessAllowed(false);
        }

    }

    public static String buildJsonAccessAllowed(SlingHttpServletRequest request, String premiumUrl,
            ProgressiveProfilingUserAccess access, String refPremiumURL, boolean addInfoNode, boolean emailHasAccount,
            int accessLevel) {

        String json = "";
        String productTypeValue = "";
        String productDescValue = "";
        JSONObject mainjson = new JSONObject();

        if (premiumUrl != null && !premiumUrl.isEmpty()) {
            premiumUrl = LinkResolver.changeInternalURLtoExternal(request, premiumUrl);
        }

        try {
            if (addInfoNode) {
                // for each required fields
                JSONObject levelinfo = new JSONObject();
                levelinfo.put("userLevel", access.getCurrentUserLevel());
                levelinfo.put("accessLevel", access.getTargetAccessLevel());
                levelinfo.put("docID", access.getDocID());
                levelinfo.put("userLevelAfter", access.getUserLevelAfterAccess());
                levelinfo.put("coreCustomer", access.getCoreCustomer());
                levelinfo.put("knowledgeCustomer", access.getKnowledgeCustomerStage());

                mainjson.put("levelInfo", levelinfo);
            }
            // Check for teaser url
            String teaserUrl = request.getParameter(Constants.Properties.PROGRESSIVEPROFILING_TEASERURL);
            if (!StringUtils.contains(teaserUrl, Constants.Paths.CAMPAIGN)) {
                // if is not a campaign, set to null to use referrer url.
                teaserUrl = null;
            }
            // get current page from request helper if teaserUrl is null
            Resource currentPage = Request.getCurrentPageResource(request, teaserUrl);
            String[] prodTypeDesc = getProductTypeAndDesc(currentPage, access.getDocID());
            productTypeValue = prodTypeDesc[0];
            productDescValue = prodTypeDesc[1];

            // Populate json
            if (access.getMode().equalsIgnoreCase(Constants.Properties.PROGRESSIVEPROFILING_MODE_MEA)) {
                mainjson.put(Constants.Properties.PROGRESSIVEPROFILING_MODE_MEAUNLOCKED, Constants.YES);
            } else if (refPremiumURL != null && !refPremiumURL.isEmpty() && !refPremiumURL.equalsIgnoreCase("null")) {

                if (!emailHasAccount) {
                    mainjson.put("URL", refPremiumURL);
                } else {
                    // Build query string
                    refPremiumURL = SignInHelper.buildSignInQueryString(request, refPremiumURL, productTypeValue,
                            productDescValue, false, emailHasAccount, accessLevel, false);
                    mainjson.put("URL", refPremiumURL);
                }

            } else {

                if (!emailHasAccount) {
                    mainjson.put("URL", premiumUrl);
                } else {
                    // Build query string
                    premiumUrl = SignInHelper.buildSignInQueryString(request, premiumUrl, productTypeValue,
                            productDescValue, false, emailHasAccount, accessLevel, false);
                    mainjson.put("URL", premiumUrl);
                }

            }
            json = mainjson.toString(3);

        } catch (JSONException e) {
            log.error("Cannot create json", e);
        }
        log.debug("buildJsonAccessAllowed {}", json);
        return json;
    }

    /**
     * Helper function to validate length of the field
     * 
     * @param value the value to test
     * @param max   maximum field length
     * @param min   minimum field length
     * @return true if the value's length is between max and min
     */
    private static boolean validateFieldLength(String value, int max, int min) {
        return value.trim().length() >= min && value.trim().length() <= max;
    }

    /**
     * Helper function to validate the maximum length of the field
     * 
     * @param value the value to test
     * @param max   maximum field length
     * @return true if the value's length less than the maximum
     */
    private static boolean validateFieldLength(String value, int max) {
        return validateFieldLength(value, max, 0);
    }

    /**
     * Form fields validation
     * 
     * @param key       Key name to know the kind of field to validate
     * @param values    Values to be validated
     * @param urlCheck  true if it should test for URL strings
     * @param maxLength test for maximum length
     * @return true if the field has valid value(s)
     */
    public static boolean isValidFormField(String key, String[] values, boolean urlCheck, int maxLength) {
        String regexName = "^([a-zA-Z\u00C0-\u017F\\s\\-,.'´]?)+$";
        boolean isValid;

        if (values != null && values.length > 0) {
            for (String aValue : values) {
                switch (key) {
                case ConstantsForm.FormProperties.FORM_PROGRESSIVEPROFILEING_FNAME:
                case ConstantsForm.FormProperties.FORM_PROGRESSIVEPROFILEING_LNAME:
                    isValid = aValue.matches(regexName) && validateFieldLength(aValue, 100, 2);
                    break;

                case ConstantsForm.FormProperties.FORM_PROGRESSIVEPROFILEING_COMPANYADDR1:
                case ConstantsForm.FormProperties.FORM_PROGRESSIVEPROFILEING_COMPANYADDR2:
                    isValid = validateFieldLength(aValue, 200);
                    break;

                case ConstantsForm.FormProperties.FORM_PROGRESSIVEPROFILEING_COMPANYNAME:
                    isValid = validateFieldLength(aValue, 120);
                    break;

                case ConstantsForm.FormProperties.FORM_PROGRESSIVEPROFILEING_TITLE:
                    isValid = aValue.matches(regexName) && validateFieldLength(aValue, 120);
                    break;

                case ConstantsForm.FormProperties.FORM_PROGRESSIVEPROFILEING_COMPANYCITY:
                    isValid = aValue.matches(regexName) && validateFieldLength(aValue, 100);
                    break;

                case ConstantsForm.FormProperties.FORM_PROGRESSIVEPROFILEING_COMPANYPROVINCE:
                case ConstantsForm.FormProperties.FORM_PROGRESSIVEPROFILEING_COMPANYPOSTAL:
                    isValid = validateFieldLength(aValue, 50);
                    break;

                default:
                    isValid = maxLength <= 0 || validateFieldLength(aValue, maxLength);
                    break;
                }

                if (!isValid) {
                    return false;
                }

                if (urlCheck) {
                    String regexURL = "([a-zA-Z]{2,}:\\/{2})?(www\\.|WWW\\.)?[a-zA-z0-9]{2,}(\\.|\\/)([a-zA-Z]{2,3}\\b)";
                    String regexIP = "(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";

                    Pattern url = Pattern.compile(regexURL);
                    Pattern ip = Pattern.compile(regexIP);

                    Matcher matcherURL;
                    Matcher matcherIP;

                    matcherURL = url.matcher(aValue);
                    matcherIP = ip.matcher(aValue);

                    if (matcherURL.matches() || matcherIP.matches()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Check Shibboleth's Session id vs Shibboleth's Cookie, to make sure we have a
     * valid session
     * 
     * @param request SlingHttpServletRequest to get the values from
     * @return true if valid, false otherwise
     */
    public static Boolean checkMyEdcSession(SlingHttpServletRequest request) {
        Boolean sessionOk = false;
        String shibCookieVal = null;
        String shinbSessionID = (String) request.getSession().getAttribute(Constants.Properties.SHIBBOLETH_SESSION_VAR);
        Cookie shibCookie = request.getCookie(Constants.Properties.SHIBBOLETH_COOKIE_NAME);
        // Check if cookie exists
        if (shibCookie != null) {
            shibCookieVal = shibCookie.getValue();
        }
        // Compare values if there are any and return result
        if (StringUtils.isNotBlank(shinbSessionID) && StringUtils.isNotBlank(shibCookieVal)) {
            sessionOk = shinbSessionID.equals(shibCookieVal);
        }
        // Removed as the JsonObject was removed on Task 349032 
        return sessionOk;
    }

    /**
     * getProductTypeAndDesc Gets the productType and productDescrtiption for the
     * login / sign in url
     * 
     * @param res   CurrentPage Resource
     * @param docId pages docId if any
     * @return String[]{productType, productDescrtiption};
     */
    public static String[] getProductTypeAndDesc(Resource res, String docId) {
        String productTypeValue = Constants.Properties.SUBSCRIPTION;
        String productDescValue = Constants.Properties.PREMIUM_CONTENT;
        Page currentPage = null;
        String resPath = "";
        String pagePath = "";
        // Get product description based on path
        String pageLanguage = Constants.Paths.FORWARD_SLASH;
        pageLanguage = pageLanguage
                .concat(LanguageUtil.getLanguageAbbreviationFromPath(pagePath,
                        Constants.SupportedLanguages.ENGLISH.getLanguageAbbreviation()).toLowerCase())
                .concat(Constants.Paths.FORWARD_SLASH);
        // Validate if resource is null
        if (res != null) {
            resPath = res.getPath();
            currentPage = res.adaptTo(Page.class);
            resPath = resPath.replace(Constants.Paths.CONTENT_EDC, "").replace(pageLanguage, "");
            productDescValue = resPath.replace(Constants.Paths.FORWARD_SLASH, Constants.UNDERSCORE);
        }
        if (currentPage != null) {
            pagePath = currentPage.getPath();
        }
        // Get product type based on path for knowledge product
        if (pagePath != null
                && (pagePath.contains(Constants.Paths.TOOL) || pagePath.contains(Constants.Paths.TOOL_ALIAS))) {
            productTypeValue = Constants.Properties.KNOWLEDGE_PRODUCT;
            // Get product type based on docID for account creation
        } else if (docId != null && docId.equalsIgnoreCase("creacc")) {
            productDescValue = Constants.Properties.ACCOUNT_CREATION;
        } else {
            // Ano other product type will be "premium content"
            productTypeValue = Constants.Properties.PREMIUM_CONTENT;
        }
        return new String[] { productTypeValue, productDescValue };
    }

}