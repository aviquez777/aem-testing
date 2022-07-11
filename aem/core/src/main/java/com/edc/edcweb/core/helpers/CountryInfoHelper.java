package com.edc.edcweb.core.helpers;

import java.io.Serializable;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.policies.ContentPolicy;
import com.edc.edcweb.core.models.CountrySearchData;

/**
 * @author  ACN
 * @version
 * @since
 *
 *
 * Helper class to perform operations about country info.
 *
 *
 */
public class CountryInfoHelper
{
    private static String basePath = Constants.Paths.COUTRYINFO_BASE;
    private static String countryInfoPath = Constants.Paths.COUTRYINFO_INFO;
    private static String countryMapPath = Constants.Paths.COUTRYINFO_ID;

    private static final Logger log = LoggerFactory.getLogger(CountryInfoHelper.class);

    public static String getPosition(String countryID, SlingHttpServletRequest request )
    {
        return getAttr(Constants.Properties.COUTRYINFO_POSITION, countryID, request );
    }

    public static String getRating( String countryID, SlingHttpServletRequest request )
    {
        return getAttr(Constants.Properties.COUTRYINFO_RATING, countryID, request );
    }

    public  static String getBusinessEnvironment( String countryID, SlingHttpServletRequest request) {
        return getAttr(Constants.Properties.MULTIPLETABS_BUSINESSENVIRONMENT, countryID, request);
    }

    private static String getAttr( String attrName, String countryID, SlingHttpServletRequest request )
    {
        String attrValue="";

        if (!countryID.isEmpty())
        {
            Page currentPage = Request.adaptToPage(request);
            String pageLanguage = LanguageUtil.getLanguageAbbreviationFromPath(currentPage.getPath(), Constants.SupportedLanguages.ENGLISH.getLanguageAbbreviation());

            String path = basePath + pageLanguage.toLowerCase() + countryInfoPath + countryID; //same value under both en and fr
            log.debug("getAttr from path {}  ", path );

            Resource country = request.getResourceResolver().getResource(path);
            if ( country != null )
            {
                ValueMap valueMapCountry = country.getValueMap();
                log.debug("valueMapCountry OK {}  ", path );
                attrValue = valueMapCountry.get(attrName, String.class);
                log.debug("populatePositionFromPolicy position:{} ",attrValue);
            }
            else
            {
                log.debug("ERROR: cannot find resource in path {}  ", path );
            }
        }
        return attrValue;
    }

    
    public static String fetchCountryID(String countrytag, SlingHttpServletRequest request ) {
        String id="";

        if (countrytag == null || countrytag.isEmpty() )
        {
            return id;
        }
        String countryCheckPath = countrytag;
        String[] listOfPaths =  countryCheckPath.split("[/]");
        log.debug("listOfPaths.length: {}" , listOfPaths.length);
        if (listOfPaths.length == 4  )
        {
            StringBuilder builder = new StringBuilder(listOfPaths[0]);
            builder.append("/");
            builder.append(listOfPaths[1]);
            builder.append("/");
            builder.append(listOfPaths[3]);
            countryCheckPath = builder.toString();
        }
        log.debug("countryCheckPath: {}" , countryCheckPath);

        Page currentPage = Request.adaptToPage(request);
        String pageLanguage = LanguageUtil.getLanguageAbbreviationFromPath(currentPage.getPath(), Constants.SupportedLanguages.ENGLISH.getLanguageAbbreviation());

        String thecountryIDpath = basePath + pageLanguage.toLowerCase() + countryMapPath;
        Resource countryMapResource = request.getResourceResolver().getResource( thecountryIDpath );
        if ( countryMapResource != null)
        {

            Iterator<Resource> resources = countryMapResource.listChildren();
            while (resources.hasNext()) {
                Resource res = resources.next();
                ValueMap valueMapID =  res.getValueMap();

                String mapTag = valueMapID.get(Constants.Properties.COUTRYINFO_TAGPATH, String.class);
                if ( mapTag != null)
                {
                    log.debug("mapTag countryid: {} tagpath {} ", res.getName() , mapTag);

                    if( mapTag.equalsIgnoreCase(countryCheckPath))
                    {
                        id = res.getName();
                        log.debug("found mapped id:{}   ", id);
                        return id;
                    }
                }
            }
        }

        return id;
    }

    //If ANY region tag id is found for the page, then return the tag id
    public static Tag getRegionTagForCountryTag(Tag countryTag, SlingHttpServletRequest request)
    {
        Tag parentTag = null;
        //the region its the parent tag that is under region path

        if(countryTag != null)
        {
            parentTag = countryTag.getParent();
            boolean found = false;
            while (!found && parentTag != null )
            {
                if ( parentTag.getParent().getPath().equalsIgnoreCase(Constants.Paths.REGION_TAGS)  )
                {
                    found = true;
                }
                else
                {
                    parentTag = parentTag.getParent();
                }
            }

        }

        log.debug("getRegionTagForCountryTag, region was not found");
        return parentTag;
    }

    //If ANY region tag id is found for the page, then return the tag id
    public static String getPageRegionTag(Page page, SlingHttpServletRequest request)
    {

		String regionTag = null;
		String regiontagId = null;
		if(page != null)
		{
			Tag[] tags = page.getTags();

			Resource tagResource =  request.getResourceResolver().getResource(Constants.Paths.REGION_TAGS);
			if(tagResource != null ) {
			    Tag regionroot = tagResource.adaptTo(Tag.class);
    			if(regionroot != null) {
    			     regiontagId = regionroot.getTagID();
    			}
			if(regiontagId != null) {
				for(Tag tag : tags)
				{
					log.debug("page tag path: {} - tag id{}" , tag.getPath(), tag.getTagID());
					regionTag = tag.getTagID();
					if(regionTag.startsWith(regiontagId)){
						return regionTag;
					} else {
					    regionTag = null;
					}
				}
			}
          }
		}

        log.debug("getPageRegionTag was not found");
        return regionTag;
    }

    public static  List<CountrySearchData> buildCountryTopTenData(SlingHttpServletRequest request, List<String> countryTags, ValueMap properties) {
        log.debug("buildCountryTopTenData");
        List<CountrySearchData> listCountry = new ArrayList<>();

        List<String> featuredIDs = new ArrayList<>();
        Iterator<String> ittag = countryTags.iterator();
        while(ittag.hasNext()){
            String countryID = fetchCountryID( ittag.next(), request);
            featuredIDs.add(countryID);
        }

        Page currentPage = Request.adaptToPage(request);
        String pageLanguage = LanguageUtil.getLanguageAbbreviationFromPath(currentPage.getPath(), Constants.SupportedLanguages.ENGLISH.getLanguageAbbreviation());
        String pageName =  Constants.Paths.COUNTRYINFO_COUNTRY_DETAILPAGES_EN;

        Resource resource = request.getResourceResolver().getResource(Constants.Paths.CONTENT_EDC + "/" + pageLanguage.toLowerCase() + pageName );
        Page countryRootPage = resource != null ? resource.adaptTo(Page.class): null;

        if (countryRootPage != null) {
            Iterator<String> it = countryTags.iterator();

            while(it.hasNext()){
                String tag = it.next();
                Page countryPage = getPageFromRegionTag( request, tag, countryRootPage);
                if ( countryPage == null )
                {
                    CountrySearchData aCountryData = buildCountryDataFromTag(request, properties, tag, featuredIDs);
                    listCountry.add(aCountryData);
                }
                else
                {
                    CountrySearchData aCountryData = buildCountryData(request, properties, countryPage, featuredIDs);
                    listCountry.add(aCountryData);
                }
            }
        }
        return listCountry;
    }


    private static Page getPageFromRegionTag( SlingHttpServletRequest request, String countryTag, Page countryRootPage) {
        Iterator<Page> countryIt = countryRootPage.listChildren();
        while (countryIt.hasNext())
        {
            Page child = countryIt.next();
            if((child != null)  )
            {
                String pageCountryPath = getPageRegionTag(child, request);
                if ( countryTag.equalsIgnoreCase(pageCountryPath) )
                {
                    log.debug("page tag countryTagPath: {}" , pageCountryPath );
                    return child;
                }
            }

        }
        return null;
    }

    public static  List<CountrySearchData> buildCountrySearchData(SlingHttpServletRequest request, ValueMap properties, CountryFilterType filterType) {

        List<String> featuredIDs =  getFeaturedCountriesIDs(request);

        log.debug("BuildCountrySearchData");
        List<CountrySearchData> listCountry = new ArrayList<>();
        Page currentPage = Request.adaptToPage(request);

        String pageName = Constants.Paths.COUNTRYINFO_COUNTRY_DETAILPAGES_EN;

        String countryRootPagePath = Constants.Paths.CONTENT_EDC + "/" + currentPage.getLanguage()+ pageName;
        Resource resource = request.getResourceResolver().getResource(countryRootPagePath);
        Page countryRootPage = resource != null ? resource.adaptTo(Page.class) : null;

        if (countryRootPage != null)
        {
            Iterator<Page> childIterator = countryRootPage.listChildren();
            while (childIterator.hasNext()) {
                Page child = childIterator.next();
                if((child != null)  ) {
                    CountrySearchData aCountryData = buildCountryData(request, properties, child, featuredIDs);
                    if ( !FilterOut(request , featuredIDs, aCountryData, filterType ) )
                    {
                        listCountry.add(aCountryData);
                    }
                }
            }
        }

        return listCountry;
    }

    public static  List<CountrySearchData> buildCountrySearchDataFromTags(SlingHttpServletRequest request, ValueMap properties, CountryFilterType filterType) {

        List<String> featuredIDs =  getFeaturedCountriesIDs(request);

        log.debug("buildCountrySearchDataFromTags");
        List<CountrySearchData> listCountry = new ArrayList<>();

        //for each country
        Resource regionTags = request.getResourceResolver().getResource(Constants.Paths.REGION_TAGS);
        Tag region_root = regionTags != null ? regionTags.adaptTo(Tag.class) : null;

        if (region_root != null) {
            Iterator<Tag> tagsIterator = region_root.listChildren();
            while (tagsIterator.hasNext()) {
                Tag regionTag = tagsIterator.next();
                CountrySearchData aCountryData = buildCountryDataFromTag(request, properties, regionTag.getTagID(), featuredIDs);
                if ( !FilterOut(request , featuredIDs, aCountryData, filterType ) )
                {
                    listCountry.add(aCountryData);
                }
            }
        }
        return listCountry;
    }


    private static boolean FilterOut(SlingHttpServletRequest request, List<String> featuredIDs, CountrySearchData aCountryData, CountryFilterType filterType) {
        if ( aCountryData == null )
        {
            return true;
        }

        ////filter out if premium
        if ( aCountryData.getCountryID().isEmpty())
        {
            return true;
        }
        if ( filterType == CountryInfoHelper.CountryFilterType.PREMIUM && !featuredIDs.contains(aCountryData.getCountryID()))
        {
            return true;
        }
        ////filter out if non premium
        else if (filterType ==  CountryInfoHelper.CountryFilterType.NONPREMIUM && featuredIDs.contains(aCountryData.getCountryID()) )
        {
            return true;
        }

        return false;
    }

    public static Map<String, String> buildRatingNamesData(SlingHttpServletRequest request, ValueMap map) {
        log.debug("BuildRatingNamesData");
        Map<String, String>  resultMap  = new LinkedHashMap<>();

        resultMap.put(Constants.Properties.COUTRYINFO_RATING_LOWRISK, map.get(Constants.Properties.COUTRYINFO_RATING_HEADING + Constants.Properties.COUTRYINFO_RATING_LOWRISK, String.class) );
        resultMap.put(Constants.Properties.COUTRYINFO_RATING_LOWMEDRISK, map.get(Constants.Properties.COUTRYINFO_RATING_HEADING + Constants.Properties.COUTRYINFO_RATING_LOWMEDRISK, String.class) );
        resultMap.put(Constants.Properties.COUTRYINFO_RATING_MEDIUMRISK, map.get(Constants.Properties.COUTRYINFO_RATING_HEADING + Constants.Properties.COUTRYINFO_RATING_MEDIUMRISK, String.class) );
        resultMap.put(Constants.Properties.COUTRYINFO_RATING_MEDHIGHRISK, map.get(Constants.Properties.COUTRYINFO_RATING_HEADING + Constants.Properties.COUTRYINFO_RATING_MEDHIGHRISK, String.class) );
        resultMap.put(Constants.Properties.COUTRYINFO_RATING_HIGHRISK, map.get(Constants.Properties.COUTRYINFO_RATING_HEADING + Constants.Properties.COUTRYINFO_RATING_HIGHRISK, String.class) );

        return resultMap;
    }

    public static Map<String, String> buildPositionNamesData(SlingHttpServletRequest request, ValueMap map) {
        log.debug("BuildPositionNamesData");
        Map<String, String>  resultMap  = new LinkedHashMap<>();
        resultMap.put(Constants.Properties.COUTRYINFO_POS_LIMITED, map.get(Constants.Properties.COUTRYINFO_POSITION_HEADING + Constants.Properties.COUTRYINFO_POS_LIMITED, String.class) );
        resultMap.put(Constants.Properties.COUTRYINFO_POS_OPEN, map.get(Constants.Properties.COUTRYINFO_POSITION_HEADING + Constants.Properties.COUTRYINFO_POS_OPEN, String.class) );
        resultMap.put(Constants.Properties.COUTRYINFO_POS_RESTRICTED, map.get(Constants.Properties.COUTRYINFO_POSITION_HEADING + Constants.Properties.COUTRYINFO_POS_RESTRICTED, String.class) );
        resultMap.put(Constants.Properties.COUTRYINFO_POS_HIGHLYRESTRICTED, map.get(Constants.Properties.COUTRYINFO_POSITION_HEADING + Constants.Properties.COUTRYINFO_POS_HIGHLYRESTRICTED, String.class) );
        resultMap.put(Constants.Properties.COUTRYINFO_POS_CLOSED, map.get(Constants.Properties.COUTRYINFO_POSITION_HEADING + Constants.Properties.COUTRYINFO_POS_CLOSED, String.class) );

        return resultMap;
    }

    public static Map<String, String> buildRegionNamesData(SlingHttpServletRequest request) {
        log.debug("BuildRegionNamesData");
        Page currentPage = Request.adaptToPage(request);
        HashMap<String, String>  resultMap  = new LinkedHashMap<>();

        Resource regionTags = request.getResourceResolver().getResource(Constants.Paths.REGION_TAGS);
        Tag region_root = regionTags != null ? regionTags.adaptTo(Tag.class) : null;

        if (region_root != null) {
            Iterator<Tag> tagsIterator = region_root.listChildren();
            while (tagsIterator.hasNext()) {
                Tag regionTag = tagsIterator.next();
                String regionName = regionTag.getLocalizedTitle(currentPage.getLanguage());
                resultMap.put(regionTag.getTagID(), regionName);
            }
        }
        return resultMap;
    }

    public static Map<String, String> buildFTANamesData(SlingHttpServletRequest request) {
        log.debug("buildFTANamesData");

        return TagHelper.getTagsList( request, Constants.Paths.FTA_TAGS, false);
    }

    public static  CountrySearchData buildCountryData(SlingHttpServletRequest request, ValueMap properties, Page child, List<String> featuredIDs ) {
        log.debug("buildCountryData");

        Page currentPage = Request.adaptToPage(request);
        //need then to get the position and rating for that country id...
        String countrytag = CountryInfoHelper.getPageRegionTag(child, request);
        String countryID = CountryInfoHelper.fetchCountryID(countrytag, request);
        String position = CountryInfoHelper.getPosition(countryID, request);
        // String rating = CountryInfoHelper.getRating(countryID, request);
        String ftaName = CountryInfoHelper.getFTATagName(child, request);
        String countryPageUrl = "";
        if ( child != null )
        {
            countryPageUrl = child.getPath();
        }
        else if ( countrytag == null || countryID == null || position == null  )
        {
            log.debug("CountrySearchData cannot access country information.");
            return null;
        }

        String countryName = "";
        String regionName = "";
        String regionLabel = "";

        TagManager tagMgr =  request.getResourceResolver().adaptTo(TagManager.class);
        if ( tagMgr != null )
        {
            Tag tagCountry= tagMgr.resolve(countrytag);
            Tag regionTag = CountryInfoHelper.getRegionTagForCountryTag(tagCountry, request);

            if ( tagCountry != null && regionTag  != null )
            {
                countryName = tagCountry.getLocalizedTitle(currentPage.getLanguage());
                regionLabel = regionTag.getLocalizedTitle(currentPage.getLanguage());
                regionName = regionTag.getTagID();
            }

        }

        String positionLabel = properties.get( Constants.Properties.COUTRYINFO_POSITION_HEADING + position, String.class);
        // String ratingLabel = properties.get( Constants.Properties.COUTRYINFO_RATING_HEADING + rating, String.class);


        if ( countryName == null || regionName == null || countryName.isEmpty() ||  regionName.isEmpty()  )
        {
            log.debug("CountrySearchData cannot be initialized for page {}", countryPageUrl);
            return null;
        }

        Boolean premium = false;
        if ( featuredIDs.contains(countryID))
        {
            log.debug("country is premium - id:{}", countryID);
            premium = true;
        }

        CountrySearchData aCountryData = new CountrySearchData();
        // aCountryData.initValues(countryName, regionName,regionLabel, countryPageUrl, position, positionLabel, rating, ratingLabel, countrytag, countryID, ftaName, premium);
        aCountryData.initValues(countryName, regionName,regionLabel, countryPageUrl, position, positionLabel, countrytag, countryID, ftaName, premium);
        return aCountryData;
    }

    private static String getFTATagName(Page page, SlingHttpServletRequest request) {

        if(page != null){
			Tag[] tags = page.getTags();
			Resource res = request.getResourceResolver().getResource(Constants.Paths.FTA_TAGS);
			if(res!=null) {
				Tag ftaRoot = res.adaptTo(Tag.class);
				if(ftaRoot != null) {
					String ftaRootID = ftaRoot.getTagID();
					for(Tag tag : tags)
					{				 
						if(tag.getTagID().startsWith(ftaRootID)){
							return tag.getName();
						}
					}
				}
			}
        }

        log.debug("getFTATagID was not found");
        return null;
    }

    public static  CountrySearchData buildCountryDataFromTag(SlingHttpServletRequest request, ValueMap properties, String countrytag, List<String> featuredIDs ) {
        log.debug("buildCountryData");
        CountrySearchData aCountryData = new CountrySearchData();
        Page currentPage = Request.adaptToPage(request);
        //need then to get the position and rating for that country id...
        String countryID = CountryInfoHelper.fetchCountryID(countrytag, request);

        if ( countryID.isEmpty())
        {
            return aCountryData;
        }

        String position = CountryInfoHelper.getPosition(countryID, request);
        // String rating = CountryInfoHelper.getRating(countryID, request);
        String countryPageUrl = "";
        String countryName = "";
        String regionName = "";
        String regionLabel = "";
        String ftaName = "";

        TagManager tagMgr =  request.getResourceResolver().adaptTo(TagManager.class);
        if ( tagMgr != null )
        {
            Tag tagCountry= tagMgr.resolve(countrytag);
            Tag regionTag = CountryInfoHelper.getRegionTagForCountryTag(tagCountry, request);

            if ( tagCountry != null && regionTag  != null )
            {
                countryName = tagCountry.getLocalizedTitle(currentPage.getLanguage());
                regionLabel = regionTag.getLocalizedTitle(currentPage.getLanguage());
                regionName = regionTag.getTagID();
            }

        }

        String positionLabel = properties.get( Constants.Properties.COUTRYINFO_POSITION_HEADING + position, String.class);
        // String ratingLabel = properties.get( Constants.Properties.COUTRYINFO_RATING_HEADING + rating, String.class);

        Boolean premium = false;
        if ( featuredIDs.contains(countryID))
        {
            premium = true;
        }
        // aCountryData.initValues(countryName, regionName,regionLabel, countryPageUrl, position, positionLabel, rating, ratingLabel, countrytag, countryID, ftaName, premium);
        aCountryData.initValues(countryName, regionName,regionLabel, countryPageUrl, position, positionLabel, countrytag, countryID, ftaName, premium);
        return aCountryData;
    }


    public static void insertLetterData(SlingHttpServletRequest request, List<CountrySearchData> list)
    {
        /*
         * for each letter from a to z
         * find the first next country beginning with this letter.
         * insert the letter data before this.
         * continue with the next letter...
         */
        Map<Character, Integer> letterMap = new  HashMap<Character, Integer>();
        char c;

        for(c = 'A'; c <= 'Z'; ++c)
        {
            Boolean foundCountry = false;
            Iterator<CountrySearchData> it = list.iterator();
            while (it.hasNext() && !foundCountry) {
                CountrySearchData data = it.next();
                if ( data.getCountryName().startsWith(String.valueOf(c)) )
                {
                    letterMap.put(new Character(c), list.indexOf(data));
                    foundCountry = true;
                }
            }
        }

        ArrayList<Character> keys = new ArrayList<Character>(letterMap.keySet());
        for(int i=keys.size()-1; i>=0;i--){
            CountrySearchData letterdata = new CountrySearchData();
            letterdata.initAsLetter(String.valueOf(keys.get(i)));
            list.add(letterMap.get(keys.get(i)), letterdata);
        }

    }


    public static ValueMap getPositionAndRatingPolicy(SlingHttpServletRequest request) {
        log.info("getPositionAndRatingPolicy");
        ValueMap propPolicy = null;

        //get the first resource under the template structure that is the correct type and that is linked to a valid content policy.
        Resource rootInPolicyStruct;
        rootInPolicyStruct = request.getResource().getResourceResolver().resolve(request,Constants.Paths.COUTRYINFO_TEMPLATE_POLICYANDRATING + com.day.cq.wcm.foundation.AllowedComponentList.STRUCTURE_JCR_CONTENT + "root"  );

        Iterator<Resource> it = rootInPolicyStruct.listChildren();
        while (it.hasNext() && propPolicy == null )
        {
            Resource childRes = it.next();
            ValueMap val = childRes.getValueMap();
            String slingtype = val.get(org.apache.sling.jcr.resource.api.JcrResourceConstants.SLING_RESOURCE_TYPE_PROPERTY, String.class);

            if ( childRes.getName().startsWith(Constants.Paths.COUNTRYINFO_TEMPLATE_POSRATINGNAME) && Constants.Paths.COUNTRYINFO_TEMPLATE_PATH_COMPONENT.contentEquals(slingtype) )
            {
                Page currentPage = Request.adaptToPage(request);
                ContentPolicy contentPolicy = ContentPolicyUtil.resolveLocalizedContentPolicy(request.getResourceResolver(),childRes, currentPage);
                if ( contentPolicy != null )
                {
                    propPolicy = contentPolicy.getProperties();
                    break;
                }
            }
        }

        return propPolicy;
    }

    public enum SortOrder {
        ASC("asc"),
        DESC("desc");

        private String value;
        SortOrder(String value) {
            this.value = value;
        }

        public static SortOrder fromString(String value) {
            for (SortOrder s : values()) {
                if (StringUtils.equals(value, s.value)) {
                    return s;
                }
            }
            return ASC;
        }
    }

    public enum CountryFilterType {
        PREMIUM("premium"),
        NONPREMIUM("nonpremium"),
        ALL("all");

        private String value;
        CountryFilterType(String value) {
            this.value = value;
        }

        public static CountryFilterType fromString(String value) {
            for (CountryFilterType s : values()) {
                if (StringUtils.equals(value, s.value)) {
                    return s;
                }
            }
            return ALL;
        }
    }

    /**
     * Implements a {@see java.util.Comparator} Comparator that will determine how two articles should be sorted.
     */
    public static class ListSort implements Comparator<CountrySearchData>, Serializable
    {
        private static final long serialVersionUID = 204096578107748876L;
        private SortOrder sortOrder;
        private String languageAbbreviation;

        public ListSort(SortOrder sortOrder, String languageAbbreviation)
        {
            this.sortOrder = sortOrder;
            this.languageAbbreviation = languageAbbreviation;
        }

        /**
         * Compare two data pages and determine alphabetical order.
         * Use of Collator for locale-sensitive comparison
         *
         * @return A negative integer, zero, or a positive integer as the first country name is less than, equal to, or greater than the second respectively.
         */
        @Override
        public int compare(CountrySearchData country1, CountrySearchData country2)
        {
            int i = 0;
            Locale locale = new Locale(languageAbbreviation);
            Collator collator = Collator.getInstance(locale);
            collator.setStrength(Collator.PRIMARY);
            //------------------------------------------------------------------
            //------------------------------------------------------------------
            if ( country1 != null && country2 != null )
            {
                i = collator.compare(country1.getCountryName(), country2.getCountryName());
                if (sortOrder == SortOrder.DESC) {
                    i = i * -1;
                }
            }
            return i;
        }
    }

    public static List<String> getFeaturedCountriesIDs(SlingHttpServletRequest request) {

        List<String> retList = new ArrayList<>();
        Page currentPage = Request.adaptToPage(request);

        String pageLanguage = LanguageUtil.getLanguageAbbreviationFromPath(currentPage.getPath(), Constants.SupportedLanguages.ENGLISH.getLanguageAbbreviation());

        String countryFeaturedPath=Constants.Paths.COUTRYINFO_FEATUREDCOUNTRIES_COMPONENTPAGE_EN;

        if ( pageLanguage.equalsIgnoreCase("fr") )
        {
            countryFeaturedPath=  Constants.Paths.COUTRYINFO_FEATUREDCOUNTRIES_COMPONENTPAGE_FR;
        }

        String conponentPath =  countryFeaturedPath + Constants.Paths.COUNTRYINFO_PATH_COUNTRYFEATUREDLIST;
        Resource posInTemplateRsrc = null;
        posInTemplateRsrc = request.getResource().getResourceResolver().resolve(conponentPath);
        Resource resourceTags = posInTemplateRsrc.getChild(Constants.Properties.ITEMS);

        if ( resourceTags == null )
        {
            log.debug("no countries selected in the resources of the featured countries");
            return retList;
        }

        Iterator<Resource> linkResources = resourceTags.listChildren();
        while (linkResources.hasNext())
        {
            Resource linkRsc = linkResources.next();
            ValueMap linkVM = linkRsc.getValueMap();
            String tag = linkVM.get(Constants.Properties.COUNTRYFEATUREDLIST_COUNTRYTAG, String.class);
            String countryID = fetchCountryID( tag, request);
            retList.add(countryID);
        }

        return retList;
    }

    public static void orderPremiumContent(List<CountrySearchData> listCountriesData) {

        ListIterator<CountrySearchData> iter = listCountriesData.listIterator();
        List<CountrySearchData> listPrem = new ArrayList<>();

        while(iter.hasNext()){
            CountrySearchData acountry = iter.next();
            if(acountry.getPremium().equalsIgnoreCase("true"))
            {
                listPrem.add(acountry);
                iter.remove();
            }
        }

        listCountriesData.addAll(0, listPrem);
    }

    public static void removeLetters(List<CountrySearchData> listCountriesData) {

        ListIterator<CountrySearchData> iter = listCountriesData.listIterator();
        while(iter.hasNext()){
            CountrySearchData acountry = iter.next();
            if(acountry.getIsLetter())
            {
                iter.remove();
            }
        }
    }
}