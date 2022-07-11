package com.edc.edcportal.core.servlets.dashboard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.jcr.query.Query;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;
import com.edc.edcportal.core.arkadin.ArkadinConstants;
import com.edc.edcportal.core.arkadin.pojo.ArkadinUserActivityPO;
import com.edc.edcportal.core.arkadin.pojo.Show;
import com.edc.edcportal.core.arkadin.services.ArkadinDAOService;
import com.edc.edcportal.core.helpers.Constants;
import com.edc.edcportal.core.helpers.WebinarsHelper;
import com.edc.edcportal.core.helpers.constants.ConstantsWebinars;
import com.edc.edcportal.core.servlets.dashboard.helpers.DashboardHelper;
import com.edc.edcportal.core.servlets.dashboard.pojo.DashboardPageListDO;
import com.google.common.collect.ImmutableList;

public class MyWebinarsTab {

    private MyWebinarsTab() {
        // Sonar lint
    }

    protected static final Logger log = LoggerFactory.getLogger(MyWebinarsTab.class);

    public static DashboardPageListDO getMyWebinars(String externalId, SlingHttpServletRequest request,
            ArkadinDAOService arkadinDAOService) {
        DashboardPageListDO pagesList = new DashboardPageListDO();
        // get the transactions from API and sort them by Webinar Status and Date
        // according the status
        Map<String, String> webinarPaths = getWebinarPaths(externalId, request, arkadinDAOService);
        // reset lists to start fresh
        if (webinarPaths.size() > 0) {
            if (webinarPaths.containsKey(Constants.Properties.ERROR_MESSAGE)) {
                pagesList.setErrorMessage(webinarPaths.get(Constants.Properties.ERROR_MESSAGE));
            } else {
                // Return the objects. Language parameter is null as we should
                // present webinars on any language
                pagesList = DashboardHelper.populatePageList(request, webinarPaths, null);
            }
        }
        return pagesList;
    }

    /**
     * get the Webinar show id from the API, get the ones the user is registered to,
     * look for the pages using the webinar ids, Sorted by webinar date ascending
     * (now to future) sort by webinar status sort the OnDemand on reverse order
     * (closest to older)
     *
     * @param externalId
     * @param request
     * @param arkadinDAOService
     * @return sorted paths or empty if user not registered to any webinars
     */
    private static Map<String, String> getWebinarPaths(String externalId, SlingHttpServletRequest request,
            ArkadinDAOService arkadinDAOService) {
        Map<String, String> webinarPaths = new LinkedHashMap<>();
        ArkadinUserActivityPO arkadinUserActivityPO = arkadinDAOService.getUserActivity(externalId);
        if (ArkadinConstants.RESULT_OK.equalsIgnoreCase(arkadinUserActivityPO.getDiag())) {
            List<String> registeredShows = new ArrayList<>();
            for (Show thisShow : arkadinUserActivityPO.getShows()) {
                if (StringUtils.isNotBlank(thisShow.getRegDate())) {
                    registeredShows.add(thisShow.getShowKey());
                }
            }
            if (!registeredShows.isEmpty()) {
                ResourceResolver resourceResolver = request.getResourceResolver();
                String query = "SELECT parent.* \r\n"
                        + "FROM [cq:Page] AS parent INNER JOIN [nt:base] AS child ON ISCHILDNODE(child,parent) \r\n"
                        + "WHERE (ISDESCENDANTNODE(parent, '" + ConstantsWebinars.QUERY_WEBINAR_PATH + "') \r\n"
                        + "OR ISDESCENDANTNODE(parent, '" + ConstantsWebinars.QUERY_WEBINAR_PATH + "')) \r\n"
                        + "AND  child.[webinarshowkey] IN(" + String.join(",", registeredShows) + ") \r\n"
                        + "ORDER By child.[startDate]";
                // Execute the query
                Iterator<Resource> pageResources = resourceResolver.findResources(String.format(query, "en", "fr"),
                        Query.JCR_SQL2);
                // Get the pages grouped and sorted by webinar status
                Map<String, List<String>> webinarGroups = getWebinarGroups(pageResources);
                // Get the webinar paths sorted by date according the status
                webinarPaths = getPathsSortedByDate(webinarGroups);
            }
        }
        // Set only if bad error happens
        if(Constants.Properties.ERROR_MESSAGE.equals(arkadinUserActivityPO.getDiag())) {
            webinarPaths.put(Constants.Properties.ERROR_MESSAGE, "webinar-page-load-error");
        }
        
        return webinarPaths;
    }

    private static Map<String, List<String>> getWebinarGroups(Iterator<Resource> pageResources) {
        Map<String, List<String>> webinarGroups = new LinkedHashMap<>();
        while (pageResources.hasNext()) {
            Page thisPage = pageResources.next().adaptTo(Page.class);
            if (thisPage != null) {
                String webinarStatus = WebinarsHelper.getWebinarStatus(thisPage, 0);
                List<String> webinarPaths = webinarGroups.getOrDefault(webinarStatus, new LinkedList<>());
                webinarPaths.add(thisPage.getPath());
                webinarGroups.put(webinarStatus, webinarPaths);
            }
        }
        return sortByWebinarStatus(webinarGroups);
    }

    private static Map<String, List<String>> sortByWebinarStatus(Map<String, List<String>> webinarGroups) {
        Map<String, List<String>> sortedMap = new LinkedHashMap<>();
        // Create a list of the status using the order we need
        List<String> statusOrder = ImmutableList.of(ConstantsWebinars.WEBINAR_STATE_LIVE,
                ConstantsWebinars.WEBINAR_STATE_UPCOMING, ConstantsWebinars.WEBINAR_STATE_ONDEMAND);
        // Create a comparator using the status, will use the index of the list
        Comparator<String> statusComparator = Comparator.comparing(c -> statusOrder.indexOf(c));
        // Sort the Map (the pages should remain order by date)
        webinarGroups.entrySet().stream().sorted(Map.Entry.comparingByKey(statusComparator))
                .forEachOrdered(x -> sortedMap.put(x.getKey(), x.getValue()));
        return sortedMap;
    }

    public static Map<String, String> getPathsSortedByDate(Map<String, List<String>> webinarGroups) {
        Map<String, String> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<String, List<String>> item : webinarGroups.entrySet()) {
            String webinarStatus = item.getKey();
            List<String> theList = item.getValue();
            if (ConstantsWebinars.WEBINAR_STATE_ONDEMAND.equals(webinarStatus)) {
                // all pages are sorted ascending, ondemand does not, reverse it
                Collections.reverse(theList);
            }
            // Create our final path map, already sorted by webinar status and the
            // "ondemand" paths are sorted as well
            for (String pagePath : theList) {
                sortedMap.put(pagePath, webinarStatus);
            }
        }
        return sortedMap;
    }
}
