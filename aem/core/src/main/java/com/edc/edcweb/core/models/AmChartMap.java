package com.edc.edcweb.core.models;

import com.edc.edcweb.core.helpers.Constants;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;

import org.apache.sling.models.annotations.injectorspecific.Self;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

@Model(adaptables = SlingHttpServletRequest.class)
public class AmChartMap {

    @Self
    SlingHttpServletRequest request;

    private static String FTAS = "ftas";
    private static String COUNTRIES = "countries";
    private static String COUNTRY_TITLE = Constants.Properties.COUNTRY + Constants.Properties.TITLE;
    private static String COUNTRY_DESCRIPTION = Constants.Properties.COUNTRY + Constants.Properties.DESCRIPTION;
    private static String COUNTRY_CODE = Constants.Properties.COUNTRY + Constants.Properties.CODE;

    private List<HashMap> accordionData;
    private Boolean hasValues;

    @PostConstruct
    public void initModel() {
        Resource resource = request.getResource();
        Resource resourceFtas = resource.getChild(FTAS);

        hasValues = resourceFtas != null;
        accordionData = buildAccordionData(resourceFtas);
    }

    private List<HashMap> buildAccordionData(Resource resourceFtas) {
        List<HashMap> data = new ArrayList<>();

        if (resourceFtas != null) {
            Iterator<Resource> ftas = resourceFtas.listChildren();

            while (ftas.hasNext()) {
                Resource fta = ftas.next();
                ValueMap ftaInfo = fta.getValueMap();
                HashMap<String, String> ftaMap = new HashMap<>();

                ftaMap.put(Constants.Properties.TITLE, ftaInfo.get(Constants.Properties.TITLE, String.class));
                ftaMap.put(Constants.Properties.DESCRIPTION, ftaInfo.get(Constants.Properties.DESCRIPTION, String.class));
                ftaMap.put(Constants.Properties.CODE, ftaInfo.get(Constants.Properties.TITLE, String.class));
                data.add(ftaMap);

                Resource resourceCountries = fta.getChild(COUNTRIES);

                if (resourceCountries != null) {
                    Iterator<Resource> countries = resourceCountries.listChildren();

                    while (countries.hasNext()) {
                        Resource country = countries.next();
                        ValueMap countryInfo = country.getValueMap();
                        HashMap<String, String> countryMap = new HashMap<>();

                        countryMap.put(Constants.Properties.TITLE, countryInfo.get(COUNTRY_TITLE, String.class));
                        countryMap.put(Constants.Properties.DESCRIPTION, countryInfo.get(COUNTRY_DESCRIPTION, String.class));
                        countryMap.put(Constants.Properties.CODE, ftaInfo.get(COUNTRY_CODE, String.class));
                        data.add(countryMap);
                    }
                }
            }
        }
        return data;
    }

    public List<HashMap> getAccordionData() {
        return accordionData;
    }

    public Boolean getHasValues() {
        return hasValues;
    }
}
