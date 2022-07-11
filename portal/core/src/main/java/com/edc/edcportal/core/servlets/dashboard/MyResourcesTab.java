package com.edc.edcportal.core.servlets.dashboard;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;
import com.edc.edcportal.core.eloqua.services.EloquaDAOService;
import com.edc.edcportal.core.exception.EDCEloquaSystemException;
import com.edc.edcportal.core.helpers.Constants;
import com.edc.edcportal.core.helpers.DateUtilsHelper;
import com.edc.edcportal.core.helpers.LanguageUtil;
import com.edc.edcportal.core.helpers.Request;
import com.edc.edcportal.core.servlets.dashboard.helpers.DashboardConstants;
import com.edc.edcportal.core.servlets.dashboard.helpers.DashboardHelper;
import com.edc.edcportal.core.servlets.dashboard.pojo.DashboardPageListDO;

public class MyResourcesTab {

    private MyResourcesTab() {
        // Sonar Lint
    }

    protected static final Logger log = LoggerFactory.getLogger(MyResourcesTab.class);

    public static DashboardPageListDO getMyResoures(String externalId, SlingHttpServletRequest request,
            EloquaDAOService eloquaDAOService) {
        String pageLanguage = Constants.SupportedLanguages.ENGLISH.getLanguageAbbreviation().toLowerCase();
        Resource currentPageRes = Request.getCurrentPageResource(request, null);
        if (currentPageRes != null) {
            Page currentPage = currentPageRes.adaptTo(Page.class);
            // Display language
            if (currentPage != null) {
                String pagePath = currentPage.getPath();
                pageLanguage = LanguageUtil.getLanguageAbbreviationFromPath(pagePath, pageLanguage);
            }
        }
        DashboardPageListDO pagesList = new DashboardPageListDO();
        // get the transactions from eloqua
        Map<String, String> rawPagePaths = null;
        try {
            rawPagePaths = eloquaDAOService.getTransactionHistoryByExternalId(externalId);
        } catch (EDCEloquaSystemException e) {
            pagesList.setErrorMessage("webinar-page-load-error");
            log.error("Dashboard Model {}", e.toString());
        }
        // reset lists to start fresh
        if (rawPagePaths != null && rawPagePaths.size() > 0) {
            // Remove the expired and sub pages
            Map<String, String> pagePaths = preProcessPagePaths(rawPagePaths);
            // Create the page list
            pagesList = DashboardHelper.populatePageList(request, pagePaths, pageLanguage);
        }
        return pagesList;
    }

    /**
     * preProcessPagePaths remove sub pages and out dated pages Divide the lists
     * into pagesList and webinarPaths
     * 
     * @param pagePaths
     * @return Map<String, String> without the pages
     */
    private static Map<String, String> preProcessPagePaths(Map<String, String> rawPagePaths) {
        Map<String, String> pagePathsTemp = new LinkedHashMap<>();
        // get next year
        Date nextYear = DateUtilsHelper.getNextYear(Calendar.getInstance());
        for (Map.Entry<String, String> item : rawPagePaths.entrySet()) {
            String pagePath = item.getKey();
            // We do not wants
            if (!pagePath.contains(DashboardConstants.WEBINAR_NODE)) {
                pagePath = pagePath.replaceAll(Constants.Paths.CONTENT_EDC+"/(en|fr)", "");
                // check the node length
                String[] pathArr = pagePath.split(Constants.FORWARD_SLASH);
                Integer nodeDepth = getNodeDepth(pagePath);
                // if the node depth is the allowe done, process the path
                if (pathArr.length == nodeDepth) {
                    // Get record date
                    String createdDateStr = item.getValue();
                    Date createdAt = DateUtilsHelper.unixTimestampToDate(createdDateStr, false);
                    // Content must be shown up to a year of what they've accessed
                    if (createdAt != null && createdAt.before(nextYear)) {
                        // We dont'need the date anymore, replace it with nodeDepth
                        pagePathsTemp.put(pagePath, nodeDepth.toString());
                    }
                }
            }
        }
        // not needed anymore
        rawPagePaths.clear();
        return pagePathsTemp;
    }

    /**
     * getNodeDepth Calculate node depth based on path to show accordingly
     * 
     * @param pagePath
     * @return getNodeDepth
     */
    private static int getNodeDepth(String pagePath) {
        int nodeDepth = 3;
        if (pagePath.contains(DashboardConstants.TOOL_NODE) || pagePath.contains(DashboardConstants.GUIDE_NODE)) {
            nodeDepth = 4;
        } else if (pagePath.contains(DashboardConstants.EBOOK_NODE)) {
            nodeDepth = 5;
        }
        return nodeDepth;
    }
}
