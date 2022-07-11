package com.edc.edcportal.core.models.ci;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.WCMMode;
import com.edc.edcportal.core.ci.CiConstants;
import com.edc.edcportal.core.ci.pojo.CiCompanySearchDO;
import com.edc.edcportal.core.ci.pojo.CiSharedObject;
import com.edc.edcportal.core.ci.pojo.items.CiCompanySearchItem;
import com.edc.edcportal.core.ci.pojo.items.CiCompanySearchItemResult;
import com.edc.edcportal.core.ci.services.CiConfigService;
import com.edc.edcportal.core.ci.services.CiDAOService;
import com.edc.edcportal.core.exception.EDCAPIMSystemException;
import com.edc.edcportal.core.helpers.Constants;
import com.edc.edcportal.core.helpers.LanguageUtil;
import com.edc.edcportal.core.helpers.LinkResolver;
import com.edc.edcportal.core.helpers.RedirectHelper;

@Model(adaptables = SlingHttpServletRequest.class)

public class SearchResults {

    private static final Logger log = LoggerFactory.getLogger(SearchResults.class);

    @Self
    private SlingHttpServletRequest request;

    @SlingObject
    private SlingHttpServletResponse response;

    @Inject
    private CiConfigService ciConfigService;

    @Inject
    private CiDAOService ciDAOService;

    private CiCompanySearchDO ciCompanySearchDO = new CiCompanySearchDO();

    @PostConstruct
    public void initModel() throws IOException {
        // Task 22143 squid:S2221 Will return null no error
        CiSharedObject ciSharedObject = (CiSharedObject) request.getSession().getAttribute(CiConstants.SEARCH_SESSION_VAR);
        if (ciSharedObject != null) {
            String currentLanguage = LanguageUtil.getLanguageAbbreviationFromPath(request.getPathInfo(),
                    Constants.SupportedLanguages.ENGLISH.getLanguageAbbreviation());
            // use currentLanguage as default in case of timeout
            String queryLanguage = StringUtils.isNotBlank(ciSharedObject.getLanguage()) ? ciSharedObject.getLanguage() : currentLanguage;
            // Check language switch,
            if (queryLanguage.equals(currentLanguage)) {
                ciCompanySearchDO = ciSharedObject.getCiCompanySearchDO();
            } else {
                // we are not on the same language, set it and re-do the search
                ciSharedObject.setLanguage(currentLanguage);
                try {
                    ciCompanySearchDO = ciDAOService.searchCompanyByName(ciSharedObject.getName(),
                            ciSharedObject.getCountry(), currentLanguage);
                } catch (EDCAPIMSystemException | IOException e) {
                    log.debug("SearchResults ciSharedObject not found on sesssion {}", e.getMessage());
                }
            }
        } else {
            // Do not redirect if we are on author
            WCMMode mode = WCMMode.fromRequest(request);
            if (mode.equals(WCMMode.DISABLED)) {
                String language = LanguageUtil.getLanguageAbbreviationFromPath(request.getPathInfo(),
                        Constants.SupportedLanguages.ENGLISH.getLanguageAbbreviation());
                String redirectTo = Constants.Paths.CONTENT_EDC.concat(Constants.FORWARD_SLASH).concat(language)
                        .concat(ciConfigService.getLandingSearchPageNode());
                redirectTo = LinkResolver.changeInternalURLtoExternal(request,
                        LinkResolver.addHtmlExtension(redirectTo, Constants.Paths.CONTENT_EDC));
                RedirectHelper.redirectTo(response, redirectTo);
            } else {
                // Populate dummy data for author
                populateForAuthor();
            }
        }
    }

    /**
     * Add Dummy data to display just when authoring the page
     */
    private void populateForAuthor() {
        List<CiCompanySearchItem> results = new LinkedList<>();
        CiCompanySearchItem searchItem = new CiCompanySearchItem();
        List<CiCompanySearchItemResult> result = new LinkedList<>();
        CiCompanySearchItemResult resultItem = new CiCompanySearchItemResult();
        // Sample Company ID to Accenture LLP
        resultItem.setCompanyId("ylYwP");
        resultItem.setCompanyName("companyName".concat(CiConstants.SAMPLE_TEXT));
        resultItem.setCountryName("countryName".concat(CiConstants.SAMPLE_TEXT));
        resultItem.setProvince("province".concat(CiConstants.SAMPLE_TEXT));
        resultItem.setCity("city".concat(CiConstants.SAMPLE_TEXT));
        resultItem.setStreetAddress("streetAddress".concat(CiConstants.SAMPLE_TEXT));
        resultItem.setPostalCode("postalCode".concat(CiConstants.SAMPLE_TEXT));
        resultItem.setIndustry("industry".concat(CiConstants.SAMPLE_TEXT));
        result.add(resultItem);
        searchItem.setResult(result);
        results.add(searchItem);
        ciCompanySearchDO.setResponseStatus(CiConstants.APIErrors.RESULTS_FOUND.getError());
        ciCompanySearchDO.setResults(results);
    }

    /**
     * Get the ciCompanySearchDO object,
     * 
     * @return ciCompanySearchDO from Session, empty otherwise
     */
    public CiCompanySearchDO getCiCompanySearchDO() {
        return ciCompanySearchDO;
    }

    /**
     * get all the necessary data on one shot
     * 
     * @return Map<String, String> with data
     */
    public Map<String, String> getData() {
        Map<String, String> data = new HashMap<>();
        data.put("servletUrl", CiConstants.CI_COUNTRY_LIST_JSON_SERVLET_URL);
        data.put("companyProfilePageNode", ciConfigService.getCompanyProfilePageNode());
        data.put("errorResultsFound", CiConstants.APIErrors.RESULTS_FOUND.getError());
        return data;
    }

    /**
     * Must be integer to allow comparison with itemList.count
     * 
     * @return getPageCount from config
     */

    public int getPageCount() {
        return ciConfigService.getPageCount();
    }
}
