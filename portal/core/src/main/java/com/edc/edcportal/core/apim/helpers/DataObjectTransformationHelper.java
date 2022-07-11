package com.edc.edcportal.core.apim.helpers;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;

import com.edc.edcportal.core.apim.APIMConstants;
import com.edc.edcportal.core.apim.pojo.InfoPaymentDO;
import com.edc.edcportal.core.eloqua.EloquaConnectionManagerConstants;
import com.edc.edcportal.core.eloqua.pojo.EloquaDataItem;
import com.edc.edcportal.core.eloqua.pojo.EloquaUserProfileDO;
import com.edc.edcportal.core.helpers.Constants;
import com.edc.edcportal.core.helpers.FieldMapUtils;
import com.edc.edcportal.core.helpers.FormFieldsUtil;
import com.edc.edcportal.core.services.FieldMappingConfigService;

/**
 * InfoPaymentDTO Data Transformation Object to read from EloaquaDO and Output
 * InfoPaymentDO
 * 
 *
 */
public class DataObjectTransformationHelper {

    private DataObjectTransformationHelper() {
        // Sonar Lint
    }

    /**
     * populateInfoPymtDOFromEloquaUserProfileDO populate InfoPymtDO From
     * EloquaUserProfileDO
     * 
     * @param eloquaUserProfileDO       to read the data from
     * @param fieldMappingConfigService to retrieve Eloauqua's field values
     * @param request                   to retrieve the LOV nodes
     * @return
     */
    public static InfoPaymentDO populateInfoPymtDOFromEloquaUserProfileDO(EloquaUserProfileDO eloquaUserProfileDO,
            FieldMappingConfigService fieldMappingConfigService, SlingHttpServletRequest request) {

        InfoPaymentDO infoPaymentDO = new InfoPaymentDO();

        String value;
        String country;
        String lovPath = null;
        String phoneExt;

        // ExternalUserIdentifier / externalUserId
        value = getEloquaValue(fieldMappingConfigService.getExternalUserIdMap(), eloquaUserProfileDO);
        infoPaymentDO.setExternalUserId(value);

        // InformationPaymentReceivedDate / createdDateTime
        SimpleDateFormat dateformat = new SimpleDateFormat(APIMConstants.APIM_DATE_FORMAT);
        Date date = new Date();
        value = dateformat.format(date);
        infoPaymentDO.setCreatedDateTime(value);

        // InformationPaymentProfileTypeCode / profile
        value = getEloquaValue(fieldMappingConfigService.getProfileFieldIdMap(), eloquaUserProfileDO);
        infoPaymentDO.setProfile(value);

        // InformationPaymentProfileTypeEnglishDescription / profileType
        if (StringUtils.isNotBlank(value)) {
            lovPath = Constants.DataPaths.MYEDCDATA.concat(Constants.Properties.PROFILE_TYPE_FIELD);
            value = FormFieldsUtil.getSelectedLabels(request, null, value, lovPath,
                    EloquaConnectionManagerConstants.MULTI_VALUE_SEPARATOR, APIMConstants.MULTI_VALUE_SEPARATOR);
            infoPaymentDO.setProfileType(value);
        }
        // KvsProductEnglishDescription / product
        value = getEloquaValue(fieldMappingConfigService.getProductFieldIdMap(), eloquaUserProfileDO);
        infoPaymentDO.setProduct(value);

        // KvsProductCategoryEnglishDescription / productDescription
        value = getEloquaValue(fieldMappingConfigService.getProductDescriptionFieldIdMap(), eloquaUserProfileDO);
        infoPaymentDO.setProductDescription(value);

        // UserLanguageCode / language
        value = getEloquaValue(fieldMappingConfigService.getLanguageMap(), eloquaUserProfileDO);
        infoPaymentDO.setLanguage(value);

        // UserCaslConsentEnglishDescription / casl
        value = APIMConstants.USER_CASL_CONSENT;
        infoPaymentDO.setCasl(value);

        // UserMobilePhoneNumber / mobileNumber
        value = getEloquaValue(fieldMappingConfigService.getMobileNumberMap(), eloquaUserProfileDO);
        infoPaymentDO.setMobileNumber(value);

        // UserFirstName / firstName
        value = getEloquaValue(fieldMappingConfigService.getFirstNameMap(), eloquaUserProfileDO);
        infoPaymentDO.setFirstName(value);

        // UserLastName / LastName
        value = getEloquaValue(fieldMappingConfigService.getLastNameMap(), eloquaUserProfileDO);
        infoPaymentDO.setLastName(value);

        // UserEmailAddress / emailId
        value = getEloquaValue(fieldMappingConfigService.getEmailIdMap(), eloquaUserProfileDO);
        infoPaymentDO.setEmailId(value);

        // UserCaslConsentCode / caslCode
        value = APIMConstants.USER_CASL_CONSENT_CODE;
        infoPaymentDO.setCaslCode(value);

        // ContactBusinessPhoneNumber / mainPhone
        value = getEloquaValue(fieldMappingConfigService.getMainPhoneMap(), eloquaUserProfileDO);

        if (StringUtils.isNotBlank(value)) {
            // Transform to numbers only
            value = getNumbersFromValue(value);
            // Get phone extension value
            phoneExt = getEloquaValue(fieldMappingConfigService.getPhoneExtMap(), eloquaUserProfileDO);

            if (StringUtils.isNotBlank(value) && value != null) {
                value = StringUtils.isNotBlank(phoneExt) ? value.concat(APIMConstants.PHONE_EXT_PREFIX).concat(phoneExt)
                        : value;
                infoPaymentDO.setMainPhone(value);
            }
        }

        // ContactJobTitleText / title
        value = getEloquaValue(fieldMappingConfigService.getTitleMap(), eloquaUserProfileDO);
        //US #321052 If empty, might be a Financial Institution
        if (StringUtils.isBlank(value)) {
            value = getEloquaValue(fieldMappingConfigService.getFiJobRoleMap(), eloquaUserProfileDO);
        }
        infoPaymentDO.setTitle(value);

        // CompanyLegalOrganizationName / companyName
        value = getEloquaValue(fieldMappingConfigService.getCompanyNameMap(), eloquaUserProfileDO);
        // US #321052 Financial Institution Profile Updates, if other, get the other value
        if (Constants.OTHER_VALUE.equalsIgnoreCase(value)) {
            value = getEloquaValue(fieldMappingConfigService.getCompanyNameOtherMap(), eloquaUserProfileDO);
        }
        infoPaymentDO.setCompanyName(value);

        // CompanyHeadquarterAddressLine1Text / companyAddress1
        value = getEloquaValue(fieldMappingConfigService.getCompanyAddress1Map(), eloquaUserProfileDO);
        infoPaymentDO.setCompanyAddress1(value);

        // CompanyHeadquarterAddressLine2Text / companyAddress2
        value = getEloquaValue(fieldMappingConfigService.getCompanyAddress2Map(), eloquaUserProfileDO);
        infoPaymentDO.setCompanyAddress2(value);

        // CompanyHeadquarterCityText / companyCity
        value = getEloquaValue(fieldMappingConfigService.getCompanyCityMap(), eloquaUserProfileDO);
        infoPaymentDO.setCompanyCity(value);

        // CompanyHeadquarterPostalZipCode companyPostal
        value = getEloquaValue(fieldMappingConfigService.getCompanyPostalMap(), eloquaUserProfileDO);
        infoPaymentDO.setCompanyPostal(value);

        // CompanyHeadquarterCountryEdcCode / companyCountry
        country = getEloquaValue(fieldMappingConfigService.getCompanyCountryMap(), eloquaUserProfileDO);
        infoPaymentDO.setCompanyCountry(country);

        // CompanyHeadquarterCountryEnglishDescription / companyCountry
        if (StringUtils.isNotBlank(country)) {
            lovPath = Constants.DataPaths.MYEDCDATA.concat("countries");
            value = FormFieldsUtil.getSelectedLabels(request, null, country, lovPath,
                    EloquaConnectionManagerConstants.MULTI_VALUE_SEPARATOR, APIMConstants.MULTI_VALUE_SEPARATOR);
            infoPaymentDO.setCompanyCountryDesc(value);
        }
        // CompanyHeadquarterProvinceEdcCode / companyProvince
        String provinceValue = getEloquaValue(fieldMappingConfigService.getCompanyProvinceMap(), eloquaUserProfileDO);
        infoPaymentDO.setCompanyProvince(provinceValue);

        if (StringUtils.isNotBlank(country)) {
            // Select the province for Canada
            if (Constants.CAN_COUNTRY_CODE.equals(country)) {
                lovPath = Constants.DataPaths.MYEDCDATA.concat("province");
            }
            // Set the state for USA
            if (Constants.USA_COUNTRY_CODE.equals(country)) {
                lovPath = Constants.DataPaths.MYEDCDATA.concat("states");
            }
            // Select the input field if is not usa or Canada Province can be blank

            if (!Constants.CAN_COUNTRY_CODE.equals(country) && !Constants.USA_COUNTRY_CODE.equals(country)) {
                lovPath = null;
                // CompanyHeadquarterProvinceEdcCode should be null for non USA / CAN
                infoPaymentDO.setCompanyProvince(null);
            }
            if (lovPath != null) {
                provinceValue = FormFieldsUtil.getSelectedLabels(request, null, provinceValue, lovPath,
                        EloquaConnectionManagerConstants.MULTI_VALUE_SEPARATOR, APIMConstants.MULTI_VALUE_SEPARATOR);
            }
            infoPaymentDO.setCompanyProvinceDesc(provinceValue);
        }

        // CompanyEmployeeSizeRangeCode / employees
        value = getEloquaValue(fieldMappingConfigService.getEmployeesMap(), eloquaUserProfileDO);
        infoPaymentDO.setEmployees(value);

        // CompanyAnnualSalesRangeCode / annualSales
        value = getEloquaValue(fieldMappingConfigService.getAnnualSalesMap(), eloquaUserProfileDO);
        infoPaymentDO.setAnnualSales(value);

        // CompanyExportStatusCode / tradeStatus
        value = getEloquaValue(fieldMappingConfigService.getTradeStatusMap(), eloquaUserProfileDO);
        infoPaymentDO.setTradeStatus(value);

        // CompanyPrimaryIndustryCode / industry
        value = getEloquaValue(fieldMappingConfigService.getIndustryMap(), eloquaUserProfileDO);
        infoPaymentDO.setIndustry(value);

        // CompanyMarketOfInterestEdcCodeList / marketsInt
        value = getEloquaValue(fieldMappingConfigService.getMarketsIntMap(), eloquaUserProfileDO);
        //  US #321052  MOI are not present but API expects an empty array
        String[] arrVal = new String[0];
        if (StringUtils.isNotBlank(value)) {
            arrVal = StringUtils.replace(value, EloquaConnectionManagerConstants.MULTI_VALUE_SEPARATOR, Constants.COMMA)
                            .split(Constants.COMMA);
        }
        infoPaymentDO.setMarketsIntCodeList(arrVal);
        // CompanyExportChallengeCodeList / painPoint
        value = getEloquaValue(fieldMappingConfigService.getPainPointMap(), eloquaUserProfileDO);
        if (StringUtils.isNotBlank(value)) {
            infoPaymentDO.setPainPointCodeList(
                    StringUtils.replace(value, EloquaConnectionManagerConstants.MULTI_VALUE_SEPARATOR, Constants.COMMA)
                            .split(Constants.COMMA));
        }

        //US #321052  FiClientAnnualSalesRangeCode 
        value = getEloquaValue(fieldMappingConfigService.getFiClientsAnnualSalesMap(), eloquaUserProfileDO);
        infoPaymentDO.setFiClientAnnualSalesRangeCode(value);

        return infoPaymentDO;
    }

    /**
     * getEloquaValue from EloquaUserProfileDO
     * 
     * @param fieldArr            FieldMap Array to retrieve the requested value
     * @param eloquaUserProfileDO to read the data from
     * @return String with value, null if blank or none found
     */
    private static String getEloquaValue(String[] fieldArr, EloquaUserProfileDO eloquaUserProfileDO) {
        String value = null;
        String eloquaId = FieldMapUtils.getEloquaId(fieldArr);
        if (StringUtils.isNotBlank(eloquaId)) {
            EloquaDataItem dataItem = eloquaUserProfileDO.getFormFieldsValues().get(eloquaId);
            if (dataItem != null) {
                value = dataItem.getFormFieldValue();
                if (dataItem.getRequiered() && StringUtils.isBlank(value)) {
                    value = dataItem.getEloquaValue();
                }
                if (StringUtils.isBlank(value)) {
                    value = null;
                }
            }
        }
        return value;
    }

    /**
     * getNumbersFromValue from value
     * 
     * @param value to get the numbers from
     * @return String with value, null if blank or none found
     */
    private static String getNumbersFromValue(String value) {
        String numbersRegex = "(\\d+)";
        Pattern p = Pattern.compile(numbersRegex);
        Matcher m = p.matcher(value);
        String numbers = null;
        String numGroup;
        while (m.find()) {
            numGroup = m.group();
            numbers = StringUtils.isNotBlank(numbers) && numbers != null ? numbers.concat(numGroup) : numGroup;
        }
        return numbers;
    }
}
