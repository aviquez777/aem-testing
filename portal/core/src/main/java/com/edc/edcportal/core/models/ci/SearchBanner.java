package com.edc.edcportal.core.models.ci;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edc.edcportal.core.ci.CiConstants;
import com.edc.edcportal.core.ci.pojo.CiCompanySearchDO;
import com.edc.edcportal.core.ci.pojo.CiSharedObject;
import com.edc.edcportal.core.ci.services.CiConfigService;

@Model(adaptables = SlingHttpServletRequest.class)

public class SearchBanner {
    private static final Logger log = LoggerFactory.getLogger(SearchBanner.class);

    @Self
    private SlingHttpServletRequest request;

    @SlingObject
    private SlingHttpServletResponse response;

    @Inject
    private CiConfigService ciConfigService;

    @Inject
    private ValueMap properties;

    private CiCompanySearchDO ciCompanySearchDO = new CiCompanySearchDO();

    @PostConstruct
    public void initModel() {
        String bannerType = properties.get(CiConstants.BANNER_TYPE_POPERTY_FIELD, String.class);
        // Remove the results object if loading on Default page
        if (CiConstants.BANNER_TYPE_DEFAULT.equals(bannerType)) {
            request.getSession().removeAttribute(CiConstants.SEARCH_SESSION_VAR);
        } else {
            CiSharedObject ciSharedObject = (CiSharedObject) request.getSession().getAttribute(CiConstants.SEARCH_SESSION_VAR);
            if (ciSharedObject != null) {
                // Task 22143 squid:S2221 Will return null no error
                ciCompanySearchDO = ciSharedObject.getCiCompanySearchDO();
                if (!CiConstants.APIErrors.RESULTS_FOUND.getError().equals(ciCompanySearchDO.getResponseStatus())) {
                    // Remove the search terms but keep the ciCompanySearchDO as the response status
                    // is there
                    ciSharedObject = new CiSharedObject();
                    ciSharedObject.setCiCompanySearchDO(ciCompanySearchDO);
                    request.getSession().setAttribute(CiConstants.SEARCH_SESSION_VAR, ciSharedObject);
                }
            } else {
                log.debug("SearchResults ciSharedObject not found on sesssion {}", ciSharedObject); 
            }
        }
    }

    public CiCompanySearchDO getCiCompanySearchDO() {
        return ciCompanySearchDO;
    }

    public String getSearchPageNode() {
        return ciConfigService.getSearchResultsPageNode();
    }

}
