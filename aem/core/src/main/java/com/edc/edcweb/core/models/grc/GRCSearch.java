package com.edc.edcweb.core.models.grc;

import java.io.Serializable;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.edc.edcweb.core.helpers.LanguageUtil;
import com.edc.edcweb.core.helpers.constants.ConstantsGRC;

import com.edc.edcweb.core.models.FormFieldOptionGRC;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.policies.ContentPolicy;
import com.edc.edcweb.core.helpers.Constants;
import com.edc.edcweb.core.helpers.ContentPolicyUtil;
import com.edc.edcweb.core.helpers.CountryInfoHelper;
import com.edc.edcweb.core.helpers.LinkResolver;
import com.edc.edcweb.core.helpers.constants.ConstantsAccessibility;

/**
 * @author ACN
 * @version 1.0.0
 * @since 1.0.0
 *
 *
 * Backing Java code for GRCSearch.
 *
 */
@Model(adaptables = SlingHttpServletRequest.class)
public class GRCSearch {

    private static final Logger log = LoggerFactory.getLogger(GRCSearch.class);

    @ScriptVariable
    private ValueMap properties;

    @Inject
    @Default(values = "")
    private Resource resource;

    @Self
    private SlingHttpServletRequest request;

    @Inject
    @Optional
    private Page currentPage;

    private ArrayList<GRCCountry> countriesData;
    private ArrayList<GRCRegistry> statesRegistry;
    private GRCRegistry grcRegistry;
    private String langAbbr = "en";
    private String countryCode = "";
    private static String basePath = Constants.Paths.COUTRYINFO_BASE;
    private static String countryMapPath = Constants.Paths.COUTRYINFO_ID;
    private ArrayList<FormFieldOptionGRC> usstates;

    // Accesibility
    private String directionsText;
    private String resultsText;
    private String noresultsText;
    private Locale pageLanguage;
    private String pagePath;
    private ResourceResolver resResolver;

    /**
     * Populates the model.
     *
     */
    @PostConstruct
    public void initModel() {

        log.debug(" initModel  ");

        try {
            pageLanguage = currentPage.getLanguage();
            pagePath = currentPage.getPath();
        	resResolver = request.getResourceResolver();
            langAbbr = LanguageUtil.getLanguageAbbreviationFromPath(pagePath, Constants.SupportedLanguages.ENGLISH.getLanguageAbbreviation());

            countriesData = fetchAllCountriesNameIdUrl();
            usstates = getDropdownOptions(this.request);

            // Return data for different search mode: banner or search bar
            String displaytype = properties.get(ConstantsGRC.GRCProperties.DISPLAY_TYPE, String.class);

            if (ConstantsGRC.GRCProperties.DISPLAY_TYPE_INCONTENT.equalsIgnoreCase(displaytype)) {
                // Get country code from url selectors
                String[] pageSelectors = request.getRequestPathInfo().getSelectors();
                if (pageSelectors.length > 1
                        && pageSelectors[0].equalsIgnoreCase(ConstantsGRC.GRCProperties.GRC_VERIFICATION)) {
                    countryCode = pageSelectors[1];

                    // Generate GRCSearchData
                    populateRegistry(countryCode);
                }
            }

            // Accessibility policies
            ContentPolicy contentPolicy = ContentPolicyUtil.resolveLocalizedContentPolicy(this.request, this.currentPage);

            if (contentPolicy != null) {
                ValueMap policy_properties = contentPolicy.getProperties();

                this.directionsText = policy_properties.get(ConstantsAccessibility.CAProperties.ACCESSIBILITY_DIRECTIONS_TEXT, String.class);
                this.resultsText = policy_properties.get(ConstantsAccessibility.CAProperties.ACCESSIBILITY_RESULTS_TEXT, String.class);
                this.noresultsText = policy_properties.get(ConstantsAccessibility.CAProperties.ACCESSIBILITY_NORESULTS_TEXT, String.class);
            }

            log.debug(" initModel end");
        } catch (Exception e) {
            log.debug("initModel Exception error {}", e);
            log.error("error ", e);
        }
    }

    /**
     * Retrieve an ArrayList which include country info with format:
     * <Name>|<Code>|<TargetPageURL>
     *
     * @return ArrayList<String> All Countries with
     * <Name>|<Code>|<TargetPageURL>
     */
    private ArrayList<GRCCountry> fetchAllCountriesNameIdUrl() {

    	log.debug(" *** fetchAllCountriesNameIdUrl() start ");

        ArrayList<GRCCountry> allCountries = new ArrayList<GRCCountry>();
        HashMap<String, String> countriestagNameMap = getCountriesTagNameMap();

        String thecountryIDpath = basePath + langAbbr.toLowerCase() + countryMapPath;
        Resource countryMapResource = resResolver.getResource(thecountryIDpath);
        if (countryMapResource != null) {

            Iterator<Resource> resources = countryMapResource.listChildren();
            while (resources.hasNext()) {
                Resource res = resources.next();
                String countryCode = res.getName();
                String countryNames = "";
                String url = getSearchResultPageURL(countryCode.toLowerCase());
                GRCCountry aCountry = new GRCCountry();
                ValueMap valueMapID = res.getValueMap();
                String strCountryTagInData = valueMapID.get(Constants.Properties.COUTRYINFO_TAGPATH, String.class);
                String strCountryNameInData = strCountryTagInData != null ? strCountryTagInData.substring(strCountryTagInData.lastIndexOf("/")+1): "";

                countryNames = countriestagNameMap.get(strCountryNameInData);
                //countryNames is format of String <CountryLocaleName>#<CountryEnglishName>
                if(countryNames!=null && countryNames.length()>0 && countryNames.indexOf("#")!=-1) {
                    String[] names = countryNames.split("[#]");
                    aCountry.setCountryCode(countryCode);
                    aCountry.setCountryName(names[0]);
                    if(names.length>1) {
                        aCountry.setCountryNameEn(names[1]);
                    }
                    else {
                        //If cannot find English name, use locale name
                        aCountry.setCountryNameEn(names[0]);
                    }
                    aCountry.setUrl(url);
                    allCountries.add(aCountry);
                }
            }
        }

        //Sort
        allCountries.sort(new ListSort(langAbbr));

        log.debug(" *** fetchAllCountriesNameIdUrl() end ");
        return allCountries;
    }

    /**
     * Create a HashMap<String, String> from Taxonomy:<countryTagName, countryLocaleName#countryEnName>
     *
     * @return HashMap<String, String>
     */
    private HashMap<String, String> getCountriesTagNameMap() {

    	HashMap<String, String> countriesTagNameMap = new HashMap<String, String>();
    	Resource regionTags = resResolver.getResource(Constants.Paths.REGION_TAGS);
        Tag region_root = regionTags != null ? regionTags.adaptTo(Tag.class) : null;

        if (region_root != null) {

            Iterator<Tag> allSubTagsItor = region_root.listAllSubTags();

            while (allSubTagsItor.hasNext()) {
                Tag aCountryTag = allSubTagsItor.next();
                String subTagPath = aCountryTag.getTagID();
                String tagCountryName = subTagPath.substring(subTagPath.lastIndexOf("/")+1, subTagPath.length());
                String enCountryName = aCountryTag.getLocalizedTitle(new Locale("en"));
                countriesTagNameMap.put(tagCountryName, aCountryTag.getLocalizedTitle(pageLanguage)+"#"+enCountryName );
            }
        }

    	return countriesTagNameMap;
    }



    /**
     * By providing a country code, return the GRC search result page url.
     *
     * @param countryCode 2 characters country code, such as 'AF' or 'US'.
     * @return String GRC search result page url for given country.
     */
    private String getSearchResultPageURL(String countryCode) {

        // Target URL: en/premium/tool/global-risk-check/company.verification.{ID}.html
        String url = "";
        String baseURL = pagePath.substring(0, pagePath.indexOf('/' + langAbbr + '/'));
        baseURL += "/" + langAbbr.toLowerCase() + Constants.Paths.PREMIUM;
        if (langAbbr.equalsIgnoreCase(Constants.Properties.ENGLISH_ABBR)) {
            url = baseURL + Constants.Paths.GLOBAL_RISK_CHECK + ConstantsGRC.Paths.RESULT_PAGE_SELECTOR + "." + countryCode;
        } else {
            url = baseURL + Constants.Paths.GLOBAL_RISK_CHECK_ALIAS + ConstantsGRC.Paths.RESULT_PAGE_SELECTOR_ALIAS + "." + countryCode;
        }

        return LinkResolver.addHtmlExtension(url);
    }

    /**
     * Populate the country GRC registry info for given country code.
     */
    private void populateRegistry(String searchCountryCode) {

        Resource countryRegistries = resource.getChild(ConstantsGRC.GRCProperties.REGISTRIES);
        Iterator<Resource> registries = countryRegistries.listChildren();

        while (registries.hasNext()) {
            Resource registry = registries.next();
            ValueMap registryProperties = registry.getValueMap();
            String countryTag = registryProperties.get(ConstantsGRC.GRCProperties.REGISTRY_TAG, String.class);
            if (countryTag != null) {
                String grcCountryId = CountryInfoHelper.fetchCountryID(countryTag, request);
                if (grcCountryId.equalsIgnoreCase(searchCountryCode)) {

                    GRCRegistry aGRCRegistry = new GRCRegistry();
                    aGRCRegistry.setCode(grcCountryId);
                    aGRCRegistry.setBusRegDesc(
                            registryProperties.get(ConstantsGRC.GRCProperties.REGISTRY_DESCRIPTION, String.class));
                    aGRCRegistry.setBusRegIcon(
                            registryProperties.get(ConstantsGRC.GRCProperties.REGISTRY_ICON, String.class));
                    aGRCRegistry.setBusRegName(
                            registryProperties.get(ConstantsGRC.GRCProperties.REGISTRY_NAME, String.class));
                    aGRCRegistry.setBusRegTips(
                            registryProperties.get(ConstantsGRC.GRCProperties.REGISTRY_TIPS, String.class));
                    aGRCRegistry.setBusRegTipsTitle(
                            registryProperties.get(ConstantsGRC.GRCProperties.REGISTRY_TIPS_TITLE, String.class));
                    aGRCRegistry.setBusRegUrl(
                            registryProperties.get(ConstantsGRC.GRCProperties.REGISTRY_URL, String.class));
                    aGRCRegistry.setBusRegUrlText(
                            registryProperties.get(ConstantsGRC.GRCProperties.REGISTRY_URL_TEXT, String.class));
                    aGRCRegistry.setTag(
                            registryProperties.get(ConstantsGRC.GRCProperties.REGISTRY_TAG, String.class));

                    grcRegistry = aGRCRegistry;

                    break;

                }
            }

        }

        if (grcRegistry != null && grcRegistry.getCode() != null
                && grcRegistry.getCode().equalsIgnoreCase(ConstantsGRC.GRCProperties.GRC_COUNTRY_US)) {
            populateStatesTips();
        }
    }

    /**
     * Populate the GRC registry info for all states in US.
     */
    private void populateStatesTips() {
        Resource recStatesTips = resource.getChild(ConstantsGRC.GRCProperties.STATES_TIPS);
        Iterator<Resource> statesTipsItor = recStatesTips.listChildren();

        ArrayList<GRCRegistry> allStateTips = new ArrayList<GRCRegistry>();

        while (statesTipsItor.hasNext()) {

            GRCRegistry aGRCRegistry = new GRCRegistry();
            Resource aStateTipsResource = statesTipsItor.next();
            ValueMap stateTipsProperties = aStateTipsResource.getValueMap();

            aGRCRegistry.setBusRegName(
                    stateTipsProperties.get(ConstantsGRC.GRCProperties.REGISTRY_NAME, String.class));
            aGRCRegistry.setBusRegDesc(
                    stateTipsProperties.get(ConstantsGRC.GRCProperties.REGISTRY_DESCRIPTION, String.class));
            aGRCRegistry.setBusRegIcon(
                    stateTipsProperties.get(ConstantsGRC.GRCProperties.REGISTRY_ICON, String.class));
            aGRCRegistry.setBusRegUrl(
                    stateTipsProperties.get(ConstantsGRC.GRCProperties.REGISTRY_URL, String.class));
            aGRCRegistry.setBusRegUrlText(
                    stateTipsProperties.get(ConstantsGRC.GRCProperties.REGISTRY_URL_TEXT, String.class));
            aGRCRegistry.setBusRegTips(
                    stateTipsProperties.get(ConstantsGRC.GRCProperties.REGISTRY_TIPS, String.class));
            aGRCRegistry.setBusRegTipsTitle(
                    stateTipsProperties.get(ConstantsGRC.GRCProperties.REGISTRY_TIPS_TITLE, String.class));
            aGRCRegistry.setTag(stateTipsProperties.get(ConstantsGRC.GRCProperties.STATE, String.class));
            aGRCRegistry.setName(getStateName(stateTipsProperties.get(ConstantsGRC.GRCProperties.STATE, String.class)));

            allStateTips.add(aGRCRegistry);
        }

        statesRegistry = allStateTips;
    }

    /**
     * Retrieve localized tag name for state by providing a US state tag id
     */
    private String getStateName(String stateTagId) {

        String stateName = "";
        TagManager tagMgr = resResolver.adaptTo(TagManager.class);
        if (tagMgr != null) {
            Tag stateTag = tagMgr.resolve(stateTagId);

            if (stateTag != null) {
                Locale locale = new Locale("en");
                stateName = stateTag.getLocalizedTitle(locale);
            }
        }

        return stateName;
    }

    private static ArrayList<FormFieldOptionGRC> getDropdownOptions(SlingHttpServletRequest request){
        Resource fieldResource = request.getResourceResolver().getResource(Constants.Paths.EDCDATA_US_STATES);
        ArrayList<FormFieldOptionGRC> options = new ArrayList<>();

        if (fieldResource!= null) {
            Iterable<Resource> children = fieldResource.getChildren();
            Iterator<Resource> itr = children.iterator();

            while (itr.hasNext()) {
                Resource item = itr.next();
                FormFieldOptionGRC fieldOption = item.adaptTo(FormFieldOptionGRC.class);

                options.add(fieldOption);

            }
        }

        return options;
    }

    public ArrayList<GRCCountry> getCountriesData() {
        return this.countriesData;
    }

    public ArrayList<FormFieldOptionGRC> getUsstates() {
        return usstates;
    }

    public ArrayList<GRCRegistry> getStatesRegistry() {
        return this.statesRegistry;
    }

    public GRCRegistry getGrcRegistry() {
        return this.grcRegistry;
    }

    public String getDirectionsText() {
        return directionsText;
    }

    public String getResultsText() {
        return resultsText;
    }

    public String getNoresultsText() {
        return noresultsText;
    }

    public String getCountryCode() {
        return countryCode.toUpperCase();
    }

    public String getLangAbbr() {
        return langAbbr;
    }

    /**
     * Implements a {
     *
     * @see java.util.Comparator} Comparator that will determine how two
     * countries should be sorted.
     */
    private static class ListSort implements Comparator<GRCCountry>, Serializable {

        private static final long serialVersionUID = 204096578107748876L;
        private String languageAbbreviation;

        public ListSort(String languageAbbreviation) {
            this.languageAbbreviation = languageAbbreviation;
        }

        /**
         * Compare two countries and determine alphabetical order. Use of
         * Collator for locale-sensitive comparison
         *
         * @return A negative integer, zero, or a positive integer as the first
         * country name is less than, equal to, or greater than the second
         * respectively.
         */
        @Override
        public int compare(GRCCountry country1, GRCCountry country2) {
            int i = 0;
            Locale locale = new Locale(languageAbbreviation);
            Collator collator = Collator.getInstance(locale);
            collator.setStrength(Collator.PRIMARY);
            //------------------------------------------------------------------
            //------------------------------------------------------------------
            if (country1 != null && country2 != null) {
                i = collator.compare(country1.getCountryName(), country2.getCountryName());
            }
            return i;
        }
    }
}
