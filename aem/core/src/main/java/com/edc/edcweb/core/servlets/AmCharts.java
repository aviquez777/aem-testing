package com.edc.edcweb.core.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.edc.edcweb.core.helpers.Constants;
import com.edc.edcweb.core.helpers.LanguageUtil;
import com.edc.edcweb.core.helpers.ResourceResolverHelper;

/**
 * @author ACN
 * @version 1.0.0
 * @since 1.0.0
 *
 * AmCharts is used to get AmChart data (apply for Map and Timeline)
 *
 */
@Component(
        immediate = true,
        service = Servlet.class,
        property = {
            "sling.servlet.extensions=json",
            "sling.servlet.methods=GET",
            "sling.servlet.selectors=amchartmap",
            "sling.servlet.selectors=amcharttimeline",
            "sling.servlet.resourceTypes=edc/components/structure/page"}
)
public class AmCharts extends SlingAllMethodsServlet {

    private static final long serialVersionUID = -3522134777775086197L;
    private static final String AMCHARTMAP = "amchartmap";
    private static final String TITLE = "title";

    @Override
    protected final void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws
            ServletException, IOException {

        // Get AmChart component
        String selector = request.getRequestPathInfo().getSelectors()[0];

        //Get the page from the resource
        Resource resource = request.getResource();
        PageManager pageManager = resource.getResourceResolver().adaptTo(PageManager.class);
        Page currentPage = pageManager.getContainingPage(resource);

        // Look for the first card grid on the page
        String resourceType = "";
        if (selector.equalsIgnoreCase(AMCHARTMAP)) {
        	resourceType = Constants.Paths.AMCHART_MAP_RESOURCE_TYPE;
        }
        else {
        	resourceType = Constants.Paths.AMCHART_TIMELINE_RESOURCE_TYPE;
        }

        //Recursively fined the first AmChart(Assuming only one chart exists for a page)
        Resource chartRsrc =  ResourceResolverHelper.getResourceByType(currentPage, resourceType);

        if (chartRsrc != null) {
            Resource chartChildren = getChartChildren(chartRsrc, selector);

            if (chartChildren != null && chartChildren.hasChildren()) {
                // ---------------------------------------------------------------------
                // JSON inialize
                // ---------------------------------------------------------------------
                PrintWriter out = response.getWriter();
                // ---------------------------------------------------------------------
                response.setContentType(Constants.Properties.APPLICATION_SLASH_JSON);
                response.setCharacterEncoding(Constants.UTF_8);
                // JSON objects
                JSONObject jsonobj = new JSONObject();

                if (selector.equalsIgnoreCase(AMCHARTMAP)) {
                    String pagePath = currentPage.getPath();
                    String languageAbbreviation = LanguageUtil.getLanguageAbbreviationFromPath(pagePath, Constants.SupportedLanguages.ENGLISH.getLanguageAbbreviation());

                    setChartMapInfo(chartChildren, languageAbbreviation, jsonobj);
                }

                String jsonstring = jsonobj.toString();

                out.append(jsonstring);
                out.flush();
            }
        }
    }

    private Resource getChartChildren(Resource chartRsrc, String chartType) {
        Resource children = null;

        if (chartType.equalsIgnoreCase(AMCHARTMAP)) {
            children = chartRsrc.getChild("ftas");
        }

        return children;
    }

    private void setChartMapInfo(Resource chartChildren, String lang, JSONObject jsonobj) throws ServletException {
        try {
            Iterator<Resource> ftasIter = chartChildren.listChildren();

            // Create JSON arrays
            JSONArray areasData = new JSONArray();
            JSONArray legendsData = new JSONArray();
            JSONArray imagesData = new JSONArray();

            int counter = 0;

            while (ftasIter.hasNext()) {
                Resource ftaRscr = ftasIter.next();

                // Get FTA data
                ValueMap ftaValues = ftaRscr.getValueMap();

                // Create json object
                JSONObject legend = new JSONObject();

                // Set legend information
                legend.put(TITLE, ftaValues.get(TITLE, ""));
                legend.put("color", ftaValues.get("color", ""));

                // Add legend object (completed in this point)
                legendsData.put(legend);

                // Get countries
                Resource countries = ftaRscr.getChild("countries");

                if (countries != null && countries.hasChildren()) {
                    Iterator<Resource> countriesIter = countries.listChildren();

                    while (countriesIter.hasNext()) {
                        Resource countryRscr = countriesIter.next();
                        ValueMap countryValues = countryRscr.getValueMap();

                        // Create json objects
                        JSONObject area = new JSONObject();
                        JSONObject image = new JSONObject();

                        // Set area information relative to Country
                        String description = countryValues.get("countrydescription", "").replace("\"", "'");
                        area.put("groupId", ftaValues.get(TITLE, ""));
                        area.put("id", countryValues.get("countrycode", ""));
                        area.put("description", description);

                        // Add area information (completed in this point)
                        areasData.put(area);

                        // Get longitude and latitude
                        String lng = countryValues.get("longitude", String.class);
                        String lat = countryValues.get("latitude", String.class);

                        // Image information should be include just if longitude and latitude exists
                        if (lng != null && lat != null) {
                            String title = countryValues.get("countrytitle", "");

                            // Set image information
                            image.put("description", description);
                            image.put(TITLE, title);
                            image.put("label", title);
                            image.put("latitude", Double.parseDouble(lat));
                            image.put("longitude", Double.parseDouble(lng));
                            image.put("labelShiftY", Integer.parseInt(countryValues.get("labelshiftY", "0")));
                            image.put("lineId", counter);

                            // Add image information (completed in this point)
                            imagesData.put(image);

                            counter++;
                        }
                    }
                }
            }

            jsonobj.put("language", lang);
            jsonobj.put("areas", areasData);
            jsonobj.put("images", imagesData);
            jsonobj.put("legend", legendsData);
        } catch (JSONException e) {
            throw new ServletException("AmCharts error", e);
        }
    }
}
