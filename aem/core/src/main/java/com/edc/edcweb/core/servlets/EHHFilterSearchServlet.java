package com.edc.edcweb.core.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.edc.edcweb.core.helpers.Constants;
import com.edc.edcweb.core.helpers.LanguageUtil;
import com.edc.edcweb.core.helpers.LinkResolver;
import com.edc.edcweb.core.helpers.ResourceResolverHelper;
import com.edc.edcweb.core.helpers.ServletResponseHelper;
import com.edc.edcweb.core.helpers.TagHelper;
import com.edc.edcweb.core.helpers.constants.ConstantsEHH;
import com.edc.edcweb.core.helpers.ehh.EHHHelper;
import com.edc.edcweb.core.service.KnowledgeService;

/**
 * @author ACN
 * @version 1.0.0
 * @since 1.0.0
 *
 *        QnA is used to get better EHH answers for user question (query param)
 *
 */
@SuppressWarnings({"CQRules:AMSCORE-553", "CQRules:CQBP-75", "squid:S1948"})
@Component(immediate = true, service = Servlet.class, property = { 
            "sling.servlet.extensions=json",
            "sling.servlet.methods=GET", 
            "sling.servlet.paths=" + ConstantsEHH.EHHProperties.EHH_PATH_FILTERSEARCHSERVLET 
            })
public class EHHFilterSearchServlet extends SlingAllMethodsServlet {

    private static final long serialVersionUID = -3522134777775086197L;
    private static final Logger log = LoggerFactory.getLogger(EHHFilterSearchServlet.class);

    @Reference
    @Inject
    KnowledgeService knowledgeService;

    private static String localeEn = "en";

    private static String generalMarketNameImApiResponse = "general";
    private static String generalSubcategoryName = "general";
    private static String categoryNameConst = "categoryName";
    
    private Map<String, JSONArray> mapMarketCategories;
    private Map<String, JSONArray> mapCategoryQuestionNodes;

    @Override
    protected final void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {

    	// clear any data on this variables
    	this.mapMarketCategories = new HashMap<>();
        this.mapCategoryQuestionNodes = new HashMap<>();
    	
    	
        log.info(EHHFilterSearchServlet.class.getName(), " Method Name doGet() [IN]");

        try {
            EHHHelper ehhHelper = new EHHHelper(request);
            Page currentPage = getCurrentPage(request);
            // Get language abbreviation
            String currentPagePath = currentPage != null ? currentPage.getPath() : null;
            String langAbbr = LanguageUtil.getLanguageAbbreviationFromPath(currentPagePath,
                    ConstantsEHH.EHHProperties.ENGLISH_ABBR);

            // Look for the first EHHFilter component in the page
            Resource ehhFilterRsrc = ResourceResolverHelper.getResourceByType(currentPage,
                    Constants.Paths.EHH_FILTER_RESOURCE_TYPE);

            // If not found, throw error
            if (ehhFilterRsrc == null || !ehhFilterRsrc.isResourceType(Constants.Paths.EHH_FILTER_RESOURCE_TYPE)) {
                log.error("Error:doGet - ehhFilterRsrc == null");
                log.info("EHH Filter - Cannot access resource. ");
                response.setStatus(500);
                response.getWriter().write("Internal Server Error: Can not find EHHFilter component in request page.");
                return;
            }

            // Get multifields values for category settings
            List<ValueMap> mrhFields =  getCompCategorySettings(ehhFilterRsrc);

            // Call the service to get JSON of all questions
            String apiJSONResponse = knowledgeService.findAllQuestions(langAbbr.toLowerCase());

            // Initialize the Category-Questions map
            this.mapCategoryQuestionNodes = ehhHelper.generateCategoryQuestionsMap(apiJSONResponse);

            // Reformat API response to JSON format UI required
            String questionsJson = reformatJSONForFilter(request, mrhFields);

            ServletResponseHelper.writeResponse(response, questionsJson);

        } catch (Exception e) {
            log.error("EHHFilterSearchServlet doGet error: ", e);
        }

        log.info("EHHFilterSearchServlet Method Name doGet() [OUT]");
    }

    /**
     * Return current page resource from referer header param
     *
     * @return Resource currentPage
     */
    private Resource getCurrentPageResource(SlingHttpServletRequest request) {
        // Get the page from the referer
        Resource currentPageResc = null;

        try {
            URI urlPage = null;
            String referer = request.getHeader(ConstantsEHH.EHHProperties.EHH_REFERER);
            ResourceResolver resourceResolver = request.getResourceResolver();

            if (referer != null) {
                urlPage = new URI(referer);
            }

            if (urlPage != null) {
                String currentPagePath = urlPage.getPath().replace(".html", "");
                currentPageResc = resourceResolver
                        .resolve(LinkResolver.changeManualExternalURLtoInternal(currentPagePath));

                if (currentPageResc.isResourceType(Resource.RESOURCE_TYPE_NON_EXISTING)) {
                    currentPageResc = null;
                }
            }
        } catch (URISyntaxException e) {
            log.error("EHHFilterSearchServlet getCurrentPageResource: ", e);
        }

        return currentPageResc;
    }

    private ArrayList<ValueMap> getCompCategorySettings(Resource ehhFilterRsrc) {
    
        ArrayList<ValueMap> categorySettings = new ArrayList<>();
        Resource categoryChildResource = ehhFilterRsrc.getChild(ConstantsEHH.EHHProperties.EHH_CATEGORIES);
            if(categoryChildResource != null ) {
                Iterator<Resource> categoryItor = categoryChildResource.listChildren();
                while(categoryItor.hasNext()) {
                    categorySettings.add(categoryItor.next().getValueMap());
                    }
                }
    
            return categorySettings;
    }

    /*
     * Private function to reformat the API JSON response
     */
    private String reformatJSONForFilter(SlingHttpServletRequest request, List<ValueMap> mrhFields) {

        String result = "";
        try {
            JSONObject job = new JSONObject();
            JSONArray categoryJAry;
            categoryJAry = generateCategoryJsonArray(request, mrhFields);
            job.put("markets", getMarketsJAry(request));
            job.put(ConstantsEHH.EHHProperties.EHH_CATEGORIES, categoryJAry);
            result = job.toString();
        } catch (JSONException e) {
            log.error("EHHFilterSearchServlet reformatJSONForFilter: ", e);
        }

        return result;

    }

    /*
     * Generate a JSONArray object which have category JSON objects
     */
    private JSONArray generateCategoryJsonArray(SlingHttpServletRequest request, List<ValueMap> mrhFields) { // NOSONAR
        JSONArray categoryJAry = new JSONArray();
        Iterator<ValueMap> mrhFieldsItor = mrhFields.iterator();
        TagManager tagManager = TagHelper.getTagManager(request);
        Page currentPage = getCurrentPage(request);
        try {
            while (mrhFieldsItor.hasNext()) {
                JSONObject aCategoryNode = new JSONObject();
                ValueMap categoryDialogsetting = mrhFieldsItor.next();
                String categoryTagId = (String) categoryDialogsetting.get("categorytag");
                aCategoryNode.put("categoryId", categoryTagId);
                aCategoryNode.put(categoryNameConst,
                        TagHelper.getLocalizedTagTitle(tagManager, currentPage, categoryTagId));
                // Market node include name, category description and URL:
                aCategoryNode.put("markets", getCategoryMarketsJAry(categoryTagId, categoryDialogsetting, request));

                // subCategories:
                Tag categoryTag = tagManager.resolve(categoryTagId);
                JSONArray subCategoryJAry = new JSONArray();
                Iterator<Tag> childitr = categoryTag.listAllSubTags();

                while (childitr.hasNext()) {
                    Tag subCategoryTag = childitr.next();

                    JSONObject subCategoryJsonObject = new JSONObject();
                    String subCategoryEnName = getTagEnLowerName(subCategoryTag);
                    String subCategoryName = TagHelper.getLocalizedTagTitle(currentPage, subCategoryTag);

                    subCategoryJsonObject.put("questions", getSubCategoryQuestionsJAry(categoryTag, subCategoryTag, request));

                    // Subcategory is 'general' insert as first subcategory
                    if (subCategoryEnName.equalsIgnoreCase(generalSubcategoryName)) {

                        // If not first subcategory in array move other items
                        for (int i = subCategoryJAry.length(); i >= 0; i--) {
                            Object item = i == 0 ? subCategoryJsonObject : subCategoryJAry.get(i - 1);

                            subCategoryJAry.put(i, item);
                        }
                        subCategoryJsonObject.put(generalSubcategoryName, "yes");

                    } else {
                        subCategoryJAry.put(subCategoryJsonObject);
                    }
                    subCategoryJsonObject.put("subCategoryName", subCategoryName);
                    subCategoryJsonObject.put("subCategoryId", subCategoryTag.getTagID());
                }

                // Put all sub category nodes to category
                aCategoryNode.put("subCategories", subCategoryJAry);

                // Put category node to array
                categoryJAry.put(aCategoryNode);
            }
        } catch (JSONException e) {
            log.error("EHHFilterSearchServlet generateCategoryJsonArray: ", e);
        }

        return categoryJAry;
    }

    private JSONArray getSubCategoryQuestionsJAry(Tag categoryTag, Tag subCategoryTag, SlingHttpServletRequest request) { // NOSONAR

        JSONArray questionsJAry = new JSONArray();
        try {
            Page currentPage = getCurrentPage(request);
            String langCatTitle = TagHelper.getLocalizedTagTitle(currentPage, categoryTag);
            String enCatTitle = getTagEnLowerName(categoryTag);
            String enSubCatTitle = getTagEnLowerName(subCategoryTag);

            JSONArray questionNodesInSubCat = this.mapCategoryQuestionNodes.get(enCatTitle + "|" + enSubCatTitle);
            if (questionNodesInSubCat != null) {
                questionsJAry = questionNodesInSubCat;
            }

            // set the mapMarketCategories: we need to loop all questions to know for each
            // market, what category is used:
            JSONObject marketCatNode = new JSONObject();
            marketCatNode.put(categoryNameConst, langCatTitle);
            marketCatNode.put("categoryId", categoryTag.getTagID());

            // Figure out for every market, what categories they have.
            // Need to loop questions in every sub category, and generate a map
            // <Market>-><Categories>
            for (int i = 0; i < questionsJAry.length(); i++) {

                JSONObject aQuestionObj = questionsJAry.getJSONObject(i);
                // Get market value of current question
                String questionMarket = (String) aQuestionObj.get(ConstantsEHH.EHHProperties.EHH_ATTR_METADATA_MARKET);

                // check if market value already in the map
                if (this.mapMarketCategories.containsKey(questionMarket)) {
                    JSONArray marketCategories = this.mapMarketCategories.get(questionMarket);
                    boolean isMarketCategoryExist = false;
                    for (int j = 0; j < marketCategories.length(); j++) {
                        if (marketCategories.getJSONObject(j).get(categoryNameConst).equals(langCatTitle)) {
                            isMarketCategoryExist = true;
                            break;
                        }
                    }
                    if (!isMarketCategoryExist) {
                        marketCategories.put(marketCatNode);
                    }
                } else {
                    JSONArray newMarketCategories = new JSONArray();
                    newMarketCategories.put(marketCatNode);
                    this.mapMarketCategories.put(questionMarket, newMarketCategories);
                }
            }

        } catch (Exception e) {
            log.error("EHHFilterSearchServlet getSubCategoryQuestionsJAry", e);
        }

        return questionsJAry;
    }

    private JSONArray getMarketsJAry(SlingHttpServletRequest request) {
        JSONArray marketJAry = new JSONArray();
        String[] marketsArr = ConstantsEHH.EHHProperties.EHH_MARKETS.split(",");

        try {
            // General
            JSONArray generalMarketCategories = this.mapMarketCategories.get(generalMarketNameImApiResponse);

            for (String market : marketsArr) {

                if (!ConstantsEHH.EHHProperties.EHH_MARKET_OTHER.equals(market)) {
                    market = market.replace(" ", "-");
                    marketJAry.put(
                            getMarketJObj(Constants.TAGS_KNOWLEDGE_MARKET.concat(market), generalMarketCategories, request));
                }
            }

        } catch (Exception e) {
            log.error("EHHFilterSearchServlet getMarketsJAry: ", e);
        }
        return marketJAry;
    }

    private JSONObject getMarketJObj(String tag, JSONArray generalMarketCategories, SlingHttpServletRequest request) {

        JSONObject marketObj = new JSONObject();
        try {
            TagManager tagManager = TagHelper.getTagManager(request);
            Tag marketTag = tagManager.resolve(tag);

            if (marketTag != null) {
                String marketEnName = getTagEnLowerName(marketTag);
                marketObj.put("value", marketEnName);
                JSONArray categories = this.mapMarketCategories.get(marketEnName);
                categories = mergetGeneralCategoryToMarket(generalMarketCategories, categories);
                marketObj.put(ConstantsEHH.EHHProperties.EHH_CATEGORIES, categories);
            }
        } catch (JSONException e) {
            log.error("EHHFilterSearchServlet getMarketJObj: ", e);
        }
        return marketObj;

    }

    private JSONArray mergetGeneralCategoryToMarket(JSONArray generalCategoris, JSONArray marketCategoris) {
        try {
            if (generalCategoris != null && marketCategoris != null) {

                for (int i = 0; i < generalCategoris.length(); i++) {
                    JSONObject aGeneralCategory = (JSONObject) generalCategoris.get(i);
                    String categoryName = aGeneralCategory.getString(categoryNameConst);
                    boolean isExist = false;
                    for (int j = 0; j < marketCategoris.length(); j++) {
                        JSONObject aMArketCategory = (JSONObject) marketCategoris.get(j);
                        if (categoryName.equals(aMArketCategory.getString(categoryNameConst))) {
                            isExist = true;
                            break;
                        }
                    }
                    if (!isExist) {
                        marketCategoris.put(aGeneralCategory);
                    }
                }
            }
        } catch (Exception e) {
            log.error("EHHFilterSearchServlet mergetGeneralCategoryToMarket: ", e);
        }
        return marketCategoris;
    }

    private JSONObject getCategoryMarketsJObj(String categoryTagId, String categoryDesc, String tagName, SlingHttpServletRequest request) {

        JSONObject marketObj = new JSONObject();
        try {
            TagManager tagManager = TagHelper.getTagManager(request);
            Tag marketTag = tagManager.resolve(tagName);
            EHHHelper ehhHelper = new EHHHelper(request);
            Page currentPage = getCurrentPage(request);
            if (marketTag != null) {
                String marketENName = getTagEnLowerName(marketTag);
                String marketURL = ehhHelper.getCategoryURL(tagName, categoryTagId, currentPage.getPath()); // NOSONAR
                boolean isCovid = categoryTagId.equalsIgnoreCase(Constants.TAGS_KNOWLEDGE_CATEGORY_COVID);
                boolean isMex = tagName.equalsIgnoreCase(Constants.TAGS_KNOWLEDGE_MARKET_MEX);
                boolean isUS = tagName.equalsIgnoreCase(Constants.TAGS_KNOWLEDGE_MARKET_US);
                boolean isEU = tagName.equalsIgnoreCase(Constants.TAGS_KNOWLEDGE_MARKET_EU);
                marketObj.put("value", marketENName);
                marketObj.put("description", categoryDesc);

                if (!isMex && !(isCovid && (isUS || isEU))) {
                    marketURL = LinkResolver.changeInternalURLtoExternal(request, marketURL);
                    marketObj.put("categoryURL", marketURL);
                }
            }

        } catch (JSONException | NullPointerException e) {
            log.error("EHHFilterSearchServlet getCategoryMarketsJObj: ", e);
        }
        return marketObj;
    }

    private JSONArray getCategoryMarketsJAry(String categoryTagId, ValueMap categoryDialogsetting, SlingHttpServletRequest request) {

        JSONArray marketJAry = new JSONArray();
        String[] marketsArr = ConstantsEHH.EHHProperties.EHH_MARKETS.split(",");
        String[] descriptionsArr = ConstantsEHH.EHHProperties.EHH_CATEGORY_DESC_SUFFIX.split(",");

        try {
            int marketIndex = 0;

            for (String market : marketsArr) {

                if (!ConstantsEHH.EHHProperties.EHH_MARKET_OTHER.equals(market)) {
                    market = market.replace(" ", "-");
                    marketJAry.put(getCategoryMarketsJObj(categoryTagId,
                            (String) categoryDialogsetting.get(ConstantsEHH.EHHProperties.EHH_CATEGORY_DESC_BASE
                                    .concat(descriptionsArr[marketIndex])),
                            Constants.TAGS_KNOWLEDGE_MARKET.concat(market), request));
                    marketIndex++;
                }
            }
        } catch (Exception e) {
            log.error("EHHFilterSearchServlet getCategoryMarketsJAry: ", e);
        }
        return marketJAry;
    }

    private String getTagEnLowerName(Tag tag) {
        // Use tag title if there is no English translation
        return tag.getTitle(new Locale(localeEn)).toLowerCase();
    }

    
    private Page getCurrentPage(SlingHttpServletRequest request) {
        Page currentPage = null;
        Resource currentPageResc = getCurrentPageResource(request);
        if (currentPageResc != null) {
            // Get the page from the resource
            PageManager pageManager = currentPageResc.getResourceResolver().adaptTo(PageManager.class);
            currentPage = pageManager.getContainingPage(currentPageResc);
        }
        return currentPage;
    }
    
}
