package com.edc.edcweb.core.helpers.ehh;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.edc.edcweb.core.helpers.Constants;
import com.edc.edcweb.core.helpers.LanguageUtil;
import com.edc.edcweb.core.helpers.LinkResolver;
import com.edc.edcweb.core.helpers.TagHelper;
import com.edc.edcweb.core.helpers.constants.ConstantsEHH;

/**
 * @author ACN
 * @version 1.0.0
 * @since 1.0.0
 *
 *
 * Helper functions for EHH file import.
 *
 */
public class EHHHelper {

    private static final Logger log= LoggerFactory.getLogger(EHHHelper.class);

    //Tag Id of Parent EHH Categories taxonomy
    private static String tagPathEhhCategory = ConstantsEHH.tagIds.EHH_TAG_ID;
    private static String tagPathRegion= Constants.tagIds.REGION_TAG_ID;
    private static String tagPathAuthor= ConstantsEHH.tagIds.AUTHOR_TAG_ID;

    //Locale for English
    private static String localeEN = "en";
    private static int tagTypeCountry = 1;
    private static int tagTypeAuthor = 2;
    private static int tagTypeEHHCategory = 3;

    private String euCountriesDataPath = ConstantsEHH.Paths.EDCDATA_EU_COUNTRIES;
    private SlingHttpServletRequest request;

    public EHHHelper(SlingHttpServletRequest request){
        this.request = request;
    }

    /**
     * Parse the received markdown formatted answer string.
     * This function will unescape and chagne markdown to HTML.
     * Also will change the links target to new window
     * @param strMarkdown  The question received from EHH
     * @return String parsed result
     */

    public String parseEHHQuestionStr(String strMarkdown) {
        String result = "";
        result = removeZeroLengthSpaceChar(strMarkdown);
        result = unescape(result);
        result = markdownToHTML(result);
        result = changeLinkTarget(result);
        return result;

    }


    /**
     * Returns URL for category page
     * @param TagId for country region
     * @param TagId for category
     * @return URL of category page
     */
    public String getCategoryURL(String countryTagId, String categoryTagId, String filterPagePath ) {

        //category URL  /en/premium/tool/export-help-hub/country-name/<category-name>.html
        String url = "";
        String countryTagName = getTagNameById(countryTagId);
        String categoryTagName = getTagNameById(categoryTagId);
        String baseURL = getEHHBaseURL(filterPagePath);
        if(!baseURL.isEmpty() && countryTagName != null && categoryTagName != null) {
            url = baseURL + countryTagName +"/" +categoryTagName;
            url = LinkResolver.addHtmlExtension(url);
        }
        return url;
    }

    /**
     * Returns Map for category questions.
     * The key of map will be <categoryName|subCategoryName>.
     * The value will be a JSONArray which get from EHH API
     * @return Return Map<String, JSONArray>
     */
    public Map<String, JSONArray> generateCategoryQuestionsMap(String strAPIResponseJSON) {

        HashMap<String, JSONArray> mapCategoryQuestionNodes = new HashMap<> ();

        try {
            JSONObject jsonAPIResponse = new JSONObject(strAPIResponseJSON);
            JSONArray jsonArrayAllQeustions = jsonAPIResponse.getJSONArray("value");
            ArrayList<String> euCountries = getEUCountriesList();

            for(int i=0; i<jsonArrayAllQeustions.length(); i++) {
                JSONObject jsonObjQuestion= jsonArrayAllQeustions.getJSONObject(i);
                String categoryName = jsonObjQuestion.getString(ConstantsEHH.EHHProperties.EHH_ATTR_METADATA_TOPIC);
                //read the subCategoryList
                String subCategoryList = jsonObjQuestion.getString(ConstantsEHH.EHHProperties.EHH_ATTR_METADATA_SUBTOPIC);
                //split subCategoryList
                String[] subCategories = subCategoryList.split(ConstantsEHH.SUB_CATEGORY_DELIMITER);
                for(String subCategoryName : subCategories) {
                    //We change metadata_country to "european union" if country is in Europe
                    String metadataCountryName = jsonObjQuestion.getString(ConstantsEHH.EHHProperties.EHH_ATTR_METADATA_COUNTRY);
                    if(isEUCountry(euCountries, metadataCountryName)) {
                        jsonObjQuestion.put(ConstantsEHH.EHHProperties.EHH_ATTR_METADATA_MARKET, ConstantsEHH.EHHProperties.EHH_ATTR_METADATA_COUNTRY_EU);
                    }else {
                        jsonObjQuestion.put(ConstantsEHH.EHHProperties.EHH_ATTR_METADATA_MARKET, metadataCountryName);
                    }
    
                    String mapKey = categoryName+"|"+subCategoryName;
                    if(mapCategoryQuestionNodes.containsKey(categoryName+"|"+subCategoryName)) {
                        JSONArray catQuestions = mapCategoryQuestionNodes.get(mapKey);
                        catQuestions.put(jsonObjQuestion);
                    }
                    else {
                        JSONArray questions = new JSONArray ();
                        questions.put(jsonObjQuestion);
                        mapCategoryQuestionNodes.put(mapKey, questions);
                    }
                }
            }

        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return mapCategoryQuestionNodes;
    }

    public static Map<String, String> getMarketsMap(String marketsTagId, Page currentPage, SlingHttpServletRequest request) {

        Map<String, String> resultMap = new LinkedHashMap<>();

        // Get market tags and set markets
        TagManager tagManager = TagHelper.getTagManager(request);
        Tag marketTag = tagManager.resolve(marketsTagId);
        if(marketTag != null) {
            Iterator<Tag> childTags = marketTag.listChildren();
            while(childTags.hasNext()) {
                Tag aMmarketTag = childTags.next();
                String tagName = aMmarketTag.getTitle();
                String localTagTitle = TagHelper.getLocalizedTagTitle(currentPage, aMmarketTag);
                
                if (StringUtils.isNotBlank(localTagTitle)) {
                    resultMap.put(tagName.toLowerCase(), localTagTitle);
                }
            }
        }

        return resultMap;
    }


    /**
     * Convert markdown string to HTML
     * @param markdown content
     * @return HTML content
     */
    private String markdownToHTML(String markdown) {

        String markdownResult = "";

        // Commonmark framework 0.11
        Parser parser = Parser.builder().build();
        org.commonmark.node.Node document = parser.parse(markdown);
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        markdownResult = renderer.render(document);

        return markdownResult;
    }

    /**
     * Unescape given String
     * @param Escaped String
     * @return Unescaped String
     */
    private String unescape(String input) { //NOSONAR
        StringBuilder builder = new StringBuilder();

        int i = 0;
        while (i < input.length()) {
            char delimiter = input.charAt(i);
            i++; // consume letter or backslash

            if (delimiter == '\\' && i < input.length()) {

                // consume first after backslash
                char ch = input.charAt(i);
                i++;

                if (ch == '\\' || ch == '/' || ch == '"' || ch == '\'' || ch == '.') {
                    builder.append(ch);
                } else if (ch == 'n')
                    builder.append('\n');
                else if (ch == 'r')
                    builder.append('\r');
                else if (ch == 't')
                    builder.append('\t');
                else if (ch == 'b')
                    builder.append('\b');
                else if (ch == 'f')
                    builder.append('\f');
                else if (ch == 'u') {

                    StringBuilder hex = new StringBuilder();

                    // expect 4 digits
                    if (i + 4 > input.length()) {
                        throw new IllegalArgumentException("Not enough unicode digits! ");
                    }
                    for (char x : input.substring(i, i + 4).toCharArray()) {
                        if (!Character.isLetterOrDigit(x)) {
                            throw new IllegalArgumentException("Bad character in unicode escape.");
                        }
                        hex.append(Character.toLowerCase(x));
                    }
                    i += 4; // consume those four digits.

                    int code = Integer.parseInt(hex.toString(), 16);
                    builder.append((char) code);
                } else {
                    throw new IllegalArgumentException("Illegal escape sequence: \\" + ch);
                }
            } else { // it's not a backslash, or it's the last character.
                builder.append(delimiter);
            }
        }

        return builder.toString();
    }

    /**
     * Change the target of <A> link in the answer to new window
     * @param parsedHTMLAnswer  Parsed HTML answer
     * @return Modified answer which all links will be opened in new window
     */
    private String changeLinkTarget(String parsedHTMLAnswer) {
        String result = parsedHTMLAnswer;
        if(result != null && !result.isEmpty()) {
            result = result.replace("<a href=", "<a target=\"_blank\" href=");
        }

        return result;
    }

    /**
     * Find AEM tag id by given an English Tag title
     * @param TagManager
     * @param tagEnTitle The English title in EHH file
     * @param tagType  1-Country  2-Author 3-EHH Category
     * @return tagId
     */
    public String findTagIdWithEnTitle(TagManager tagManager, String tagEnTitle, int tagType) {

        log.info(EHHHelper.class.getName(), " Method Name findTagIdWithEnTitle() [IN]");

        String tagId = null;
        Tag rootTag = null;

        if( tagEnTitle == null || tagEnTitle.isEmpty()) {
            return null;
        }
        if(tagType == tagTypeAuthor) {
            rootTag = tagManager.resolve(tagPathAuthor);
        }
        else if(tagType == tagTypeCountry) {
            rootTag = tagManager.resolve(tagPathRegion);
        }
        else if(tagType == tagTypeEHHCategory) {
            rootTag = tagManager.resolve(tagPathEhhCategory);
        }

        if(rootTag != null ) {
            Iterator<Tag> allSubTags = rootTag.listAllSubTags();
            if(allSubTags!=null) {
                while(allSubTags.hasNext()) {
                    Tag aChileTag = allSubTags.next();
                    String enTitlePath = aChileTag.getTitlePath(new Locale(localeEN));
                    enTitlePath = enTitlePath.replace(" / ","/");
                    if(enTitlePath.toLowerCase().indexOf(tagEnTitle.toLowerCase()) > -1) {
                        tagId = aChileTag.getTagID();
                        break;
                    }
                }
            }
        }

        log.info(EHHHelper.class.getName(), " Method Name findTagIdWithEnTitle() [OUT]");

        return tagId;
    }

    private String removeZeroLengthSpaceChar(String content) {
        if(content != null) {
            content = content.replaceAll("[\\p{Cf}]", "");
        }

        return content;
    }


    /**
     * Returns English or French tag title based on the local from request
     * @param tagId
     * @return  English/French tag title
     */
    private String getTagNameById(String tagId) {

        log.info(EHHHelper.class.getName(), " Method Name getTagNameById() [IN]");

        String name = null;
        if( tagId != null && tagId.trim().length()>0) {

            TagManager tagManager = TagHelper.getTagManager(this.request);
            Tag aTag = tagManager.resolve(tagId);

            if(aTag != null) {
                name = aTag.getName();
            }
        }
        log.info(EHHHelper.class.getName(), " Method Name getTagNameById() [OUT]");

        return name;
    }



    /**
     * Returns EHH base part in the URL
     * @return Return base url like '/en/premium/tool/export-help-hub/'
     */
    private String getEHHBaseURL(String currentURL) {
        String baseURL = "";
        String langAbbr = LanguageUtil.getLanguageAbbreviationFromPath(currentURL, Constants.SupportedLanguages.ENGLISH.getLanguageAbbreviation());
        if(currentURL.indexOf('/'+langAbbr+'/')>0) { //NOSONAR
            baseURL = currentURL.substring(0, currentURL.indexOf('/'+langAbbr+'/'));
            baseURL = baseURL+'/'+langAbbr+ConstantsEHH.Paths.EHH_URL;
        }
        return baseURL;
    }


    private ArrayList<String> getEUCountriesList(){
        ArrayList<String> euCountries = new ArrayList<>();

        Resource fieldResource = request.getResourceResolver().getResource(euCountriesDataPath);

        if (fieldResource!= null) {
            Iterable<Resource> children = fieldResource.getChildren();
            Iterator<Resource> itr = children.iterator();
            while(itr.hasNext()) {
                ValueMap euCountryProps = itr.next().getValueMap();
                String value = euCountryProps.get("value", String.class);
                if(value != null) {
                    euCountries.add(value.toLowerCase());
                }
            }
        }
        return euCountries;
    }

    private boolean isEUCountry(ArrayList<String> euCountriesList, String metadataCountry) {
        boolean isEUCountry = false;
        if(euCountriesList!= null && euCountriesList.size()>0) { //NOSONAR
            isEUCountry = euCountriesList.contains(metadataCountry.toLowerCase());
        }
        return isEUCountry;

    }

}
