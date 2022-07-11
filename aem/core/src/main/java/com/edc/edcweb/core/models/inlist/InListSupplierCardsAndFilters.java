package com.edc.edcweb.core.models.inlist;

import com.day.cq.wcm.api.Page;
import com.edc.edcweb.core.apim.ConstantsAPIM;
import com.edc.edcweb.core.apim.pojo.SupplierCardsAndFiltersVO;
import com.edc.edcweb.core.apim.pojo.SupplierCardsAndFiltersVO.Supplier;
import com.edc.edcweb.core.apim.pojo.inlist.filter.FilterValueInIndustries;
import com.edc.edcweb.core.apim.pojo.inlist.filter.FilterValueInMartketsServed;
import com.edc.edcweb.core.apim.pojo.inlist.filter.FilterValueInModesOfTransportation;
import com.edc.edcweb.core.apim.pojo.inlist.filter.FilterValueInQuoteResponseTime;
import com.edc.edcweb.core.apim.pojo.inlist.filter.FilterValueInServiceType;
import com.edc.edcweb.core.apim.pojo.inlist.filter.FilterValueInServices;
import com.edc.edcweb.core.apim.pojo.inlist.filter.Industries;
import com.edc.edcweb.core.apim.pojo.inlist.filter.IndustriesInCard;
import com.edc.edcweb.core.apim.pojo.inlist.filter.MarketsServed;
import com.edc.edcweb.core.apim.pojo.inlist.filter.MarketsServedInCard;
import com.edc.edcweb.core.apim.pojo.inlist.filter.ServiceTypes;
import com.edc.edcweb.core.apim.pojo.inlist.filter.ServiceTypesInCard;
import com.edc.edcweb.core.apim.pojo.inlist.filter.Services;
import com.edc.edcweb.core.apim.pojo.inlist.filter.ServicesInCard;
import com.edc.edcweb.core.apim.pojo.inlist.filter.SupplierCardsAndFiltersDO;
import com.edc.edcweb.core.apim.pojo.inlist.filter.SupplierFiltersDO;
import com.edc.edcweb.core.apim.pojo.inlist.filter.SupplierShortCard;
import com.edc.edcweb.core.apim.pojo.inlist.filter.ModesOfTransportation;
import com.edc.edcweb.core.apim.pojo.inlist.filter.ModesOfTransportationInCard;
import com.edc.edcweb.core.apim.pojo.inlist.filter.QuoteResponseTimes;
import com.edc.edcweb.core.apim.pojo.inlist.filter.ReferalResponseTimes;
import com.edc.edcweb.core.apim.services.InListDAOService;
import com.edc.edcweb.core.helpers.Constants;
import com.edc.edcweb.core.helpers.LanguageUtil;
import com.edc.edcweb.core.helpers.constants.ConstantsInList;

import org.apache.sling.models.annotations.Model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Optional;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Model(adaptables = SlingHttpServletRequest.class)
public class InListSupplierCardsAndFilters {

    @Inject
    @Optional
    private Page currentPage;
    private SupplierCardsAndFiltersVO serviceSupplier;
    private String supplierProfilePath;

    private static final Logger log = LoggerFactory.getLogger(InListSupplierCardsAndFilters.class);

    private static String categoryKey_serviceType = "service-type";
    private static String categoryKey_market = "market";
    private static String categoryKey_modesTrans = "modes-trans";
    private static String categoryKey_services = "services";
    private static String categoryKey_industries = "industries";
    private static String categoryKey_responseTimes = "response-times";

    /**
     * Get the service from the InList API
     *
     * @return SupplierFilter Object
     */
    public SupplierCardsAndFiltersVO getServiceSupplier() {
        return this.serviceSupplier;
    }

    /**
     * Returns the supplier profile page path
     * 
     * @return the path
     */
    public String getSupplierProfilePath() {
        return supplierProfilePath;
    }

    @PostConstruct
    public void initModel() {
        // build the supplier profile page path based on the current language
        String pagePath = currentPage.getPath();
        String baseURL = "";
        String languageAbbreviation = LanguageUtil.getLanguageAbbreviationFromPath(pagePath,
                Constants.SupportedLanguages.ENGLISH.getLanguageAbbreviation());
        InListDAOService inListService = getInListService();

        baseURL = pagePath.substring(0, pagePath.indexOf('/' + languageAbbreviation + '/'));
        supplierProfilePath = languageAbbreviation.equalsIgnoreCase(Constants.Properties.ENGLISH_ABBR)
                ? Constants.Paths.PREMIUM + Constants.Paths.INLIST + ConstantsInList.Paths.PROVIDER_PAGE_SELECTOR
                : Constants.Paths.PREMIUM + Constants.Paths.INLIST_ALIAS
                        + ConstantsInList.Paths.PROVIDER_PAGE_SELECTOR_ALIAS;
        supplierProfilePath = baseURL + "/" + languageAbbreviation.toLowerCase() + supplierProfilePath;

        SupplierCardsAndFiltersDO supplierCardsAndFilterDo = inListService
                .getSupplierFilter(languageAbbreviation.toLowerCase());
        serviceSupplier = transformCardFilterDOtoVO(supplierCardsAndFilterDo);
    }

    // Parse the JSON response for supplier filter and cards
    private SupplierCardsAndFiltersVO transformCardFilterDOtoVO(SupplierCardsAndFiltersDO filterCardDO) {

        SupplierCardsAndFiltersVO filter = new SupplierCardsAndFiltersVO();

        try {

            if (filterCardDO != null) {
                Map<String, SupplierCardsAndFiltersVO.FilterCategory> categories = new LinkedHashMap<>();
                List<String> availableCategories = new ArrayList<>();
                String categoryTitle;
                boolean categoryMatchAll;

                SupplierFiltersDO supplierFiltersDO = filterCardDO.getFilter();
                // Filter Data
                if (supplierFiltersDO != null) {

                    // Get list of values for Service Type
                    SupplierCardsAndFiltersVO.FilterCategory serviceType = filter.new FilterCategory();
                    ServiceTypes serviceTypes = supplierFiltersDO.getServiceTypes();
                    if (serviceTypes != null) {
                        Map<String, String> categoryMap = new LinkedHashMap<String, String>();
                        categoryTitle = serviceTypes.getFilterSectionName();
                        categoryMatchAll = serviceTypes.getIsFilterMatchAll().booleanValue();
                        List<FilterValueInServiceType> filterValueInServiceType = serviceTypes.getFilterValues();
                        for (FilterValueInServiceType aFilterValueInServiceType : filterValueInServiceType) {
                            categoryMap.put(aFilterValueInServiceType.getServiceTypeId().toString(),
                                    aFilterValueInServiceType.getName());
                        }
                        serviceType.setTitle(categoryTitle);
                        serviceType.setMatchAll(categoryMatchAll);
                        serviceType.setOptions(categoryMap);
                        categories.put(ConstantsAPIM.InListJSONProperties.JSON_PROP_SERVICETYPES, serviceType);
                        availableCategories.add(ConstantsAPIM.InListJSONProperties.JSON_PROP_SERVICETYPES);
                    }

                    // Get list of values for Markets served
                    SupplierCardsAndFiltersVO.FilterCategory markets = filter.new FilterCategory();
                    MarketsServed marketsServed = supplierFiltersDO.getMarketsServed();
                    if (marketsServed != null) {
                        Map<String, String> categoryMap = new LinkedHashMap<String, String>();
                        categoryTitle = marketsServed.getFilterSectionName();
                        categoryMatchAll = marketsServed.getIsFilterMatchAll().booleanValue();
                        List<FilterValueInMartketsServed> filterValueInMartketsServed = marketsServed.getFilterValues();
                        for (FilterValueInMartketsServed aFilterValueInMartketsServed : filterValueInMartketsServed) {
                            categoryMap.put(aFilterValueInMartketsServed.getMarketId().toString(),
                                    aFilterValueInMartketsServed.getName());
                        }

                        markets.setTitle(categoryTitle);
                        markets.setMatchAll(categoryMatchAll);
                        markets.setOptions(categoryMap);
                        categories.put(ConstantsAPIM.InListJSONProperties.JSON_PROP_MARKETSSERVED, markets);
                        availableCategories.add(ConstantsAPIM.InListJSONProperties.JSON_PROP_MARKETSSERVED);
                    }

                    // Get list of values for Modes of transportation
                    SupplierCardsAndFiltersVO.FilterCategory transModes = filter.new FilterCategory();
                    ModesOfTransportation modesOfTransportation = supplierFiltersDO.getModesOfTransportation();
                    if (modesOfTransportation != null) {
                        Map<String, String> categoryMap = new LinkedHashMap<String, String>();
                        categoryTitle = modesOfTransportation.getFilterSectionName();
                        categoryMatchAll = modesOfTransportation.getIsFilterMatchAll().booleanValue();
                        List<FilterValueInModesOfTransportation> filterValueInModesOfTransportation = modesOfTransportation
                                .getFilterValues();
                        for (FilterValueInModesOfTransportation aFilterValueInModesOfTransportation : filterValueInModesOfTransportation) {
                            categoryMap.put(aFilterValueInModesOfTransportation.getServiceId().toString(),
                                    aFilterValueInModesOfTransportation.getName());
                        }

                        transModes.setTitle(categoryTitle);
                        transModes.setMatchAll(categoryMatchAll);
                        transModes.setOptions(categoryMap);
                        categories.put(ConstantsAPIM.InListJSONProperties.JSON_PROP_MODESOFTRANSPORTATION, transModes);
                        availableCategories.add(ConstantsAPIM.InListJSONProperties.JSON_PROP_MODESOFTRANSPORTATION);
                    }

                    // Get list of values for Services

                    SupplierCardsAndFiltersVO.FilterCategory services = filter.new FilterCategory();
                    Services filterServices = supplierFiltersDO.getServices();
                    if (modesOfTransportation != null) {
                        Map<String, String> categoryMap = new LinkedHashMap<String, String>();
                        categoryTitle = filterServices.getFilterSectionName();
                        categoryMatchAll = filterServices.getIsFilterMatchAll().booleanValue();
                        List<FilterValueInServices> filterValueInServices = filterServices.getFilterValues();
                        for (FilterValueInServices aFilterValueInServices : filterValueInServices) {
                            categoryMap.put(aFilterValueInServices.getServiceId().toString(),
                                    aFilterValueInServices.getName());
                        }

                        services.setTitle(categoryTitle);
                        services.setMatchAll(categoryMatchAll);
                        services.setOptions(categoryMap);
                        categories.put(ConstantsAPIM.InListJSONProperties.JSON_PROP_SERVICES, services);
                        availableCategories.add(ConstantsAPIM.InListJSONProperties.JSON_PROP_SERVICES);
                    }

                    // Get list of values for Industries

                    SupplierCardsAndFiltersVO.FilterCategory industries = filter.new FilterCategory();
                    Industries filterIndustries = supplierFiltersDO.getIndustries();
                    if (filterIndustries != null) {
                        Map<String, String> categoryMap = new LinkedHashMap<String, String>();
                        categoryTitle = filterIndustries.getFilterSectionName();
                        categoryMatchAll = filterIndustries.getIsFilterMatchAll().booleanValue();
                        List<FilterValueInIndustries> filterValueInIndustries = filterIndustries.getFilterValues();
                        for (FilterValueInIndustries aFilterValueInIndustries : filterValueInIndustries) {
                            categoryMap.put(aFilterValueInIndustries.getIndustryId().toString(),
                                    aFilterValueInIndustries.getName());
                        }

                        industries.setTitle(categoryTitle);
                        industries.setMatchAll(categoryMatchAll);
                        industries.setOptions(categoryMap);
                        categories.put(ConstantsAPIM.InListJSONProperties.JSON_PROP_INDUSTRIES, industries);
                        availableCategories.add(ConstantsAPIM.InListJSONProperties.JSON_PROP_INDUSTRIES);
                    }

                    // Get list of values for Response times

                    SupplierCardsAndFiltersVO.FilterCategory responseTime = filter.new FilterCategory();
                    QuoteResponseTimes quoteResponseTimes = supplierFiltersDO.getQuoteResponseTimes();
                    if (quoteResponseTimes != null) {
                        Map<String, String> categoryMap = new LinkedHashMap<String, String>();
                        categoryTitle = quoteResponseTimes.getFilterSectionName();
                        categoryMatchAll = quoteResponseTimes.getIsFilterMatchAll().booleanValue();
                        List<FilterValueInQuoteResponseTime> filterValueInQuoteResponseTime = quoteResponseTimes
                                .getFilterValues();
                        for (FilterValueInQuoteResponseTime aFilterValueInQuoteResponseTime : filterValueInQuoteResponseTime) {
                            categoryMap.put(aFilterValueInQuoteResponseTime.getReferalResponseTimeId().toString(),
                                    aFilterValueInQuoteResponseTime.getDisplayValue());
                        }

                        responseTime.setTitle(categoryTitle);
                        responseTime.setMatchAll(categoryMatchAll);
                        responseTime.setOptions(categoryMap);
                        categories.put(ConstantsAPIM.InListJSONProperties.JSON_PROP_FILTER_QUOTERESPONSETIMES,
                                responseTime);
                        availableCategories.add(ConstantsAPIM.InListJSONProperties.JSON_PROP_FILTER_QUOTERESPONSETIMES);
                    }

                    filter.setFilterCategories(categories);
                    filter.setAvailableCategories(availableCategories);
                }

                // Suppliers
                List<SupplierShortCard> SupplierShortCardList = filterCardDO.getSupplierShortCards();

                if (SupplierShortCardList != null) {
                    Iterator<SupplierShortCard> suppliersIterator = SupplierShortCardList.iterator();
                    List<Supplier> suppliers = new ArrayList<>();

                    // Iterate over the supplier nodes to get supplier cards details
                    while (suppliersIterator.hasNext()) {
                        SupplierShortCard aSupplierShortCard = suppliersIterator.next();
                        SupplierCardsAndFiltersVO.Supplier supplier = filter.new Supplier();

                        // Get supplier Id
                        supplier.setId(aSupplierShortCard.getSupplierId().toString());

                        // Get supplier name
                        supplier.setName(aSupplierShortCard.getSupplierName());

                        // Get supplier english name
                        supplier.setEnglishName(aSupplierShortCard.getSupplierNameEnglish() == null ? supplier.getName()
                                : aSupplierShortCard.getSupplierNameEnglish());

                        // Get supplier description
                        supplier.setBriefDescription(aSupplierShortCard.getSupplierDescription());

                        // Get supplier city
                        supplier.setCity(aSupplierShortCard.getCity());

                        // Get supplier country
                        supplier.setCountry(aSupplierShortCard.getCountry());

                        // Get supplier number of locations
                        supplier.setNumberOfLocations(aSupplierShortCard.getNumberOfOtherLocations().toString());

                        // Get response time
                        ReferalResponseTimes referalResponseTimes = aSupplierShortCard.getReferalResponseTimes();
                        List<Integer> referalResponseTimesValues = referalResponseTimes.getValues();
                        supplier.setResponseTimeId(generateUniqueIdWithCategoryName(referalResponseTimesValues,
                                categoryKey_responseTimes));

                        // Get list of IDs for service type
                        ServiceTypesInCard ServiceTypesInCard = aSupplierShortCard.getServiceTypes();
                        List<Integer> serviceTypesValues = ServiceTypesInCard.getValues();
                        supplier.setServiceTypeIds(
                                generateUniqueIdWithCategoryName(serviceTypesValues, categoryKey_serviceType));

                        // Get list of IDs for Services
                        ServicesInCard servicesInCard = aSupplierShortCard.getServices();
                        if (servicesInCard != null) {
                            // set ids
                            List<Integer> servicesInCardValues = servicesInCard.getValues();
                            supplier.setServicesIds(
                                    generateUniqueIdWithCategoryName(servicesInCardValues, categoryKey_services));

                            // Get service names by ids for displaying in cards
                            Map<String, SupplierCardsAndFiltersVO.FilterCategory> filterCategories = filter
                                    .getFilterCategories();
                            SupplierCardsAndFiltersVO.FilterCategory filterServices = filterCategories
                                    .get(ConstantsAPIM.InListJSONProperties.JSON_PROP_SERVICES);

                            if (filterServices != null) {
                                Map<String, String> servicesOptions = filterServices.getOptions();
                                List<String> serviceNames = new ArrayList<>();
                                for (Integer aId : servicesInCardValues) {
                                    String aServiceName = servicesOptions.get(aId.toString());
                                    if (aServiceName != null) {
                                        serviceNames.add(aServiceName);
                                    }
                                }
                                supplier.setServices(serviceNames.toArray((new String[0])));
                            }
                        }

                        // Get list of IDs for modes of transportation
                        ModesOfTransportationInCard modesOfTransportationInCard = aSupplierShortCard
                                .getModesOfTransportation();
                        List<Integer> modesOfTransportationInCardValues = modesOfTransportationInCard.getValues();
                        supplier.setTransportationModesIds(generateUniqueIdWithCategoryName(
                                modesOfTransportationInCardValues, categoryKey_modesTrans));

                        // Get list of IDs for markets served
                        MarketsServedInCard marketsServedInCard = aSupplierShortCard.getMarketsServed();
                        List<Integer> marketsServedInCardValues = marketsServedInCard.getValues();
                        supplier.setMarketsServedIds(
                                generateUniqueIdWithCategoryName(marketsServedInCardValues, categoryKey_market));

                        // Get list of IDs for industries
                        IndustriesInCard industriesInCard = aSupplierShortCard.getIndustries();
                        List<Integer> industriesInCardValues = industriesInCard.getValues();
                        supplier.setSupplierIndustriesIds(
                                generateUniqueIdWithCategoryName(industriesInCardValues, categoryKey_industries));

                        suppliers.add(supplier);
                    }

                    filter.setSuppliers(suppliers);
                }

            }

        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return filter;
    }

    // This method is used to add category name on ids, so final unique ids can be
    // used by FED filter
    // Sample output will be: market-1|market-3|market-5
    private String generateUniqueIdWithCategoryName(List<Integer> ids, String categoryName) {
        String result = "";
        StringJoiner strJoiner = new StringJoiner(ConstantsAPIM.InListJSONProperties.JSON_PROP_DELIMITER);
        if (ids != null && ids.size() > 0) {
            for (Integer aId : ids) {
                strJoiner.add(categoryName + "-" + aId.toString());
            }
            result = strJoiner.toString();
        }
        return result;
    }

    private InListDAOService getInListService() {
        BundleContext bundleContext = FrameworkUtil.getBundle(InListDAOService.class).getBundleContext();
        ServiceReference serviceRef = bundleContext.getServiceReference(InListDAOService.class.getName());
        InListDAOService inListService = (InListDAOService) bundleContext.getService(serviceRef);
        return inListService;
    }
}
