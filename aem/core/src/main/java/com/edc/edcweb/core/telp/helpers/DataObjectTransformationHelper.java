package com.edc.edcweb.core.telp.helpers;

import java.text.Collator;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;
import com.edc.edcweb.core.helpers.Constants;
import com.edc.edcweb.core.helpers.FormFieldsUtil;
import com.edc.edcweb.core.helpers.LanguageUtil;
import com.edc.edcweb.core.helpers.Request;
import com.edc.edcweb.core.helpers.ResourceResolverHelper;
import com.edc.edcweb.core.helpers.constants.ConstantsForm;
import com.edc.edcweb.core.helpers.constants.ConstantsQuestionnaire;
import com.edc.edcweb.core.helpers.formvalidation.FormCleaner;
import com.edc.edcweb.core.helpers.itm.ITMConstants;
import com.edc.edcweb.core.helpers.itm.ITMHelper;
import com.edc.edcweb.core.models.FormFieldOption;
import com.edc.edcweb.core.models.questionnaire.QestionnaireDO;
import com.edc.edcweb.core.models.questionnaire.pojo.Answer;
import com.edc.edcweb.core.models.questionnaire.pojo.Question;
import com.edc.edcweb.core.telp.pojo.BeneficialOwner;
import com.edc.edcweb.core.telp.pojo.MmEntryCountryOfOperation;
import com.edc.edcweb.core.telp.pojo.MmEntrySector;
import com.edc.edcweb.core.telp.pojo.TelpCountry;
import com.edc.edcweb.core.telp.pojo.TelpFormDOPart1;
import com.edc.edcweb.core.telp.pojo.TelpFormDOPart2;

/**
 * Transforms data between questionnaire node to QestionnaireDO Transforms from
 * posted form data to TelpFormDO
 *
 */
public class DataObjectTransformationHelper {

    private DataObjectTransformationHelper() {
        // Sonar Lint
    }

    private static final Logger log = LoggerFactory.getLogger(DataObjectTransformationHelper.class);

    /**
     * Transforms data between questionnaire node to QestionnaireDO
     * 
     * @param request  to resolve nodes
     * @param formType
     * @return QestionnaireDO
     */
    public static QestionnaireDO populateTelpQuestionnaire(SlingHttpServletRequest request, String nodeName) {
        QestionnaireDO qestionnaireDO = new QestionnaireDO();
        List<Question> questionList = new LinkedList<>();
        // Get currentPage from referrer
        Resource pageRes = Request.getCurrentPageResource(request, null);
        Page currentPage = pageRes.adaptTo(Page.class);
        // Find the questionnaire
        Resource qustionaireRoot = ResourceResolverHelper.getResourceByTypeAndNode(currentPage,
                ConstantsQuestionnaire.QUESTIONNAIRE_RESOURCE_TYPE, nodeName);

        if (qustionaireRoot != null) {
            Resource qustionaireRes = qustionaireRoot
                    .getChild(ConstantsQuestionnaire.Properties.QUESTIONS.getProperty());
            Iterator<Resource> questionsItRes = qustionaireRes.listChildren();
            // Iterate over the questions
            while (questionsItRes.hasNext()) {
                Resource questionRes = questionsItRes.next();
                Question question = new Question();
                List<Answer> answerList = new LinkedList<>();
                question.setNumber(questionRes.getValueMap()
                        .get(ConstantsQuestionnaire.Properties.QUESTION_NUMBER.getProperty(), String.class));
                question.setMainText(questionRes.getValueMap()
                        .get(ConstantsQuestionnaire.Properties.MAIN_TEXT.getProperty(), String.class));
                question.setAnswersPlaceholder(questionRes.getValueMap()
                        .get(ConstantsQuestionnaire.Properties.ANSWER_PLACEEHOLDER.getProperty(), String.class));
                String saveName = questionRes.getValueMap()
                        .get(ConstantsQuestionnaire.Properties.SAVE_NAME.getProperty(), String.class);
                if (StringUtils.isNotEmpty(saveName)) {
                    question.setSaveName(saveName);
                    question.setSaveAnswer(true);
                }
                question.setSecondaryText(questionRes.getValueMap()
                        .get(ConstantsQuestionnaire.Properties.SECONDARY_TEXT.getProperty(), String.class));
                question.setHelperTitle(questionRes.getValueMap()
                        .get(ConstantsQuestionnaire.Properties.HELPER_TITLE.getProperty(), String.class));
                question.setHelperText(questionRes.getValueMap()
                        .get(ConstantsQuestionnaire.Properties.HELPER_TEXT.getProperty(), String.class));
                question.setIsMulti(questionRes.getValueMap()
                        .get(ConstantsQuestionnaire.Properties.IS_MULTIPLE.getProperty(), Boolean.class));
                Integer maxSelections = questionRes.getValueMap()
                        .get(ConstantsQuestionnaire.Properties.MAX_SELECTIONS.getProperty(), Integer.class);
                if (maxSelections != null && maxSelections > 0) {
                    question.setMaxSelections(maxSelections);
                }
                String answerType = questionRes.getValueMap()
                        .get(ConstantsQuestionnaire.Properties.ANSWER_TYPE.getProperty(), String.class);
                // Check answer type and proceed accordingly
                switch (answerType) {
                case ConstantsQuestionnaire.YES_NO:
                    // Yes No answer from multifield
                    answerList = addYesNoAnswers(questionRes);
                    break;

                case ConstantsQuestionnaire.DROP_DOWN:
                    Iterator<Resource> answersItRoot = questionRes.listChildren();
                    while (answersItRoot.hasNext()) {
                        Iterator<Resource> answerItRes = answersItRoot.next().listChildren();
                        answerList = addAnswers(answerItRes);
                    }
                    break;

                case ConstantsQuestionnaire.EXPORT_COUNTRY:
                    // Get referer and languageAbbreviation for LOVs
                    String referer = request.getHeader(Constants.Properties.REFERER);
                    String languageAbbreviation = LanguageUtil.getLanguageAbbreviationFromPath(referer,
                            ConstantsForm.SupportedLanguages.ENGLISH.getLanguageAbbreviation());
                    // Use Global constant as the node is used for others
                    String lovPath = Constants.Paths.COUTRYINFO_BASE.concat(languageAbbreviation)
                            .concat(StringUtils.removeEnd(Constants.Paths.COUTRYINFO_INFO, "/"));
                    Boolean forceElegible = Boolean.parseBoolean(questionRes.getValueMap()
                            .get(ConstantsQuestionnaire.Properties.FORCE_ELGIBLE.getProperty(), String.class));
                    answerList = getLovData(lovPath, request, currentPage, null, forceElegible, answerType);
                    break;

                case ConstantsQuestionnaire.OPERATION_COUNTRIES:
                case ConstantsQuestionnaire.ENTRY_SECTOR:
                    String placeholderVar = answerType
                            .concat(ConstantsQuestionnaire.Properties.LOV_PLACEHOLDER.getProperty());
                    question.setAnswersPlaceholder(questionRes.getValueMap().get(placeholderVar, String.class));
                    String otherlovPath = Constants.Paths.EDCDATA_BASE.concat(answerType);
                    String goToAnswerVar = answerType.concat(ConstantsQuestionnaire.Properties.LOV_GO_TO.getProperty());
                    String goToAnswer = questionRes.getValueMap().get(goToAnswerVar, String.class);
                    answerList = getLovData(otherlovPath, request, currentPage, goToAnswer, false, answerType);
                    break;
                default:
                    break;
                }
                // Add data to question object
                question.setAnswers(answerList);
                questionList.add(question);
            }
        }
        // add the question list to main data object
        qestionnaireDO.setQuestions(questionList);
        return qestionnaireDO;
    }

    /**
     * Transforms from posted form data to TelpFormDO
     * 
     * @param request to get the form parameters
     * @return TelpFormDO
     */
    public static TelpFormDOPart1 populateTelpForm1FromPostedData(SlingHttpServletRequest request, String formType) {
        TelpFormDOPart1 part1 = new TelpFormDOPart1();
        // 77621 Send TELP API the Language of the Form
        String referer = request.getHeader(Constants.Properties.REFERER);
        String languageAbbreviation = LanguageUtil.getLanguageAbbreviationFromPath(referer,
                ConstantsForm.SupportedLanguages.ENGLISH.getLanguageAbbreviation());
        part1.setLanguage(languageAbbreviation.toUpperCase());
        part1.setFinancialInstitution(FormCleaner
                .cleanAll(request.getParameter(TelpConstants.FormFields.FINANCIAL_INSTITUTION.getFieldName())));
        part1.setBankAccountManagerFirstName(FormCleaner.cleanAll(
                request.getParameter(TelpConstants.FormFields.BANK_ACCOUNT_MANAGER_FIRST_NAME.getFieldName())));
        part1.setBankAccountManagerLastName(FormCleaner.cleanAll(
                request.getParameter(TelpConstants.FormFields.BANK_ACCOUNT_MANAGER_LAST_NAME.getFieldName())));
        part1.setBankAccountManagerEmail(FormCleaner
                .cleanAll(request.getParameter(TelpConstants.FormFields.BANK_ACCOUNT_MANAGER_EMAIL.getFieldName())));
        part1.setPrimaryContactFirstName(FormCleaner
                .cleanAll(request.getParameter(TelpConstants.FormFields.PRIMARY_CONTACT_FIRST_NAME.getFieldName())));
        part1.setPrimaryContactLastName(FormCleaner
                .cleanAll(request.getParameter(TelpConstants.FormFields.PRIMARY_CONTACT_LAST_NAME.getFieldName())));
        part1.setPrimaryContactEmail(FormCleaner
                .cleanAll(request.getParameter(TelpConstants.FormFields.PRIMARY_CONTACT_EMAIL.getFieldName())));
        part1.setPrimaryContactPhone(FormCleaner
                .cleanAll(request.getParameter(TelpConstants.FormFields.PRIMARY_CONTACT_PHONE.getFieldName())));
        boolean caslValue = FormCleaner
                .getBoolean(request.getParameter(TelpConstants.FormFields.CASL_CONSENT.getFieldName()));
        part1.setCaslConsent(caslValue ? TelpConstants.VALUE_YES : TelpConstants.VALUE_NO);
        part1.setLegalName(
                FormCleaner.cleanAll(request.getParameter(TelpConstants.FormFields.LEGAL_NAME.getFieldName())));
        part1.setCompanyTradeName(
                FormCleaner.cleanAll(request.getParameter(TelpConstants.FormFields.COMPANY_TRADE_NAME.getFieldName())));
        part1.setBusinessRegistrationNumber(FormCleaner.getInteger(
                request.getParameter(TelpConstants.FormFields.BUSINESS_REGISTRATION_NUMBER.getFieldName())));
        // This is the fun part Get the UBOS
        int uboQty = FormCleaner
                .getInteger(request.getParameter(TelpConstants.FormFields.ULTIMATE_BENEFICIAL_OWNERS.getFieldName()));
        List<BeneficialOwner> beneficialOwners = new LinkedList<>();
        for (int i = 1; uboQty >= i; i++) {
            BeneficialOwner beneficialOwner = new BeneficialOwner();
            String uboFirst = FormCleaner
                    .cleanAll(request.getParameter(TelpConstants.FormFields.UBO_FIRST_NAME.getFieldName() + i));
            String uboLast = FormCleaner
                    .cleanAll(request.getParameter(TelpConstants.FormFields.UBO_LAST_NAME.getFieldName() + i));
            String uboCountryCode = FormCleaner
                    .cleanAll(request.getParameter(TelpConstants.FormFields.UBO_RESIDENCE_COUNTRY.getFieldName() + i));
            // Check if we have all the data
            if (StringUtils.isNotBlank(uboFirst) && StringUtils.isNotBlank(uboLast)
                    && StringUtils.isNotBlank(uboCountryCode)) {
                beneficialOwner.setUboFirstName(uboFirst);
                beneficialOwner.setUboLastName(uboLast);
                TelpCountry uboCountry = populateTelpCountry(request, uboCountryCode);
                beneficialOwner.setUboCountry(uboCountry);
                beneficialOwners.add(beneficialOwner);
            }
        }
        part1.setBeneficialOwners(beneficialOwners);
        // Task #118283 Submit new fields only if not TELP ("COVIDR-E" or COVIDR-D")
        if (!TelpConstants.DEFAULT_FORM_TYPE.equals(formType) && !TelpConstants.BCAP_EXT_FORM_TYPE.equals(formType)) {
            String womenOwnedOrLedCode = FormCleaner
                    .cleanAll(request.getParameter(TelpConstants.FormFields.WOMEN_OWNED_OR_LED_CODE.getFieldName()));
            if (StringUtils.isBlank(womenOwnedOrLedCode)) {
                womenOwnedOrLedCode = TelpConstants.NOT_ANSWERED;
            }
            part1.setWomenOwnedOrLedCode(womenOwnedOrLedCode);
            String indigenousOwnedOrLedCode = FormCleaner.cleanAll(
                    request.getParameter(TelpConstants.FormFields.INDIGENOUS_OWNED_OR_LED_CODE.getFieldName()));
            if (StringUtils.isBlank(indigenousOwnedOrLedCode)) {
                indigenousOwnedOrLedCode = TelpConstants.NOT_ANSWERED;
            }
            part1.setIndigenousOwnedOrLedCode(indigenousOwnedOrLedCode);
        }
        if (TelpConstants.BCAP_EXT_FORM_TYPE.equals(formType)) {
            String itmValues = FormCleaner
                    .cleanAll(request.getParameter(TelpConstants.FormFields.ITM_VALUE.getFieldName()));
            if (StringUtils.isNotBlank(itmValues)) {
                Map<String, List<String>> responses = ITMHelper.parseITMResponses(request, itmValues);
                part1.setDiversityForMajorityOwned(responses.get(ITMConstants.ITM_MAJORITY_VALUE));
                part1.setDiversityForMinorityOwned(responses.get(ITMConstants.ITM_MINORITY_VALUE));
                part1.setDiversityForLeadership(responses.get(ITMConstants.ITM_LEADERSHIP_VALUE));
            }
        }
        return part1;
    }

    public static TelpFormDOPart2 populateTelpForm2FromPostedData(SlingHttpServletRequest request, String formType) {
        TelpFormDOPart2 part2 = new TelpFormDOPart2();
        String countryCode;
        String ceoCountryCode;
        part2.setHeadquarterStreetAddress(FormCleaner
                .cleanAll(request.getParameter(TelpConstants.FormFields.HEADQUARTER_STREET_ADDRESS.getFieldName())));
        part2.setHeadquarterStreetAddressLine2(FormCleaner.cleanAll(
                request.getParameter(TelpConstants.FormFields.HEADQUARTER_STREET_ADDRESS_LINE_2.getFieldName())));
        part2.setHeadquarterCity(
                FormCleaner.cleanAll(request.getParameter(TelpConstants.FormFields.HEADQUARTER_CITY.getFieldName())));
        part2.setHeadquarterProvince(FormCleaner
                .cleanAll(request.getParameter(TelpConstants.FormFields.HEADQUARTER_PROVINCE.getFieldName())));
        part2.setHeadquarterPostalCode(FormCleaner
                .cleanAll(request.getParameter(TelpConstants.FormFields.HEADQUARTER_POSTAL_CODE.getFieldName())));
        countryCode = FormCleaner
                .cleanAll(request.getParameter(TelpConstants.FormFields.HEADQUARTER_COUNTRY.getFieldName()));
        // set the country to null, not an empty object.
        TelpCountry hqCountry = null;
        if (StringUtils.isNotBlank(countryCode)) {
            // If there's a country selected, add the object
            hqCountry = populateTelpCountry(request, countryCode);
        }
        part2.setHeadquarterCountry(hqCountry);
        part2.setChiefExecutiveOfficerFirstName(FormCleaner.cleanAll(
                request.getParameter(TelpConstants.FormFields.CHIEF_EXECUTIVE_OFFICER_FIRST_NAME.getFieldName())));
        part2.setChiefExecutiveOfficerLastName(FormCleaner.cleanAll(
                request.getParameter(TelpConstants.FormFields.CHIEF_EXECUTIVE_OFFICER_LAST_NAME.getFieldName())));
        ceoCountryCode = FormCleaner.cleanAll(
                request.getParameter(TelpConstants.FormFields.CHIEF_EXECUTIVE_OFFICER_COUNTRY.getFieldName()));
        // set the country to null, not an empty object.
        TelpCountry ceoCountry = null;
        if (StringUtils.isNotBlank(ceoCountryCode)) {
            // If there's a country selected, add the object
            ceoCountry = populateTelpCountry(request, ceoCountryCode);
        }
        part2.setChiefExecutiveOfficerCountryOfResidence(ceoCountry);
        part2.setLatestAnnualSales(FormCleaner
                .getLong(request.getParameter(TelpConstants.FormFields.LATEST_ANNUAL_SALES.getFieldName())));
        part2.setFinancialYearEndMonth(FormCleaner
                .cleanAll(request.getParameter(TelpConstants.FormFields.FINANCIAL_YEAR_END_MONTH.getFieldName())));
        part2.setExporterType(
                FormCleaner.cleanAll(request.getParameter(TelpConstants.FormFields.EXPORTER_TYPE.getFieldName())));
        countryCode = FormCleaner
                .cleanAll(request.getParameter(TelpConstants.FormFields.PRIMARY_COUNTRY_OF_EXPORT.getFieldName()));
        // set the country to null, not an empty object.
        TelpCountry exportCountry = null;
        if (StringUtils.isNotBlank(countryCode)) {
            // If there's a country selected, add the object
            exportCountry = populateTelpCountry(request, countryCode);
        }
        part2.setPrimaryCountryOfExport(exportCountry);
        SimpleDateFormat dateformat = new SimpleDateFormat(TelpConstants.TELP_DATE_FORMAT);
        Date date = new Date();
        String applicationSigningDate = dateformat.format(date);
        part2.setApplicationSigningDate(applicationSigningDate);
        part2.setAuthorizedSigningAuthorityName(FormCleaner.cleanAll(
                request.getParameter(TelpConstants.FormFields.AUTHORIZED_SIGNING_AUTHORITY_NAME.getFieldName())));
        part2.setAuthorizedSigningAuthorityTitle(FormCleaner.cleanAll(
                request.getParameter(TelpConstants.FormFields.AUTHORIZED_SIGNING_AUTHORITY_TITLE.getFieldName())));
        part2.setEligibilityStatus(
                FormCleaner.cleanAll(request.getParameter(TelpConstants.FormFields.ELIGIBILITY_STATUS.getFieldName())));
        // MmEmployees for BCAP and MMG
        part2.setMmEmployees(FormCleaner
                .cleanAll(request.getParameter(TelpConstants.FormFields.MM_EMPLOYEES.getFieldName())));
        if (formType.startsWith(TelpConstants.MMG_FORM_TYPE)) {
            populateMMGValues(request, part2);
        }
        String cdiaValue = FormCleaner.cleanAll(request.getParameter(TelpConstants.FormFields.CDIA.getFieldName()));
        part2.setcDIA(
                TelpConstants.CDIA_FORM_VALUE_YES.equals(cdiaValue) ? TelpConstants.VALUE_YES : TelpConstants.VALUE_NO);
        return part2;
    }

    /**
     * populateMMGValues just for MMG Form
     * 
     * @param request
     * @param part2
     */
    private static void populateMMGValues(SlingHttpServletRequest request, TelpFormDOPart2 part2) {
        /** Values from Questionnaire **/
        // soldInternationally
        String soldInternationally = FormCleaner
                .cleanAll(request.getParameter(TelpConstants.FormFields.SOLD_INTERNATIONALLY.getFieldName()));
        // if not valid yes or oui, simply set to N
        if (TelpConstants.FORM_VALUE_YES.equalsIgnoreCase(soldInternationally)
                || TelpConstants.FORM_VALUE_YES_FR.equalsIgnoreCase(soldInternationally)) {
            soldInternationally = TelpConstants.VALUE_YES;
        } else {
            soldInternationally = TelpConstants.VALUE_NO;
        }
        part2.setSoldInternationally(soldInternationally);

        // countryOfOperation
        // set the sectorList to an empty list.
        List<MmEntryCountryOfOperation> countryOpList = new LinkedList<>();
        String contryCodesString = FormCleaner
                .cleanAll(request.getParameter(TelpConstants.FormFields.COUNTRY_OF_OPERATION.getFieldName()));
        // If just "NONE" return the empty object
        if (StringUtils.isNotBlank(contryCodesString) && !TelpConstants.FORM_VALUE_NONE.equals(contryCodesString)) {
            String[] sectorCodes = contryCodesString.split(TelpConstants.MULTIPLE_DELIMITER);
            if (ArrayUtils.isNotEmpty(sectorCodes)) {
                String lovPath = Constants.Paths.EDCDATA_BASE.concat(ConstantsQuestionnaire.OPERATION_COUNTRIES);
                countryOpList = populateMMCountryByLov(request, lovPath, sectorCodes);
            }
        }
        part2.setMmEntryCountryOfOperation(countryOpList);

        // mmEntrySector
        // set the sectorList to an empty list.
        List<MmEntrySector> sectorList = new LinkedList<>();
        String sectorCodesString = FormCleaner
                .cleanAll(request.getParameter(TelpConstants.FormFields.ENTRY_SECTOR.getFieldName()));
        // If just "NONE" return the empty object
        if (!TelpConstants.FORM_VALUE_NONE.equals(sectorCodesString)) {
            String[] sectorCodes = sectorCodesString.split(TelpConstants.MULTIPLE_DELIMITER);
            if (ArrayUtils.isNotEmpty(sectorCodes)) {
                // If there's a country selected, add the object
                sectorList = populateSectorList(request, sectorCodes);
            }
        }
        part2.setMmEntrySector(sectorList);
    }

    /**
     * getLovData gets the answerers from a list of values If Lov is ountryRisk
     * info, use the custom logic
     * 
     * @param lovPath     Lov'resource path
     * @param request     to get the resource
     * @param currentPage to get the language
     * @param answerType
     * @return List<Answer> with data
     */
    private static List<Answer> getLovData(String lovPath, SlingHttpServletRequest request, Page currentPage,
            String goToAnswer, Boolean forceElegible, String answerType) {
        List<Answer> answerList = new LinkedList<>();
        if (StringUtils.isNotBlank(lovPath)) {
            Resource lovRes = request.getResourceResolver().resolve(lovPath);
            Iterator<Resource> lovResIt = lovRes.listChildren();
            String pageLanguage = LanguageUtil.getLanguageAbbreviationFromPath(currentPage.getPath(),
                    Constants.SupportedLanguages.ENGLISH.getLanguageAbbreviation());
            String nameVar = pageLanguage.concat(TelpConstants.NAME);
            // Path on Constant has a trailing "/", remove it
            if (lovPath.endsWith(StringUtils.removeEnd(Constants.Paths.COUTRYINFO_INFO, "/"))) {
                // CountryRisk info, use the custom logic
                answerList = getCountryRiskAnswers(lovResIt, nameVar, request, forceElegible);
            } else if (ConstantsQuestionnaire.OPERATION_COUNTRIES.equals(answerType)) {
                answerList = addOperationCountries(request, lovPath, goToAnswer, pageLanguage);
            } else if (lovPath.startsWith(Constants.Paths.EDCDATA_BASE)) {
                // LOV from edc-data
                answerList = addLovAnswers(lovResIt, nameVar, goToAnswer);
            } else {
                // The LOV node expects have the same structure
                answerList = addAnswers(lovResIt);
            }
        }
        return answerList;
    }

    private static List<Answer> addOperationCountries(SlingHttpServletRequest request, String lovPath, String goTo,
            String langAbbr) {
        List<Answer> answerList = new LinkedList<>();
        if (lovPath != null) {
            String[] topList = new String[2];
            topList[0] = Constants.Properties.CAN_COUNTRY_CODE;
            topList[1] = Constants.Properties.USA_COUNTRY_CODE;
            Map<String, String> options = FormFieldsUtil.getFormFieldOptionsMap(request, lovPath, true, null, topList,
                    langAbbr);
            for (Map.Entry<String, String> entry : options.entrySet()) {
                answerList.add(setAnswerValues(entry.getValue(), entry.getKey(), goTo, null));
            }
        }
        return answerList;
    }

    /**
     * getCountryRiskAnswers use custom logic to create answers based on the
     * CountryRisk nodes
     * 
     * @param lovResIt       resource iterator form node
     * @param countryNameVar Proper language country name
     * @param request        to get the resource
     * @return List<Answer> with data
     */
    private static List<Answer> getCountryRiskAnswers(Iterator<Resource> lovResIt, String countryNameVar,
            SlingHttpServletRequest request, boolean forceElegible) {
        List<Answer> answerList = new LinkedList<>();
        Map<String, String> countries = new HashMap<>();
        Resource countriesRes = request.getResourceResolver().resolve(Constants.Paths.EDCDATA_COUNTRIES);
        Iterator<Resource> countiesIt = countriesRes.listChildren();
        while (countiesIt.hasNext()) {
            Resource country = countiesIt.next();
            String edcCode = country.getValueMap().get(Constants.VALUE, String.class);
            String countryName = country.getValueMap().get(countryNameVar, String.class);
            // Set the country map based on the EDC Code as nodes were changed
            countries.put(edcCode, countryName);
        }
        while (lovResIt.hasNext()) {
            Resource country = lovResIt.next();
            String goTo = "-1";
            String position = country.getValueMap().get(Constants.Properties.COUTRYINFO_POSITION, String.class);
            // Country listed as “closed” or “open on a highly restricted basis”
            if (forceElegible || (position != null && (position.equals(Constants.Properties.COUTRYINFO_POS_OPEN)
                    || position.equals(Constants.Properties.COUTRYINFO_POS_RESTRICTED)))) {
                goTo = "0";
            }
            String nodeName = country.getName();
            // get the country map based on the EDC Code
            if (countries.containsKey(nodeName)) {
                String label = countries.get(nodeName);
                answerList.add(setAnswerValues(label, nodeName, goTo, null));
            } else {
                log.debug("getCountryRiskAnswers Country Info Node Not Found : {}", nodeName);
            }
        }
        String langAbbr = LanguageUtil.getLanguageAbbreviationFromPath(request.getPathInfo(),
                Constants.SupportedLanguages.ENGLISH.getLanguageAbbreviation());
        alphabeticalOrderAnswer(answerList, langAbbr);
        return answerList;
    }

    /**
     * addAnswers add individual answer to the Answer Object List using LOV from
     * edc-data nodes
     * 
     * @param answerItRes resource iterator form node
     * @return List<Answer> with data
     */
    private static List<Answer> addLovAnswers(Iterator<Resource> answerItRes, String countryNameVar, String goTo) {
        List<Answer> answerList = new LinkedList<>();
        while (answerItRes.hasNext()) {
            Resource answerRes = answerItRes.next();
            String label = answerRes.getValueMap().get(countryNameVar, String.class);
            String value = answerRes.getValueMap().get(ConstantsQuestionnaire.Properties.ANSWER_VALUE.getProperty(),
                    String.class);
            answerList.add(setAnswerValues(label, value, goTo, null));
        }
        return answerList;
    }

    /**
     * addAnswers add individual answer to the Answer Object List
     * 
     * @param answerItRes resource iterator form node
     * @return List<Answer> with data
     */
    private static List<Answer> addAnswers(Iterator<Resource> answerItRes) {
        List<Answer> answerList = new LinkedList<>();
        while (answerItRes.hasNext()) {
            Resource answerRes = answerItRes.next();
            String label = answerRes.getValueMap().get(ConstantsQuestionnaire.Properties.ANSWER_LABEL.getProperty(),
                    String.class);
            String value = answerRes.getValueMap().get(ConstantsQuestionnaire.Properties.ANSWER_VALUE.getProperty(),
                    String.class);
            String goTo = answerRes.getValueMap().get(ConstantsQuestionnaire.Properties.ANSWER_GO_TO.getProperty(),
                    String.class);
            String userStatus = answerRes.getValueMap()
                    .get(ConstantsQuestionnaire.Properties.ANSWER_USER_STATUS.getProperty(), String.class);
            answerList.add(setAnswerValues(label, value, goTo, userStatus));
        }
        return answerList;
    }

    /**
     * Set Answer values to Answer object
     * 
     * @param label      Answer's label text
     * @param value      Answer's value
     * @param goTo       Answer's goto value
     * @param userStatus Answer's user Status value
     * @return Answer with data
     */
    private static Answer setAnswerValues(String label, String value, String goTo, String userStatus) {
        Answer answer = new Answer();
        answer.setLabel(label);
        answer.setValue(value);
        answer.setGoTo(goTo);
        answer.setUserStatus(userStatus);
        return answer;
    }

    /**
     * populateTelpCountry populates the TelpCountry object with the selected
     * country's data
     * 
     * @param request
     * @param countryCode
     * @return TelpCountry with the data, empty TelpCountry if no data found
     */
    private static TelpCountry populateTelpCountry(SlingHttpServletRequest request, String countryCode) {
        String[] countryCodes = new String[] { countryCode };
        List<TelpCountry> list = populateCountryByLov(request, Constants.Paths.EDCDATA_COUNTRIES, countryCodes);
        return list.get(0);
    }

    private static List<TelpCountry> populateCountryByLov(SlingHttpServletRequest request, String lovPath,
            String[] countryCodes) {
        List<TelpCountry> countryList = new LinkedList<>();
        FormFieldOption countryForm = null;
        Resource fieldResource = request.getResourceResolver().getResource(lovPath);
        if (fieldResource != null) {
            for (String countryCode : countryCodes) {
                Resource countryRes = fieldResource.getChild(countryCode.toLowerCase());
                if (countryRes != null) {
                    countryForm = countryRes.adaptTo(FormFieldOption.class);
                }
                if (countryForm != null) {
                    TelpCountry telpCountry = new TelpCountry();
                    telpCountry.setCountryCode(countryCode);
                    telpCountry.setCountryNameEn(countryForm.getEnName());
                    telpCountry.setCountryNameFr(countryForm.getFrName());

                    countryList.add(telpCountry);
                }
            }

        }
        return countryList;
    }

    
    private static List<MmEntryCountryOfOperation> populateMMCountryByLov(SlingHttpServletRequest request, String lovPath,
            String[] countryCodes) {
        List<MmEntryCountryOfOperation> countryList = new LinkedList<>();
        FormFieldOption countryForm = null;
        Resource fieldResource = request.getResourceResolver().getResource(lovPath);
        if (fieldResource != null) {
            for (String countryCode : countryCodes) {
                Resource countryRes = fieldResource.getChild(countryCode.toLowerCase());
                if (countryRes != null) {
                    countryForm = countryRes.adaptTo(FormFieldOption.class);
                }
                if (countryForm != null) {
                    MmEntryCountryOfOperation mmCountry = new MmEntryCountryOfOperation();
                    TelpCountry telpCountry = new TelpCountry();
                    telpCountry.setCountryCode(countryCode);
                    telpCountry.setCountryNameEn(countryForm.getEnName());
                    telpCountry.setCountryNameFr(countryForm.getFrName());
                    mmCountry.setCountry(telpCountry);
                    countryList.add(mmCountry);
                }
            }

        }
        return countryList;
    }

    private static List<MmEntrySector> populateSectorList(SlingHttpServletRequest request, String[] sectorCodes) {
        List<MmEntrySector> sectorList = new LinkedList<>();
        // Use same defined model
        FormFieldOption entryForm = null;
        Resource fieldResource = request.getResourceResolver()
                .getResource(Constants.Paths.EDCDATA_BASE.concat(ConstantsQuestionnaire.ENTRY_SECTOR));
        if (fieldResource != null) {
            for (String sectorCode : sectorCodes) {
                Resource sectorRes = fieldResource.getChild(sectorCode.toLowerCase());
                if (sectorRes != null) {
                    entryForm = sectorRes.adaptTo(FormFieldOption.class);
                }
                if (entryForm != null) {
                    MmEntrySector mmEntrySector = new MmEntrySector();
                    mmEntrySector.setSectorCode(sectorCode);
                    sectorList.add(mmEntrySector);
                }
            }
        }
        return sectorList;
    }

    /**
     * addYesNoAnswers Get the Answers from the Yes No Type of question
     * 
     * @param questionRes
     * @return list with the answers
     */
    private static List<Answer> addYesNoAnswers(Resource questionRes) {
        List<Answer> answerList = new LinkedList<>();
        // Answers Yes
        Answer answer = new Answer();
        String label = ConstantsQuestionnaire.Properties.ANSWER_LABEL.getProperty().concat(ConstantsQuestionnaire.YES);
        String value = ConstantsQuestionnaire.Properties.ANSWER_VALUE.getProperty().concat(ConstantsQuestionnaire.YES);
        String goTo = ConstantsQuestionnaire.Properties.ANSWER_GO_TO.getProperty().concat(ConstantsQuestionnaire.YES);
        String userStatus = ConstantsQuestionnaire.Properties.ANSWER_USER_STATUS.getProperty()
                .concat(ConstantsQuestionnaire.YES);
        String specialDeclaration = ConstantsQuestionnaire.Properties.SPECIAL_DECLARATION.getProperty()
                .concat(ConstantsQuestionnaire.YES);
        answer.setLabel(questionRes.getValueMap().get(label, String.class));
        answer.setValue(questionRes.getValueMap().get(value, String.class));
        answer.setGoTo(questionRes.getValueMap().get(goTo, String.class));
        answer.setUserStatus(questionRes.getValueMap().get(userStatus, String.class));
        answer.setSpecialDeclaration(questionRes.getValueMap().get(specialDeclaration, false));
        answerList.add(answer);
        // Answer No
        answer = new Answer();
        label = ConstantsQuestionnaire.Properties.ANSWER_LABEL.getProperty().concat(ConstantsQuestionnaire.NO);
        value = ConstantsQuestionnaire.Properties.ANSWER_VALUE.getProperty().concat(ConstantsQuestionnaire.NO);
        goTo = ConstantsQuestionnaire.Properties.ANSWER_GO_TO.getProperty().concat(ConstantsQuestionnaire.NO);
        userStatus = ConstantsQuestionnaire.Properties.ANSWER_USER_STATUS.getProperty()
                .concat(ConstantsQuestionnaire.NO);
        specialDeclaration = ConstantsQuestionnaire.Properties.SPECIAL_DECLARATION.getProperty()
                .concat(ConstantsQuestionnaire.NO);
        answer.setLabel(questionRes.getValueMap().get(label, String.class));
        answer.setValue(questionRes.getValueMap().get(value, String.class));
        answer.setGoTo(questionRes.getValueMap().get(goTo, String.class));
        answer.setUserStatus(questionRes.getValueMap().get(userStatus, String.class));
        answer.setSpecialDeclaration(questionRes.getValueMap().get(specialDeclaration, false));
        answerList.add(answer);

        return answerList;
    }

    private static void alphabeticalOrderAnswer(List<Answer> selections, String langAbbr) {

        selections.sort(new Comparator<Answer>() {
            public int compare(Answer option1, Answer option2) {
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
                    optionValue1 = option1.getLabel().toUpperCase();
                    optionValue2 = option2.getLabel().toUpperCase();
                } else {
                    locale = Locale.CANADA_FRENCH;
                    optionValue1 = option1.getLabel().toUpperCase();
                    optionValue2 = option2.getLabel().toUpperCase();
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
}
