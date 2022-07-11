package com.edc.edcweb.core.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ArrayUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.edc.edcweb.core.helpers.Constants;
import com.edc.edcweb.core.helpers.ResourceResolverHelper;
import com.edc.edcweb.core.helpers.SearchForPages;
import com.edc.edcweb.core.helpers.TagResult;
import com.edc.edcweb.core.helpers.Tags;
import com.edc.edcweb.core.helpers.constants.ConstantsWebinars;
import com.edc.edcweb.core.helpers.webinars.WebinarsHelper;
import com.edc.edcweb.core.servlets.json.JsonFormatter;
import com.edc.edcweb.core.servlets.json.JsonFromCollections;
import com.edc.edcweb.core.servlets.json.JsonFromPages;

/**
 * @author Peter Crummey
 * @version 0.0.4-SNAPSHOT
 * @since 0.0.4-SNAPSHOT
 *
 *
 *        Servlet to create KLP and Tag Result pages for the Card Grid component
 *        used by the EDC web site.
 *
 *
 */
@Component(immediate = true, service = Servlet.class, property = { "sling.servlet.extensions=json",
        "sling.servlet.methods=GET", "sling.servlet.selectors=articlesbytagid",
        "sling.servlet.resourceTypes=edc/components/structure/page" }, configurationPid = "com.edc.edcweb.core.servlets.CardGrid")
@Designate(ocd = CardGrid.Configuration.class)
public class CardGrid extends SlingSafeMethodsServlet {
    private static final long serialVersionUID = -1522154221945085097L;
    private static final Logger log = LoggerFactory.getLogger(CardGrid.class);
    private String[] cardGridPaths;
    private String[] tagIds = null;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        Page currentPage = null;
        Resource componentRsrc = null;
        for (String cardGridPath : this.cardGridPaths) {
            componentRsrc = request.getResource().getResourceResolver()
                    .resolve(request.getResource().getPath() + cardGridPath);
            if (!componentRsrc.getResourceType().equals(Resource.RESOURCE_TYPE_NON_EXISTING)) {
                break;
            }
        }

        // If not found on the cardGridPaths, look for it recursively
        if (componentRsrc == null || !componentRsrc.isResourceType(Constants.Paths.CARD_GRID_RESOURCE_TYPE)) {
            // Get the page from the resource
            Resource resource = request.getResource();
            PageManager pageManager = resource.getResourceResolver().adaptTo(PageManager.class);
            currentPage = pageManager.getContainingPage(resource);
            // Look for the first card grid on the page
            componentRsrc = ResourceResolverHelper.getResourceByType(currentPage,
                    Constants.Paths.CARD_GRID_RESOURCE_TYPE);

            // If not found CardGrid looking for TradeInsights search component
            if (componentRsrc == null || !componentRsrc.isResourceType(Constants.Paths.CARD_GRID_RESOURCE_TYPE)) {
                componentRsrc = ResourceResolverHelper.getResourceByType(currentPage,
                        Constants.Paths.TRADEINSIGHTS_SEARCH_RESOURCE_TYPE);
            }

            // If not found CardGrid or TradeInsights looking for Insights search component
            if (componentRsrc == null || (!componentRsrc.isResourceType(Constants.Paths.CARD_GRID_RESOURCE_TYPE)
            && !componentRsrc.isResourceType(Constants.Paths.TRADEINSIGHTS_SEARCH_RESOURCE_TYPE))) {
                componentRsrc = ResourceResolverHelper.getResourceByType(currentPage,
                        Constants.Paths.INSIGHTS_SEARCH_RESOURCE_TYPE);
            }
        }

        // Still not found, throw error
        if (componentRsrc == null || (!componentRsrc.isResourceType(Constants.Paths.CARD_GRID_RESOURCE_TYPE)
                && !componentRsrc.isResourceType(Constants.Paths.TRADEINSIGHTS_SEARCH_RESOURCE_TYPE)
                && !componentRsrc.isResourceType(Constants.Paths.INSIGHTS_SEARCH_RESOURCE_TYPE))) {
            log.error("Error:doGet - componentRsrc == null");
            log.info("Card Grid or Trade Insight Search - Cannot access resource. ");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("ERROR");
            return;
        }
        // ---------------------------------------------------------------------
        ValueMap properties = componentRsrc.getValueMap();
        // SonarQube fix
        List<Page> pages = getPages(componentRsrc, properties, request);
        // SonarQube fix
        List<Page> filteredPages = getFilteredPages(pages, componentRsrc, currentPage, properties);
        // ---------------------------------------------------------------------
        // Have the sorted list. Write it out to JSON...
        // ---------------------------------------------------------------------
        PrintWriter out = response.getWriter();
        // ---------------------------------------------------------------------
        response.setContentType(Constants.Properties.APPLICATION_SLASH_JSON);
        response.setCharacterEncoding(Constants.UTF_8);
        String json = "";
        // SonarQube fix
        try {
            json = populateJson(request, properties, filteredPages, tagIds);
        } catch (ServletException | JSONException e) {
            log.error("CardGrid populateJson error {}", e.getMessage());
        }
        out.append(json);
        out.flush();
        // ---------------------------------------------------------------------
    }

    // SonarQube fix
    private List<Page> getPages(Resource componentRsrc, ValueMap properties, SlingHttpServletRequest request) {
        List<Page> pages;
        // Set default values if found tradeinsights component, or use default behavior for InsightsSearch -----------------
        boolean showAllCards = Boolean.parseBoolean(properties.get(Constants.Properties.SHOW_ALL_CARDS, String.class));
        if (componentRsrc.isResourceType(Constants.Paths.TRADEINSIGHTS_SEARCH_RESOURCE_TYPE) || (componentRsrc.isResourceType(Constants.Paths.INSIGHTS_SEARCH_RESOURCE_TYPE) && !showAllCards)) {
            // ---------------------------------------------------------------------
            // We only need pages tagged with access-type
            // ---------------------------------------------------------------------
            tagIds = new String[] { Constants.TAGS_EDC_ACCESSTYPE };
            pages = SearchForPages.getPagesByTagIds(request, tagIds, true,
                    properties.get(Constants.Properties.ARTICLES_PATH, String.class));
        } else if (componentRsrc.isResourceType(Constants.Paths.INSIGHTS_SEARCH_RESOURCE_TYPE) && showAllCards) {
            // ---------------------------------------------------------------------
            // Get the tag Ids that the author has selected.
            // ---------------------------------------------------------------------
            tagIds = properties.get(Constants.Properties.TAG_IDS, String[].class);
            pages = SearchForPages.getPagesByTagIds(request, tagIds, true,
                    properties.get(Constants.Properties.ARTICLES_PATH, String.class));
        } else {
            // ---------------------------------------------------------------------
            // Get the tag Ids that the author has selected. If there is only one
            // Id and it is the EDC tag namespace, we want to include the tag
            // array and the master tags list in the JSON.
            // ---------------------------------------------------------------------
            tagIds = properties.get(Constants.Properties.TAG_IDS, String[].class);
            pages = TagResult.findMatchingPages(request, properties);
        }
        return pages;
    }

    // SonarQube fix
    private List<Page> getFilteredPages(List<Page> pages, Resource componentRsrc, Page currentPage, ValueMap properties) {
        List<Page> filteredPages = new ArrayList<>();
        boolean isEventPage = false;
        // This will solve the duplicated issue //Bug:27587
        // Check if we need the currentPage
        if (currentPage == null && componentRsrc != null) {
            PageManager pageManager = componentRsrc.getResourceResolver().adaptTo(PageManager.class);
            currentPage = pageManager.getContainingPage(componentRsrc);
        }
        // Make sure we have a valid currentPage to read
        if (currentPage != null) {
            // Task 71941 Check if we are on the "events & webinars landing page"
            isEventPage = currentPage.getPath().endsWith(Constants.Paths.EVENTS);
        }
        for (Page page : pages) {
            Set<?> pageTags = new HashSet<>(
                    Arrays.asList(page.getProperties().get(Constants.Properties.CQ_TAGS, String[].class)));
            // HERE If selected, override the check of premium and non premium tags
            boolean showAllCards = Boolean.parseBoolean(properties.get(Constants.Properties.SHOW_ALL_CARDS, String.class));
            if (!showAllCards) {
                showAllCards = (pageTags.contains(Constants.TAGS_EDC_PREMIUM) || pageTags.contains(Constants.TAGS_EDC_NONPREMIUM));
            }
            // -----------------------------------------------------------------
            // Do not add the current page to the results
            // -----------------------------------------------------------------
            if (showAllCards) {
                // always add page
                boolean addPage = true;
                // Task 71941 if current page is the events & webinars landing page only (other
                // pages will user normal behavior) and if this page from the list is a webinar.
                if (isEventPage && pageTags.contains(Constants.TAGS_EDC_EVENT_TYPE)) {
                    // add this page to the gird only if the webinar status is "On Demand"
                    addPage = ConstantsWebinars.WEBINAR_STATE_ONDEMAND.equals(WebinarsHelper.getWebinarStatus(page, 0));
                }
                if (addPage) {
                    filteredPages.add(page);
                }
            }
        }
        return filteredPages;
    }

    // SonarQube fix
    private String populateJson(SlingHttpServletRequest request, ValueMap properties, List<Page> filteredPages,
            String[] tagIds) throws ServletException, JSONException {
        boolean showAllCards = Boolean.parseBoolean(properties.get(Constants.Properties.SHOW_ALL_CARDS, String.class));
        // Handle special case for tradeinsights usage: user can only use tag
        // 'Access-Type' or ('Access-Type/Premium' +'Access-Type/Non-Premium') on
        // cardgrid
        // In this case, we will add tag info in each page node, and add full tag lists
        // in JSON info, to make sure sentence builder works
        if (!showAllCards) {
            showAllCards = ArrayUtils.isNotEmpty(tagIds) && tagIds[0].equalsIgnoreCase(Constants.TAGS_EDC_ACCESSTYPE);
        }
        boolean includeTagsAndMasterList = false;
        if ((tagIds != null) && (tagIds.length >= 1) && showAllCards) {
            includeTagsAndMasterList = true;
        }

        boolean includeCurrentPage = properties.get(Constants.Properties.INCLUDE_CURRENT_PAGE, Constants.NO)
                .equalsIgnoreCase(Constants.YES);
        // ---------------------------------------------------------------------
        Map<String, String> allTags = null;
        JSONObject collectionJson = null;
        if (includeTagsAndMasterList) {
            allTags = Tags.listAllTags(request, true);
            collectionJson = JsonFromCollections.createJson(request, allTags);
        }
        // ---------------------------------------------------------------------
        JsonFormatter jsonFormatter = new JsonFromPages(includeTagsAndMasterList, includeCurrentPage);
        JSONArray pagesJson = jsonFormatter.createJsonFromListOfPages(request, filteredPages);

        JSONObject job = new JSONObject();
        int articlesPerPage = properties.get(Constants.Properties.ARTICLES_PER_PAGE,
                Constants.CARD_GRID_DEFAULT_ARTICLES_PER_PAGE);
        job.put(Constants.Properties.PAGE_SIZE, articlesPerPage);
        job.put(Constants.Properties.ARTICLES, pagesJson);
        if (includeTagsAndMasterList) {
            job.put(Constants.Properties.MASTER_TAG_LIST, collectionJson);
        }
        return job.toString(3);
    }

    @Activate
    @Modified
    protected void activate(Configuration config) {
        this.cardGridPaths = config.cardGridPath();
        log.info("Card Grid - configuration");
        log.info("Card Grid Paths:");
        log.info("-----------------------");
        for (String cardGridPath : this.cardGridPaths) {
            log.info(cardGridPath);
        }
    }

    @ObjectClassDefinition(name = "EDC Card Grid Servlet")
    public @interface Configuration {
        @AttributeDefinition(name = "Component JCR path", description = "Paths to Card Grid components under 'jcr:content' (not including 'jcr:content')")
        String[] cardGridPath() default { "/root/responsivegrid/cardgrid", "/root/responsivegrid/tagresultcardgrid",
                "/root/responsivegrid/tier2successstrories" };
    }

}