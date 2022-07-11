package com.edc.edcweb.core.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.day.cq.wcm.api.policies.ContentPolicy;
import com.edc.edcweb.core.helpers.ContentPolicyUtil;
import com.edc.edcweb.core.helpers.LanguageUtil;
import com.edc.edcweb.core.helpers.constants.ConstantsAccessibility;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;
import com.edc.edcweb.core.helpers.Constants;
import com.edc.edcweb.core.helpers.CountryInfoHelper;

/**
 * @author ACN
 * @version 1.0.0
 * @since 1.0.0
 *
 *
 *        Backing Java code CountryTopTen Featured Countnries.
 *
 */

@Model(adaptables = SlingHttpServletRequest.class)
public class CountrySearch {
    private static final Logger log = LoggerFactory.getLogger(CountrySearch.class);

    @ScriptVariable
    private ValueMap properties;

    // use a map to share values to the htl component
    private Map<String, String> propertiesMap = new HashMap<>();

    @Self
    private SlingHttpServletRequest request;

    @Inject
    @Optional
    private Page currentPage;

    private List<CountrySearchData> countriesdata;

//     private Map<String, String> ratingMap = new LinkedHashMap<>();
    private Map<String, String> positionMap = new LinkedHashMap<>();
    private Map<String, String> regionMap = new LinkedHashMap<>();
    private Map<String, String> ftaMap = new LinkedHashMap<>();
    private String directionsText;
    private String resultsText;
    private String noresultsText;

    /**
     * Populates the model.
     *
     */
    @PostConstruct
    public void initModel() {
        // default implementation for model initialization not needed as all members are
        // injected.
        log.debug(" initModel  ");

        this.propertiesMap.put(Constants.Properties.COUNTRYSEARCH_FILTERTITLE,
                properties.get(Constants.Properties.COUNTRYSEARCH_FILTERTITLE, String.class));
        this.propertiesMap.put(Constants.Properties.COUNTRYSEARCH_REGIONTITLE,
                properties.get(Constants.Properties.COUNTRYSEARCH_REGIONTITLE, String.class));
        this.propertiesMap.put(Constants.Properties.COUNTRYSEARCH_POSITIONTITLE,
                properties.get(Constants.Properties.COUNTRYSEARCH_POSITIONTITLE, String.class));
        this.propertiesMap.put(Constants.Properties.COUNTRYSEARCH_RATINGTITLE,
                properties.get(Constants.Properties.COUNTRYSEARCH_RATINGTITLE, String.class));
        this.propertiesMap.put(Constants.Properties.COUNTRYSEARCH_POSITIONCOL,
                properties.get(Constants.Properties.COUNTRYSEARCH_POSITIONCOL, String.class));
        this.propertiesMap.put(Constants.Properties.COUNTRYSEARCH_RATINGCOL,
                properties.get(Constants.Properties.COUNTRYSEARCH_RATINGCOL, String.class));
        this.propertiesMap.put(Constants.Properties.LINKTARGET,
                properties.get(Constants.Properties.LINKTARGET, String.class));
        this.propertiesMap.put(Constants.Properties.COUNTRYSEARCH_MSG_NODATAFOUND,
                properties.get(Constants.Properties.COUNTRYSEARCH_MSG_NODATAFOUND, String.class));
        this.propertiesMap.put(Constants.Properties.COUNTRYSEARCH_BTN_TAPTOCLOSE,
                properties.get(Constants.Properties.COUNTRYSEARCH_BTN_TAPTOCLOSE, String.class));
        this.propertiesMap.put(Constants.Properties.COUNTRYSEARCH_BTN_CLEARALL,
                properties.get(Constants.Properties.COUNTRYSEARCH_BTN_CLEARALL, String.class));
        this.propertiesMap.put(Constants.Properties.COUNTRYSEARCH_BTN_FILTER,
                properties.get(Constants.Properties.COUNTRYSEARCH_BTN_FILTER, String.class));
        this.propertiesMap.put(Constants.Properties.COUNTRYSEARCH_POSHELPLINKTEXT,
                properties.get(Constants.Properties.COUNTRYSEARCH_POSHELPLINKTEXT, String.class));
        this.propertiesMap.put(Constants.Properties.COUNTRYSEARCH_CCCHELPLINKTEXT,
                properties.get(Constants.Properties.COUNTRYSEARCH_CCCHELPLINKTEXT, String.class));
        this.propertiesMap.put(Constants.Properties.COUNTRYSEARCH_EXITTEXT,
                properties.get(Constants.Properties.COUNTRYSEARCH_EXITTEXT, String.class));
        this.propertiesMap.put(Constants.Properties.COUNTRYSEARCH_FTATITLE,
                properties.get(Constants.Properties.COUNTRYSEARCH_FTATITLE, String.class));
        this.propertiesMap.put(Constants.Properties.COUNTRYSEARCH_FTALINKTEXT,
                properties.get(Constants.Properties.COUNTRYSEARCH_FTALINKTEXT, String.class));
        this.propertiesMap.put(Constants.Properties.COUNTRYSEARCH_FTAHELPTEXT,
                properties.get(Constants.Properties.COUNTRYSEARCH_FTAHELPTEXT, String.class));
        this.propertiesMap.put(Constants.Properties.COUNTRYSEARCH_FTAHELPTITLE,
                properties.get(Constants.Properties.COUNTRYSEARCH_FTAHELPTITLE, String.class));

        // Accessibility policies
        ContentPolicy contentPolicy = ContentPolicyUtil.resolveLocalizedContentPolicy(this.request, this.currentPage);

        if (contentPolicy != null) {
            ValueMap policy_properties = contentPolicy.getProperties();

            this.directionsText = policy_properties.get(ConstantsAccessibility.CAProperties.ACCESSIBILITY_DIRECTIONS_TEXT, String.class);
            this.resultsText = policy_properties.get(ConstantsAccessibility.CAProperties.ACCESSIBILITY_RESULTS_TEXT, String.class);
            this.noresultsText = policy_properties.get(ConstantsAccessibility.CAProperties.ACCESSIBILITY_NORESULTS_TEXT, String.class);
        }

        try {
            // get the policy of the position and rating component in the template of
            // current page.
            // ---------------------------------------------------------------------
            ValueMap positionPolicyProp = CountryInfoHelper.getPositionAndRatingPolicy(request);
            String languageAbbreviation = LanguageUtil.getLanguageAbbreviationFromPath(currentPage.getPath(), Constants.SupportedLanguages.ENGLISH.getLanguageAbbreviation());

            if (positionPolicyProp == null) {
                log.error("Error:doGet - positionPolicy == null");
                log.info("Country Search - policy error.. ");
                return;
            }

            this.propertiesMap.put(Constants.Properties.COUTRYINFO_POSITION_HELPTITLE,
                    positionPolicyProp.get(Constants.Properties.COUTRYINFO_POSITION_HELPTITLE, String.class));
            this.propertiesMap.put(Constants.Properties.COUTRYINFO_POSITION_HELPTEXT,
                    positionPolicyProp.get(Constants.Properties.COUTRYINFO_POSITION_HELPTEXT, String.class));
            this.propertiesMap.put(Constants.Properties.COUTRYINFO_RATING_HELPTITLE,
                    positionPolicyProp.get(Constants.Properties.COUTRYINFO_RATING_HELPTITLE, String.class));
            this.propertiesMap.put(Constants.Properties.COUTRYINFO_RATING_HELPTEXT,
                    positionPolicyProp.get(Constants.Properties.COUTRYINFO_RATING_HELPTEXT, String.class));

            regionMap = CountryInfoHelper.buildRegionNamesData(request);
            positionMap = CountryInfoHelper.buildPositionNamesData(request, positionPolicyProp);
        //     ratingMap = CountryInfoHelper.buildRatingNamesData(request, positionPolicyProp);
            ftaMap = CountryInfoHelper.buildFTANamesData(request);
            // 3894 - remove position 'limited information'
            positionMap.remove(Constants.Properties.COUTRYINFO_POS_LIMITED);
            // ---------------------------------------------------------------------
            countriesdata = CountryInfoHelper.buildCountrySearchData(request, positionPolicyProp,
                    CountryInfoHelper.CountryFilterType.ALL);
            CountryInfoHelper.insertLetterData(request, countriesdata);
            countriesdata.sort(new CountryInfoHelper.ListSort(CountryInfoHelper.SortOrder.ASC, languageAbbreviation));

        } catch (Exception e) {
            log.debug("initModel Exception error {}", e);
            log.error("error ", e);
        }

    }

    public Map<String, String> getPropertiesMap() {
        return this.propertiesMap;
    }

    public Map<String, String> getMapRegions() {
        return this.regionMap;
    }

    public Map<String, String> getMapPositions() {
        return this.positionMap;
    }

//     public Map<String, String> getMapRatings() {
//         return this.ratingMap;
//     }

    public Map<String, String> getMapFTA() {
    	if(this.ftaMap==null || this.ftaMap.isEmpty()) {
    		return null; 
    	}
        return this.ftaMap;
    }

    public List<CountrySearchData> getCountriesData() {
        return this.countriesdata;
    }

    public List<CountrySearchData> getCountriesPremiumData() {
        // copy the countriesdata list then order the premium and remove the letters
        List<CountrySearchData> listPremium = new ArrayList<>(this.countriesdata);
        CountryInfoHelper.orderPremiumContent(listPremium);
        CountryInfoHelper.removeLetters(listPremium);
        return listPremium;
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
}
