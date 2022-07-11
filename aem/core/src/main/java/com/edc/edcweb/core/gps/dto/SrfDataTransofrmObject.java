package com.edc.edcweb.core.gps.dto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.request.RequestParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edc.edcweb.core.gps.GpsJsonDataBindingUtil;
import com.edc.edcweb.core.gps.helpers.GpsConstants;
import com.edc.edcweb.core.gps.helpers.SrfConstants;
import com.edc.edcweb.core.gps.pojo.GpsFormFields;
import com.edc.edcweb.core.gps.pojo.GpsFormObjects;
import com.edc.edcweb.core.gps.pojo.srf.CompanyIdentifier;
import com.edc.edcweb.core.gps.pojo.srf.CompanyInformation;
import com.edc.edcweb.core.gps.pojo.srf.ContactInfo;
import com.edc.edcweb.core.gps.pojo.srf.SrfDO;
import com.edc.edcweb.core.gps.pojo.srf.SupplierInformation;
import com.edc.edcweb.core.helpers.Constants;
import com.edc.edcweb.core.helpers.LanguageUtil;
import com.edc.edcweb.core.helpers.constants.ConstantsForm;
import com.edc.edcweb.core.helpers.formvalidation.FileValidationHelper;
import com.edc.edcweb.core.helpers.formvalidation.FormCleaner;
import com.edc.edcweb.core.lovapi.helpers.LovApiConstants;
import com.edc.edcweb.core.lovapi.service.LovApiDAOService;
import com.fasterxml.jackson.core.JsonProcessingException;

public class SrfDataTransofrmObject {

    private static final Logger log = LoggerFactory.getLogger(SrfDataTransofrmObject.class);

    private SrfDataTransofrmObject() {
        // SonarQube
    }

    public static GpsFormObjects prepareFormObjects(SlingHttpServletRequest request, GpsFormFields gpsFormFields, LovApiDAOService lovApiDAOService) {
        GpsFormObjects gpsFormObjects = new GpsFormObjects();
        // set the form type
        gpsFormObjects.setFormType(SrfConstants.SRF_FORM_TYPE);
        // we want a confirmation number on the return json
        gpsFormObjects.setReturnConfNum(true);
        // check file attachments first
        Map<String, String> errorMsgs = validateAttachments(gpsFormFields.getFileFields(), lovApiDAOService);
        if (!errorMsgs.isEmpty()) {
            gpsFormObjects.setAttachmentList(null);
            gpsFormObjects.setErrorMsgs(errorMsgs);
        } else {
            // File attachments Ok, prepare the form JSON
            SrfDO srfDO = populateDO(request, gpsFormFields.getFormFields());
            // Check any messages from the Data Object
            Map<String, String> errorMsgsFromDo = srfDO.getErrorMsgs();
            if (errorMsgsFromDo.isEmpty()) {
                // No errors use the same file fields
                gpsFormObjects.setAttachmentList(gpsFormFields.getFileFields());
                /// Prepare and set the Json
                gpsFormObjects.setFormJson(prepareSrfJson(srfDO));
            } else {
                // Merge any messages from the Data Object
                gpsFormObjects.setErrorMsgs(mergeMaps(errorMsgsFromDo, gpsFormObjects.getErrorMsgs()));
            }
        }
        return gpsFormObjects;
    }

    private static Map<String, String> validateAttachments(List<RequestParameter> attachmentList, LovApiDAOService lovApiDAOService) {
        long maxUploadSize = SrfConstants.MAX_UPLOAD_SIZE;
        long maxUpladFileQty = SrfConstants.MAX_UPLOAD_FILE_QTY;

        long formTotalSize = 0;
        long formFileQty = 0;

        Map<String, String> errorMsgs = new HashMap<>();
        String errorMsg;

        for (RequestParameter param : attachmentList) {
            // Check File extension
            String fileName = param.getFileName();
            if (!FileValidationHelper.checkSRFValidFileExtension(fileName, lovApiDAOService)) {
                // extension not found on the valid list
                errorMsg = "The file type you've selected is not supported for file: ".concat(fileName);
                errorMsgs.put(param.getName(), errorMsg);
            } else {
                // Check to see if the file is bigger than expected
                long currentFileSize = param.getSize();
                if (FileValidationHelper.checkFileSizeExceeds(currentFileSize, maxUploadSize)) {
                    errorMsg = "Max file size exceeded for file ".concat(param.getFileName()).concat(": ")
                            .concat(Long.toString(currentFileSize)).concat(" of ").concat(Long.toString(maxUploadSize));
                    errorMsgs.put(param.getName(), errorMsg);
                } else {
                    // this file looks fine, keep going
                    formTotalSize += currentFileSize;
                    formFileQty++;
                }
            }
            // break if we got an error already
            if (!errorMsgs.isEmpty()) {
                break;
            }
        }
        // Check to see if the total file's size is bigger than expected
        if (formTotalSize > maxUploadSize) {
            errorMsg = "Max upload size exceeded : ".concat(Long.toString(formTotalSize)).concat(" of ")
                    .concat(Long.toString(maxUploadSize));
            errorMsgs.put("maxUploadSize", errorMsg);
        }
        // Check to see if the total file's quantity is bigger than expected
        if (formFileQty > maxUpladFileQty) {
            errorMsg = "Max number of files exceeded ".concat(Long.toString(formFileQty)).concat(" of ")
                    .concat(Long.toString(maxUpladFileQty));
            errorMsgs.put("maxUpladFileQty", errorMsg);
        }
        if (formFileQty == 0) {
            errorMsg = "Must attach a minimum of 1 file";
            errorMsgs.put("OneFileMin", errorMsg);
        }
        return errorMsgs;
    }

    private static SrfDO populateDO(SlingHttpServletRequest request, Map<String, String[]> fieldList) {
        SrfDO srfDO = new SrfDO();
        String fieldName;
        String value;
        Map<String, String> errorMsgs = new HashMap<>();

        // Get language from referrer
        String referer = request.getHeader(Constants.Properties.REFERER);
        value = LanguageUtil.getLanguageAbbreviationFromPath(referer,
                ConstantsForm.SupportedLanguages.ENGLISH.getLanguageAbbreviation()); // NOSONAR legacy code
        srfDO.setLanguage(StringUtils.upperCase(value));
        // Code of conduct
        fieldName = SrfConstants.FormFields.CODE_OF_CONDUCT_CONSENT.getFieldName();
        if (FormCleaner.getBoolean(fieldList.get(fieldName))) {
            srfDO.setCodeOfConductConsent(GpsConstants.VALUE_YES);
        } else {
            errorMsgs.put(fieldName, "This field is required");
        }
        // Add first error Messages
        srfDO.setErrorMsgs(errorMsgs);
        // ContactInfo, add only if no errors
        ContactInfo contactInfo = ppopulateContactInfo(fieldList);
        Map<String, String> subErrors = contactInfo.getErrorMsgs();
        if (subErrors.isEmpty()) {
            srfDO.setContactInfo(contactInfo);
        } else {
            srfDO.setErrorMsgs(mergeMaps(subErrors, srfDO.getErrorMsgs()));
        }
        // SupplierInformation, add only if no errors
        SupplierInformation supplierInformation = populateSupplierInformation(fieldList);
        subErrors = supplierInformation.getErrorMsgs();
        if (subErrors.isEmpty()) {
            srfDO.setSupplierInformation(supplierInformation);
        } else {
            srfDO.setErrorMsgs(mergeMaps(subErrors, srfDO.getErrorMsgs()));
        }
        // CompanyIdentifier, add only if no errors
        CompanyIdentifier companyIdentifier = populateCompanyIdentifier(fieldList,
                srfDO.getSupplierInformation().getSupplierType());
        subErrors = companyIdentifier.getErrorMsgs();
        if (subErrors.isEmpty()) {
            srfDO.setCompanyIdentifier(companyIdentifier);
        } else {
            srfDO.setErrorMsgs(mergeMaps(subErrors, srfDO.getErrorMsgs()));
        }
        // CompanyInformation, add only if no errors
        CompanyInformation companyInformation = populateCompanyInformation(fieldList,
                srfDO.getSupplierInformation().getSupplierType());
        subErrors = companyInformation.getErrorMsgs();
        if (subErrors.isEmpty()) {
            srfDO.setCompanyInformation(companyInformation);
        } else {
            srfDO.setErrorMsgs(mergeMaps(subErrors, srfDO.getErrorMsgs()));
        }

        return srfDO;
    }

    private static ContactInfo ppopulateContactInfo(Map<String, String[]> fieldList) {
        ContactInfo contactInfo = new ContactInfo();
        // FirstName
        String fieldName = SrfConstants.FormFields.FIRST_NAME.getFieldName();
        contactInfo.setFirstName(FormCleaner.cleanAll(fieldList.get(fieldName)));
        // LastName
        fieldName = SrfConstants.FormFields.LAST_NAME.getFieldName();
        contactInfo.setLastName(FormCleaner.cleanAll(fieldList.get(fieldName)));
        // EmailAddress
        fieldName = SrfConstants.FormFields.EMAIL_ADDRESS.getFieldName();
        contactInfo.setEmailAddress(FormCleaner.cleanAll(FormCleaner.cleanAll(fieldList.get(fieldName))));
        // MainPhone
        fieldName = SrfConstants.FormFields.MAIN_PHONE.getFieldName();
        contactInfo.setMainPhone(FormCleaner.cleanAll(fieldList.get(fieldName)));
        // PhoneExt
        fieldName = SrfConstants.FormFields.PHONE_EXT.getFieldName();
        contactInfo.setPhoneExt(FormCleaner.cleanAll(fieldList.get(fieldName)));
        // Fax
        fieldName = SrfConstants.FormFields.FAX.getFieldName();
        contactInfo.setFax(FormCleaner.cleanAll(fieldList.get(fieldName)));

        return contactInfo;
    }

    private static SupplierInformation populateSupplierInformation(Map<String, String[]> fieldList) {
        SupplierInformation supplierInformation = new SupplierInformation();
        // SupplierType
        String fieldName = SrfConstants.FormFields.SUPPLIER_TYPE.getFieldName();
        supplierInformation.setSupplierType(FormCleaner.cleanAll(fieldList.get(fieldName)));
        // ReferredByFirstName
        fieldName = SrfConstants.FormFields.REFERRED_BY_FIRST_NAME.getFieldName();
        supplierInformation.setReferredByFirstName(FormCleaner.cleanAll(fieldList.get(fieldName)));
        // ReferredByLastName
        fieldName = SrfConstants.FormFields.REFERRED_BY_LAST_NAME.getFieldName();
        supplierInformation.setReferredByLastName(FormCleaner.cleanAll(fieldList.get(fieldName)));
        // CorporateStatus
        fieldName = SrfConstants.FormFields.CORPORATE_STATUS.getFieldName();
        String corpStatus = FormCleaner.cleanAll(fieldList.get(fieldName));
        supplierInformation.setCorporateStatus(corpStatus);
        if (LovApiConstants.OTHER_VALUE.equals(corpStatus)) {
            // OtherCorportateStatus
            fieldName = SrfConstants.FormFields.OTHER_CORPORTATE_STATUS.getFieldName();
            supplierInformation.setOtherCorportateStatus(FormCleaner.cleanAll(fieldList.get(fieldName)));
        }
        // BusinessServices
        fieldName = SrfConstants.FormFields.BUSINESS_SERVICES.getFieldName();
        supplierInformation.setBusinessServices(FormCleaner.cleanArray(fieldList.get(fieldName)));
        // OtherBusinessServices
        fieldName = SrfConstants.FormFields.OTHER_BUSINESS_SERVICES.getFieldName();
        String otherBS = FormCleaner.cleanAll(fieldList.get(fieldName));
        supplierInformation.setOtherBusinessServices(FormCleaner.getArrayFromString(otherBS, SrfConstants.OTHER_DELIMITER));
        return supplierInformation;
    }

    private static CompanyIdentifier populateCompanyIdentifier(Map<String, String[]> fieldList, String supplierType) {
        CompanyIdentifier companyIdentifier = new CompanyIdentifier();
        String fieldName;
        // Canada Specific Values
        if (SrfConstants.SupplierType.CAN.name().equals(supplierType)) {
            // CanGstHstNumber
            fieldName = SrfConstants.FormFields.CAN_GST_HST_NUMBER.getFieldName();
            companyIdentifier
                    .setCanGstHstNumber(FormCleaner.cleanAll(fieldList.get(fieldName)));
            // CanSinNumber
            fieldName = SrfConstants.FormFields.CAN_SIN_NUMBER.getFieldName();
            companyIdentifier.setCanSinNumber(FormCleaner.cleanAll(fieldList.get(fieldName)));
        }
        // USA Specific Values
        if (SrfConstants.SupplierType.USA.name().equals(supplierType)) {
            // UsaTin
            fieldName = SrfConstants.FormFields.USA_TIN.getFieldName();
            companyIdentifier.setUsaTin(FormCleaner.cleanAll(fieldList.get(fieldName)));
        }
        // International Specific Values
        if (SrfConstants.SupplierType.INT.name().equals(supplierType)) {
            // IntBin
            fieldName = SrfConstants.FormFields.INT_BIN.getFieldName();
            companyIdentifier.setIntBin(FormCleaner.cleanAll(fieldList.get(fieldName)));
            // IntVat
            fieldName = SrfConstants.FormFields.INT_VAT.getFieldName();
            companyIdentifier.setIntVat(FormCleaner.cleanAll(fieldList.get(fieldName)));
            // IntNin
            fieldName = SrfConstants.FormFields.INT_SIN.getFieldName();
            companyIdentifier.setIntSin(FormCleaner.cleanAll(fieldList.get(fieldName)));
        }
        // RegistrationType
        fieldName = SrfConstants.FormFields.REGISTRATION_TYPE.getFieldName();
        companyIdentifier.setRegistrationType(FormCleaner.cleanAll(fieldList.get(fieldName)));
        return companyIdentifier;
    }

    private static CompanyInformation populateCompanyInformation(Map<String, String[]> fieldList, String supplierType) {
        CompanyInformation companyInformation = new CompanyInformation();
        // LegalName
        String fieldName = SrfConstants.FormFields.LEGAL_NAME.getFieldName();
        companyInformation.setLegalName(FormCleaner.cleanAll(fieldList.get(fieldName)));
        // KnownAs
        fieldName = SrfConstants.FormFields.KNOWN_AS.getFieldName();
        companyInformation.setKnownAs(FormCleaner.cleanAll(fieldList.get(fieldName)));
        // ParentCompany
        fieldName = SrfConstants.FormFields.PARENT_COMPANY.getFieldName();
        companyInformation.setParentCompany(FormCleaner.cleanAll(fieldList.get(fieldName)));
        // Website
        fieldName = SrfConstants.FormFields.WEBSITE.getFieldName();
        companyInformation.setWebsite(FormCleaner.cleanAll(fieldList.get(fieldName)));
        // CompanyCountry
        fieldName = SrfConstants.FormFields.COMPANY_COUNTRY.getFieldName();
        String companyCountry = FormCleaner.cleanAll(fieldList.get(fieldName));
        companyInformation.setCompanyCountry(companyCountry);
        // CompanyAddress1
        fieldName = SrfConstants.FormFields.COMPANY_ADDRESS_1.getFieldName();
        companyInformation.setCompanyAddress1(FormCleaner.cleanAll(fieldList.get(fieldName)));
        // CompanyAddress2
        fieldName = SrfConstants.FormFields.COMPANY_ADDRESS_2.getFieldName();
        companyInformation.setCompanyAddress2(FormCleaner.cleanAll(fieldList.get(fieldName)));
        // CompanyCity
        fieldName = SrfConstants.FormFields.COMPANY_CITY.getFieldName();
        companyInformation.setCompanyCity(FormCleaner.cleanAll(fieldList.get(fieldName)));
        // CompanyProvince
        fieldName = SrfConstants.FormFields.COMPANY_PROVINCE.getFieldName();
        companyInformation.setCompanyProvince(FormCleaner.cleanAll(fieldList.get(fieldName)));
        // CompanyPostal
        fieldName = SrfConstants.FormFields.COMPANY_POSTAL.getFieldName();
        companyInformation.setCompanyPostal(FormCleaner.cleanAll(fieldList.get(fieldName)));
        // BCorpCertification
        fieldName = SrfConstants.FormFields.B_CORP_CERTIFICATION.getFieldName();
        companyInformation.setbCorpCertification(FormCleaner.cleanAll(fieldList.get(fieldName)));
        // CertifiedDiverseSupplier
        fieldName = SrfConstants.FormFields.CERTIFIED_DIVERSE_SUPPLIER.getFieldName();
        String certifiedDiverseSupplier = FormCleaner.cleanAll(fieldList.get(fieldName));
        companyInformation.setCertifiedDiverseSupplier(certifiedDiverseSupplier);
        if (GpsConstants.VALUE_YES.equalsIgnoreCase(certifiedDiverseSupplier)) {
            // Add only if CertifiedDiverseSupplierYes is yes 
            // Bug 225774 json key name change on POJO's @JsonProperty("CertifiedDiveseSupplierType")
            fieldName = SrfConstants.FormFields.CERTIFIED_DIVERSE_SUPPLIER_YES.getFieldName();
            String[] diverseSup = FormCleaner.cleanArray(fieldList.get(fieldName));
            companyInformation.setCertifiedDiverseSupplierYes(diverseSup);
            if (ArrayUtils.contains(diverseSup, LovApiConstants.OTHER_VALUE)) {
                // OtherDiversityOrganizations
                fieldName = SrfConstants.FormFields.OTHER_DIVERSITY_ORGANIZATIONS.getFieldName();
                String otherDivOrg = FormCleaner.cleanAll(fieldList.get(fieldName));
                companyInformation.setOtherDiversityOrganizations(FormCleaner.getArrayFromString(otherDivOrg, SrfConstants.OTHER_DELIMITER));
            }
        } else {
            // Bug 225774
            // Add only if CertifiedDiverseSupplierYes is no 
            fieldName = SrfConstants.FormFields.DIVERSE_SUPPLIER.getFieldName();
            companyInformation.setDiverseSupplier(FormCleaner.cleanAll(fieldList.get(fieldName)));
        }
        // Canada Specific Values
        if (SrfConstants.SupplierType.CAN.name().equals(supplierType)) {
            // RemitNameOfEntity
            fieldName = SrfConstants.FormFields.REMIT_NAME_OF_ENTITY.getFieldName();
            companyInformation.setRemitNameOfEntity(FormCleaner.cleanAll(fieldList.get(fieldName)));
            // RemitTransitNumber
            fieldName = SrfConstants.FormFields.REMIT_TRANSIT_NUMBER.getFieldName();
            companyInformation
                    .setRemitTransitNumber(FormCleaner.cleanAll(fieldList.get(fieldName)));
            // RemitInstitutionNumber
            fieldName = SrfConstants.FormFields.REMIT_INSTITUTION_NUMBER.getFieldName();
            companyInformation
                    .setRemitInstitutionNumber(FormCleaner.cleanAll(fieldList.get(fieldName)));
            // RemitAccountNumber
            fieldName = SrfConstants.FormFields.REMIT_ACCOUNT_NUMBER.getFieldName();
            companyInformation
                    .setRemitAccountNumber(FormCleaner.cleanAll(fieldList.get(fieldName)));
            // RemitEftEmailaddress
            fieldName = SrfConstants.FormFields.REMIT_EFT_EMAILADDRESS.getFieldName();
            companyInformation.setRemitEftEmailaddress(FormCleaner.cleanAll(fieldList.get(fieldName)));
        }
        // International Specific Values
        if (SrfConstants.SupplierType.INT.name().equals(supplierType)) {
            // RemitBeneficiaryName
            fieldName = SrfConstants.FormFields.REMIT_BENEFICIARY_NAME.getFieldName();
            companyInformation.setRemitBeneficiaryName(FormCleaner.cleanAll(fieldList.get(fieldName)));
            // RemitBeneficiaryAccount
            fieldName = SrfConstants.FormFields.REMIT_BENEFICIARY_ACCOUNT.getFieldName();
            companyInformation.setRemitBeneficiaryAccount(FormCleaner.cleanAll(fieldList.get(fieldName)));
            // RemitBeneficiaryIBAN
            fieldName = SrfConstants.FormFields.REMIT_BENEFICIARY_IBAN.getFieldName();
            companyInformation.setRemitBeneficiaryIBAN(FormCleaner.cleanAll(fieldList.get(fieldName)));
            // RemitBeneficiaryCurrency
            fieldName = SrfConstants.FormFields.REMIT_BENEFICIARY_CURRENCY.getFieldName();
            companyInformation.setRemitBeneficiaryCurrency(FormCleaner.cleanAll(fieldList.get(fieldName)));
            // RemitBeneficiaryBankName
            fieldName = SrfConstants.FormFields.REMIT_BENEFICIARY_BANK_NAME.getFieldName();
            companyInformation.setRemitBeneficiaryBankName(FormCleaner.cleanAll(fieldList.get(fieldName)));
            // RemitBeneficiaryBankRoutingMethod
            fieldName = SrfConstants.FormFields.REMIT_BENEFICIARY_BANK_ROUTING_METHOD.getFieldName();
            companyInformation.setRemitBeneficiaryName(FormCleaner.cleanAll(fieldList.get(fieldName)));
            // RemitBeneficiaryIntermediaryBankName
            fieldName = SrfConstants.FormFields.REMIT_BENEFICIARY_INTERMEDIARY_BANK_NAME.getFieldName();
            companyInformation.setRemitBeneficiaryIntermediaryBankName(FormCleaner.cleanAll(fieldList.get(fieldName)));
            // RemitBeneficiaryIntermediaryBankRoutingMethod
            fieldName = SrfConstants.FormFields.REMIT_BENEFICIARY_INTERMEDIARY_BANK_ROUTING_METHOD.getFieldName();
            companyInformation.setRemitBeneficiaryIntermediaryBankRoutingMethod(FormCleaner.cleanAll(fieldList.get(fieldName)));
        }
        // RemitSameAddress
        fieldName = SrfConstants.FormFields.REMIT_SAME_ADDRESS.getFieldName();
        companyInformation.setRemitSameAddress(GpsConstants.VALUE_YES);
        if (!Boolean.parseBoolean(FormCleaner.getBooleanString(fieldList.get(fieldName)))) {
            // If not Same Address then, use provided address
            companyInformation.setRemitSameAddress(GpsConstants.VALUE_NO);
            // RemitCompanyCountry
            fieldName = SrfConstants.FormFields.REMIT_COMPANY_COUNTRY.getFieldName();
            String remitCompanyCountry = FormCleaner.cleanAll(fieldList.get(fieldName));
            companyInformation.setRemitCompanyCountry(remitCompanyCountry);
            // RemitCompanyAddress1
            fieldName = SrfConstants.FormFields.REMIT_COMPANY_ADDRESS_1.getFieldName();
            companyInformation.setRemitCompanyAddress1(FormCleaner.cleanAll(fieldList.get(fieldName)));
            // RemitCompanyAddress2
            fieldName = SrfConstants.FormFields.REMIT_COMPANY_ADDRESS_2.getFieldName();
            companyInformation.setRemitCompanyAddress2(FormCleaner.cleanAll(fieldList.get(fieldName)));
            // RemitCompanyCity
            fieldName = SrfConstants.FormFields.REMIT_COMPANY_CITY.getFieldName();
            companyInformation.setRemitCompanyCity(FormCleaner.cleanAll(fieldList.get(fieldName)));
            // RemitCompanyProvince
            fieldName = SrfConstants.FormFields.REMIT_COMPANY_PROVINCE.getFieldName();
            companyInformation.setRemitCompanyProvince(FormCleaner.cleanAll(fieldList.get(fieldName)));
            // RemitCompanyPostal
            fieldName = SrfConstants.FormFields.REMIT_COMPANY_POSTAL.getFieldName();
            companyInformation.setRemitCompanyPostal(FormCleaner.cleanAll(fieldList.get(fieldName)));
        }
        // Comments
        fieldName = SrfConstants.FormFields.COMMENTS.getFieldName();
        companyInformation.setComments(FormCleaner.cleanAll(fieldList.get(fieldName)));
        return companyInformation;
    }

    private static String prepareSrfJson(SrfDO srfDO) {
        String formJson = null;
        try {
            formJson = GpsJsonDataBindingUtil.pojoToJson(srfDO);
        } catch (JsonProcessingException e) {
            log.error("SrfDataTreansofrmObject prepareSrfJson error {}", e.getStackTrace());
        }
        return formJson;
    }

    private static Map<String, String> mergeMaps(Map<String, String> map1, Map<String, String> map2) {
        if (map1 != null) {
            map1.forEach(map2::putIfAbsent);
        }
        return map2;
    }
}
