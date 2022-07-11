package com.edc.edcportal.core.helpers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edc.edcportal.core.eloqua.EloquaConnectionManagerConstants;
import com.edc.edcportal.core.models.FormFieldOption;
import com.edc.edcportal.core.services.FieldMappingConfigService;

public class FormFieldsUtil {

    private FormFieldsUtil() {
        // empty constructor
    }

    private static final Logger log = LoggerFactory.getLogger(FormFieldsUtil.class);

    /**
     * 
     * @param request     request
     * @param dataPath
     * @param alphaOrder  boolean true or false
     * @param excludeList
     * @param topList
     * @param langAbbr    force specific language, if null will select current lang
     * @return
     */
    public static Map<String, String> getFormFieldOptionsMap(SlingHttpServletRequest request, String dataPath,
            boolean alphaOrder, String[] excludeList, String[] topList, String langAbbr) {

        ArrayList<FormFieldOption> fieldOptions = getFormFieldOptions(request, dataPath, alphaOrder, excludeList,
                topList);
        Map<String, String> retMap = new LinkedHashMap<>();
        Iterator<FormFieldOption> iterator = fieldOptions.iterator();

        if (StringUtils.isBlank(langAbbr)) {
            langAbbr = getLanguageAbbr(request);
        }

        while (iterator.hasNext()) {

            FormFieldOption anOption = iterator.next();
            String displayText = langAbbr.equalsIgnoreCase("en") ? anOption.getEnName() : anOption.getFrName();
            String eloquaValue = anOption.getValue();
            retMap.put(eloquaValue, displayText);
        }
        return retMap;
    }

    public static String[] getFormFileds(String formFields) {
        // add the Eloqua GUID field to all the forms, when importing from PP
        formFields = formFields.concat(",").concat(EloquaConnectionManagerConstants.ELOQUA_GUID_FIELD_NAME);
        // Remove any whitespace and split by comma
        return formFields.split("\\s*,\\s*");
    }

    /*
     * 
     */
    private static ArrayList<FormFieldOption> getFormFieldOptions(SlingHttpServletRequest request, String dataPath,
            boolean alphaOrder, String[] excludeList, String[] topList) {

        Resource fieldResource = request.getResourceResolver().getResource(dataPath);
        ArrayList<FormFieldOption> options = new ArrayList<>();
        ArrayList<FormFieldOption> topListOptions = new ArrayList<>();

        if (fieldResource != null) {
            Iterable<Resource> children = fieldResource.getChildren();
            Iterator<Resource> itr = children.iterator();

            while (itr.hasNext()) {
                Resource item = itr.next();
                FormFieldOption fieldOption = item.adaptTo(FormFieldOption.class);

                // exclude items in filters
                if (excludeList != null && excludeList.length > 0
                        && Arrays.asList(excludeList).contains(fieldOption.getValue())) {
                    continue;
                }

                // If option value belongs to topList, add it to 'topListOptions'
                if (fieldOption != null) {
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

    private static void alphabeticalOrder(ArrayList<FormFieldOption> selections, String langAbbr) {

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
                    optionValue1 = option1.getEnName().toUpperCase();
                    optionValue2 = option2.getEnName().toUpperCase();
                } else {
                    locale = Locale.CANADA_FRENCH;
                    optionValue1 = option1.getFrName().toUpperCase();
                    optionValue2 = option2.getFrName().toUpperCase();
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

    public static String getSelectedLabels(SlingHttpServletRequest request, String fieldName, String fieldValue,
            String lovPath, String delimiter, String resultSeparator) {
        if (StringUtils.isBlank(lovPath)) {
            lovPath = Constants.DataPaths.MYEDCDATA + fieldName;
        }
        if (StringUtils.isNotBlank(fieldValue)) {
            String[] values = fieldValue.split(delimiter);
            Map<String, String> options = FormFieldsUtil.getFormFieldOptionsMap(request, lovPath, false, null, null, "en");
            fieldValue = null;
            for (String value : values) {
                if (fieldValue == null) {
                    fieldValue = options.get(value);
                } else {
                    String optionText = options.get(value);
                    if (StringUtils.isNotBlank(optionText)) {
                        fieldValue = fieldValue.concat(resultSeparator).concat(optionText);
                    }
                }
            }
        }
        return fieldValue;
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

    
    public static String[] getFieldMappingFromConfig(String fieldName, FieldMappingConfigService fieldMappingConfigService) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
            String methodName = "get".concat(StringUtils.capitalize(fieldName)).concat("Map");
            Method method = fieldMappingConfigService.getClass().getDeclaredMethod(methodName);
            return (String[]) method.invoke(fieldMappingConfigService);
    }

}
