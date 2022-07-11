package com.edc.edcweb.core.gps.dto;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edc.edcweb.core.gps.GpsJsonDataBindingUtil;
import com.edc.edcweb.core.gps.pojo.GpsFormObjects;
import com.edc.edcweb.core.gps.pojo.telp.TelpV2;
import com.edc.edcweb.core.gps.pojo.telp.TelpV2Country;
import com.edc.edcweb.core.helpers.Constants;
import com.edc.edcweb.core.helpers.LanguageUtil;
import com.edc.edcweb.core.helpers.constants.ConstantsForm;
import com.edc.edcweb.core.helpers.formvalidation.FormCleaner;
import com.edc.edcweb.core.helpers.itm.ITMConstants;
import com.edc.edcweb.core.helpers.itm.ITMHelper;
import com.edc.edcweb.core.models.FormFieldOption;
import com.edc.edcweb.core.telp.helpers.TelpConstants;
import com.edc.edcweb.core.telpv2.helpers.TelpV2Constants;
import com.fasterxml.jackson.core.JsonProcessingException;

public class TelpV2DataTransofrmObject {

    private static final Logger log = LoggerFactory.getLogger(TelpV2DataTransofrmObject.class);

    private TelpV2DataTransofrmObject() {
        // SonarQube
    }

    /**
     * Prepare Form Objects to send to the API
     * 
     * @param request
     * @return GpsFormObjects
     */
    public static GpsFormObjects prepareFormObjects(SlingHttpServletRequest request) {
        GpsFormObjects gpsFormObjects = new GpsFormObjects();
        // set the form type
        gpsFormObjects.setFormType(TelpV2Constants.TELPV2_FORM_TYPE);
        // we want a confirmation number on the return json
        gpsFormObjects.setReturnConfNum(true);
        TelpV2 telpV2 = populateDO(request);
        /// Prepare and set the Json
        gpsFormObjects.setFormJson(prepareTelv2Json(telpV2));
        return gpsFormObjects;
    }

    /**
     * populate TelpV2 Data Object
     * 
     * @param request
     * @return TelpV2 Data Object with data
     */
    private static TelpV2 populateDO(SlingHttpServletRequest request) {
        TelpV2 telpV2 = new TelpV2();
        String countryCode;
        String referer = request.getHeader(Constants.Properties.REFERER);
        String languageAbbreviation = LanguageUtil.getLanguageAbbreviationFromPath(referer,
                ConstantsForm.SupportedLanguages.ENGLISH.getLanguageAbbreviation());
        telpV2.setLanguage(languageAbbreviation.toUpperCase());
        telpV2.setFinancialInstitution(FormCleaner
                .cleanAll(request.getParameter(TelpV2Constants.FormFields.FINANCIAL_INSTITUTION.getFieldName())));
        telpV2.setBankAccountManagerFirstName(FormCleaner.cleanAll(
                request.getParameter(TelpV2Constants.FormFields.BANK_ACCOUNT_MANAGER_FIRST_NAME.getFieldName())));
        telpV2.setBankAccountManagerLastName(FormCleaner.cleanAll(
                request.getParameter(TelpV2Constants.FormFields.BANK_ACCOUNT_MANAGER_LAST_NAME.getFieldName())));
        telpV2.setBankAccountManagerEmail(FormCleaner
                .cleanAll(request.getParameter(TelpV2Constants.FormFields.BANK_ACCOUNT_MANAGER_EMAIL.getFieldName())));
        telpV2.setPrimaryContactFirstName(FormCleaner
                .cleanAll(request.getParameter(TelpV2Constants.FormFields.PRIMARY_CONTACT_FIRST_NAME.getFieldName())));
        telpV2.setPrimaryContactLastName(FormCleaner
                .cleanAll(request.getParameter(TelpV2Constants.FormFields.PRIMARY_CONTACT_LAST_NAME.getFieldName())));
        telpV2.setPrimaryContactEmail(FormCleaner
                .cleanAll(request.getParameter(TelpV2Constants.FormFields.PRIMARY_CONTACT_EMAIL.getFieldName())));
        telpV2.setPrimaryContactPhone(FormCleaner
                .cleanAll(request.getParameter(TelpV2Constants.FormFields.PRIMARY_CONTACT_PHONE.getFieldName())));
        boolean caslValue = FormCleaner
                .getBoolean(request.getParameter(TelpV2Constants.FormFields.CASL_CONSENT.getFieldName()));
        telpV2.setCaslConsent(caslValue ? TelpV2Constants.VALUE_YES : TelpV2Constants.VALUE_NO);
        telpV2.setLegalName(
                FormCleaner.cleanAll(request.getParameter(TelpV2Constants.FormFields.LEGAL_NAME.getFieldName())));
        telpV2.setCompanyTradeName(FormCleaner
                .cleanAll(request.getParameter(TelpV2Constants.FormFields.COMPANY_TRADE_NAME.getFieldName())));
        telpV2.setBusinessRegistrationNumber(FormCleaner.getInteger(
                request.getParameter(TelpV2Constants.FormFields.BUSINESS_REGISTRATION_NUMBER.getFieldName())));
        telpV2.setHeadquarterStreetAddress(FormCleaner
                .cleanAll(request.getParameter(TelpV2Constants.FormFields.HEADQUARTER_STREET_ADDRESS.getFieldName())));
        telpV2.setHeadquarterStreetAddressLine2(FormCleaner.cleanAll(
                request.getParameter(TelpV2Constants.FormFields.HEADQUARTER_STREET_ADDRESS_LINE_2.getFieldName())));
        telpV2.setHeadquarterCity(
                FormCleaner.cleanAll(request.getParameter(TelpV2Constants.FormFields.HEADQUARTER_CITY.getFieldName())));
        telpV2.setHeadquarterProvince(FormCleaner
                .cleanAll(request.getParameter(TelpV2Constants.FormFields.HEADQUARTER_PROVINCE.getFieldName())));
        telpV2.setHeadquarterPostalCode(FormCleaner
                .cleanAll(request.getParameter(TelpV2Constants.FormFields.HEADQUARTER_POSTAL_CODE.getFieldName())));
        countryCode = FormCleaner
                .cleanAll(request.getParameter(TelpConstants.FormFields.HEADQUARTER_COUNTRY.getFieldName()));
        // set the country to null, not an empty object.
        TelpV2Country hqCountry = null;
        if (StringUtils.isNotBlank(countryCode)) {
            // If there's a country selected, add the object
            hqCountry = populateTelpCountry(request, countryCode);
        }
        telpV2.setHeadquarterCountry(hqCountry);
        telpV2.setLatestAnnualSales(FormCleaner
                .getLong(request.getParameter(TelpV2Constants.FormFields.LATEST_ANNUAL_SALES.getFieldName())));
        telpV2.setFinancialYearEndMonth(FormCleaner
                .cleanAll(request.getParameter(TelpV2Constants.FormFields.FINANCIAL_YEAR_END_MONTH.getFieldName())));
        telpV2.setExporterType(
                FormCleaner.cleanAll(request.getParameter(TelpV2Constants.FormFields.EXPORTER_TYPE.getFieldName())));
        countryCode = FormCleaner
                .cleanAll(request.getParameter(TelpV2Constants.FormFields.PRIMARY_COUNTRY_OF_EXPORT.getFieldName()));
        // set the country to null, not an empty object.
        TelpV2Country exportCountry = null;
        if (StringUtils.isNotBlank(countryCode)) {
            // If there's a country selected, add the object
            exportCountry = populateTelpCountry(request, countryCode);
        }
        telpV2.setCountryOfExport(exportCountry);
        SimpleDateFormat dateformat = new SimpleDateFormat(TelpV2Constants.TELP_DATE_FORMAT);
        Date date = new Date();
        String applicationSigningDate = dateformat.format(date);
        telpV2.setApplicationSigningDate(applicationSigningDate);
        telpV2.setAuthorizedSigningAuthorityName(FormCleaner.cleanAll(
                request.getParameter(TelpV2Constants.FormFields.AUTHORIZED_SIGNING_AUTHORITY_NAME.getFieldName())));
        telpV2.setAuthorizedSigningAuthorityTitle(FormCleaner.cleanAll(
                request.getParameter(TelpV2Constants.FormFields.AUTHORIZED_SIGNING_AUTHORITY_TITLE.getFieldName())));
        telpV2.setEligibilityStatus(FormCleaner
                .cleanAll(request.getParameter(TelpV2Constants.FormFields.ELIGIBILITY_STATUS.getFieldName())));
        telpV2.setEmployees(
                FormCleaner.cleanAll(request.getParameter(TelpV2Constants.FormFields.EMPLOYEES.getFieldName())));
        String cdiaValue = FormCleaner.cleanAll(request.getParameter(TelpV2Constants.FormFields.CDIA.getFieldName()));
        telpV2.setCDIA(TelpV2Constants.CDIA_FORM_VALUE_YES.equals(cdiaValue) ? TelpV2Constants.VALUE_YES
                : TelpV2Constants.VALUE_NO);
        String itmValues = FormCleaner
                .cleanAll(request.getParameter(TelpV2Constants.FormFields.ITM_VALUE.getFieldName()));
        if (StringUtils.isNotBlank(itmValues)) {
            Map<String, List<String>> responses = ITMHelper.parseITMResponses(request, itmValues);
            telpV2.setDiversityForMajorityOwned(responses.get(ITMConstants.ITM_MAJORITY_VALUE));
            telpV2.setDiversityForMinorityOwned(responses.get(ITMConstants.ITM_MINORITY_VALUE));
            telpV2.setDiversityForLeadership(responses.get(ITMConstants.ITM_LEADERSHIP_VALUE));
        }
        return telpV2;
    }

    /**
     * populateTelpCountry populates the TelpCountry object with the selected
     * country's data
     * 
     * @param request
     * @param countryCode
     * @return TelpCountry with the data, empty TelpCountry if no data found
     */
    private static TelpV2Country populateTelpCountry(SlingHttpServletRequest request, String countryCode) {
        String[] countryCodes = new String[] { countryCode };
        List<TelpV2Country> list = populateCountryByLov(request, Constants.Paths.EDCDATA_COUNTRIES, countryCodes);
        return list.get(0);
    }

    /**
     * Populate Country By Lov
     * 
     * @param request
     * @param lovPath
     * @param countryCodes
     * @return List Of TelpV2Country
     */
    private static List<TelpV2Country> populateCountryByLov(SlingHttpServletRequest request, String lovPath,
            String[] countryCodes) {
        List<TelpV2Country> countryList = new LinkedList<>();
        FormFieldOption countryForm = null;
        Resource fieldResource = request.getResourceResolver().getResource(lovPath);
        if (fieldResource != null) {
            for (String countryCode : countryCodes) {
                Resource countryRes = fieldResource.getChild(countryCode.toLowerCase());
                if (countryRes != null) {
                    countryForm = countryRes.adaptTo(FormFieldOption.class);
                }
                if (countryForm != null) {
                    TelpV2Country telpCountry = new TelpV2Country();
                    telpCountry.setCountryCode(countryCode);
                    telpCountry.setCountryNameEn(countryForm.getEnName());
                    telpCountry.setCountryNameFr(countryForm.getFrName());

                    countryList.add(telpCountry);
                }
            }

        }
        return countryList;
    }

    /**
     * Prepare Telv2 Json
     * 
     * @param telpV2
     * @return Telv2 Json Sting to send to GPS
     */
    private static String prepareTelv2Json(TelpV2 telpV2) {
        String formJson = null;
        try {
            formJson = GpsJsonDataBindingUtil.pojoToJson(telpV2);
        } catch (JsonProcessingException e) {
            log.error("TelpV2DataTransofrmObject prepareTelv2Json error {}", e.getStackTrace());
        }
        return formJson;
    }

}
