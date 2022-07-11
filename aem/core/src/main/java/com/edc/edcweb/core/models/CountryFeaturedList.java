package com.edc.edcweb.core.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.edc.edcweb.core.helpers.LanguageUtil;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Default;
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
 *        Backing Java code for Country Featured List
 *
 */

@Model(adaptables = SlingHttpServletRequest.class)
public class CountryFeaturedList {
    private static final Logger log = LoggerFactory.getLogger(CountryFeaturedList.class);

    @ScriptVariable
    private ValueMap properties;

    @Inject
    @Default(values = "")
    private Resource resource;

    // use a map to share values with client to save some coding
    private HashMap<String, String> propertiesMap = new HashMap<>();

    @Self
    private SlingHttpServletRequest request;

    @Inject
    @Optional
    private Page currentPage;

    private List<CountrySearchData> countriesdata;

    /**
     * Populates the model.
     *
     */
    @PostConstruct
    public void initModel() {
        // default implementation for model initialization not needed as all members are
        // injected.
        log.debug(" initModel  ");
        List<String> multiCountryTags;

        Resource resourceTags = resource.getChild(Constants.Properties.ITEMS);
        if (resourceTags == null) {
            log.debug("no country tags selected in dialog  ");
            return;
        }

        populateFromModel();

        multiCountryTags = populateCountryTags(resourceTags);

        try {
            // now we have to get the policy from the position rating component on that
            // page:)
            // ---------------------------------------------------------------------
            ValueMap positionPolicyProp = CountryInfoHelper.getPositionAndRatingPolicy(request);
            String languageAbbreviation = LanguageUtil.getLanguageAbbreviationFromPath(currentPage.getPath(), Constants.SupportedLanguages.ENGLISH.getLanguageAbbreviation());

            if (positionPolicyProp == null) {
                log.error("Error:doGet - positionPolicy == null");
                log.info("Country Featured List - policy error.. ");
                return;
            }

            // ---------------------------------------------------------------------
            countriesdata = CountryInfoHelper.buildCountryTopTenData(request, multiCountryTags, positionPolicyProp);
            countriesdata.sort(new CountryInfoHelper.ListSort(CountryInfoHelper.SortOrder.ASC, languageAbbreviation));

        } catch (Exception e) {
            log.debug("initModel Exception error {}", e);
            log.error("error ", e);
        }

    }

    private List<String> populateCountryTags(Resource resourceTags) {
        List<String> listTags = new ArrayList<>();
        // get all the child elements of 'items' i.e. items0, items1, items2
        Iterator<Resource> linkResources = resourceTags.listChildren();

        while (linkResources.hasNext()) {
            Resource linkRsc = linkResources.next();
            ValueMap linkVM = linkRsc.getValueMap();
            String tag = linkVM.get(Constants.Properties.COUNTRYFEATURED_TAG, String.class);
            listTags.add(tag);
        }
        return listTags;
    }

    private void populateFromModel() {
        ValueMap myproperties = resource.adaptTo(ValueMap.class);
        if (myproperties != null) {
            // title, text, column 1, 2, 3
            propertiesMap.put(Constants.Properties.COUNTRYFEATURED_TITLE,
                    myproperties.get(Constants.Properties.COUNTRYFEATURED_TITLE, String.class));
            propertiesMap.put(Constants.Properties.COUNTRYFEATURED_TEXT,
                    myproperties.get(Constants.Properties.COUNTRYFEATURED_TEXT, String.class));
            propertiesMap.put(Constants.Properties.COUNTRYFEATURED_CLOSE,
                    myproperties.get(Constants.Properties.COUNTRYFEATURED_CLOSE, String.class));
            propertiesMap.put(Constants.Properties.COUNTRYFEATURED_ACTIVATE,
                    myproperties.get(Constants.Properties.COUNTRYFEATURED_ACTIVATE, String.class));
            propertiesMap.put(Constants.Properties.COUNTRYFEATURED_COLCOUNTRY,
                    myproperties.get(Constants.Properties.COUNTRYFEATURED_COLCOUNTRY, String.class));
            propertiesMap.put(Constants.Properties.COUNTRYFEATURED_COLPOSITION,
                    myproperties.get(Constants.Properties.COUNTRYFEATURED_COLPOSITION, String.class));
            propertiesMap.put(Constants.Properties.COUNTRYFEATURED_COLRATING,
                    myproperties.get(Constants.Properties.COUNTRYFEATURED_COLRATING, String.class));
            propertiesMap.put(Constants.Properties.LINKTARGET,
                    myproperties.get(Constants.Properties.LINKTARGET, String.class));

        }

    }

    public Map<String, String> getPropertiesMap() {
        return this.propertiesMap;
    }

    public List<CountrySearchData> getCountriesData() {
        return this.countriesdata;
    }

}
