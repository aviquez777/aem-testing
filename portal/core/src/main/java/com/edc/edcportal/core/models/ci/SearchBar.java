package com.edc.edcportal.core.models.ci;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.WCMMode;
import com.edc.edcportal.core.ci.CiConstants;
import com.edc.edcportal.core.ci.pojo.CiSharedObject;
import com.edc.edcportal.core.ci.services.CiConfigService;
import com.edc.edcportal.core.helpers.Constants;

@Model(adaptables = SlingHttpServletRequest.class)

public class SearchBar {
    private static final Logger log = LoggerFactory.getLogger(SearchBar.class);

    @Self
    private SlingHttpServletRequest request;

    @Inject
    private Page currentPage;

    @Inject
    private CiConfigService ciConfigService;

    private String name;
    private String country;

    @PostConstruct
    public void initModel() {
        // Task 22143 squid:S2221 Will return null no error
        CiSharedObject ciSharedObject = (CiSharedObject) request.getSession().getAttribute(CiConstants.SEARCH_SESSION_VAR);
        if (ciSharedObject != null) {
            name = ciSharedObject.getName();
            country = ciSharedObject.getCountry();
        } else {
            log.debug("SearchForm ciSharedObject not found on memory {}", ciSharedObject);
        }
    }

    public String getServletUrl() {
        return CiConstants.CI_COMPANY_SEARCH_SERVICE_SERVLET_URL;
    }

    /** Bug 137751 
     * getCountryJsonUrl Calculate the json servlet url based on the pre-defined
     * Landing Search PageNode It cannot contain "/content/edc" because of
     * dispatcher rules, nor "/premium" so it'll be cached
     * 
     * @return json servlet url
     */
    public String getCountryJsonUrl() {
        StringBuilder premiumPath = new StringBuilder();
        WCMMode mode = WCMMode.fromRequest(request);
        if (!mode.equals(WCMMode.DISABLED)) {
            // add the /content/edc for author only
            premiumPath.append(Constants.Paths.CONTENT_EDC);
        }
        // create the path: "/<language>/<LandingSearchPageNode>/_jcr_content.countrylist.json"
        premiumPath.append(Constants.FORWARD_SLASH)
                .append(currentPage.getLanguage().getLanguage())
                .append(ciConfigService.getLandingSearchPageNode())
                .append(CiConstants.CI_COUNTRY_LIST_JSON_SERVLET_URL);
        // get the resulting string and Lower Case it
        String teaserPath = premiumPath.toString().toLowerCase();
        // remove the "/premium" path so it will be cached
        return StringUtils.replace(teaserPath, CiConstants.PREMIUM, "");
    }

    public String getName() {
        return name;
    }

    public String getCountry() {
        return country;
    }
}
