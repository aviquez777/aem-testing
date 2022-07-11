package com.edc.edcweb.core.helpers;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edc.edcweb.core.models.FormFieldOption;

public class FormFieldsUtil {
    
    private FormFieldsUtil () {
        // SonarQube
    }

    private static final Logger log = LoggerFactory.getLogger(FormFieldsUtil.class);
    private static String annualSalesPath = Constants.Paths.EDCDATA_ANNUALSALES;
    private static String countriesPath = Constants.Paths.EDCDATA_COUNTRIES;
    private static String empolyeesPath = Constants.Paths.EDCDATA_EMPLOYEES;
    private static String painPointsPath = Constants.Paths.EDCDATA_PAINPOINTS;
    private static String industryPath = Constants.Paths.EDCDATA_INDUSTRY;
    private static String provincePath = Constants.Paths.EDCDATA_PROVINCE;
    private static String usStatesPath = Constants.Paths.EDCDATA_US_STATES;
    private static String tradeStatusPath = Constants.Paths.EDCDATA_TRADESTATUS;
    private static String formErrorsPath = Constants.Paths.EDCDATA_FORMERRORS;
    private static String industryServedPrimaryPath = Constants.Paths.EDCDATA_INDUSTRYSERVED_PRIMARY;
    private static String industryServedSecondaryPath = Constants.Paths.EDCDATA_INDUSTRYSERVED_SECONDARY;

    // TAP Form
    private static String legalFormPath = Constants.Paths.EDCDATA_LEGALFORM;
    private static String companyProductPath = Constants.Paths.EDCDATA_COMPANYPRODUCT;
    private static String companyServicePath = Constants.Paths.EDCDATA_COMPANYSERVICE;
    private static String channelSellPath = Constants.Paths.EDCDATA_CHANNELSELL;
    private static String onlineSalesPath = Constants.Paths.EDCDATA_ONLINESALES;
    private static String exportingExperiencePath = Constants.Paths.EDCDATA_EXPORTINGEXPERIENCE;
    private static String expensesPath = Constants.Paths.EDCDATA_EXPENSES;
    private static String exportSalesPath = Constants.Paths.EDCDATA_EXPORTSALES;

    // Product inquiry Form
    private static String fiRelationshipPath = Constants.Paths.EDCDATA_FIRELATIONSHIP;
    private static String poFuturePath = Constants.Paths.EDCDATA_POFUTURE;
    private static String yearPath = Constants.Paths.EDCDATA_YEAR;
    private static String tradeStatusPIPath = Constants.Paths.EDCDATA_TRADESTATUS_PI;

    public static List<FormFieldOption> getCountriesList(SlingHttpServletRequest request) {
        return getFormFieldOptions(request, countriesPath, true, null, null);
    }

    public static List<FormFieldOption> getCountriesForAddress(SlingHttpServletRequest request) {
        String[] topList = { "CAN", "USA" };
        return getFormFieldOptions(request, countriesPath, true, null, topList);
    }

    public static List<FormFieldOption> getCountriesForMarketOfInterest(SlingHttpServletRequest request) {

        String[] excludeList = { "CAN" };
        return getFormFieldOptions(request, countriesPath, true, excludeList, null);
    }

    public static List<FormFieldOption> getAnnualSalesList(SlingHttpServletRequest request) {
        return getFormFieldOptions(request, annualSalesPath, false, null, null);
    }

    public static List<FormFieldOption> getEmployeeList(SlingHttpServletRequest request) {
        return getFormFieldOptions(request, empolyeesPath, false, null, null);
    }

    public static List<FormFieldOption> getPainPointList(SlingHttpServletRequest request) {
        return getFormFieldOptions(request, painPointsPath, false, null, null);
    }

    public static List<FormFieldOption> getUsStatesList(SlingHttpServletRequest request) {
        return getFormFieldOptions(request, usStatesPath, false, null, null);
    }

    public static List<FormFieldOption> getProvinceList(SlingHttpServletRequest request) {
        return getFormFieldOptions(request, provincePath, false, null, null);
    }

    public static List<FormFieldOption> getTradeStatusList(SlingHttpServletRequest request) {
        return getFormFieldOptions(request, tradeStatusPath, false, null, null);
    }

    public static List<FormFieldOption> getIndustryList(SlingHttpServletRequest request) {
        return getFormFieldOptions(request, industryPath, false, null, null);
    }

    public static Map<String, String> getPainPointListMap(SlingHttpServletRequest request) {
        return getFormFieldOptionsMap(request, painPointsPath, true, null, null);
    }

    public static Map<String, String> getProvinceListMap(SlingHttpServletRequest request) {
        return getFormFieldOptionsMap(request, provincePath, false, null, null);
    }

    public static Map<String, String> getTradeStatusListMap(SlingHttpServletRequest request) {
        return getFormFieldOptionsMap(request, tradeStatusPath, false, null, null);
    }

    public static Map<String, String> getIndustryListMap(SlingHttpServletRequest request) {
        return getFormFieldOptionsMap(request, industryPath, true, null, null);
    }

    public static Map<String, String> getAnnualSalesListMap(SlingHttpServletRequest request) {
        String[] excludeList = { "Don't know / Not applicable" }; // This option is to be included only for inList
                                                                  // Service Provider intake form
        return getFormFieldOptionsMap(request, annualSalesPath, false, excludeList, null);
    }

    public static Map<String, String> getEmployeeListMap(SlingHttpServletRequest request) {
        // Bug-26499 Get ordered data from source. Ordering on String will cause error
        // on numbers: '50' > '100'
        return getFormFieldOptionsMap(request, empolyeesPath, false, null, null);
    }

    public static Map<String, String> getCountriesListMap(SlingHttpServletRequest request) {
        return getFormFieldOptionsMap(request, countriesPath, true, null, null);
    }

    public static Map<String, String> getCountriesForAddressMap(SlingHttpServletRequest request) {
        String[] topList = { "CAN", "USA" };
        return getFormFieldOptionsMap(request, countriesPath, true, null, topList);
    }

    public static Map<String, String> getProvincesForAddressMap(SlingHttpServletRequest request) {
        return getFormFieldOptionsMap(request, provincePath, true, null, null);
    }

    public static Map<String, String> getUsStatesMap(SlingHttpServletRequest request) {
        return getFormFieldOptionsMap(request, usStatesPath, true, null, null);
    }

    public static Map<String, String> getCountriesForMarketOfInterestMap(SlingHttpServletRequest request) {
        String[] excludeList = { "CAN" };
        return getFormFieldOptionsMap(request, countriesPath, true, excludeList, null);
    }

    public static Map<String, String> getFormErrorsListMap(SlingHttpServletRequest request) {
        return getFormFieldOptionsMap(request, formErrorsPath, false, null, null);
    }

    public static Map<String, String> getIndustryServedPrimaryListMap(SlingHttpServletRequest request) {
        return getFormFieldOptionsMap(request, industryServedPrimaryPath, true, null, null);
    }

    public static Map<String, String> getIndustryServedSecondaryListMap(SlingHttpServletRequest request) {
        return getFormFieldOptionsMap(request, industryServedSecondaryPath, true, null, null);
    }

    // Broker RegistrationForm
    public static Map<String, String> getProvinceForBrokerRegistrationForm(SlingHttpServletRequest request) {
        return getFormFieldOptionsMap(request, provincePath, false, null, null);
    }

    // TAP Form
    public static Map<String, String> getLegalFormMap(SlingHttpServletRequest request) {
        return getFormFieldOptionsMap(request, legalFormPath, false, null, null);
    }

    public static Map<String, String> getCompanyProductMap(SlingHttpServletRequest request) {
        return getFormFieldOptionsMap(request, companyProductPath, false, null, null);
    }

    public static Map<String, String> getCompanyServiceMap(SlingHttpServletRequest request) {
        return getFormFieldOptionsMap(request, companyServicePath, false, null, null);
    }

    public static Map<String, String> getChannelSelleMap(SlingHttpServletRequest request) {
        return getFormFieldOptionsMap(request, channelSellPath, false, null, null);
    }

    public static Map<String, String> getOnlineSaleMap(SlingHttpServletRequest request) {
        return getFormFieldOptionsMap(request, onlineSalesPath, false, null, null);
    }

    public static Map<String, String> getExportingExperienceMap(SlingHttpServletRequest request) {
        return getFormFieldOptionsMap(request, exportingExperiencePath, false, null, null);
    }

    public static Map<String, String> getExpensesMap(SlingHttpServletRequest request) {
        return getFormFieldOptionsMap(request, expensesPath, true, null, null);
    }

    public static Map<String, String> getExportSalesMap(SlingHttpServletRequest request) {
        return getFormFieldOptionsMap(request, exportSalesPath, true, null, null);
    }

    public static Map<String, String> getFiRelationshipMap(SlingHttpServletRequest request) {
        return getFormFieldOptionsMap(request, fiRelationshipPath, false, null, null);
    }

    public static Map<String, String> getPoFutureMap(SlingHttpServletRequest request) {
        return getFormFieldOptionsMap(request, poFuturePath, false, null, null);
    }

    public static Map<String, String> getYearMap(SlingHttpServletRequest request) {
        return getFormFieldOptionsMap(request, yearPath, false, null, null);
    }

    public static Map<String, String> getTradeStatusPIListMap(SlingHttpServletRequest request) {
        return getFormFieldOptionsMap(request, tradeStatusPIPath, false, null, null);
    }

    // Separate the function  so it accepts the language as parameter
    public static Map<String, String> getFormFieldOptionsMap(SlingHttpServletRequest request, String dataPath,
            boolean alphaOrder, String[] excludeList, String[] topList, String langAbbr) {

        List<FormFieldOption> fieldOptions = getFormFieldOptions(request, dataPath, alphaOrder, excludeList,
                topList);
        Map<String, String> retMap = new LinkedHashMap<>();
        Iterator<FormFieldOption> iterator = fieldOptions.iterator();

        while (iterator.hasNext()) {

            FormFieldOption anOption = iterator.next();
            String displayText = langAbbr.equalsIgnoreCase("en") ? anOption.getEnName() : anOption.getFrName();
            String eloquaValue = anOption.getValue();
            retMap.put(eloquaValue, displayText);
        }
        return retMap;
    }

    // Original function which calculates language from request
    public static Map<String, String> getFormFieldOptionsMap(SlingHttpServletRequest request, String dataPath,
            boolean alphaOrder, String[] excludeList, String[] topList) {
        String langAbbr = getLanguageAbbr(request);
        // call the same function which now accepts the language as parameter
        return getFormFieldOptionsMap(request, dataPath, alphaOrder, excludeList, topList, langAbbr);
    }

    private static List<FormFieldOption> getFormFieldOptions(SlingHttpServletRequest request, String dataPath,
            boolean alphaOrder, String[] excludeList, String[] topList) {

        Resource fieldResource = request.getResourceResolver().getResource(dataPath);
        List<FormFieldOption> options = new ArrayList<>();
        List<FormFieldOption> topListOptions = new ArrayList<>();

        if (fieldResource != null) {
            Iterable<Resource> children = fieldResource.getChildren();
            Iterator<Resource> itr = children.iterator();

            while (itr.hasNext()) {
                Resource item = itr.next();
                FormFieldOption fieldOption = item.adaptTo(FormFieldOption.class);

                if (fieldOption != null) {
                    // exclude items in filters
                    if (excludeList != null && excludeList.length > 0
                            && Arrays.asList(excludeList).contains(fieldOption.getValue())) {
                        continue;
                    }

                    // If option value belongs to topList, add it to 'topListOptions'
                    if (topList == null || topList.length == 0
                            || (!Arrays.asList(topList).contains(fieldOption.getValue()))) {
                        options.add(fieldOption);
                    } else {
                        topListOptions.add(fieldOption);
                    }
                }
            }

        }

        // Sorting options based on page language
        if (alphaOrder) {
            String langAbbr = getLanguageAbbr(request);
            log.debug("page language abbrivation is {}", langAbbr);
            alphabeticalOrder(options, langAbbr);
        }

        // Add topListOptions to the top section of optionList(Maybe sorted)
        if (!topListOptions.isEmpty()) {
            topListOptions.addAll(options);
            options = topListOptions;
        }

        return options;
    }

    private static void alphabeticalOrder(List<FormFieldOption> selections, String langAbbr) {

        selections.sort(new Comparator<FormFieldOption>() {
            public int compare(FormFieldOption option1, FormFieldOption option2) {
                int comValue = 0;

                // compare if any one is null
                if (option1 == null && (option2 == null)) {
                    return 0;
                } else if (option1 == null) {
                    return -1;
                } else if (option2 == null) {
                    return 1;
                }

                String optionValue1 = "";
                String optionValue2 = "";

                Locale locale = Locale.ENGLISH;
                if (langAbbr.equalsIgnoreCase("en")) {
                    optionValue1 = option1.getEnName();
                    optionValue2 = option2.getEnName();
                } else {
                    optionValue1 = option1.getFrName();
                    optionValue2 = option2.getFrName();
                }

                if (optionValue1 == null && optionValue2 == null) {
                    comValue = 0;
                } else if (optionValue1 == null) {
                    comValue = -1;
                } else if (optionValue2 == null) {
                    comValue = 1;
                } else {
                    Collator collator = Collator.getInstance(locale);
                    comValue = collator.compare(optionValue1, optionValue2);
                }
                return comValue;

            }
        });
    }

    private static String getLanguageAbbr(SlingHttpServletRequest request) {

        String langAbbr = "";
        String url = request.getRequestURL().toString();
        langAbbr = LanguageUtil.getLanguageAbbreviationFromPath(url,
                Constants.SupportedLanguages.ENGLISH.getLanguageAbbreviation());

        return langAbbr;
    }
    
    /**
     * Get MultiSelect Options
     * 
     * @param request the SlingHttpServletRequest
     * @param lovPath the List of Values path
     * @param lang    the language to use
     * @return Map<String, Map<String, String>> with options empty list if none
     */
    public static Map<String, Map<String, String>> getMutiSelectOptions(SlingHttpServletRequest request, String lovPath,
            String lang) {
        Map<String, Map<String, String>> listOfValues = new LinkedHashMap<>();
        if (StringUtils.isNotBlank(lovPath)) {
            Resource regionsRes = request.getResourceResolver().getResource(lovPath);
            if (regionsRes != null) {
                Iterator<Resource> regionIt = regionsRes.listChildren();
                while (regionIt.hasNext()) {
                    Resource region = regionIt.next();
                    String nameVar = Constants.SupportedLanguages.ENGLISH.getLanguageAbbreviation()
                            .equalsIgnoreCase(lang) ? Constants.LOV_EN_FIELD_NAME : Constants.LOV_FR_FIELD_NAME;
                    String regionName = region.getValueMap().get(nameVar, String.class);
                    String path = region.getPath();
                    String[] topList = null;
                    if (path.contains(Constants.NORTH_AMERICA_REGION)) {
                        topList = new String[2];
                        topList[0] = Constants.CAN_COUNTRY_CODE;
                        topList[1] = Constants.USA_COUNTRY_CODE;
                    }
                    Map<String, String> countries = getFormFieldOptionsMap(request, path, true, null, topList, lang);
                    listOfValues.put(regionName, countries);
                }
            }
        }
        return listOfValues;
    }
}
